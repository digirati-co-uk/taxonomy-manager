package com.digirati.taxman.rest.taxonomy;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import java.io.File;

public class ConceptSchemeImportData {

    @FormParam("data")
    @PartType("application/octet-stream")
    private File dataFile;

    public File getDataFile() {
        return dataFile;
    }
}
