#Reactive Streams Starter

### Get started
- create a bitbucket account if you don't have one
- clone project
- in IDE or on command line run mvn clean install in directory of project

#### Local MongoDb
For this demo I use MongoDb locally on my machine.
Information about how to install MongoDb locally , see https://docs.mongodb.com/manual/installation/

To see what is going on in my MongoDb environment I use MongoDB Compass Community
https://docs.mongodb.com/compass/master/install/

### Install Spring Boot CLI
https://docs.spring.io/spring-boot/docs/2.0.3.RELEASE/reference/htmlsingle/#getting-started-installing-the-cli

### First install
install the project with maven, mvn clean install

### Run application
mvn spring-boot:run

### Build runnable JAR file
mvn package

### Build docker image file
mvn package docker:build

#### Run in Docker
docker run -p 10000:10000 -t elstarit/reactive-streams-starter

#### Stop Docker container
docker ps

#### Will give you a container id and run
docker stop <containerid>

### Run profiles
```
mvn spring-boot:run -Pprod
```
the Spring boot 2.0 way
```
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Push to CloudFoundry

### IBM Cloud
```
bluemix login -u userName -o orgName -s spaceName
bluemix app push reactive-streams-starter -f manifest.yml
```

### Pivotal
```
cf login -u userName -o orgName -s spaceName
cf push reactive-streams-starter -f manifest.yml
```

## Swagger
There is a Swagger UI
```
http://localhost:10000/swagger-ui.html
```
where you can test the endpoints

## NEW Router endpoints
```
http://localhost:10000/reactive/streams/router/topic/all
```

## Frontend
```
http://localhost:4200/
```


