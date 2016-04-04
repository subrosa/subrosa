#!/bin/bash

psql -v ON_ERROR_STOP=1 --username postgres <<-EOSQL
    CREATE USER engine;
    ALTER USER engine WITH PASSWORD 'engine';
    CREATE DATABASE engine;
    GRANT ALL PRIVILEGES ON DATABASE engine TO engine;
EOSQL
psql -U postgres -d engine -f /engine.sql

