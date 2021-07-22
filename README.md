# Coding Exercise

Imagine we are making a private messaging service for our new company Perry’s Summer Vacation Goods and Services. We need you to design and create a scalable API to be able to handle the many messages this company is going to handle.

## Develop an application meeting the following requirements:
* The application must be able to create and get users.
* We do not expect you do handle any kind of authentication for users.
* The application must allow users to send a message to one other user.
* No need to consider group chats.
* The application must allow editing and deleting messages.
* The application must be able to get all the messages sent between two users.
* The application must allow a user to "like" a message.
* The application must be able to get a list of other users that have sent or received messages to/from a specified user.

## Technical requirements:
* The application should be a REST API (No need for any kind of UI)
* Use what language you are comfortable with, but Typescript, Javascript, Java preferred.
* The source code must be shared in a public repository (Github, Bitbucket, etc).
* The application should be ready to run in the cloud or in a container (You can use any technology available in AWS).
* The application data must be persisted in a database of some type.


## Other notes:
* We do not expect that you spend more than 8 hours on this challenge, so some rough edges are acceptable.
* In terms of testing, implement only what unit/integration testing you find necessary to build it. We will not judge for lack of test coverage.
* Take into consideration how you might scale this application for a large amount of load. No need to implement any kind of stress/load test.
* We do not expect you do handle any kind of authentication for users.
* Treat this as a proof of concept, so documentation is not important.
* Have fun with it!

# How to execute:
* Clone project
```sh
$ cd messenger
$ mvn clean install
OR
$ ./mvnw clean install
$ docker-compose up -d
```
When docker container is up, API can be accessed under http://0.0.0.0:8084/perry/messenger
A Postman collection is available.
