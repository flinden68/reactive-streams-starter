#Reactive Streams Starter

### Get started
- install MongoDb or have access to a MongoDb
- clone project
- in IDE or on command line run mvn clean install in directory of project

### Run application
spring boot:run

### Local MongoDb
For this demo I use MongoDb locally on my machine.
Information about how to install MongoDb locally , see https://docs.mongodb.com/manual/installation/

To see what is going on in my MongoDb environment I use MongoDB Compass Community
https://docs.mongodb.com/compass/master/install/

###Build runnable JAR file
mvn package

###Build docker image file
mvn package docker:build

####Run in Docker
docker run -p 10000:10000 -t elstarit/reactive-streams-starter

####Stop Docker container
docker ps

####Will give you a container id and run
docker stop <containerid>

###Run profiles
```
mvn spring-boot:run -Pprod
```
the Spring boot 2.0 way
```
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

##Push to CloudFoundry
```
bluemix login -u userName -o orgName -s spaceName
bluemix app push reactive-streams-starter
```

##Swagger
There is a Swagger UI, http://localhost:10000/swagger-ui.html, where you can test the endpoints
