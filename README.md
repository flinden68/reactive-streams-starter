#Reactive Streams Starter

### Get started
- clone project
- in IDE or on command line run mvn clean install in directory of project

### Run application
spring boot:run


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