/**
 * @author peterk
 */
#include <cfuhash.h>//hashtable implementation :https://github.com/codebrainz/libcfu
#include <errno.h>
#include <inttypes.h>
#include <pthread.h>
#include <rdkafka.h>  /* for Kafka driver */
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>
#include <syslog.h>

static rd_kafka_t *rk;

static int run = 1;

/**
 * Message delivery report callback.
 * Called once for each message.
 * See rdkafka.h for more information.
 */
static void msg_delivered (rd_kafka_t *rk,
			   void *payload, size_t len,
			   int error_code,
			   void *opaque, void *msg_opaque) {

	if (error_code)
		fprintf(stderr, "%% Message delivery failed: %s\n",
			rd_kafka_err2str(error_code));
	else
		fprintf(stderr, "%% Message delivered (%zd bytes)\n", len);
}

/**
 * Message delivery report callback using the richer rd_kafka_message_t object.
 */
static void msg_delivered2 (rd_kafka_t *rk,
                            const rd_kafka_message_t *rkmessage, void *opaque) {
        if (rkmessage->err)
		fprintf(stderr, "%% Message delivery failed: %s\n",
                        rd_kafka_message_errstr(rkmessage));
	else
		fprintf(stderr,
                        "%% Message delivered (%zd bytes, offset %"PRId64", "
                        "partition %"PRId32")\n",
                        rkmessage->len, rkmessage->offset, rkmessage->partition);
}


/**
 * Kafka logger callback (optional)
 */
static void logger (const rd_kafka_t *rk, int level,
		    const char *fac, const char *buf) {
	struct timeval tv;
	gettimeofday(&tv, NULL);
	fprintf(stderr, "%u.%03u RDKAFKA-%i-%s: %s: %s\n",
		(int)tv.tv_sec, (int)(tv.tv_usec / 1000),
		level, fac, rd_kafka_name(rk), buf);
}



//global hashtable to save existing pairs key->partition
cfuhash_table_t *partitions_candidates;
//counter to track last used partition candidate number
int32_t counter = 0;
//mutex to synchronize sender threads for the period of new partition number calculation
pthread_mutex_t mutex_partitions;


/**
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
 * This partitioner uses hashtable to save already calculated key-partition pairs
 * To register the new pair, we enter synchronized block - expected effect is minimal, for new registration is rare
 *
 */

static int32_t partitioner_fair_round_robin (const rd_kafka_topic_t *rkt, const void *keydata, size_t keylen, int32_t partition_cnt, void *rkt_opaque, void *msg_opaque){

	 fprintf(stderr, "%% Works with partitioner \n");

	 int32_t *val;

	 const char* pKey = keydata;

     val = cfuhash_get(partitions_candidates, pKey);

     if(val == NULL){

    	 //enter this block only for new keys
    	 pthread_mutex_lock(&mutex_partitions);

    	 //re-check the entry in the synch block
    	 val = cfuhash_get(partitions_candidates, pKey);

    	 if(val==NULL){

             fprintf(stderr, "%% There is no partition for key %s\n", pKey);

			 val = malloc(sizeof(int32_t));

			 *val = counter % partition_cnt; //divide partition candidates between available partitions

			 fprintf(stderr, "%% Chosen partition for key %s is %d\n",pKey, *val);

			 cfuhash_put(partitions_candidates, pKey, val);

			 counter++;
    	 }

    	 pthread_mutex_unlock (&mutex_partitions);

     }else{
    	 fprintf(stderr, "%% There was partition for key %s ,it is %d \n",pKey, *val);
     }

     return *val;
}




/**
 * Example, where you send topic data, specifying message key and using partitioner
 *
 * Note, partition number for specific topic defined by kafka admin
 * In this case expect a few topics and a lot of partitions per topic
 */
int main (int argc, char **argv) {

	    //assuming, I expect such large number of partitions
	    partitions_candidates = cfuhash_new_with_initial_size(10000);

	    pthread_mutex_init(&mutex_partitions, NULL);


	    rd_kafka_topic_t *rkt;

	    //from now on, you need assume we work with kafka cluster,
	    //you have to specify several instances
	    char *brokers = "10.72.2.90:9092,10.72.2.90.9093";

        char *topic = "TOPIC3";//example topic

		int partition = RD_KAFKA_PARTITION_UA;//undefined partition - but it is redefined by partitioner (see below)

        rd_kafka_conf_t *conf;

		rd_kafka_topic_conf_t *topic_conf;

		char errstr[512];

        int report_offsets = 0;

        char tmp[16];

		conf = rd_kafka_conf_new(); //create configuration

		rd_kafka_conf_set(conf, "internal.termination.signal", tmp, NULL, 0);

		topic_conf = rd_kafka_topic_conf_new();

        //set defined partitioner callback (!important!)
		rd_kafka_topic_conf_set_partitioner_cb(topic_conf, partitioner_fair_round_robin);

        char buf[2048];
		int sendcnt = 0;

		/* Set up a message delivery report callback.
		* It will be called once for each message, either on successful
		* delivery to broker, or upon failure to deliver to broker. */

		/* If offset reporting (-o report) is enabled, use the
		* richer dr_msg_cb instead. */
		if (report_offsets) {
		       rd_kafka_topic_conf_set(topic_conf,
		       "produce.offset.report",
		       "true", errstr, sizeof(errstr));
               rd_kafka_conf_set_dr_msg_cb(conf, msg_delivered2);
		} else
		       rd_kafka_conf_set_dr_cb(conf, msg_delivered);

		/* Create Kafka handle */
		if (!(rk = rd_kafka_new(RD_KAFKA_PRODUCER, conf,	errstr, sizeof(errstr)))) {
					fprintf(stderr,
						"%% Failed to create new producer: %s\n",
						errstr);
					exit(1);
		}

				/* Set logger */
	    rd_kafka_set_logger(rk, logger);
	    rd_kafka_set_log_level(rk, LOG_DEBUG);

		/* Add brokers */
		if (rd_kafka_brokers_add(rk, brokers) == 0) {
			fprintf(stderr, "%% No valid brokers specified\n");
			exit(1);
		}

		/* Create topic */
		rkt = rd_kafka_topic_new(rk, topic, topic_conf);




		while (run ) {

			    fprintf(stderr,	"%% Type message content and hit enter\n");

			    fgets(buf, sizeof(buf), stdin);
				size_t len = strlen(buf);
				if (buf[len-1] == '\n')
					buf[--len] = '\0';

                char key_buf[1024];


                fprintf(stderr,	"%% Type message key and hit enter to send(the key defines partition)\n");
                fgets(key_buf, sizeof(key_buf), stdin);
				size_t key_len = strlen(key_buf);

				/* Send/Produce message. */
				if (rd_kafka_produce(rkt, partition,
						     RD_KAFKA_MSG_F_COPY,
						     /* Payload and length */
						     buf, len,
						     /* Optional key and its length */
							 key_buf, key_len,
						     /* Message opaque, provided in
						      * delivery report callback as
						      * msg_opaque. */
						     NULL) == -1) {
					fprintf(stderr,
						"%% Failed to produce to topic %s "
						"partition %i: %s\n",
						rd_kafka_topic_name(rkt), partition,
						rd_kafka_err2str(
							rd_kafka_errno2err(errno)));
					/* Poll to handle delivery reports */
					rd_kafka_poll(rk, 0);
					continue;
				}

				fprintf(stderr, "%% Sent %zd bytes to topic %s partition %i\n",
				len, rd_kafka_topic_name(rkt), partition);
				sendcnt++;
				/* Poll to handle delivery reports */
				rd_kafka_poll(rk, 0);
		}

		/* Poll to handle delivery reports */
		rd_kafka_poll(rk, 0);

		/* Wait for messages to be delivered */
		while (run && rd_kafka_outq_len(rk) > 0)
				rd_kafka_poll(rk, 100);

		/* Destroy topic */
		rd_kafka_topic_destroy(rkt);

		/* Destroy the handle */
		rd_kafka_destroy(rk);

		pthread_mutex_destroy(&mutex_partitions);
		pthread_exit(NULL);

		cfuhash_clear(partitions_candidates);
		cfuhash_destroy(partitions_candidates);

        return 0;

}





