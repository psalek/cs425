This is a Spring Boot Headless Maven project. You do not need a web server to run this.

To run the project you need to do the following:

1) Go to src/main/resources and edit application.properties and change the following property
	spring.jpa.hibernate.ddl-auto = validate -> spring.jpa.hibernate.ddl-auto = create

2) While in application.properties make sure the database parameters are correct (URL, schema, username and password)

3) From the root package edu.mum.cs.projects.attendance, run the main() in a class named DatabaseLoader.java
This will create all the necessary tables in database and load them with realistic data

4) After you successfully create database tables, you need to change the following property back to normal:
	spring.jpa.hibernate.ddl-auto = create -> spring.jpa.hibernate.ddl-auto = validate
	
5) Run the reporting app from root package edu.mum.cs.projects.attendance. Just run the main() in:
AttendanceReport.java

The Excel reports are saved under:
src/main/resources/reports/

