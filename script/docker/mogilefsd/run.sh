#!/bin/bash -ex

cat <<CONF >> /etc/mogilefs/mogilefsd.conf
db_dsn DBI:Pg:database=mogilefs;host=$DB_PORT_5432_TCP_ADDR;port=$DB_PORT_5432_TCP_PORT;
db_user $SEC_MOGILEFS_DB_USER
db_pass $SEC_MOGILEFS_DB_PASS
listen 0.0.0.0:7001
CONF

mogdbsetup --verbose --yes \
            --type=Postgres \
            --dbhost=$DB_PORT_5432_TCP_ADDR \
            --dbport=$DB_PORT_5432_TCP_PORT \
            --dbuser=$SEC_MOGILEFS_DB_USER \
            --dbpass=$SEC_MOGILEFS_DB_PASS \
            --dbname=mogilefs

cat <<CONF > /etc/mogilefs/mogilefs.conf
trackers = 127.0.0.1:7001
domain = subrosa
CONF

#mogadm host add storage-1 --ip=$MOGSTORED_PORT_7500_TCP_ADDR --status=alive
#mogadm device add storage-1 1
#mogadm domain add subrosa
#mogadm check

exec -c mogilefsd
