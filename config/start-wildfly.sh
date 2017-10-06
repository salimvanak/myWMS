#!/bin/bash
# wait 5 seconds for the db services to start.

if [ -n "$DEBUG" ]; then
	$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 -Dpostgres.uri=$POSTGRES_URI -Dpostgres.dbname=$POSTGRES_DB -Djava.awt.headless=true -Djava.io.tmpdir=/tmp-java/ --debug $DEBUG;
else
	$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 -Dpostgres.uri=$POSTGRES_URI -Dpostgres.dbname=$POSTGRES_DB -Djava.awt.headless=true -Djava.io.tmpdir=/tmp-java/;
fi
