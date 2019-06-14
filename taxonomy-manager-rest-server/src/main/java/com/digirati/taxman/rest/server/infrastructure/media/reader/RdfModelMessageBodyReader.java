package com.digirati.taxman.rest.server.infrastructure.media.reader;

import com.digirati.taxman.rest.server.infrastructure.media.MediaTypes;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A request body deserializer for payloads that contain linked RDF data in XML (i.e. <code>
 * application/rdf+xml</code>) format. The deserialized output will contain a full graph, if the
 * provided RDF was well formed.
 */
@Provider
public class RdfModelMessageBodyReader implements MessageBodyReader<Model> {

    @Override
    public boolean isReadable(
            Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        if (!type.isAssignableFrom(Model.class)) {
            return false;
        }

        return mediaType.isCompatible(MediaTypes.APPLICATION_LD_JSON_WITH_SKOS)
                || mediaType.isCompatible(MediaTypes.APPLICATION_RDF_XML);
    }

    @Override
    public Model readFrom(
            Class<Model> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream)
            throws IOException, WebApplicationException {

        String readerStrategy;
        if (mediaType.isCompatible(MediaTypes.APPLICATION_RDF_XML)) {
            readerStrategy = "RDF/XML";
        } else if (mediaType.isCompatible(MediaTypes.APPLICATION_LD_JSON_WITH_SKOS)) {
            readerStrategy = "JSON-LD";
        } else {
            throw new WebApplicationException();
        }

        Model model = ModelFactory.createDefaultModel();
        model.read(entityStream, null, readerStrategy);

        if (model.isEmpty()) {
            throw new NoContentException("No RDF data found in request body");
        }

        return model;
    }
}
