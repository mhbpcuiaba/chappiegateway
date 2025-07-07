# 🚀 ChappieGateway

**ChappieGateway** is a fast, modern, and educational API Gateway built on top of [Netty](https://netty.io/), inspired by professional frameworks like Armeria and Spring Boot.  
It’s designed to be a hands-on, high-quality open-source project where contributors can learn and build something meaningful — together.

---

## ✨ Purpose

✅ To provide a **lightweight, high-performance gateway** for HTTP traffic.  
✅ To serve as an **educational example** of best practices in Java, Netty, CI/CD, and open-source contribution.  
✅ To showcase how to design and grow an open-source project professionally.  
✅ To bridge the gap between raw Netty and full-stack frameworks like Spring Boot or Armeria.

---

## 📈 Vision & Future Roadmap

While the current MVP demonstrates a clean startup, configuration, and basic routing response, the long-term vision includes:

🌟 Pluggable routing (path → handlers)  
🌟 Pre- and post-request filters (like interceptors)  
🌟 YAML/JSON-driven configuration & hot reload  
🌟 Metrics & health-check endpoints  
🌟 Rate-limiting, authentication hooks  
🌟 gRPC & WebSocket support  
🌟 Plugins ecosystem  
🌟 Production-ready benchmarks  
🌟 Polished developer experience (banner, CLI, docs)

We aim to make **ChappieGateway** not only a learning playground but also a solid foundation for custom gateway solutions.

---

## 🚧 Current Status: MVP

✅ Clean Netty-based HTTP server  
✅ YAML-based configuration  
✅ Asynchronous, declarative startup (Armeria-inspired)  
✅ Graceful shutdown (with JVM hook)  
✅ Professional structure & CI-ready  
✅ Unit & integration tests  
✅ Cool startup banner

---

## 🛠️ Building & Running

### Prerequisites
- JDK 21+
- Maven 3.9+

### Build
```bash
mvn clean package
