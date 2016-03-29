^/*eslint-env node*/

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
// GC-Events-Sample specific code
//------------------------------------------------------------------------------

//initialize events statistics
var initialEventData = '{"topEvents":[{"row":["loading",0]}],"eventSourceHistory":[{"row":["loading","loading"]},{"row":["loading",0]}],"eventTable":[{"row":["loading","loading",0]}]}';
var eventData = initialEventData;
 
//endpoint to get last statistics
app.get("/eventData", function(req, res){
  res.json(eventData);
});

// endpoint to post last statistics 
app.post("/eventData", function(req, res){
  console.log("posted body=" + req.body);
  eventData = req.body;
  res.json('{"response":"success"}');
  console.log("posted eventData=" + eventData);
});
