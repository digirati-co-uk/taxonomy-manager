package com.digirati.taxman.rest.server.taxonomy.identity;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

abstract class AbstractIdResolver {
    private final Pattern pattern;
    private final String template;

    AbstractIdResolver(String template) {
        if (!template.contains(":id:")) {
            throw new IllegalArgumentException("No :id: variable found in template: " + template);
        }

        this.pattern = Pattern.compile(template.replace(":id:", "([^/]+)"));
        this.template = template.replace(":id:", "{id}");
    }

    public Optional<UUID> resolve(URI uri) {
        // @TODO: Where are the callers passing in null?
        if (uri.getPath() == null) {
            return Optional.empty();
        }

        var match = pattern.matcher(uri.getPath());
        if (!match.find()) {
            return Optional.empty();
        }

        var uuid = match.group(1);
        return Optional.of(UUID.fromString(uuid));
    }

    public URI resolve(UUID uuid) {
        var uri = getUriInfo().getRequestUri();

        return UriBuilder.fromUri(template)
                .scheme(uri.getScheme())
                .host(uri.getHost())
                .port(uri.getPort())
                .resolveTemplate("id", uuid.toString())
                .build();
    }

    protected abstract UriInfo getUriInfo();
}
