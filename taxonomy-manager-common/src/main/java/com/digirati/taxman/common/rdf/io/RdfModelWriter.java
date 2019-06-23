package com.digirati.taxman.common.rdf.io;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.github.jsonldjava.core.JsonLdOptions;
import java.io.OutputStream;
import org.apache.jena.atlas.json.JsonBuilder;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.JsonLDWriteContext;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.sparql.util.Context;

public class RdfModelWriter {
    public <T extends RdfModel> void write(
            T resource, RdfModelFormat format, OutputStream entityStream, Context context) throws RdfModelException {

        Resource jenaResource = resource.getResource();
        RDFWriter writer =
                RDFWriter.create()
                        .source(jenaResource.getModel())
                        .format(format.getFormat())
                        .context(context)
                        .build();

        writer.output(entityStream);
    }
}
