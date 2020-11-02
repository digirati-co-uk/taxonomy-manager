package com.digirati.taxman.common.rdf;

import com.google.common.collect.Multimap;
import org.apache.jena.rdf.model.Resource;

import java.util.Map;

public interface RdfModelCreationListener {
    void onCreation(RdfModel model, Resource resource, Multimap<String, String> additionalAttributes) throws RdfModelException;
}
