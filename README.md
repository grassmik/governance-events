The GC-Events project provides a Node.js 
application that renders some amazing charts 
showing InfoSphere event statistics. 
It uses the Message Hub service to communicate with
an Information Server Kafka client. 

Usage:

1. Fork this project into a new DevOps Services project and make it a Bluemix project during the fork: click the Fork Project button on the DevOps Services project page: https://hub.jazz.net/project/grasselt/GC-Events/overview. 

2. Add a Message Hub service to the Bluemix project and run the application.
3. From the iis folder, copy the Information Server Kafka client (gcevents.jar) to your local machine. Run the jar with:
   
   java -jar gcevents.jar iishost http://gc-events.mybluemix.net demo 

 NOTE: The first parameter is your Information Server host. If you do not have an Information Server installed, you can still use this client to create demo events: specify a dummy host name and the demo parameter as shown in the sample command. The second parameter points to the URL that routes to your Bluemix application as displayed on the Bluemix dashboard. Specify "demo" as the third parameter to enable automatic creation of sample events. This works even if you specify a dummy hostname as the first parameter.
4. In a browser open your Bluemix application, for example http://gc-events.mybluemix.net

The application now updates the displayed charts as events come in. 


Source code:

The source of the Bluemix application is available in the app.js and public/index.html files. 

To get and build the Information Server Kafka Client source code, follow these steps:

1. In eclipse, create a new Java Project.
2. Right-click the src folder of your new project and select Import....
3. Expand the General folder and click Archive File.
4. Browse to the gcevent.jar file and expand the folder in the left screen.
5. Select only the gcevents folder and the org/json folder (uncheck all other folders) and click Finish.
6. Right-click your project and click Properties.
7. Select Java Build Path on the left and then click the Add External Jars button on the right.
8. Browse to the <IIS install dir>\shared-open-source\kafka\install\libs folder, select all jar files, click Open, click OK.

You can now edit and build the Information Server Kafka client implementation available in the gcevents package of your project.