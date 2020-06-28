# Testing

On this page I try to difference three kinds of testing to clear my mind of confusion.
I'm struggling how to test the EJB- and DAO-layer of my applications in an easy and fast way.
So the terms may not be well-defined, but with the notes I think it should get clear, what I mean / try to achieve. 

**Note**: Database is just the synonym for the database and several other web services, which are not part oy the system under test. 

## Unit testing

_State: confident_

**Unit tests**:
* Accessing all (package private) methods is possible
    * Clearly identifying the (not) working methods
* Using mocks to
    * define wanted return values / results of other units
    * verifying calls of other units
    * avoid `@Inject`-NPE without using constructor injection
* No deployment required, therefor fast
* No real integration with the database (no DB needed)

**Tools**:
* [JUnit Jupiter](https://junit.org/junit5/) 
* [Mockito](https://site.mockito.org/)
* [Maven Surefire Plugin](http://maven.apache.org/surefire/maven-surefire-plugin/)


## Open-box (White-box) integration testing

_State: Struggling. I'm not clear if I'm on the right track._

**Requirements**:
* Accessing all (package private) methods is possible to test every method
* Integration with the database (regardless if real instance or as a container)
* CDI to resolve
* Run from IDE and inside maven build
* Using mocks / stubs only for third-party services, not inside own application (there may be exceptions) 

**Manual configuration**:
* How to solve CDI? 
    * Constructor injection with a lot of manually setup for each class
    * Use a CDI container locally (_How to do it correctly to make it work?_)
* Tests executable from IDE and in maven build
* Integration with the database (regardless if real instance or as a container)

**Arquillian**:
* Real application server
* Extremely Slow deployment times (at least for WAS traditional)
* Accessing all (package private) methods is possible
* Integration with the database (regardless if real instance or as a container)
* Tests executable from IDE and in maven build
    _Note: Only JUnit 4 yet, because the JUnit test gets forwarded to Arquillian and run on the server, not on the local machine.
    The server then returns the results to JUnit)._
* Testing of REST-Services or running Selenium is possible, e.g. Graphene etc.

**Tools**:
* [JUnit Jupiter or vintage](https://junit.org) 
* [Arquillian](http://arquillian.org/)
* [Testcontainers](https://www.testcontainers.org/) or self-written stubs for databases and other services
* [Maven Failsafe plugin](https://maven.apache.org/surefire/maven-failsafe-plugin/)
* [DB Unit](http://dbunit.sourceforge.net/)
* [Mockito](https://site.mockito.org/)

## Closed-box (Black-box) integration / end-to-end testing

_State: I think I'm clear about the possibilities of the tools or how to make it, but have not tried it._

* Deploying the full app to a application server and testing the public interfaces (e.g. REST-Service or front end)
    * No knowledge about the implementation - just comparing the outcome with expected one
    * No real possibility to identify the cause or method which makes the test fail (e.g. which of the five DB-queries returned wrong results)
    * Application server can run in container or be a real server
* Run from IDE and inside maven build (e.g. with [Open Liberty dev mode](https://openliberty.io/blog/2019/10/22/liberty-dev-mode.html))

**Notes Containers (Docker and Testcontainers)**:
* Provide deploy environment for application, databases and other services (no need for a real instance, e.g. database) 
* When using Testcontainers for the database there is no need to reset the schema / test-data, because you can just use a fresh docker image


**Tools**:
* [Maven Failsafe plugin](https://maven.apache.org/surefire/maven-failsafe-plugin/)
* [Docker](https://www.docker.com)
* [Testcontainers](https://www.testcontainers.org/) or self-written stubs for databases and other services


## Out of scope for now

As for the moment the following topics are out of scope. 

### Access to needed infrastructure

* Is it possible to get the tools needed, e.g. an application server to run Arquillian on it.

### Notes on testing from QA view

* Testing correct configuration of stages
* Testing functionality and client requirements (may duplicate to developing - working together may reduce work)
* (In theory:) Only closed-box (black-box) testing
* Maybe verifying results in the database, in folders, ...
