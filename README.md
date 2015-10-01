subrosa
=======

# Subrosa API

## Vagrant and VirtualBox Setup
This is the recommended way to setup the Subrosa API. The setup is automated with a goal of single-step provisioning and deployments.

### Prerequisites
 * [Vagrant](https://www.vagrantup.com/downloads.html)
 * [VirtualBox](https://www.virtualbox.org/wiki/Downloads)

### Hints (Optional)
To aid in management of your sandbox, it is recommended that you make entries into your /etc/hosts file for the environment components.
```
127.0.0.1	local.subrosagames.com
10.10.10.42     db
10.10.10.42     mq
10.10.10.42     sr
```

While not entirely necessary, this will allow you to do things like
```
psql -h db -U engine
curl 'http://sr:8080/subrosa/v1/game'
```

### Provisioning
After installing the prerequisites and configuring gradle, run `./sr init` on the vagrant VM and wait for the deployment to finish. After everything is done you should be running a working application at localhost:8080.

### Managing Deployments

These are run on the vagrant VM:

`./sr init` initialize environment

`./sr deploy api` rebuild and redeploy the API

