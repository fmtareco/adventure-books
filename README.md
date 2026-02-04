# Adventure Books

## Initial Configuration
 - Java 21 (LTS, spring initializer default)
 - Maven project
 - Initial dependencies:
    - Spring Web 			- to support the Web REST API and embeded Apache Tomcat 
	- Spring Data Jpa		- to handle the persistence using Hibernate
	- PostgreSQL Driver		- to include PostgreSQL JDBC Driver
	- Spring Boot Actuator	- to monitor application status, namelly health
	- Lombok				- to reduce boilerplate code
	
## Project Structure
 ├── config
     - environment & other configurations
 ├── controller
	 - REST API controller's 
 ├── model
	 - domain entities & enums
 ├── repository
	 - Jpa Repository
 ├── service
	 - application/gane logic & rules
 ├── loader
	 - initial load of Book samples
 └── exception
	 - validation & other exceptions


	 

