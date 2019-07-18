package com.digirati.taxman.rest.server.infrastructure.media.reader;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.rdf.RdfModelFormat;
import com.digirati.taxman.common.rdf.io.RdfModelReader;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.common.taxonomy.ConceptSchemeImportModel;
import com.digirati.taxman.common.taxonomy.ConceptSchemeModel;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@Provider
public class ConceptSchemeImportReader extends AbstractMessageBodyReader<ConceptSchemeImportModel> {

    private final RdfModelFactory modelFactory;

    public ConceptSchemeImportReader() {
        this(new RdfModelFactory());
    }

    @Inject
    public ConceptSchemeImportReader(RdfModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return ConceptSchemeImportModel.class.isAssignableFrom(type);
    }

    @Override
    public ConceptSchemeImportModel readFrom(Class<ConceptSchemeImportModel> type,
                                             Type genericType,
                                             Annotation[] annotations,
                                             MediaType mediaType,
                                             MultivaluedMap<String, String> httpHeaders,
                                             InputStream entityStream) throws IOException, WebApplicationException {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            entityStream.transferTo(outputStream);
            InputStream schemeStream = getPushbackInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
            InputStream conceptStream = getPushbackInputStream(new ByteArrayInputStream(outputStream.toByteArray()));

            RdfModelFormat format = getRdfModelFormat(mediaType);
            RdfModelReader reader = new RdfModelReader(modelFactory);

            Collection<ConceptModel> concepts = readConcepts(conceptStream, reader, format);
            ConceptSchemeModel conceptScheme = readConceptScheme(schemeStream, reader, format);

            return new ConceptSchemeImportModel(conceptScheme, concepts);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private ConceptSchemeModel readConceptScheme(InputStream pushbackStream,
                                                 RdfModelReader reader,
                                                 RdfModelFormat format) {

        try {
            return reader.read(ConceptSchemeModel.class, format, pushbackStream);
        } catch (RdfModelException e) {
            throw new WebApplicationException(e);
        }
    }

    private List<ConceptModel> readConcepts(InputStream pushbackStream,
                                            RdfModelReader reader,
                                            RdfModelFormat format) {
        try {
            return reader.readAll(ConceptModel.class, format, pushbackStream);
        } catch (RdfModelException e) {
            throw new WebApplicationException(e);
        }
    }
}
