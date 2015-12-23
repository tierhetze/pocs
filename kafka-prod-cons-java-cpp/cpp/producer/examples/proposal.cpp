/*
 * proposal.cpp
 *
 *  Created on: Jul 27, 2015
 *      Author: peter
 */
#include <cstdlib>
#include <cstring>
#include "rdkafkacpp.h"
#include "client_api_cpp_proposal.h"


#define ARRAY_SIZE(array) (sizeof((array))/sizeof((array[0])))



int main(int argc, char **argv) {

	//test sample data
	static const std::string regions[]   = {"LN", "LN", "LN"};
	static const std::string sides[]     = {"A", "B"};

	static const std::string securities[] = { "SEC1","SEC2","CES3"};
	static const std::string producers[] = {"S1", "S2"};

    //note, now if you do not define partition number on Kafka, it will have only single (ZERO) partition per topic.
	//so now you have to define partitions number on Kafka server
	//by command like that:
	//kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 2 --partitions 10 --topic TOPIC3

	static const std::string topics[] = {"TOPIC7", "TOPIC8"};

	static const std::string brokers = "10.72.2.90:9092,10.72.2.90:9093";

    KafkaClient client;
	bool connected = client.connectAsProducer(brokers, topics, 2);


	std::cout << "Press any key and enter to start send messages" << std::endl;
	std::string here;
    std::cin >> here;


	if(connected){
        //randomly send different messages to different topics
		for(int i=0;i<1000000;i++){

			int index = rand() % ARRAY_SIZE(regions);
			std::string region = regions[index];

			int index_sides = rand() % ARRAY_SIZE(sides);
			std::string side = sides[index_sides];

			int index_sec = rand() % ARRAY_SIZE(securities);
			std::string security = securities[index_sec];

			int index_p = rand() % ARRAY_SIZE(producers);
			std::string  prodinstance = producers[index_p];

			std::string key;
			key.append(region);
			key.append(side);
			key.append(security);
			key.append(prodinstance);

			char *keyc = new char[key.length() + 1];
			std::strcpy(keyc, key.c_str());

			int index_t = rand() % ARRAY_SIZE(topics);
			std::string topic = topics[index_t];



			int message_size = 100;
			char *message  = new char[message_size];
			for(int y=0;y<message_size;y++){
				message[y] = rand();
			}



			client.produce(topic, keyc ,key.size(), message, message_size);

			std::cout << "message " << i << " sent to topic " << topic << std::endl;


		}
    }

	std::cout << "The sending ended(!!!), press any key to stop the program" << std::endl;
	std::cin >> here;





}



