The GC-Events project provides a Node.js 
application that renders some amazing charts 
showing InfoSphere event statistics. 
It uses the Message Hub service to communicate with
an Information Server Kafka client. 

Usage:

 
1. Fork this project into a new DevOps Services project.
2. Add a Message Hub service to the new project and deploy the application.
3. From the iis folder, copy the gcevents.jar to your local machine. If you use Information Server, copy it to the machine where it is installed. Run the jar with:
   
   java -jar gcevents.jar localhost:52181 http://gc-events.mybluemix.net demo 

 NOTE: specify the URL that routes to your Bluemix application, for example http://gc-events.mybluemix.net.
4. In a browser open your Bluemix application, for example http://gc-events.mybluemix.net

The appication shows some charts and you can see how they get updated in real-time as events come in. With the specified demo parameter, the application automatically creates sample events. You can omit the demo parameter to just read the events coming from Information Server. 