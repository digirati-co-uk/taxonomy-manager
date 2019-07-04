package com.digirati.taxman.rest.server.infrastructure.media.writer;

import com.digirati.taxman.rest.server.taxonomy.autocomplete.AutocompletionResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class AutocompletionResultWriter implements MessageBodyWriter<AutocompletionResult> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return AutocompletionResult.class.isAssignableFrom(type)
                && MediaType.APPLICATION_JSON.equals(mediaType.getType());
    }

    @Override
    public void writeTo(AutocompletionResult autocompletionResult,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream)
            throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(entityStream, autocompletionResult);
    }
}
