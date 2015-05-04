#!/bin/bash

service postgresql start

psql -U postgres -d postgres -c "alter user postgres with password 'postgres';"

createuser engine
psql -U postgres -d postgres -c "alter user engine with password 'engine';"
createdb engine -O engine
psql -d engine -f engine.sql

createuser mogilefs
psql -U postgres -d postgres -c "alter user mogilefs with password 'mogilefs';"
createdb mogilefs -O mogilefs;

service postgresql stop

