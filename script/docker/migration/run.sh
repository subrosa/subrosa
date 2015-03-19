#!/bin/bash -ex

cat <<EOF > conf/flyway.properties
flyway.url=jdbc:postgresql://${DB_PORT_5432_TCP_ADDR}:${DB_PORT_5432_TCP_PORT}/engine
flyway.user=${SEC_ENGINE_DB_USER}
flyway.password=${SEC_ENGINE_DB_PASS}
flyway.locations=filesystem:sql
flyway.sqlMigrationPrefix=
flyway.sqlMigrationSeparator=_
flyway.outOfOrder=true
flyway.initOnMigrate=true
EOF

cat conf/flyway.properties

ls sql/

./flyway $@
