#
# Copyright (c) 2001-2006 by Fraunhofer IML, Dortmund.
# All rights reserved.
#
# $Revision: 221 $ provided by $Author: lxjordan $
#

0. Preface
1. Requirements
2. Set up Ant's runtime
3. Set up configuration for HibernateTool


0. Preface
==========

If you develop an enterprise application according to the EJB 3.0 Standard 
you will found your data model on annotated entities. With the annotations 
you specify constraints and assoziations on these entities. But how do you 
set up appropriate tables in the database? The EJB 3.0 Specification does not 
expect a container to export the annotations as DDL to a database. Although 
JBoss has the ability it is not very comfortable and saftey. It would be nice
to have an ant task that does the work for us.

Notice: In the following words seperated by '>' make up a sequence of menu items 
or buttons you have to press to achieve the designated goal.

1. Requirements
===============

- Eclipse 3.2 installed
- HibernateTool Plugin for Eclipse 3.2 installed
- Eclipse is up and running.

2. Set up Ant's runtime
=======================

   Window > Preferences... > Ant > Runtime

          - Select tab Classpath and then list entry Global Entries
          - Add External JARs > browse to ECLIPSE_HOME/plugins/org.hibernate.eclipse_3.2.0.beta9a/lib
          - Add all jars from folder annotations
          - Do the same for all jars in folder hibernate and folder tools
          - You also have to add the postgresql-XXX.jdbc3.jar from your postgre installation.

          - Select tab Tasks
          - Press Add Task
          - Put in hibernatetool as the name of the new task
          - Try to find hibernate-tools.jar in the drop down list
          - Expand the directory on the left to ./org/hibernate/tool/ant
          - Select HibernateToolTask on the right and press OK

3. Set up configuration for HibernateTool
=========================================

   Take a look at hibernate.cfg.xml in this directory.
   You have to adapt the values of the following properties to your database.
   
   - hibernate.connection.url
   - hibernate.default_schema
   - hibernate.connection.password
   - hibernate.connection.username
   
   
After that you should be able to run targets of database-build.xml!
