package com.digirati.taxman.rest.server.infrastructure.media.reader;

import com.digirati.taxman.common.rdf.RdfModelFormat;
import com.digirati.taxman.rest.MediaTypes;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public abstract class AbstractMessageBodyReader<T> implements MessageBodyReader<T> {

    protected PushbackInputStream getPushbackInputStream(InputStream entityStream) throws IOException {
        var pushbackStream = new PushbackInputStream(entityStream, 1);
        int read = pushbackStream.read();
        if (read == -1) {
            throw new NoContentException("No content available in request body");
        }
        pushbackStream.unread(read);
        return pushbackStream;
    }

    protected RdfModelFormat getRdfModelFormat(MediaType mediaType) {
        if (mediaType.isCompatible(MediaTypes.APPLICATION_RDF_XML)) {
            return RdfModelFormat.RDFXML;
        } else if (mediaType.isCompatible(MediaTypes.APPLICATION_JSONLD_SKOS)) {
            return RdfModelFormat.JSON_LD;
        } else {
            throw new WebApplicationException("Unsupported media type");
        }
    }
}
