package com.digirati.taxman.rest.server.infrastructure.media.reader;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.rdf.RdfModelFormat;
import com.digirati.taxman.common.rdf.io.RdfModelReader;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A request body deserializer for payloads that contain linked RDF data in XML (i.e. <code>
 *  application/rdf+xml</code>) format. The deserialized output will contain a full graph, if the
 * provided RDF was well formed.
 */
@Provider
public class RdfModelMessageBodyReader extends AbstractMessageBodyReader<RdfModel> {

    private final RdfModelFactory modelFactory;

    public RdfModelMessageBodyReader() {
        this(new RdfModelFactory());
    }

    @Inject
    public RdfModelMessageBodyReader(RdfModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

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

        var pushbackStream = getPushbackInputStream(entityStream);
        RdfModelFormat format = getRdfModelFormat(mediaType);
        RdfModelReader reader = new RdfModelReader(modelFactory);
        try {
            return reader.read(type, format, pushbackStream);
        } catch (RdfModelException e) {
            throw new WebApplicationException(e);
        }
    }
}
