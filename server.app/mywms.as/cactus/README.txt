

Cactus tests
=============

Cactus is a framework for executing JUnit tests within the application server.

- based on servlets 
- deployed as a web application within an .ear

There are ant tasks in the build skript, that will package and deploy 
a cactus enabled .ear.

The reason for using cactus is that mywms basic services are local only. 
A web application that lives within the enterprise application is allowed 
to access session beans through local interfaces. So is the cactus web app.

The advantage is that cactus provides you also a remote test class. So you can 
run a cactus test within eclipse as you usally would do with simple JUnit tests. 

All you have to provide is a cactus.properties file with property

cactus.contextURL=http://your-jboss-server-address:your-jboss-server-port/cactustest

in the classpath when running a cactustest.


Test data
==========

There are SQL Skripts in folder ./sql to setup test data for a PostgreSQL DB.


Setup Eclipse
==============

- look for folder cactus within your mywms project
- add all libraries in cactus/WEB-INF/lib to the build path
- select folder cactus/src
- right klick > Build Path > Use as Source Folder
- edit cactus.properties so that they conform with your jboss installation

Now you can create a new cactus test case in cactus/src.

- all tests have to extend org.apache.cactus.ServletTestCase
- the rest is like creating a JUnit test. 

		NOTE: only JUnit version 3.x is supported
		
- use the provided Cactus ant tasks to deploy a mywms.ear containing your test.
- right klick on your test > Run as > JUnit test

That's it.
