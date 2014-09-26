#!/bin/bash

DIR="$( cd "$( dirname "$0" )" && pwd )"
EC2_HOST="ec2-54-69-137-20.us-west-2.compute.amazonaws.com"

killz(){
	echo "Killing all docker containers:"
	sudo docker ps
        ids=$(sudo docker ps -qa)
	if [ -n "$ids" ]; then
		echo $ids | xargs sudo docker kill
		echo $ids | xargs sudo docker rm
	fi
}

stop(){
	echo "Stopping all docker containers:"
	sudo docker ps
        ids=$(sudo docker ps -qa)
	if [ -n "$ids" ]; then
		echo $ids | xargs sudo docker stop
		echo $ids | xargs sudo docker rm
	fi
}

start(){
    echo "Starting docker containers"

    POSTGRESQL=$(sudo docker run \
        -d \
        -p 5432:5432 \
        --name db \
        ${EC2_HOST}:5000/db)
    echo "Started PostgreSQL in container $POSTGRESQL"

    RABBITMQ=$(sudo docker run \
        -d \
        -p 5672:5672 \
        -p 15672:15672 \
        --name mq \
        -e RABBITMQ_PASS="admin" \
        tutum/rabbitmq)
    echo "Started RabbitMQ in container $RABBITMQ"

    TOMCAT=$(sudo docker run \
        -d \
        -p 8080:8080 \
        --name api \
        --link db:db \
        --link mq:mq \
        -e TOMCAT_PASS="admin" \
        tutum/tomcat:8.0)
    echo "Started Tomcat in container $TOMCAT"

}

update(){
    echo "Updating docker containers"
    sudo docker pull ${EC2_HOST}:5000/db
    sudo docker pull tutum/rabbitmq:latest
    sudo docker pull tutum/tomcat:8.0
}

case "$1" in
	restart)
		killz
		start
		;;
	start)
		start
		;;
	stop)
		stop
		;;
	kill)
		killz
		;;
	update)
		update
		;;
	status)
		sudo docker ps
		;;
	log)
	    sudo docker logs -f api
	    ;;
	*)
		echo $"Usage: $0 {start|stop|restart|status|kill|update}"
		RETVAL=1
esac

