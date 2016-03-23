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
package gcevents;

import java.util.Date;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;


/**
 * class ConsumerThread
 * 
 * The class ConsumerThread is part of a ConsumerGroupExample group that consumes events from a Kafka topic.
 * Statistics about the consumed events are gathered and sent to the given Bluemix application.
 */
public class ConsumerThread implements Runnable {
	/// the Kafka stream to consume events from
	private KafkaStream<byte[], byte[]> m_stream;
	/// the thread index in the consumer group
	private int threadIndex;
	/// the instance that aggregates event statistics and post them to the Bluemix application
	private EventsStatistics stats;

	
	/**
	 * constructor
	 * 
	 * @param a_stream the Kafka topic stream to consume events from
	 * @param a_threadIndex the thread index in the consumer group
	 * @param a_applicationURL the URL of the Bluemix application to post statistics to
	 */
	public ConsumerThread(KafkaStream<byte[], byte[]> a_stream, int a_threadIndex, EventsStatistics a_stats) {
		threadIndex = a_threadIndex;
		m_stream = a_stream;
		this.stats = a_stats;
	}

	
	/**
	 * method that consumes incoming events from the Kafka topic
	 */
	public void run() {
		ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
		try {

			while (it.hasNext()) {
				synchronized (stats) {
					System.out.println(new Date() + ": Consumer thread " + threadIndex + " consumes a message");
					stats.addMessage(new String(it.next().message()));
					stats.post();
				}
			}
		} catch (Exception e) {
			System.err.println(new Date() + ": Consumer thread " + threadIndex + " got exception " + e.getMessage());
		}

		System.out.println("Thread: " + threadIndex + " terminates");
	}


}
