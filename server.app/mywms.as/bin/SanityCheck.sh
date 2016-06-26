#!/bin/bash

MYWMS_HOME=..
MYWMS_LIB=$MYWMS_HOME/lib

CLASSPATH=$MYWMS_LIB/mconcurrent.jar:\
$MYWMS_LIB/itext-1.4.jar:\
$MYWMS_LIB/jbossall-client.jar:\
$MYWMS_LIB/jboss-aop-jdk50-client.jar:\
$MYWMS_LIB/jboss-aspect-jdk50-client.jar:\
$MYWMS_LIB/jboss-common-client.jar:\
$MYWMS_LIB/postgresql-8.0-311.jdbc3.jar

echo $CLASSPATH

echo java -cp $CLASSPATH org.mywms.cmdtools.SanityCheck
