/**
 * @author peterk
 */
#include <iostream>
#include <string>
#include <cstdlib>
#include <cstdio>
#include <csignal>
#include <cstring>
#include <getopt.h>
#include <map>
#include <pthread.h>

/*
 * Typically include path in a real application would be
 * #include <librdkafka/rdkafkacpp.h>
 */
#include "rdkafkacpp.h"

static bool run = true;
static bool exit_eof = false;

static void sigterm(int sig) {
	run = false;
}

class ExampleDeliveryReportCb: public RdKafka::DeliveryReportCb {
public:
	void dr_cb(RdKafka::Message &message) {
		std::cout << "Message delivery for (" << message.len() << " bytes): "
				<< message.errstr() << std::endl;
	}
};

class ExampleEventCb: public RdKafka::EventCb {
public:
	void event_cb(RdKafka::Event &event) {
		switch (event.type()) {
		case RdKafka::Event::EVENT_ERROR:
			std::cerr << "ERROR (" << RdKafka::err2str(event.err()) << "): "
					<< event.str() << std::endl;
			if (event.err() == RdKafka::ERR__ALL_BROKERS_DOWN)
				run = false;
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
 * Partitioner example, provided within installation
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
 * map implementation inside is not SO important, I took the one I found
 * It must be hashmap , however, to have a fast find
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
	int32_t partitioner_cb(const RdKafka::Topic *topic, const std::string *key,	int32_t partition_cnt, void *msg_opaque) {

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
		     std::cerr << "% The partition for key " << key_data << " ,it is " << val << std::endl;
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

void msg_consume(RdKafka::Message* message, void* opaque) {
	switch (message->err()) {
	case RdKafka::ERR__TIMED_OUT:
		break;

	case RdKafka::ERR_NO_ERROR:
		/* Real message */
		std::cout << "Read msg at offset " << message->offset() << std::endl;
		if (message->key()) {
			std::cout << "Key: " << *message->key() << std::endl;
		}
		printf("%.*s\n", static_cast<int>(message->len()),
				static_cast<const char *>(message->payload()));
		break;

	case RdKafka::ERR__PARTITION_EOF:
		/* Last message */
		if (exit_eof) {
			run = false;
		}
		break;

	default:
		/* Errors */
		std::cerr << "Consume failed: " << message->errstr() << std::endl;
		run = false;
	}
}




/**
 * Example, where you send topic data, specifying message key and using partitioner
 *
 * Note, partition number for specific topic defined by kafka admin
 * In this case expect a few topics and a lot of partitions per topic
 */
int main(int argc, char **argv) {

	std::string brokers = "10.72.2.90:9092,10.72.2.90.9093";
	std::string errstr;
	std::string topic_str="TOPIC5";
	std::string mode="P";
	std::string debug;
	int32_t partition = RdKafka::Topic::PARTITION_UA;
	int64_t start_offset = RdKafka::Topic::OFFSET_BEGINNING;
	bool do_conf_dump = true;

	TheirHashPartitionerCb hash_partitioner;//not used
	FairPartitionerCb fair_partitioner;//used here

    /*
	 * Create configuration objects
	 */
	RdKafka::Conf *conf = RdKafka::Conf::create(RdKafka::Conf::CONF_GLOBAL);
	RdKafka::Conf *tconf = RdKafka::Conf::create(RdKafka::Conf::CONF_TOPIC);


	if (tconf->set("partitioner_cb", &fair_partitioner, errstr) !=
	            RdKafka::Conf::CONF_OK) {
	          std::cerr << errstr << std::endl;
	          exit(1);
	}

	/*
	 * Set configuration properties
	 */
	conf->set("metadata.broker.list", brokers, errstr);

	if (!debug.empty()) {
		if (conf->set("debug", debug, errstr) != RdKafka::Conf::CONF_OK) {
			std::cerr << errstr << std::endl;
			exit(1);
		}
	}

	ExampleEventCb ex_event_cb;
	conf->set("event_cb", &ex_event_cb, errstr);

	if (do_conf_dump) {
		int pass;

		for (pass = 0; pass < 2; pass++) {
			std::list<std::string> *dump;
			if (pass == 0) {
				dump = conf->dump();
				std::cout << "# Global config" << std::endl;
			} else {
				dump = tconf->dump();
				std::cout << "# Topic config" << std::endl;
			}

			for (std::list<std::string>::iterator it = dump->begin();
					it != dump->end();) {
				std::cout << *it << " = ";
				it++;
				std::cout << *it << std::endl;
				it++;
			}
			std::cout << std::endl;
		}
		//exit(0);
	}

	signal(SIGINT, sigterm);
	signal(SIGTERM, sigterm);


	/*
	 * Producer mode
	 */
	ExampleDeliveryReportCb ex_dr_cb;

	/* Set delivery report callback */
	conf->set("dr_cb", &ex_dr_cb, errstr);

	/*
	 * Create producer using accumulated global configuration.
	 */
	RdKafka::Producer *producer = RdKafka::Producer::create(conf, errstr);
	if (!producer) {
		std::cerr << "Failed to create producer: " << errstr << std::endl;
		exit(1);
	}

	std::cout << "% Created producer " << producer->name() << std::endl;

	/*
	 * Create topic handle.
	 */
	RdKafka::Topic *topic = RdKafka::Topic::create(producer, topic_str,
			tconf, errstr);
	if (!topic) {
		std::cerr << "Failed to create topic: " << errstr << std::endl;
		exit(1);
	}

	/*
	 * Read messages from stdin and produce to broker.
	 */
	while (run) {

		std::cout << "Enter message content and press enter" << std::endl;

		std::string line;
		std::getline(std::cin, line);

		if (line.empty()) {
			producer->poll(0);
			continue;
		}

		std::cout << "Enter message key and press enter" << std::endl;

		std::string key;
		std::getline(std::cin, key);

		if (key.empty()) {
			producer->poll(0);
			continue;
		}



		/*
		 * Produce message
		 */
		RdKafka::ErrorCode resp = producer->produce(topic, partition,
				RdKafka::Producer::RK_MSG_COPY /* Copy payload */,
				const_cast<char *>(line.c_str()), line.size(),
				const_cast<char *>(key.c_str()), key.size(), NULL);
		if (resp != RdKafka::ERR_NO_ERROR)
			std::cerr << "% Produce failed: " << RdKafka::err2str(resp)
					<< std::endl;
		else
			std::cerr << "% Produced message (" << line.size() << " bytes)"
					<< std::endl;

		producer->poll(0);
	}
	run = true;

	while (run and producer->outq_len() > 0) {
		std::cerr << "Waiting for " << producer->outq_len() << std::endl;
		producer->poll(1000);
	}

	delete topic;
	delete producer;



	/*
	 * Wait for RdKafka to decommission.
	 * This is not strictly needed (when check outq_len() above), but
	 * allows RdKafka to clean up all its resources before the application
	 * exits so that memory profilers such as valgrind wont complain about
	 * memory leaks.
	 */
	RdKafka::wait_destroyed(5000);

	return 0;
}
