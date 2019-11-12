package com.digirati.taxman.common.rdf.identity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class IdResolver {

    // This does not need to be ever instanciated
    private IdResolver() {
    }

    public static Optional<UUID> resolve(String uri, Pattern pattern) {
        if (uri == null)
            return Optional.empty();

        try {
            var parsedUri = new URI(uri);
            return resolve(parsedUri, pattern);
        } catch (URISyntaxException ignored) {
            return Optional.empty();
        }
    }
    public static Optional<UUID> resolve(URI uri, Pattern pattern) {
        if (uri == null || pattern == null) {
            return Optional.empty();
        }

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

//    public static URI resolve(UUID uuid, URI uri, String template) {
//
//        return UriBuilder.fromUri(template)
//                .scheme(uri.getScheme())
//                .host(uri.getHost())
//                .port(uri.getPort())
//                .resolveTemplate("id", uuid.toString())
//                .build();
//    }
}
