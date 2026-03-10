package com.chappiegateway.core.routing;

import java.util.*;

public final class PathTemplate {

    private final List<String> segments;

    public PathTemplate(String template) {
        this.segments = tokenize(template);
    }

    public Optional<Map<String, String>> match(String path) {
        List<String> pathSegments = tokenize(path);

        if (segments.size() != pathSegments.size()) {
            return Optional.empty();
        }

        Map<String, String> params = new HashMap<>();

        for (int i = 0; i < segments.size(); i++) {
            String templateSegment = segments.get(i);
            String actualSegment = pathSegments.get(i);

            if (isParam(templateSegment)) {
                String paramName = templateSegment.substring(1, templateSegment.length() - 1);
                params.put(paramName, actualSegment);
            } else if (!templateSegment.equals(actualSegment)) {
                return Optional.empty();
            }
        }

        return Optional.of(params);
    }

    private boolean isParam(String segment) {
        return segment.startsWith("{") && segment.endsWith("}");
    }

    private List<String> tokenize(String path) {
        return Arrays.stream(path.split("/"))
                .filter(s -> !s.isBlank())
                .toList();
    }
}

