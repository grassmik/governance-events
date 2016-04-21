# The Governance Events Sample

The Gov-Events-Sample project provides a sample application that renders some amazing charts showing Information Server events statistics. The statistics are gathered by an Events Kafka client sample deployed on the Information Server side.

## What you'll need

* A [Bluemix](https://developer.ibm.com/sso/bmregistration?lang=en_US&ca=dw-_-bluemix-_-wa-simplenode1-app-_-article) account
* A [DevOps Services](https://hub.jazz.net/?utm_source=dw&utm_campaign=bluemix&utm_content=wa-simplenode1-app&utm_medium=article) account

## Deploying and Building the application on Bluemix

Use the magical button below to automatically deploy this sample application to Bluemix. 

0. Click <a href="https://bluemix.net/deploy?repository=https://github.com/grassmik/governance-events" target="_blank"><img src="http://bluemix.net/deploy/button.png" alt="Bluemix button" /></a> to open the Deploy to Bluemix page.
0. Log in to or sign up for Bluemix.
0. Name your new app and specify any options as needed if you do not like the defaults.
0. Click DEPLOY.

When the deployment is completed, a private DevOps Services project is set up. The project contains a running instance of the sample application, a configured build pipeline and a dedicated Git repository that you can use to make updates to the application.

After the application is deployed, you need to complete the build pipeline and rebuild the project with the following steps:

0. Click EDIT CODE
0. Click BUILD & DEPLOY to open the pipeline page
0. In the Build Stage, click the Stage Configuration icon and select Configure Stage
0. Modify the second line of the Build Shell Command script to **mvn -B package install**
0. In the Build Archive Directory field, enter **app**
0. Click SAVE
0. In the Build Stage, click the Run Stage icon. After the build, the Build Stage will automatically trigger the Deploy Stage. Wait for the Deploy Stage to finish.

## Running the application

0. Open your Bluemix dashboard, click the new application and use the displayed Routes URL to open the web interface. The application is now waiting for events sent by the Information Server Events Kafka client sample.
0. Follow the instructions on the bottom of the web interface to download and run the Kafka client sample.
0. After the Kafka client sample is started, the application updates the displayed charts as events come in.
