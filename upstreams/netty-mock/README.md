# Netty Mock Upstream

Small standalone Java 21 + Netty HTTP upstream used to test ChappieGateway behavior.

Planned endpoints:

- GET /hello
- GET /slow?delay=2000
- GET /fail
- GET /flaky

Run:

    mvn clean compile exec:java
