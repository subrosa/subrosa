#!/bin/bash -ex

cat <<CONF > /etc/mogilefs/mogilefsd.conf
db_dsn DBI:Pg:database=$MOGILEFS_DB_NAME;host=$DB_PORT_5432_TCP_ADDR;port=$DB_PORT_5432_TCP_PORT;
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
            --dbname=$MOGILEFS_DB_NAME

cat <<PGPASS > /home/mogilefsd/.pgpass
$DB_PORT_5432_TCP_ADDR:$DB_PORT_5432_TCP_PORT:$MOGILEFS_DB_NAME:$SEC_MOGILEFS_DB_USER:$SEC_MOGILEFS_DB_PASS
PGPASS
chmod 600 /home/mogilefsd/.pgpass

echo "Creating mogilefs host entry"
hostSql=$(cat <<SQL
    update host
        set status = 'alive', hostip = '${MOGSTORED_PORT_7500_TCP_ADDR}', http_port = 7500, hostname = 'storage-1'
        where hostid = 1;
    insert into host (hostid, status, hostip, http_port, hostname)
        select 1, 'alive', '${MOGSTORED_PORT_7500_TCP_ADDR}', 7500, 'storage-1'
        where not exists (select 1 from host where hostid = 1);
SQL
)
echo ${hostSql} | psql -h $DB_PORT_5432_TCP_ADDR -p $DB_PORT_5432_TCP_PORT -U $SEC_MOGILEFS_DB_USER -d $MOGILEFS_DB_NAME

echo "Creating mogilefs device entry"
devSql=$(cat <<SQL
    update device
        set hostid = 1, status = 'alive'
        where devid = 1;
    insert into device (devid, hostid, status)
        select 1, 1, 'alive'
        where not exists (select 1 from device where devid = 1);
SQL
)
echo ${devSql} | psql -h $DB_PORT_5432_TCP_ADDR -p $DB_PORT_5432_TCP_PORT -U $SEC_MOGILEFS_DB_USER -d $MOGILEFS_DB_NAME

dmid=0
for env in 'sr-prod' 'sr-dev'; do
    echo "Creating mogilefs domain entry '${env}'"
    dmid=$((dmid+1))
    dmSql=$(cat <<SQL
        update domain
            set namespace = '${env}'
            where dmid = ${dmid};
        insert into domain (dmid, namespace)
            select ${dmid}, '${env}'
            where not exists (select 1 from domain where dmid = ${dmid});
SQL
    )
    echo ${dmSql} | psql -h $DB_PORT_5432_TCP_ADDR -p $DB_PORT_5432_TCP_PORT -U $SEC_MOGILEFS_DB_USER -d $MOGILEFS_DB_NAME
done

exec -c mogilefsd

