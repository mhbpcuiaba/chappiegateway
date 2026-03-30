### Correct ordered TODO list (concise)

* create simple backend service (ex: `GET /hello` on port 9000)
* create `routes.yaml`
* create `RouteDefinition`
* create `RouteTable`
* create `RouteLoader`

* load routes at startup
* implement `RoutingFilter`
* store `RouteMatch` using existing `RoutingAttributes`
* connect `RoutingFilter` → `AsyncUpstreamTerminalHandler`
* implement real `AsyncUpstreamClient` (HTTP call)

* test end-to-end: client → gateway → backend → client
* create `RouteConfigWatcher` (WatchService)
* reload `RouteTable` on file change using `AtomicReference`
* verify hot reload works (like NGINX reload)
