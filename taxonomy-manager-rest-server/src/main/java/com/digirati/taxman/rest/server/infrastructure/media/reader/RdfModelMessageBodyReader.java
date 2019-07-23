package com.digirati.taxman.rest.server.infrastructure.media.reader;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFormat;
import com.digirati.taxman.rest.MediaTypes;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class RdfModelMessageBodyReader implements MessageBodyReader<Model> {
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Model.class.isAssignableFrom(type);
    }

    @Override
    public Model readFrom(Class<Model> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        RdfModelFormat format;
        if (mediaType.isCompatible(MediaTypes.APPLICATION_RDF_XML)) {
            format = RdfModelFormat.RDFXML;
        } else if (mediaType.isCompatible(MediaTypes.APPLICATION_JSONLD_SKOS)) {
            format = RdfModelFormat.JSON_LD;
        } else {
            throw new WebApplicationException("Unsupported media type");
        }

        Model model;
        try {
            model = ModelFactory.createDefaultModel();
            model.read(entityStream, null, format.getType());

            return model;
        } catch (JenaException ex) {
            throw new WebApplicationException("RDF error produced on deserialization", ex);
        }
    }
}
