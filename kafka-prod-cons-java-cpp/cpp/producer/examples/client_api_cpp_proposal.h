#include <pthread.h>
#include <sys/types.h>
#include <cstdio>
#include <iostream>
#include <map>
#include <string>
#include <utility>

#include "../src-cpp/rdkafkacpp.h"
#include "../src-cpp/rdkafkacpp_int.h"

/**
 * @author peterk
 * Kafka producer API proposal
 *
 * Serialization of message omitted, we assume some serializer, aka GPB
 * That could be agreed per topic or in general.
 * 
 */

#ifndef KCLIENT
#define KCLIENT

class KafkaClient  {




public:
        KafkaClient();
        ~KafkaClient();

        //global flag, true - while connected and event callback has no errors yet
        //expect only 1 instance of such producer, but if you need more you need get around that
        static bool run;

        //Intentionally static signal callback
        static void sigterm(int sig);



        /**
         * @brief connectAsProducer - connect to kafka server to serve as producer for several topics
         *
         * The design assumes a limited number of topics (1-100, realistically probably no more than ~ 10)
         * Topic is a wide-wide-scope definition
         * List of topics known from start (that is difference from previous design)
		 * 
		 * Previous design, where you had 7000 topics is against Kafka's intention.
		 * It is not scalable. Instead - partitions should be employed.
         *
         * And usually producer knows all its topics by name
         *
         * More fine-grained parts of the topic distributed among partitions.
         * The way topic parts distributed defined by partition callback!
         *
         * You need now a KEY to send with every message to Kafka
         * this key used to distribute data between partitions and watched by the partition callback for such distribution
         *
         * Also, usually, topic may refer to its own partitioner (sometime for performance reasons)
         * But for us is OK if we use the universal one PARTITIONER
         * (universal, but note - not default one)
         *
         * @param brokersList  - ip:port,ip:port...etc (separator is "," - this is how RDKafka API accustomed to get it, so I did not change that )
         * @param topics_list  - topics to serve by this producer
         * @param topics_num   - number topics in the list
         *
         * @return true for success, false for failure
         */
        bool connectAsProducer(const std::string brokersList,  const std::string topics_list[] , const int topics_num);

        /**
         * @brief isConnected
         * @return true if connected to kafka server. false otherwise
         * it turns false if callback return errors.
         */
        bool isConnected();

        /**
         * @brief produce - publish data to server (added key of data!)
         * @param topic_name
         * @param key        - message key - it is essential to define "on flight" what partition is chosen
         * @param key_length - message key length
         * @param buffer     - the data associated with topic
         * @param size       - data buffer size
         */
        void produce(std::string topic_name, char * key, int key_length, char *buffer, int size);

        /**
         * @brief disconnect - disconnect from kafka server
         */
        void disconnect();


private:


        //some predefined callbacks(!)

        //report callback
        class ExampleDeliveryReportCb: public RdKafka::DeliveryReportCb {
        public:
        	void dr_cb(RdKafka::Message &message) {
        		std::cout << "Message delivery for (" << message.len() << " bytes): "
        				<< message.errstr() << std::endl;
        	}
        };

        //event callback
        class ExampleEventCb: public RdKafka::EventCb {

        public:

        	void event_cb(RdKafka::Event &event) {
        		switch (event.type()) {
        		case RdKafka::Event::EVENT_ERROR:
        			std::cerr << "ERROR (" << RdKafka::err2str(event.err()) << "): "
        					<< event.str() << std::endl;
        			if (event.err() == RdKafka::ERR__ALL_BROKERS_DOWN)
        				std::cerr << "ERROR!!! \"STATS\": " << event.str() << std::endl;
        			    KafkaClient::run = false;
        			break;

        		case RdKafka::Event::EVENT_STATS:
        			std::cerr << "\"STATS\": " << event.str() << std::endl;
        			break;

        		case RdKafka::Event::EVENT_LOG:
        			fprintf(stderr, "LOG-%i-%s: %s\n", event.severity(),
        					event.fac().c_str(), event.str().c_str());
        			break;

        		default:
        			std::cerr << "EVENT " << event.type() << " ("
        					<< RdKafka::err2str(event.err()) << "): " << event.str()
        					<< std::endl;
        			break;
        		}
        	}
        };

        /**
         * Partitioner callback example, provided within installation
         * Such partitioner is not extremely good.
         * It does not guarantee fair distribution of keys between available partitions
         * Key distribution depends on number of available keys and bucket size, etc..
         * I propose another more fair distribution, that depends on key counter..
         *
         */
        class TheirHashPartitionerCb: public RdKafka::PartitionerCb {
        public:
        	int32_t partitioner_cb(const RdKafka::Topic *topic, const std::string *key,
        			int32_t partition_cnt, void *msg_opaque) {
        		return djb_hash(key->c_str(), key->size()) % partition_cnt;
        	}
        private:

        	static inline unsigned int djb_hash(const char *str, size_t len) {
        		unsigned int hash = 5381;
        		for (size_t i = 0; i < len; i++)
        			hash = ((hash << 5) + hash) + str[i];
        		return hash;
        	}
        };

        /**
         * This is partitioner, that I propose
         * map implementation inside is not SO important, change it if you want,
         * important only find=O(1), I took the one I found
         *
         * This partitioner (callback) assumes that message keys are of char* type
         * suppose you have 5 keys
         *
         * aaa
         * bbb
         * ccc
         * ddd
         * eee
         *
         *
         * And only 2 partitions defined on Kafka for this topic
         *
         * Suppose you sent messages with keys in that exact order
         *
         * eee
         * aaa
         * bbb
         * ccc
         * ddd
         *
         * This partitioner will sort keys between partitions in such a way:
         *
         * Partition 0: eee, bbb, ddd
         * Partition 1: aaa, ccc
         *
         * As you see, new key falls in available partitions by order and fairly
         *
         *
         * This partitioner uses a map to save already calculated key-partition pairs
         * To register the new pair, we enter synchronized block - expected effect is minimal, for new registration is rare
         */
        class FairPartitionerCb: public RdKafka::PartitionerCb {


        public:
        	~FairPartitionerCb(){
        	    partitions_candidates.clear();

        	}

        	int32_t partitioner_cb(const RdKafka::Topic *topic, const std::string *key,	int32_t partition_cnt, void *msg_opaque){

        		     std::cerr << "% Works with partitioner " << std::endl;

                     int32_t val = 0;

                     std::string key_data = key->c_str();

                     if(partitions_candidates.count(key_data)==0){

        		    	 //enter this block only for new keys
        		    	 pthread_mutex_lock(&mutex_partitions);

        		    	 //re-check the entry in the synch block
                         if(partitions_candidates.count(key_data)==0){

        		    		 std::cerr << "% There is no partition for key " << key_data << ", assigning..." << std::endl;

                             partitions_candidates.insert( std::pair<std::string,int32_t>(key_data, counter % partition_cnt) );

                             counter++;
        		    	 }

        		    	 pthread_mutex_unlock (&mutex_partitions);

        		     }

        		     val = partitions_candidates.at(key_data);

                     std::cerr << "% The partition for topic " << topic <<  " and  key " << key_data << "  is " << val << std::endl;
                     return val;

        	}
	  private:
		//a map to save existing pairs key->partition
		std::map<std::string, int32_t> partitions_candidates;
		//counter to track last used partition candidate number
		int32_t counter = 0;
		//mutex to synchronize sender threads for the period of new partition number calculation
		pthread_mutex_t mutex_partitions;
	  };

      //producer of this connector
      RdKafka::Producer *producer;
     
	 //flag to print configs
      bool do_conf_dump;

      //report callback
      ExampleDeliveryReportCb reportCb;
	  
      //event callback
      ExampleEventCb eventCb;

      std::string errstr;
	  
      std::string debug;

	  //defines the handler of the topic
	  //each topic managed by this object on producer side
      class TopicHandler{
          public:
    	  RdKafka::Topic *topic;
		  //I assign this partitioner to all topics, because its usage is quite universal
    	  FairPartitionerCb*  defaultPartitioner = new FairPartitionerCb();
          ~TopicHandler(){
             delete topic;
             delete defaultPartitioner;
    	  }
      };

      //one producer connector must have handlers for each topic configuration
      //key-topic name, value - topic handler
      std::map<std::string, TopicHandler*> topic_handlers_map;
	  //mutex to avoid concurrent re-initialization of the producer
      pthread_mutex_t mutex_connection;


};

#endif





