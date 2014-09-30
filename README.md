subrosa
=======

# Subrosa API

## Vagrant and VirtualBox Setup
This is the recommended way to setup the Subrosa API if you don't plan on doing any development.  The setup is automated and only requires one command after installing the prerequisites. 

Prerequisites
 * [Vagrant](https://www.vagrantup.com/)
 * [VirtualBox](https://www.virtualbox.org/)

After installing the prerequisites all you need to do is run: ```./sr init``` and wait for the deployment to finish.  After everything is done you should have a working server at http://10.10.10.42:8080.

## Developer Setup 
If you plan on contributing to the Subrosa API you will likely want to set up your own instance locally.

### Dependencies
You will need to install the following to be able to run the Subrosa API locally:
 * [JDK 1.7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) 
 * [Tomcat](http://tomcat.apache.org/)
 * [PostgreSQL](http://www.postgresql.org/)
 * [Maven](http://maven.apache.org/)
 * [ActiveMQ](http://activemq.apache.org/)


#### Import certificates
Download [nexus.subrosagames.com.crt](https://trac.subrosagames.com/raw-attachment/wiki/technology/sandbox/api/nexus.subrosagames.com.crt) and run the following (the default password for java system keystores is `changeit`):

```
sudo keytool -import -trustcacerts -alias "nexus.subrosagames.com" -file nexus.subrosagames.com.crt -keystore  $JAVA_HOME/jre/lib/security/cacerts
```
