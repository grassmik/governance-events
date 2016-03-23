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
package com.ibm.iis.shared-open-source.gc-events-sample;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;


/**
 * class CDemoProducerExample
 * 
 * The class DemoProducerExample is a Java sample how to produce demo events to a Kafka topic.
 * Demo events are sent to the Kafka topic during a given time.
 */
public class DemoProducerExample {

	/// the Kafka topic to consume
	private final String topic;
	/// the Kafka producer
	private KafkaProducer<String, String> producer;
  
	/// the demo events that are produced in a random manner
	static String[] demoEvents = new String[] {
			"{\"exceptionSummaryName\":\"AgeCheckCustomer\",\"exceptionSummaryUID\":\"ES3799694797\",\"applicationType\":\"DataStage\",\"eventType\":\"NEW_EXCEPTIONS_EVENT\",\"_isEncrypted\":false,\"projectName\":\"dstage1\"}",
			"{\"exceptionSummaryName\":\"MDM Load\",\"exceptionSummaryUID\":\"ES3799694797\",\"applicationType\":\"DataStage\",\"eventType\":\"NEW_EXCEPTIONS_EVENT\",\"_isEncrypted\":false,\"projectName\":\"dstage1\"}",
			"{\"exceptionSummaryName\":\"Age_check_BANK_CUSTOMERS\",\"exceptionSummaryUID\":\"ec1481df.f92d8a7c.8gi55tku4.us62b40.eslueo.3j1gef3ltc43p7q8cf6f6\",\"applicationType\":\"Information Analyzer\",\"eventType\":\"NEW_EXCEPTIONS_EVENT\",\"_isEncrypted\":false,\"projectName\":\"IOD_BANK_DEMO\"}",
			"{\"ASSET_TYPE\":\"Information Governance Rule\",\"ASSET_RID\":\"6662c0f2.e1b13efc.8gi55ct6c.l1eh0ds.3ttrq2.dueampgjs67s528k6b1el\",\"eventType\":\"IGC_BUSINESSRULE_EVENT\",\"_isEncrypted\":false,\"ASSET_CONTEXT\":\"\",\"ACTION\":\"RETURN_TO_DRAFT\",\"ASSET_NAME\":\"mike\"}",
			"{\"ASSET_TYPE\":\"Category\",\"ASSET_RID\":\"6662c0f2.ee6a64fe.8gi55tkv1.l9pjbs2.45dm9g.v0gali68jai4qgif85aqd\",\"eventType\":\"IGC_BUSINESSCATEGORY_EVENT\",\"_isEncrypted\":false,\"ASSET_CONTEXT\":\"\",\"ACTION\":\"CREATE\",\"ASSET_NAME\":\"sdfsf\"}",
			"{\"ASSET_TYPE\":\"Term\",\"ASSET_RID\":\"6662c0f2.ee6a64fe.8gi55tkv1.l9pjbs2.45dm9g.v0gali68jai4qgif85aqd\",\"eventType\":\"IGC_BUSINESSTERM_EVENT\",\"_isEncrypted\":false,\"ASSET_CONTEXT\":\"\",\"ACTION\":\"CREATE\",\"ASSET_NAME\":\"sdfsf\"}",
			"{\"exceptionSummaryName\":\"NBR_YEARS_CLIENTS\",\"exceptionSummaryUID\":\"ec1481df.f92d8a7c.8gi55tl0j.rs0707g.5gqdp7.b1qpji6nd06qtvk4lpa24\",\"applicationType\":\"Information Analyzer\",\"eventType\":\"NEW_EXCEPTIONS_EVENT\",\"_isEncrypted\":false,\"projectName\":\"IOD_BANK_DEMO\"}",
			"{\"exceptionSummaryName\":\"Zip_Domain_Validation\",\"exceptionSummaryUID\":\"ec1481df.f92d8a7c.8gi55tl9c.m7ab330.gol2pi.ivlb34dadsfn7emrh85pc\",\"applicationType\":\"Information Analyzer\",\"eventType\":\"NEW_EXCEPTIONS_EVENT\",\"_isEncrypted\":false,\"projectName\":\"IOD_BANK_DEMO\"}",
			"{\"exceptionSummaryName\":\"MDM Duplicate\",\"exceptionSummaryUID\":\"ES3799694797\",\"applicationType\":\"MDM Server\",\"eventType\":\"MDM Duplicate\",\"_isEncrypted\":false,\"projectName\":\"dstage1\"}",
			"{\"exceptionSummaryName\":\"IMAM Import\",\"exceptionSummaryUID\":\"ES3799694797\",\"applicationType\":\"IMAM\",\"eventType\":\"IMAM Event\",\"_isEncrypted\":false,\"projectName\":\"dstage1\"}"
	};


	/**
	 * constructor
	 * 
	 * @param a_broker the hostname and port of a kafka broker
	 * @param a_topic the Kafka topic to produce into
	 */
	public DemoProducerExample(String a_broker, String a_topic) {
		this.producer = createProducer(a_broker);
		this.topic = a_topic;
	}
   
	/**
	 * method to produce demo events during a given time
	 * 
	 * @param duration the duration of the demo
	 */
	public void run(int duration) {
		try {
			long startTime = System.currentTimeMillis();
			Random r = new Random();
			while (System.currentTimeMillis() - startTime < duration) {
				// the next random events to produce
				int i = r.nextInt(demoEvents.length);
				// the next record to produce
				ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, demoEvents[i]);
				// send the next record to the kafka topic
				System.out.println(new Date() + ": Producer sends event " + i + " to kafka topic " + topic);
				producer.send(record, new Callback() {					
					@Override
					public void onCompletion(RecordMetadata recordMetadata, Exception exception) {
						if (exception != null) {
							System.err.println(new Date() + ": Producer got exception " + exception.toString());
						}
					}
				});
				// wait 3 seconds until next event
				Thread.sleep(3000);
		   	}
		} catch (Exception e) {
			System.err.println("Producer got exception " + e.getMessage());
	   	}
	}
   
   
	/**
	 * method to define the Kafka Producer configuration
	 * 
	 * @param a_broker the hostname and port of a kafka broker
	 * @return a new instance of KafkaProducer
	 */
	private static KafkaProducer<String, String> createProducer(String a_broker) {
		Properties props = new Properties();
		props.put("bootstrap.servers", a_broker);
		props.put("compression.type", "none");
		props.put("value.serializer", StringSerializer.class.getName());
		props.put("key.serializer", StringSerializer.class.getName());
		
		return new KafkaProducer<String, String>(props);
	}


}
