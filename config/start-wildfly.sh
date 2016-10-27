#!/bin/bash
# wait 5 seconds for the db services to start.

$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 -Dpostgres.uri=$POSTGRES_URI -Dpostgres.dbname=$POSTGRES_DB
