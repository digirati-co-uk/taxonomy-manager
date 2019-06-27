package com.digirati.taxman.common.rdf.io;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFormat;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.sparql.util.Context;

import java.io.OutputStream;

/**
 * A serializer for {@link RdfModel}s.
 */
public class RdfModelWriter {
    /**
     * Serialize an {@link RdfModel} to a given {@code format} and write it to the {@code entityStream}.
     *
     * @param resource     The typed model to be serialized.
     * @param format       The format to serialize data in.
     * @param entityStream The {@code OutputStream} that data is written to.
     * @param context      The serialization context.
     * @param <T>          The type of model being serialized.
     * @throws RdfModelException if the graph could not produce valid output.
     */
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
