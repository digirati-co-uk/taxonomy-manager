package com.digirati.taxman.common.rdf.io;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.rdf.RdfModelFormat;
import com.google.common.collect.Iterables;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;

import java.io.InputStream;
import java.util.List;

/**
 * A generic reader for annotated {@link RdfModel} classes, built as an easy way to provide
 * type-safe wrappers over RDF models.
 */
public class RdfModelReader {

    private final RdfModelFactory modelFactory;

    public RdfModelReader(RdfModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    /**
     * Read a list of {@link RdfModel} types from a stream of input containing RDF data in any of
     * the {@link RdfModelFormat}s.
     *
     * @throws RdfModelException if an error occurred during reading of the RDF data.
     */
    public <T extends RdfModel> List<T> readAll(
            Class<T> type, RdfModelFormat format, InputStream modelStream)
            throws RdfModelException {

        Model model;
        try {
            model = ModelFactory.createDefaultModel();
            model.read(modelStream, null, format.getType());
        } catch (JenaException ex) {
            throw new RdfModelException("RDF error produced on deserialization", ex);
        }

        return modelFactory.createListFromModel(type, model);
    }

    /**
     * Read a single typed {@link RdfModel} from the RDF graph provided in the {@code modelStream}.
     *
     * @param type        The class of the typed model to read.
     * @param format      The format the graph in {@code modelStream} is encoded in.
     * @param modelStream The input stream containing the RDF graph.
     * @param <T>         The type of the typed model.
     * @return A typed {@link RdfModel} deserialized from the input graph.
     * @throws RdfModelException if the graph is ill-formed, or an error is produced by JENA.
     */
    public <T extends RdfModel> T read(
            Class<T> type, RdfModelFormat format, InputStream modelStream)
            throws RdfModelException {
        return Iterables.getOnlyElement(readAll(type, format, modelStream));
    }
}
