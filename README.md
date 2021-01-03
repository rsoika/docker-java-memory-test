# Docker-Java-Memory-Test

** cgroup driver problem **

https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/install-kubeadm/#configure-cgroup-driver-used-by-kubelet-on-control-plane-node

https://stackoverflow.com/questions/64582065/why-is-openjdk-docker-container-ignoring-memory-limits-in-kubernetes
https://gridscale.io/community/tutorials/kubernetes-cluster-mit-kubeadm/





Verify cgroupDriver used by kubelet


	$ sudo cat /var/lib/kubelet/config.yaml | grep cgroupDriver
	cgroupDriver: systemd



Verify cgroupDriver used by Docker


	$ sudo docker info | grep -i cgroup
	Cgroup Driver: systemd
	


	NAME              STATUS   ROLES    AGE    VERSION
	ixchel-master-1   Ready    master   183d   v1.19.0	NOT OK
	ixchel-worker-1   Ready    <none>   183d   v1.19.3	NOT OK  -> jetzt OK
	ixchel-worker-2   Ready    <none>   183d   v1.19.3	OK		 -> upgrade from 1.19.0 - 3
	ixchel-worker-3   Ready    <none>   183d   v1.19.3	NOT OK	 -> jetzt OK
	ixchel-worker-5   Ready    <none>   48d    v1.19.3	OK	     -> upgrade from 1.19.1 - 3




Muster config.yaml 

	apiVersion: kubelet.config.k8s.io/v1beta1
	authentication:
	  anonymous:
	    enabled: false
	  webhook:
	    cacheTTL: 0s
	    enabled: true
	  x509:
	    clientCAFile: /etc/kubernetes/pki/ca.crt
	authorization:
	  mode: Webhook
	  webhook:
	    cacheAuthorizedTTL: 0s
	    cacheUnauthorizedTTL: 0s
	cgroupDriver: systemd
	clusterDNS:
	- 10.96.0.10
	clusterDomain: cluster.local
	cpuManagerReconcilePeriod: 0s
	evictionPressureTransitionPeriod: 0s
	fileCheckFrequency: 0s
	healthzBindAddress: 127.0.0.1
	healthzPort: 10248
	httpCheckFrequency: 0s
	imageMinimumGCAge: 0s
	kind: KubeletConfiguration
	logging: {}
	nodeStatusReportFrequency: 0s
	nodeStatusUpdateFrequency: 0s
	rotateCertificates: true
	runtimeRequestTimeout: 0s
	staticPodPath: /etc/kubernetes/manifests
	streamingConnectionIdleTimeout: 0s
	syncFrequency: 0s
	volumeStatsAggPeriod: 0s




Restart with

	$ sudo systemctl daemon-reload
	$ sudo systemctl restart kubelet






	
	
Fix it with: https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/install-kubeadm/#configure-cgroup-driver-used-by-kubelet-on-control-plane-node

https://www.ibm.com/support/knowledgecenter/ja/SSBS6K_2.1.0.3/troubleshoot/kubelet_fails.html

https://gridscale.io/community/tutorials/kubernetes-cluster-mit-kubeadm/



Simple demonstration Wildfly 20.0.1-final (openJDK 11):


Start Wildfly Container with docker and a memory limit of 230M:

	$ docker run -it --rm --name java-wildfly-test -p 8080:8080 -e JAVA_OPTS='-XX:MaxRAMPercentage=75.0' -m=300M jboss/wildfly:20.0.1.Final


Verify Memory:

	$ docker stats
	515e549bc01f        java-wildfly-test                                                                                                    0.14%               219MiB / 300MiB       73.00%              906B / 0B           0B / 0B             43



Same test with kubectl 


	$ kubectl run java-wildfly-test --image=jboss/wildfly:20.0.1.Final --limits='memory=300M' --env="JAVA_OPTS='-XX:MaxRAMPercentage=75.0'" 


Verify Memory:

	$ kubectl top pod java-wildfly-test
	NAME                CPU(cores)   MEMORY(bytes)   
	java-wildfly-test   1089m        441Mi 





delete the pod with


	$ kubectl delete pod java-wildfly-test
	















# Jakarta Project

This is just a simple Jakarta EE project to test memory behavior of Wildfly running in a Docker container.

I suspect there is a memory problem related to the Docker Memory Limits and Java 11. This project provides some kind of a test environment based on Wildfly 20.0.1. I do not think that the issue is related to Wildfly but am trying to setup a test environment which is more similar to that environments which we use in Imixs-Workflow to run our open source workflow engine. 

In the following there are two szenarios:

 * Deplyoment on Docker-Swarm
 * Deployment on Kubernetes
 
The memory consumption of the same container is heavily different in both environments.

# Build

The application provides a simple Jakarata EE Jax-RS Rest Service and is build with latest release of Wildfly 20.0.1. I use the official Wildfly Docker image here which is using Java 11. 

To build the application together with a Docker image run:

	$ mvn clean install -Pdocker
	

# Test Docker Swarm

To test the core behavior of the Docker container we run the container run a local docker swarm.

To init a local docker-swarm run:

	$ docker swarm init --advertise-addr 127.0.0.1


## Run
	
To run use docker
	
	
	$ docker run -it --rm --name docker-java-memory-test -p 8080:8080 -e JAVA_OPTS='-XX:MaxRAMPercentage=75.0' -m=340M soika/docker-java-memory-test
	

	$ docker run -it --rm --name java-wildfly-test -p 8080:8080 -e JAVA_OPTS='-XX:MaxRAMPercentage=75.0' -m=230M jboss/wildfly:20.0.1.Final



## Test

	http://[DOCKER-HOST-NAME]:8080/docker-java-memory-test/api/data
	
	
	
## Test Memory Consumption

	$ docker stats 
	CONTAINER ID        NAME                                                           CPU %               MEM USAGE / LIMIT   MEM %               NET I/O             BLOCK I/O           PIDS
	31cb1d4cfb1f        docker-java-memory-test_test-app.1.gia7uh49o3ss4cqca7nczjumm   10.44%              331.7MiB / 340MiB   97.55%              13.6kB / 2.11MB     70.6MB / 86kB       67


The docker container seems not to overflow the memory limits of 340M
	
See also: https://www.skillbyte.de/java-heap-settings-in-docker-containern/



# Test Kubernetes



	$ kubectl run java-wildfly-test --image=jboss/wildfly:20.0.1.Final --limits='memory=230M' --env="JAVA_OPTS='-XX:MaxRAMPercentage=75.0'" 


delete the pod with


	$ kubectl delete pod java-wildfly-test
	




$ kubectl logs $(kubectl get pods|grep mycontainer|awk '{ print $1 }'|head -1)|grep MaxHeapSize uintx     MaxHeapSize := 314572800     {product}



The file "kubernetes-deployment.yaml" provides a deployment resource example for Kubernetes. 
The POD is started with resource limits


**push to kubernetes registry (optional):**

	docker build -t soika/docker-java-memory-test:latest .
	docker tag soika/docker-java-memory-test:latest registry.foo.com/library/soika/docker-java-memory-test:latest
	docker push registry.foo.com/library/soika/docker-java-memory-test:latest

	
**deploy:**

	$ kubectl apply -f kubernetes-deployment.yaml	
	
	
## Test Memory Consumption

After deployment you can use kubectl top to verify the memory consumption:

	$ kubectl top  pod docker-java-memory-test-b7c7d95f4-rrnpc
	NAME                                      CPU(cores)   MEMORY(bytes)   
	docker-java-memory-test-b7c7d95f4-rrnpc   271m         675Mi    	

Immediately after start the container takes much more memory as defined in the limits:


	
	
	