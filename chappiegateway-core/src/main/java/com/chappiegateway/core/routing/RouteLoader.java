package com.chappiegateway.core.routing;

import com.chappiegateway.core.model.HttpMethod;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.URI;
import java.util.*;

public final class RouteLoader {

    private static final String ROUTES_FILE = "routes.yaml";

    public List<Route> load() {

        InputStream is =
                RouteLoader.class
                        .getClassLoader()
                        .getResourceAsStream(ROUTES_FILE);

        if (is == null) {
            throw new IllegalStateException("routes.yaml not found in classpath");
        }

        Yaml yaml = new Yaml();

        Map<String, Object> root =
                yaml.load(is);

        List<Map<String, Object>> routes =
                (List<Map<String, Object>>) root.get("routes");

        List<Route> result =
                new ArrayList<>();

        for (Map<String, Object> r : routes) {

            String id =
                    (String) r.get("id");

            List<String> methodsRaw =
                    (List<String>) r.get("methods");

            Set<HttpMethod> methods =
                    new HashSet<>();

            for (String m : methodsRaw) {
                methods.add(HttpMethod.valueOf(m));
            }

            String pathTemplate =
                    (String) r.get("pathTemplate");

            String upstream =
                    (String) r.get("upstreamBaseUri");

            result.add(
                    new Route(
                            id,
                            methods,
                            pathTemplate,
                            URI.create(upstream)
                    )
            );
        }

        return result;
    }
}