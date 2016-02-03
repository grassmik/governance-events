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

var MessageHub = require('message-hub-rest');
var services = process.env.VCAP_SERVICES;
var instance = new MessageHub(services);

//endpoint to get data
app.get("/eventData", function(req, res){
  getEventData(res);
});


//read current event data
function getEventData(responseObj){

var response = '{"topEvents":[{"row":["Event1",13]},{"row":["Event2",23]},{"row":["Event3",33]}],"eventSourceHistory":[{"row":["Time","MDM Server","Information Analyzer","Exception Stage"]},{"row":["02:00",1000,400,1000]},{"row":["02:10",1170,460,800]},{"row":["02:20",660,1120,400]}],"eventTable":[{"row":["EventA","Source1",34]},{"row":["EventB","Source2",54]},{"row":["EventC","Source2",2]},{"row":["EventD","Source3",12]},{"row":["EventE","Source3",66]},{"row":["EventF","Source4",223]}]}';

 responseObj.json(response);
   
}
