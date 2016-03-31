//***************************************************************************
// (c) Copyright IBM Corp. 2016 All rights reserved.
// 
// The following sample of source code is owned by International 
// Business Machines Corporation or one of its subsidiaries ("IBM") and is 
// copyrighted and licensed, not sold. You may use, copy, modify, and 
// distribute the Sample in any form without payment to IBM, for the purpose of 
// assisting you in the development of your applications.
// 
// The Sample code is provided to you on an "AS IS" basis, without warranty of 
// any kind. IBM HEREBY EXPRESSLY DISCLAIMS ALL WARRANTIES, EITHER EXPRESS OR 
// IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. Some jurisdictions do 
// not allow for the exclusion or limitation of implied warranties, so the above 
// limitations or exclusions may not apply to you. IBM shall not be liable for 
// any damages you suffer as a result of using, copying, modifying or 
// distributing the Sample, even if IBM has been advised of the possibility of 
// such damages.
//***************************************************************************
package com.ibm.iis.shared_open_source.gov_events_sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;


/**
 * class ConsumerGroupExample
 * 
 * The class ConsumerGroupExample is a Java sample how to consume events from a Kafka topic.
 * Statistics about the consumed events are gathered and sent to the given Bluemix application
 * by a thread of type ConsumerThread.
 */
public class ConsumerGroupExample {
	/// the connector to the Kafka server
	private final ConsumerConnector consumer;
	/// the Kafka topic to consume
	private final String topic;
	/// the thread executor service to run the ConsumerThread into
	private ExecutorService executor;
	/// the instance that aggregates event statistics and post them to the Bluemix application
	private EventsStatistics stats;

	
	/**
	 * constructor
	 * 
	 * @param a_zookeeper the hostname and port of a zookeeper server the Kafka server is connected to
	 * @param a_groupId the unique consumer group ID
	 * @param a_topic the Kafka topic to consume
	 * @param a_applicationURL the URL of the Bluemix application to post statistics to
	 */
	public ConsumerGroupExample(String a_zookeeper, String a_groupId, String a_topic, String a_applicationURL) {
		consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig(a_zookeeper, a_groupId));
		this.topic = a_topic;
		this.executor = null;
		this.stats = new EventsStatistics(a_applicationURL);
	}

	
	/**
	 * method to stop the Kafka consumer threads
	 */
	public void shutdown() {
		if (consumer != null)
			consumer.shutdown();
		if (executor != null)
			executor.shutdown();
		try {
			if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
				System.err.println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
			}
		} catch (InterruptedException e) {
			System.err.println("Interrupted during shutdown, exiting uncleanly");
		}
	}

	/**
	 * method to start the Kafka consumer threads (asynchronous)
	 * 
	 * @param a_numThreads the number of threads to use
	 */
	public void run(int a_numThreads) {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(a_numThreads));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

		// now launch all the threads
		//
		executor = Executors.newFixedThreadPool(a_numThreads);

		// now create an object to consume the messages
		//
		int threadNumber = 0;
		for (final KafkaStream<byte[], byte[]> stream : streams) {
			executor.submit(new ConsumerThread(stream, threadNumber, stats));
			threadNumber++;
		}
	}

	/**
	 * method to define the Kafka Consumer configuration
	 * 
	 * @param a_zookeeper the hostname and port of a zookeeper server the Kafka server is connected to
	 * @param a_groupId the unique consumer group ID
	 * @return a new instance of ConsumerConfig
	 */
	private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", a_zookeeper);
		props.put("group.id", a_groupId);
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		props.put("autooffset.reset", "smallest");

		return new ConsumerConfig(props);
	}

	
	/**
	 * main entry point of the jar file
	 * 
	 * @param args the user arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if ((args.length != 2) && ((args.length != 3) || !args[2].equals("demo"))) {
			System.out
					.println("sample usage: java -jar Gov-Events-Sample-1.0.jar localhost gov-events-sample.mybluemix.net [demo]");
			return;
		}

		// zookeeper hostname and default port in the shared-open-source patch
		String zooKeeper = args[0] + ":52181";
		// kafka hostname and default port in the shared-open-source patch
		String kafka = args[0] + ":59092";
		// URL of the bluemix application forked from sample application Gov-Events-Sample
		String bluemix = args[1];

		// default Kafka topic name
		String topic = "InfosphereEvents";
		/* unique group ID for the consumers.
		 * Consumers of the same group share all events of the Kafka topic
		 */
		String groupId = "gov-events-sample" + System.currentTimeMillis();
		// default number of consumer threads
		int threads = 1;

		ConsumerGroupExample example = null;
		try {
			System.out.println("connecting to zookeeper " + zooKeeper);
			example = new ConsumerGroupExample(zooKeeper, groupId, topic, bluemix);
			example.run(threads);
			System.out.println("listening on " + topic);
		    System.out.println("you can now start creating events with Information Server");
		} catch (Exception e) {
			System.err.println("Consumer group got exception " + e.getMessage());
			return;
		}

		// duration of the example in ms (= 20 min)
		int duration = 12000000;
		try {
			DemoProducerExample demo = null;
			if (args.length == 3) {
				// create some demo events
				System.out.println("Creating demo events during " + (duration/1000) + " seconds before ending...");
				demo = new DemoProducerExample(kafka, topic);
				demo.run(duration);				
			} else {
				// wait for IIS events
				System.out.println("Waiting " + (duration/1000) + " seconds before ending...");
				Thread.sleep(duration);
			}
		} finally {
			if (example != null) example.shutdown();
		}
	}


}