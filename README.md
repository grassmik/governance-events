<a href="https://bluemix.net/deploy?repository=https://github.com/grassmik/GovernanceEvents" target="_blank"><img src="http://bluemix.net/deploy/button.png" alt="Bluemix button" /></a>

# Gov-Events-Sample

The Gov-Events-Sample project provides a sample application that renders some amazing charts showing Information Server events statistics. The statistics are gathered by an Events Kafka client sample deployed on the Information Server side.

## What you'll need

* A [Bluemix](https://developer.ibm.com/sso/bmregistration?lang=en_US&ca=dw-_-bluemix-_-wa-simplenode1-app-_-article) account
* A [DevOps Services](https://hub.jazz.net/?utm_source=dw&utm_campaign=bluemix&utm_content=wa-simplenode1-app&utm_medium=article) account

## Getting and running the Bluemix app

0. Open the project GIT URL: https://hub.jazz.net/git/charpiot/Gov-Events-Sample
0. Click the **FORK PROJECT** button to create your own copy of the code, provide a unique name (e.g.,"eventsmonitor") and click the **CREATE** button.
0. Click the **EDIT CODE** button to begin working with the application.
0. Click the **Deploy the App from the Workspace** button and wait for the deployment to finish.
0. Click **Open deployed application** to open the web interface. The application is now waiting for events sent by the Information Server Events Kafka client sample.

## Deploying the Events Kafka client sample

0. At the bottom of the deployed application, click on the **deployment page** link.
0. Follow the instructions to deploy the Events kafka client sample.

The application now updates the displayed charts as events come in.

## Building the source code:

The source code of the Bluemix application is available in the app.js and public/index.html files. 

The java source code Events Kafka client sample source is available in the src directory.

To build the Bluemix application and the Events Kafka client sample source code, follow these steps:

0. In your forked Bluemix project, click the **EDIT CODE** button to go to the edition view.
0. Click the **BUILD & DEPLOY** button to go to the pipeline view.
0. If not already defined, create a first step to build the project:
   * Give a name of your choice, for example Build.
   * In the "Input" tab, make sure that the input type is "SCM reference" and that the GIT referenced is correct.
   * In the "Work" tab, add a "Build" work, give it a name of your choice, for example "Build", select Maven as type, modify the build script to "mvn -B package install" and replace the output directory from default "target" to "app".
0. If not already defined, create a second step to deploy the project:
   * Give a name of your choice, for example Deploy.
   * In the "Input" tab, make sure that the input type is "building artefacts" and that previous step and work name are selected.
   * In the "Work" tab, add a "Deploy" work and give it a name of your choice, for example "Deploy". You can keep the default value of all other configuration parameters.

You can now edit the source code in your Bluemix project. Each time a code modification is synchronized into the GIT repository, the Bluemix app will be built and deployed automatically.
