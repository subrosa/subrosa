subrosa
=======

# Subrosa API

## Vagrant and VirtualBox Setup
This is the recommended way to setup the Subrosa API. The setup is automated with a goal of single-step provisioning and deployments.

### Prerequisites
 * [Vagrant](https://www.vagrantup.com/downloads.html)
 * [VirtualBox](https://www.virtualbox.org/wiki/Downloads)
 * [Gradle](http://www.gradle.org/downloads)

### Configuration
Create a `gradle.properties` with your personal details. A sample is checked in.
```
cp gradle.properties-sample gradle.properties
```

### Hints (Optional)
To aid in management of your sandbox, it is recommended that you make entries into your /etc/hosts file for the environment components.
```
10.10.10.42     sr.com
10.10.10.42     db
10.10.10.42     mq
```

While not entirely necessary, this will allow you to do things like
```
psql -h db -U engine
curl 'http://sr.com:8080/subrosa/v1/game'
```

### Provisioning
After installing the prerequisites and configuring gradle, run `./sr init` and wait for the deployment to finish. After everything is done you should have a working server at http://10.10.10.42:8080.

### Managing Deployments

`./sr init` initialize environment

`./sr deploy` rebuild and redeploy the API

`./sr log` tail the API logs
