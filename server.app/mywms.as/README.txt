#
# Copyright (c) 2001-2007 by Fraunhofer IML, Dortmund.
# All rights reserved.
#
# $Revision: 746 $ provided by $Author: mkrane $
#

0. Preface
1. Quick Install
2. Authentication in JBoss
3. JMS Queues in JBoss
4. Using PostgreSQL

0. Preface
==========
Welcome to the new myWMS. 
Some things have changed in myWMS:
- myWMS is an open source software more than ever
- myWMS is still a framework - but its on the best way to become 
  a full featured example
  
Best regards your myWMS team.
  
1. Quick Install
================
This installation guide is dedicated to users of the JBoss 4.0.4GA.

a) Create a Datasource for myWMS.
Just copy hsqldb-ds.xml to mywms-ds.xml and change the jndi-name to:
  myWMSDS

b) Edit the file 'login-config.xml' in the 'conf' directory of your JBoss
and add the application-policy mentioned in chapter 2. 
Restart your Applicaion Server to read the contents of the new 
'login-config.xml'.

c) create a Queue myWMS2mfcControll
   create a Queue mfcControll2myWMS

d) Deploy the 'myWMS.ear' to the 'deploy' directory of your JBoss.

Thats it. Have fun.
 

2. Authentication in JBoss
==========================
myWMS uses JAAS to authenticate users.
The permissions of the users are handled by the myWMS system on its own.
Roles and Users can be manipulated, using myWMS mechanisms.

The bad news is: It is not working from the scratch.

The good news is: Nearly anything needed for this authentication is available, 
if you are using JBoss.
Please proceed using the following HOWTO.

This is a HOWTO for the JBoss 4.0.4GA:
Change to the conf directory of your executed JBoss server.
For example: 
  C:\...\jboss-4.0.4.GA\server\default\conf

Edit the file 
  login-config.xml
  
Add the following application-policy:
    <application-policy name = "myWMS">
       <authentication>
          <login-module 
             code = "org.jboss.security.auth.spi.DatabaseServerLoginModule"
             flag = "required">
             <module-option 
               name = "unauthenticatedIdentity">guest</module-option>
             <module-option 
               name = "dsJndiName">java:/myWMSDS</module-option>
             <module-option name = "principalsQuery">SELECT u."password" AS "PASSWD" FROM mywms_user AS u WHERE u.name=?</module-option>
             <module-option name = "rolesQuery">SELECT r.name AS "ROLEID", 'Roles' AS "Roles" FROM mywms_user AS u, mywms_role AS r, mywms_user_mywms_role AS ur WHERE u.id=ur.mywms_user_id AND r.id=ur.roles_id AND u.name=?</module-option>
          </login-module>
       </authentication>
    </application-policy>

Restart the JBoss server.    

3. JMS Queues in JBoss
======================

Create the required queues in 
  ...\jboss-4.0.5.GA\server\default\deploy\jms\jbossmq-destinations-service.xml
  
Add the following tags to the <server> section:

  <mbean code="org.jboss.mq.server.jmx.Queue"
	 name="jboss.mq.destination:service=Queue,name=myWMS2mfcControll">
    <depends optional-attribute-name="DestinationManager">jboss.mq:service=DestinationManager</depends>
  </mbean>
  <mbean code="org.jboss.mq.server.jmx.Queue"
	 name="jboss.mq.destination:service=Queue,name=mfcControll2myWMS">
    <depends optional-attribute-name="DestinationManager">jboss.mq:service=DestinationManager</depends>
  </mbean>
  
If you whant to connect to the queues beside the JBoss server (from a standanlone client), search the file java.policy.
Usually you find this file in 
  ...\jre1.5.x_y\lib\security\java.policy
  
Please adde the following lines to your grant section:

grant {
   ...
   permission java.net.SocketPermission "127.0.0.1:1099", "connect, resolve";
   permission java.net.SocketPermission "localhost:1099", "connect, resolve";
   permission java.net.SocketPermission "<your ip>:1099", "connect, resolve";
   permission java.net.SocketPermission "<your hostname>:1099", "connect, resolve";
}


(Please replace <your ip> and <your hostname> with the appropriate data.)

4. Using PostgreSQL
===================
First: using PostgreSQL is not bad at all. Try it!

PostgreSQL Setup
----------------
Setup an empty PostgreSQL database.
I suggest to create a localized encoded database, if you plan to use other 
than plain ASCII characters in your data. For example: 
'latin1' is perfect for the German use case.
Please make sure, that the database accepts connections on TCP/IP sockets
from your JBoss host.

There is no database creation script included at this development stage of the 
myWMS. You can let JBoss and its hibernate layer create a new table setup.
In the META-INF folder inside the myWMS.par inside the myWMS.ear there is a 
persistence.xml (...\myWMS.ear\myWMS.par\META-INF\persistence.xml).
Please enable the following entry:
  <property name="hibernate.hbm2ddl.auto" 
             value="update" />

Then pack the myWMS.ear again and deploy it.

This will create the new tables during deploy and will not drop them during 
undeploy. After the first start of the application you can disable this 
statement again safely.

JBoss Setup
-----------
Please make sure that no other datasource exists.
That means: no other xml file in the deploy directory of your JBoss defines the 
'myWMSDS' as 'jndi-name' for a 'local-tx-datasource'.

Please create a new file 'mywms-ds.xml' (this is a clever filename, but there 
is no need to give the file this name).
Add the following contents to the new file:
	<datasources>
	  <local-tx-datasource>
	    <jndi-name>myWMSDS</jndi-name>
	    <connection-url>jdbc:postgresql://localhost:5432/mywms</connection-url>
	    <driver-class>org.postgresql.Driver</driver-class>
	    <user-name>myPassUserName</user-name>
	    <password>myPassword</password>
	      <!-- corresponding type-mapping in the standardjbosscmp-jdbc.xml (optional) -->
	      <metadata>
	         <!-- 
	         <type-mapping>PostgreSQL 7.2</type-mapping>
	         <type-mapping>PostgreSQL 8.1</type-mapping>
	          -->
	         <type-mapping>PostgreSQL 8.0</type-mapping>
	      </metadata>
	  </local-tx-datasource>
	
	</datasources>

Please change the 'connection-url', the 'user-name' and the 'password' to what
your settings should be.
