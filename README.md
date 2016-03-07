# GC-Events

The GC-Events project provides an application that renders some amazing charts showing Information Server events statistics. It uses the [IBM Message Hub](https://console.ng.bluemix.net/catalog/services/message-hub) to communicate with an Information Server Kafka client.

## What you'll need

* A [Bluemix](https://developer.ibm.com/sso/bmregistration?lang=en_US&ca=dw-_-bluemix-_-wa-simplenode1-app-_-article) account
* A [DevOps Services](https://hub.jazz.net/?utm_source=dw&utm_campaign=bluemix&utm_content=wa-simplenode1-app&utm_medium=article) account

## Getting and running the Bluemix app

0. Open the project GIT URL: https://hub.jazz.net/git/grasselt/GC-Events
0. Click the **FORK PROJECT** button to create your own copy of the code, provide a unique name (e.g.,"eventsmonitor") and click the **CREATE** button.
0. Click the **EDIT CODE** button to begin working with the application.
0. Click the **Deploy the App from the Workspace** button and wait for the deployment to finish. A solid red dot and (stopped) indicates that the deployment failed. Why? Because the application depends on the IBM Message Hub service that is added in the next step. 
0. Click **DASHBOARD**, click your new application (e.g.,"eventsmonitor") and click **ADD SERVICE OR API**
0. In the Web and Application category select **Message Hub**, click the **CREATE** button and then **RESTAGE** and wait for the application staging to finish. Your app is running now.
0. Click the Routes link (e.g., eventsmonitor.mybluemix.net) of your application to open it. The application is now waiting for events sent by the Information Server Events Kafka client sample.

## Getting and running the Information Server Events Kafka client sample

0. Download the Kafka client sample (gcevents.jar) to your local machine: https://ibm.box.com/s/8zq0xjavcgypdnf83uoa8clc0dylys3p
0. Run the sample with this command:
    
   java -jar gcevents.jar iishost http://eventsmonitor.mybluemix.net demo
     
    NOTE: The first parameter is your Information Server host. If you do not have an Information Server installed, you can still use this client to create demo events: specify a dummy host name and the demo parameter as shown in the sample command. The second parameter points to the URL that routes to your Bluemix application as displayed on the Bluemix dashboard. Specify "demo" as the third parameter to enable automatic creation of sample events. This works even if you specify a dummy hostname as the first parameter.

The application now updates the displayed charts as events come in.

## Getting the source code:

The source of the Bluemix application is available in the app.js and public/index.html files. 

To get and build the Kafka client sample source code, follow these steps:

0. In eclipse, create a new Java Project.
0. Right-click the src folder of your new project and select Import....
0. Expand the General folder and click Archive File.
0. Browse to the gcevent.jar file and expand the folder in the left screen.
0. Select only the gcevents folder and the org/json folder (uncheck all other folders) and click Finish.
0. Right-click your project and click Properties.
0. Select Java Build Path on the left and then click the Add External Jars button on the right.
0. Browse to the <IIS install dir>\shared-open-source\kafka\install\libs folder, select all jar files, click Open, click OK.
 
You can now edit and build the Information Server Kafka client implementation available in the gcevents package of your project.