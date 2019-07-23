package com.digirati.taxman.rest.server.infrastructure.media.writer;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFormat;
import com.digirati.taxman.common.rdf.annotation.jsonld.JsonLdFrame;
import com.digirati.taxman.common.rdf.io.RdfModelWriter;
import com.digirati.taxman.rest.MediaTypes;
import com.github.jsonldjava.core.JsonLdOptions;
import com.google.common.io.Resources;
import org.apache.jena.riot.JsonLDWriteContext;
import org.apache.jena.sparql.util.Context;
import org.jboss.resteasy.spi.util.FindAnnotation;
import org.json.JSONObject;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Provider
public class TypedRdfModelMessageBodyWriter implements MessageBodyWriter<RdfModel> {

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
            throws IOException {

        RdfModelFormat format;
        Context context = new Context();

        JsonLdFrame frame = FindAnnotation.findAnnotation(annotations, JsonLdFrame.class);
        if (frame != null && mediaType.isCompatible(MediaTypes.APPLICATION_JSONLD_SKOS)) {
            var frameUrl = TypedRdfModelMessageBodyWriter.class.getClassLoader().getResource(frame.input());
            var frameString = Resources.toString(frameUrl, StandardCharsets.UTF_8);

            JSONObject frameObject = new JSONObject(frameString);
            if (frame.injectId()) {
                frameObject.put("@id", model.getUri().toASCIIString());
            }

            var jsonLdOptions = new JsonLdOptions();
            jsonLdOptions.setCompactArrays(true);
            jsonLdOptions.setOmitGraph(true);
            jsonLdOptions.setOmitDefault(false);
            jsonLdOptions.setUseNativeTypes(true);
            jsonLdOptions.useNamespaces = true;

            var jsonLdContext = new JsonLDWriteContext();
            jsonLdContext.setFrame(frameObject.toString());
            jsonLdContext.setOptions(jsonLdOptions);

            context = jsonLdContext;
            format = RdfModelFormat.JSON_LD_FRAMED;
        } else if (mediaType.isCompatible(MediaTypes.APPLICATION_JSONLD_SKOS)) {
            format = RdfModelFormat.JSON_LD;
        } else if (mediaType.isCompatible(MediaTypes.APPLICATION_RDF_XML)) {
            format = RdfModelFormat.RDFXML;
        } else {
            throw new WebApplicationException("Unsupported media type, should have been rejected by isWriteable(..)");
        }

        RdfModelWriter writer = new RdfModelWriter();
        try {
            writer.write(model, format, entityStream, context);
        } catch (RdfModelException e) {
            throw new WebApplicationException(e);
        }
    }
}
