# modules

chappie-core
chappie-routing
chappie-loadbalancer
chappie-observability
chappie-api
chappie-websocket




# ChappieGateway Roadmap

## Phase 1 - Single Gateway Node (Gateway Fundamentals)

[DONE] Hello endpoint
[DONE] Slow endpoint

- Timeout
- Retry
- Circuit Breaker
- Round Robin Load Balancer
- Health Checks
- Prometheus Metrics
- wrk/k6 Benchmarking

Goals:
- Netty fundamentals
- Request lifecycle
- Resiliency patterns
- Upstream selection
- Failure handling
- Latency analysis
- Gateway observability

---

## Phase 2 - Gateway Cluster (Distributed Gateway)

- 3 Gateway Nodes
- 3 Upstream Nodes
- Redis

- Distributed Rate Limiter
- Distributed Cache
- Service Discovery

- k6 Cluster Benchmarking

Goals:
- Distributed state management
- Consistency tradeoffs
- Coordination between gateway nodes
- Redis-based infrastructure patterns
- Cluster load balancing
- Horizontal scaling

---

## Phase 3 - WebSocket Gateway (RingCentral Path)

- WebSocket Support
- Redis Connection Registry
- Redis Subscription Registry
- Kafka Fanout
- Presence Tracking
- Topic/Channel Subscriptions

- 100k+ Connections Benchmark

Goals:
- Long-lived connections
- Fanout architectures
- Real-time systems
- Connection management
- Backpressure
- Gateway-to-gateway coordination

---

## Phase 4 - Scale Testing

- 10+ Gateway Nodes
- Redis Cluster
- Kafka Cluster

- 100k+ RPS
- 100k+ Concurrent WebSocket Connections

- Capacity Planning
- Cost Analysis
- Failure Testing
- Rolling Deployments

Goals:
- Production-scale validation
- Capacity planning
- Infrastructure economics
- High availability
- Large-scale distributed systems

---

Target Architecture (RingCentral Dream Job)

API Gateway Cluster
|
+-- Timeout
+-- Retry
+-- Circuit Breaker
+-- Load Balancing
+-- Service Discovery

WebSocket Gateway Cluster
|
+-- Redis Connection Registry
+-- Kafka Fanout
+-- Presence
+-- Subscriptions

Shared Infrastructure
|
+-- Redis
+-- Kafka
+-- Prometheus