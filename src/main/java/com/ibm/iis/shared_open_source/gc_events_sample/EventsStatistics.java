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
package com.ibm.iis.shared_open_source.gc_events_sample;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


/**
 * class EventsStatistics
 * 
 * The class EventsStatistics gathers statistics about the events consumed by the ConsumerThread instances.
 * Method post allows to send the event statistics to the given Bluemix application.
 */
public class EventsStatistics {
	/// the URL of the Bluemix application to post statistics to
	String applicationURL;
	/// the mapping between event name and Event instance
	Map<String, Event> events = new HashMap<String, Event>();
	/// the list of events sorted by number of occurrences
	List<Event> sortedEvents = new ArrayList<Event>();
	/// the mapping between event source name and EventSource instance
	Map<String, EventSource> sources = new HashMap<String, EventSource>();
	/// the history of the event sources statistics (statistics over the last 20 events received)
	List<EventSource[]> eventSourceHistory = new ArrayList<EventSource[]>();
	/// the last time the list of last event sources was updated
	Long lastHistoryUpdate = System.currentTimeMillis() - 10000;
	/// the list of event source names
	List<String> knownEventSourceNames = new ArrayList<String>();

	
	/**
	 * subclass Event
	 * 
	 * The class EventsStatistics.Event counts the number of events that comes in with a given name and a given source.
	 */
	private class Event {
		/// the event source (attribute applicationType or first 3 characters of attribute eventType)
		public String source;
		/// the event name (attribute exceptionSummaryName or eventType)
		public String name;
		/// the number of events of this name and source.
		public int count;
		
		/**
		 * constructor
		 * 
		 * @param source the source of the event
		 * @param name the name of the event
		 */
		public Event(String source, String name) {
			this.source = source;
			this.name = name;
			this.count = 1;
		}
	}

	private class EventSource {
		/// the event source (attribute applicationType or first 3 characters of attribute eventType)
		public String source;
		/// the number of events of this source.
		public int count;
		
		/**
		 * constructor
		 * 
		 * @param source the source of the event
		 */
		public EventSource(String source) {
			this.source = source;
			this.count = 1;
		}

		private EventSource(String source, int count) {
		   this.source = source;
		   this.count = count;
		}

		/// clone the current EventSource instance
		public EventSource clone() {
			return new EventSource(source, count);
		}
	}
  
   
	/**
	 * constructor
	 * 
	 * @param url the URL of the Bluemix application to post statistics to
	 */
	public EventsStatistics(String url) {
		this.applicationURL = url + "/eventData";
	}

	
	/**
	 * method called when a new kafka topic message is consumed. Statistics are updated to consider this message
	 * 
	 * @param message the Kafka message containing a JSON representation of the event
	 */
	public void addMessage(String message) {
		JsonObject jo = null;
		try {
			jo = Json.createReader(new StringReader(message)).readObject();
		}
		catch (Exception e) {
			System.err.println(new Date() + ": Statistics got exception " + e.getMessage());
			return;
		}
		String eventName = "unidentified";  // all events
		String eventSourceName = "unknown source";  // all events
		if (jo.containsKey("eventType")) { // information server events
			if(jo.containsKey("exceptionSummaryName")) {
				eventName = jo.getString("exceptionSummaryName"); // IA and ES case
				eventSourceName = jo.getString("applicationType");
			} else {
				eventName = jo.getString("eventType");
				eventSourceName = eventName.substring(0, 3);
			}
		}
		
		// maintain event sources
		if (!sources.containsKey(eventSourceName)) {
			sources.put(eventSourceName, new EventSource(eventSourceName));
			knownEventSourceNames.add(eventSourceName);
		} else {
			sources.get(eventSourceName).count += 1;
		}
	   
		// maintain events
		if (!events.containsKey(eventName)) {
			Event event = new Event(eventSourceName, eventName);
			events.put(eventName, event);
			sortedEvents.add(event);
		} else {
			events.get(eventName).count += 1;
		}
	   
		// maintain event source history
		if (eventSourceHistory.size() < 21 || (System.currentTimeMillis() - lastHistoryUpdate) > 10000) {
			//create copies of current sources
			List<EventSource> snapshot = new ArrayList<EventSource>();
			for (int i = 0; i < this.knownEventSourceNames.size(); i++) {
				EventSource source = sources.get(knownEventSourceNames.get(i));
				snapshot.add(source.clone());
			}
			eventSourceHistory.add((EventSource[])snapshot.toArray(new EventSource[0]));
			if(eventSourceHistory.size() > 21) {
				eventSourceHistory.remove(0);
			} 
		}

		// sort event list by count
		Collections.sort(sortedEvents, new Comparator<Event>() {
			@Override
			public int compare(Event event1, Event event2)
			{
	        	if (event1.count > event2.count) {
	        		return  -1; // we want highest counts first
	        	} else {
	        		return 1;
	        	} 	
			}
		});
	}


	/**
	 * method to post the current event statistics to the Bluemix application
	 * 
	 * @return POST query response
	 * @throws Exception
	 */
	public String post() throws Exception
	{
		// get the Json representation of the event statistics
		String body = createJson().toString();
		System.out.println(new Date() + ": Statistics posted to " + applicationURL + ": " + body);
		
		// Define the server endpoint to send the HTTP request to
		URL url = new URL(applicationURL);
		HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

		// Indicate that we want to write to the HTTP request body
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setUseCaches(false);
		urlConnection.setRequestMethod("POST");
		urlConnection.setRequestProperty( "Content-Type", "text/plain"); 
		// Writing the post data to the HTTP request body
		BufferedWriter httpRequestBodyWriter = 
				new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
		httpRequestBodyWriter.write(body);
		httpRequestBodyWriter.close();

		// Reading from the HTTP response body
		Scanner httpResponseScanner = new Scanner(urlConnection.getInputStream());
		StringBuffer response = new StringBuffer();
		while(httpResponseScanner.hasNextLine()) {
			response.append(httpResponseScanner.nextLine());
		}
		httpResponseScanner.close();
		System.out.println(new Date() + ": Statistics post response " + response.toString());
		return response.toString();
	}

	
	/**
	 * method that returns the Json representation of the current event statistics
	 */
	private JsonObject createJson() {
		JsonObjectBuilder job = Json.createObjectBuilder();

		/*
		 * top events
		 */
		JsonArrayBuilder topEventsArray = Json.createArrayBuilder();
		Iterator<Event> topEventsIt =  sortedEvents.iterator();
		int topnum = 0;
		while (topEventsIt.hasNext() && topnum < 10) {
			Event event = topEventsIt.next();
			JsonObjectBuilder topEventRow = Json.createObjectBuilder();
	 		JsonArrayBuilder topEventArray = Json.createArrayBuilder();
	 		topEventArray.add(event.name);
	 		topEventArray.add(event.count);
	 		topEventRow.add("row", topEventArray);
	 		topEventsArray.add(topEventRow);
	 		topnum++;
		}
	    job.add("topEvents", topEventsArray);
	        
	    /*
	     * history
	     */
		JsonArrayBuilder historyArray = Json.createArrayBuilder();
		JsonObjectBuilder historyLabelsRow = Json.createObjectBuilder();
		JsonArrayBuilder historyLabelsArray = Json.createArrayBuilder();
		// first row has the labels
	     historyLabelsArray.add("Time Index");
	     for (int i = 0; i < knownEventSourceNames.size(); i++){	    	 
	    	 historyLabelsArray.add(knownEventSourceNames.get(i));
	     }
	     historyLabelsRow.add("row", historyLabelsArray);
    	 historyArray.add(historyLabelsRow);
	     // fill history data rows
	     Iterator<EventSource[]> sourcesHistoryIt = eventSourceHistory.iterator();
	     int timeLabelValue = eventSourceHistory.size() * -3 + 3;
	     while (sourcesHistoryIt.hasNext()) {
	    	 EventSource[] snapshot = sourcesHistoryIt.next();
	    	 JsonObjectBuilder historyDataRow = Json.createObjectBuilder();
	    	 JsonArrayBuilder historyDataArray = Json.createArrayBuilder();
	    	 historyDataArray.add(timeLabelValue + " sec");
	    	 for(int i = 0; i < snapshot.length; i++) {
	    		 historyDataArray.add(snapshot[i].count);
	    	 }
	    	 //every row needs to be complete - even if previous snapshots do not have all sources
	    	 for (int i = 0; i < knownEventSourceNames.size()-snapshot.length; i++) {
	    		 historyDataArray.add(0);
	    	 }
	    	 historyDataRow.add("row", historyDataArray);
	    	 historyArray.add(historyDataRow);
	    	 timeLabelValue += 3;
	     }
	     job.add("eventSourceHistory", historyArray);
	     
	     /*
	      * table
	      */
	     JsonArrayBuilder tableArray = Json.createArrayBuilder();
	     topEventsIt =  sortedEvents.iterator();
	     while (topEventsIt.hasNext()) {
	    	 Event event = topEventsIt.next();
			JsonObjectBuilder eventRow = Json.createObjectBuilder();
	 		JsonArrayBuilder eventArray = Json.createArrayBuilder();
	    	 eventArray.add(event.name);
	      	 eventArray.add(event.source);
	   	     eventArray.add(event.count);
	    	 eventRow.add("row", eventArray);
	    	 tableArray.add(eventRow);
	     }
	     job.add("eventTable", tableArray);
	     
	     return job.build();
	}


}
