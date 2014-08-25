subrosa
=======

# Subrosa API

## Developer Setup 

### Dependencies
You will need to install the following to be able to run the Subrosa API locally:
 * [JDK 1.7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) 
 * [Tomcat](http://tomcat.apache.org/)
 * [PostgreSQL](http://www.postgresql.org/)
 * [Maven](http://maven.apache.org/)
 * [ActiveMQ](http://activemq.apache.org/)


### Configure Maven to use self-signed cert

####Setup ~/.m2/settings.xml

```
<settings>
  <servers>
    <server>
      <id>subrosa-snapshots</id>
      <username>walden</username>
      <password>password</password>
    </server>
    <server>
      <id>nexus</id>
      <username>walden</username>
      <password>password</password>
    </server>
  </servers>
  <mirrors>
    <mirror>
      <!--This sends everything else to /public -->
      <id>nexus</id>
      <mirrorOf>*</mirrorOf>
      <url>https://nexus.subrosagames.com/content/groups/public</url>
    </mirror>
  </mirrors>
  <profiles>
    <profile>
      <id>nexus</id>
      <!--Enable snapshots for the built in central repo to direct -->
      <!--all requests to nexus via the mirror -->
      <repositories>
        <repository>
          <id>central</id>
          <url>http://central</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </repository>
      </repositories>
     <pluginRepositories>
        <pluginRepository>
          <id>central</id>
          <url>http://central</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>
  <activeProfiles>
    <!--make the profile active all the time -->
    <activeProfile>nexus</activeProfile>
  </activeProfiles>
</settings>
```

#### Import certificates
Download [nexus.subrosagames.com.crt](https://trac.subrosagames.com/raw-attachment/wiki/technology/sandbox/api/nexus.subrosagames.com.crt) and run the following (the default password for java system keystores is `changeit`):

```
sudo keytool -import -trustcacerts -alias "nexus.subrosagames.com" -file nexus.subrosagames.com.crt -keystore  $JAVA_HOME/jre/lib/security
```
