package com.digirati.taxman.rest.server.infrastructure.media.reader;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.rdf.RdfModelFormat;
import com.digirati.taxman.common.rdf.io.RdfModelReader;
import com.digirati.taxman.rest.MediaTypes;
import com.google.common.collect.HashMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A request body deserializer for payloads that contain linked RDF data in XML (i.e. <code>
 *  application/rdf+xml</code>) format. The deserialized output will contain a full graph, if the
 * provided RDF was well formed.
 */
@Provider
@ApplicationScoped
public class TypedRdfModelMessageBodyReader implements MessageBodyReader<RdfModel> {

    private static final Logger logger = LoggerFactory.getLogger(TypedRdfModelMessageBodyReader.class);

    @Inject
    RdfModelFactory modelFactory;

    @Override
    public boolean isReadable(
            Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return RdfModel.class.isAssignableFrom(type);
    }

    @Override
    public RdfModel readFrom(
            Class<RdfModel> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream)
            throws IOException {

        var pushbackStream = new PushbackInputStream(entityStream, 1);
        int read = pushbackStream.read();
        if (read == -1) {
            throw new NoContentException("No content available in request body");
        }

        pushbackStream.unread(read);

        var attributes = HashMultimap.<String, String>create();
        httpHeaders.forEach(attributes::putAll);

        attributes.forEach((key, attr) -> {
            logger.info("header {} = {}", key, attr);
        });

        RdfModelFormat format;
        if (mediaType.isCompatible(MediaTypes.APPLICATION_RDF_XML)) {
            format = RdfModelFormat.RDFXML;
        } else if (mediaType.isCompatible(MediaTypes.APPLICATION_JSONLD_SKOS)) {
            format = RdfModelFormat.JSON_LD;
        } else {
            throw new WebApplicationException("Unsupported media type");
        }

        RdfModelReader reader = new RdfModelReader(modelFactory);
        try {
            return reader.read(type, format, pushbackStream, attributes);
        } catch (RdfModelException e) {
            throw new WebApplicationException(e);
        }
    }
}
