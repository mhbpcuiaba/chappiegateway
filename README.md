# 🚀 ChappieGateway

A lightweight educational API Gateway built on Netty designed to explore the internal architecture of high-performance gateways.

This project is part of my personal research into high-load distributed infrastructure, 
focusing on technologies used in large-scale communication platforms such as Netty, asynchronous I/O, and gateway pipelines.

The goal is not to build a production gateway but to deeply understand the mechanics behind high-performance networking systems.

## ✨ Motivation

Modern communication platforms (e.g., unified communications, messaging, and realtime APIs) rely heavily on high-throughput gateways capable of:

- Processing hundreds of thousands of requests per second.

- Maintaining large numbers of concurrent connections.

- Routing traffic efficiently to backend services

- Applying cross-cutting concerns like authentication, rate limiting, and logging

Frameworks like Netty power many of these systems.

This project explores how such systems are structured internally.




## Learning Goals

This project focuses on understanding:

- Netty event loop architecture
- Non-blocking networking
- Gateway request pipelines
- Filter chains
- Routing
- Asynchronous upstream calls
- Backpressure handling

# Gateway Execution Pipeline

ChappieGateway processes requests through an asynchronous gateway pipeline designed around Netty’s non-blocking event loop model.

```asciidoc
                 +-------------------+
Client Request → |  Netty HTTP Server |
                 +-------------------+
                            │
                            ▼
                 +--------------------+
                 | AsyncGatewayHandler |
                 +--------------------+
                            │
                            ▼
                 +----------------------+
                 |  Async Filter Chain  |
                 +----------------------+
                   │        │        │
                   ▼        ▼        ▼
              Logging   Timeout   RouteCheck
               Filter    Filter     Filter
                            │
                            ▼
                     +-------------+
                     |   Router    |
                     +-------------+
                            │
                            ▼
                 +---------------------+
                 | AsyncUpstreamClient |
                 +---------------------+
                            │
                            ▼
                     Backend Service
```

# Architecture Overview

Simplified gateway pipeline:

```bash

Client
   │
   ▼
Netty HTTP Server
        │
        ▼
AsyncGatewayHandler
        │
        ▼
DefaultAsyncFilterChain
        │
        ├── LoggingFilter
        ├── TimeoutFilter
        ├── RouteNotFoundFilter
        │
        ▼
Router
        │
        ▼
AsyncUpstreamClient
        │
        ▼
OutboundResponse
```


**Responsibilities:**

| Component       | Responsibility                                     |
| --------------- | -------------------------------------------------- |
| Netty Server    | Accept connections and decode HTTP requests        |
| Gateway Handler | Convert Netty requests into internal request model |
| Filter Chain    | Apply gateway filters                              |
| Router          | Determine upstream destination                     |
| Upstream Client | Execute async HTTP call to backend                 |


# Current Features (v0.1)

Minimal working gateway implementation:

- Netty HTTP server
- Internal request/response model
- Filter chain
- Basic routing
- Logging filter
- Mock upstream client


# Planned Features

Future exploration areas:

- WebSocket proxy
- Rate limiting
- Redis distributed rate limiting
- Circuit breakers
- Observability (OpenTelemetry)
- Service discovery
- Backpressure strategies


# Running the Gateway

```bash
mvn clean package
java -jar chappiegateway-core/target/chappiegateway-core.jar
```

Server starts on:


```bash
http://localhost:8080
```

## Example Request
curl http://localhost:8080/hello

Response:

200 OK
Hello from upstream service




# Why Netty?

**Netty provides:**

- asynchronous networking
- event loop architecture
- zero-copy buffers
- high throughput under load

**It is used by many large scale systems including:**

- communication platforms
- RPC frameworks
- gateways
- messaging systems


# Project Status

Early research project focused on understanding Netty and gateway internals.

# Author

Marcos Pinto
Senior Backend Engineer — Distributed Systems

