#include "client_api_cpp_proposal.h"
#include <csignal>


/**
 * @author peterk
 *
 * Kafka producer API Impl proposal
 *
 */

bool KafkaClient::run;

KafkaClient::KafkaClient(){
	producer = NULL;//inited in connectAsProducer
	do_conf_dump = true;//for tests
	KafkaClient::run = false;
}

KafkaClient::~KafkaClient() {
    disconnect();
    std::map<std::string,TopicHandler*>::iterator iter;
    for (iter = topic_handlers_map.begin(); iter != topic_handlers_map.end(); iter++) {
    	delete iter->second;
    }
    topic_handlers_map.erase(iter);
}


//signal handler
void KafkaClient::sigterm(int sig) {
   KafkaClient::run = false;
}



bool KafkaClient::connectAsProducer(const std::string brokersList, const std::string topics_list[] , const int topics_num) {

	if(KafkaClient::run){
		return true;
	}

	pthread_mutex_lock(&mutex_connection);

	//double check
	if(KafkaClient::run){
		return true;
	}


    std::string brokers   = brokersList;

    /*
	* Create configuration objects
	*/
	RdKafka::Conf *conf = RdKafka::Conf::create(RdKafka::Conf::CONF_GLOBAL);

    /*
	 * Set configuration properties
	*/
	conf->set("metadata.broker.list", brokers, errstr);
    conf->set("event_cb", &eventCb, errstr);

	if (do_conf_dump) {
		int pass;

		for (pass = 0; pass < 2; pass++) {
			std::list<std::string> *dump;
			if (pass == 0) {
				dump = conf->dump();
				std::cout << "# Global config" << std::endl;
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
	}




	signal(SIGINT, KafkaClient::sigterm);
	signal(SIGTERM, KafkaClient::sigterm);


	/* Set delivery report callback */
	conf->set("dr_cb", &reportCb, errstr);

	/*
	 * Create producer using accumulated global configuration.
	 */
	producer = RdKafka::Producer::create(conf, errstr);
	if (!producer) {
		std::cerr << "Failed to create producer: " << errstr << std::endl;
		return false;
	}

	std::cout << "% Created producer " << producer->name() << std::endl;





	//create multiple topics configurations
	for(int i=0; i < topics_num ; i++){

		std::string topic_name = topics_list[i];

		std::cout << "% Creating topic config  " << topic_name << std::endl;

		TopicHandler* tHandler = new TopicHandler();

		RdKafka::Conf *tconf = RdKafka::Conf::create(RdKafka::Conf::CONF_TOPIC);

		if (tconf->set("partitioner_cb", tHandler->defaultPartitioner, errstr) !=
							RdKafka::Conf::CONF_OK) {
			 std::cerr << errstr << std::endl;
			 return false;
		}

		/*
		* Create topic handle.
		*/
		tHandler->topic = RdKafka::Topic::create(producer, topic_name,	tconf, errstr);

		if (!tHandler->topic) {
			std::cerr << "Failed to create topic: " << errstr << std::endl;
			return false;
		}

		topic_handlers_map.insert( std::pair<std::string,TopicHandler*>(topic_name, tHandler));

	}


	KafkaClient::run = true;
	pthread_mutex_unlock(&mutex_connection);

	return true;
}



void KafkaClient::produce(std::string topic_name, char * key, int key_length, char * buffer, int size ){
    if (isConnected()) {

    	std::cout << "message " << topic_name << " " << key << " " <<  key_length << " " << buffer << " " << size << std::endl;

    	TopicHandler* tHandler = topic_handlers_map.at(topic_name);

    	RdKafka::ErrorCode resp = producer->produce(tHandler->topic, RdKafka::Topic::PARTITION_UA, RdKafka::Producer::RK_MSG_COPY, buffer, size, key, key_length, NULL);

		if (resp != RdKafka::ERR_NO_ERROR)
			std::cerr << "% Produce failed: " << RdKafka::err2str(resp) << std::endl;
		else
			std::cerr << "% Produced message (" << size << " bytes)" << std::endl;

    	producer->poll(0);

    } else {
    	std::cerr << "% Produce failed, producer is not connected " << std::endl;
    }
}

bool KafkaClient::isConnected() {
    if (!KafkaClient::run) {
        return false;
    } else {
        return true;
    }
}

void KafkaClient::disconnect() {
	KafkaClient::run = false;
    delete producer;
}






