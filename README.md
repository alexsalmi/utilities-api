# Utilities API

REST API built with [Java Spring Boot](http://projects.spring.io/spring-boot/) to be used by my various side projects.

## Technologies Used
- Code - [Java](https://www.oracle.com/java/)
- Framework - [Spring Boot](https://spring.io/projects/spring-boot)
- Unit Testing - [JUnit](https://junit.org/junit5/)
- Database - [MySQL](https://www.mysql.com/) (Hosted in [AWS RDS](https://aws.amazon.com/rds/))
- Deployment - [AWS Elastic Beanstalk](https://aws.amazon.com/elasticbeanstalk/)

## APIs
- /contact/*

  - Contains endpoints to send contact requests to my personal email address using SendGrid's APIs. Used by my portfolio website for users to send contact requests to me. Also includes endpoints to fetch past requests from my MySQL database.

- /apikey/*

  - Contains endpoints to generate/validate API keys used by some of my other APIs. The API keys are hashed and stored in my MySQL database.

## Background
As I've been working on my side projects, I realized I needed a common REST API that I could utilize for various back end functionality, including communicating with my database, as well as third party services. I also wanted to gain more experience with Java, AWS, and MySQL, and this project was the perfect opportunity to do so.
#

# Spring Boot Docs
## Requirements

For building and running the application you need:

- [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Maven 3](https://maven.apache.org)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `de.codecentric.springbootsample.Application` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

## Copyright

Released under the Apache License 2.0. See the [LICENSE](https://github.com/codecentric/springboot-sample-app/blob/master/LICENSE) file.