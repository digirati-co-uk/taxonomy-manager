package com.digirati.taxman.common.rdf.io;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFormat;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFWriter;

import java.io.OutputStream;

public class RdfModelWriter {
    public <T extends RdfModel> void write(
            T resource, RdfModelFormat format, OutputStream entityStream) throws RdfModelException {

        Resource jenaResource = resource.getResource();
        RDFWriter writer =
                RDFWriter.create()
                        .source(jenaResource.getModel())
                        .format(format.getFormat())
                        .build();

        writer.output(entityStream);
    }
}
