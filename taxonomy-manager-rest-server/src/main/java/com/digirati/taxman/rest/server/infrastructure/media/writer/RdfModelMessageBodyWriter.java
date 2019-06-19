package com.digirati.taxman.rest.server.infrastructure.media.writer;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFormat;
import com.digirati.taxman.common.rdf.io.RdfModelWriter;
import com.digirati.taxman.rest.MediaTypes;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class RdfModelMessageBodyWriter implements MessageBodyWriter<RdfModel> {

    @Override
    public boolean isWriteable(
            Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        return RdfModel.class.isAssignableFrom(type)
                && (mediaType.isCompatible(MediaTypes.APPLICATION_RDF_XML)
                        || mediaType.isCompatible(MediaTypes.APPLICATION_JSONLD_SKOS));
    }

    @Override
    public void writeTo(
            RdfModel model,
            Class<?> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream)
            throws IOException, WebApplicationException {

        RdfModelFormat format;
        if (mediaType.isCompatible(MediaTypes.APPLICATION_RDF_XML)) {
            format = RdfModelFormat.RDFXML;
        } else if (mediaType.isCompatible(MediaTypes.APPLICATION_JSONLD_SKOS)) {
            format = RdfModelFormat.JSON_LD;
        } else {
            throw new WebApplicationException("Unsupported media type, should have been rejected by isWriteable(..)");
        }

        RdfModelWriter writer = new RdfModelWriter();
        try {
            writer.write(model, format, entityStream);
        } catch (RdfModelException e) {
            throw new WebApplicationException(e);
        }
    }
}
