/*eslint-env node*/

//------------------------------------------------------------------------------
// node.js Bluemix starter code
//------------------------------------------------------------------------------

// This application uses express as its web server
// for more info, see: http://expressjs.com
var express = require('express');

// cfenv provides access to your Cloud Foundry environment
// for more info, see: https://www.npmjs.com/package/cfenv
var cfenv = require('cfenv');

// create a new express server
var app = express();

// serve the files out of ./public as our main files
app.use(express.static(__dirname + '/public'));

// get the app environment from Cloud Foundry
var appEnv = cfenv.getAppEnv();

// start server on the specified port and binding host
app.listen(appEnv.port, '0.0.0.0', function() {

	// print a message when the server starts listening
  console.log("server starting on " + appEnv.url);
});

//------------------------------------------------------------------------------
// GC-Events specific code
//------------------------------------------------------------------------------

//initialize Message Hub Rest Client
var MessageHub = require('message-hub-rest');
var topicName = 'gcevents';
var consumerGroupName = 'my-consumers' +  appEnv.app.instance_id;
var consumerInstanceName = consumerGroupName + 'Instance';
var instance = new MessageHub(appEnv.services);
var consumerInstance;
var eventData = '{"topEvents":[{"row":["loading",0]}],"eventSourceHistory":[{"row":["loading","loading","loading","loading"]},{"row":["loading",0,0,0]}],"eventTable":[{"row":["loading","loading",0]}]}';

//endpoint to get data
app.get("/eventData", function(req, res){
  res.json(eventData);
});

//endpoint to produce sample event data
app.get("/sampleEventData", function(req, res){
    var sampleEventData = '{"topEvents":[{"row":["Event1",13]},{"row":["Event2",23]},{"row":["Event3",33]}],"eventSourceHistory":[{"row":["Time","MDM Server","Information Analyzer","Exception Stage"]},{"row":["02:00",1000,400,1000]},{"row":["02:10",1170,460,800]},{"row":["02:20",660,1120,400]}],"eventTable":[{"row":["EventA","Source1",34]},{"row":["EventB","Source2",54]},{"row":["EventC","Source2",2]},{"row":["EventD","Source3",12]},{"row":["EventE","Source3",66]},{"row":["EventF","Source4",223]}]}';
    var list = new MessageHub.MessageList();
    list.push(sampleEventData);

    instance.produce(topicName, list.messages)
      .then(function(response) {
          console.log(response);
      })
      .fail(function(error) {
      	console.log('produce failed'); 
        throw new Error(error);
      });

  res.json(sampleEventData);
});

// create topic

  instance.topics.create(topicName)
    .then(function(response) {
      console.log(topicName + ' topic created.');
      // Set up a consumer group of the provided name.
      return instance.consume(consumerGroupName, consumerInstanceName, { 'auto.offset.reset': 'largest' });
    })
    .then(function(response) {
      consumerInstance = response[0];
      console.log('Consumer Instance created.');
      // Set offset for current consumer instance.
      return consumerInstance.get(topicName);
    })
    .fail(function(error) {
    console.log(error);
    });

// Set up an interval which will poll Message Hub for new messages on the topic.
 
  var produceInterval = setInterval(function() {

    // Attempt to consume messages
    if(consumerInstance) {
      consumerInstance.get(topicName)
        .then(function(data) {
          console.log('Recieved data length: ' + data.length);        	 
          if(data.length > 0) {
            console.log('Recieved data: ' + data);
            eventData = data;

         //   for(var index in data) {
         //     data[index] = JSON.parse(data[index]);
         //   }
          }
        })
        .fail(function(error) {
          throw new Error(error);
        });
    }
  }, 2000);


