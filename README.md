# Docker-Java-Memory-Test

This is just a simple Jakarta EE project to test memory behavior of Wildfly running in a Docker container.

I suspect there is a memory problem related to the Docker Memory Limits and Java 11. This project provides some kind of a test environment based on Wildfly 20.0.1. I do not think that the issue is related to Wildfly but am trying to setup a test environment which is more similar to that environments which we use in Imixs-Workflow to run our open source workflow engine. 

## Build

The application provides a simple Jakarata EE Jax-RS Rest Service and is build with latest release of Wildfly 20.0.1. I use the official Wildfly Docker image here which is using Java 11. 

To build the application together with a Docker image run:

	$ mvn clean install -Pdocker

## Run

Start docker swarm so we can use the memory limits in docker-compose.yaml file.

	$ docker swarm init --advertise-addr 127.0.0.1
	
	
To run use docker-compose 

	$ docker stack deploy -c docker-compose.yaml memory-test

To undeploy run:

	$ docker stack rm  memory-test
	

## Test

	http://[DOCKER-HOST-NAME]:8080/kubernetes-memory-test/api/data
	
	
	
## Test Memory

	$ docker stats 
	CONTAINER ID        NAME                                               CPU %               MEM USAGE / LIMIT   MEM %               NET I/O             BLOCK I/O           PIDS
	a71df37b711c        memory-test_test-app.1.th1i2t173h2v0kcwlqmfn9pnn   17.01%              322.5MiB / 380MiB   89.87%              3.42kB / 0B         0B / 86kB           67
	
	
	
	