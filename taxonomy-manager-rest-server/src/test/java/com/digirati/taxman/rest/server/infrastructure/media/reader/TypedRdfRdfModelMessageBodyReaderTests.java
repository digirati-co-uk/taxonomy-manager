package com.digirati.taxman.rest.server.infrastructure.media.reader;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelContext;
import com.digirati.taxman.common.rdf.annotation.RdfConstructor;
import com.digirati.taxman.common.rdf.annotation.RdfType;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.InputStream;

import static org.jboss.resteasy.spi.HttpResponseCodes.SC_BAD_REQUEST;
import static org.jboss.resteasy.spi.HttpResponseCodes.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TypedRdfRdfModelMessageBodyReaderTests {
    private static final Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
    private static final ResourceClass rootResourceClass =
            new ResourceBuilder().buildRootResource(RdfModelResource.class).buildClass();

    @BeforeAll
    public static void setup() {
        var providerFactory = dispatcher.getProviderFactory();
        providerFactory.registerProvider(TypedRdfModelMessageBodyReader.class);

        var registry = dispatcher.getRegistry();
        registry.addResourceFactory(new POJOResourceFactory(rootResourceClass));
    }

    /**
     * Mock request dispatcher method for fake test requests. Returns the {@link MockHttpResponse}
     * after being handled by the {@link TypedRdfModelMessageBodyReader} and {@link RdfModelResource}.
     */
    MockHttpResponse dispatch(String contentType, InputStream content) throws Exception {
        var request = MockHttpRequest.get("/").contentType(contentType).content(content);
        var response = new MockHttpResponse();

        dispatcher.invoke(request, response);

        return response;
    }

    @Test
    public void readFrom_ThrowsNoContentWhenEmpty_RdfXml() throws Exception {
        var response = dispatch("application/rdf+xml", InputStream.nullInputStream());

        assertEquals(SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void readFrom_ThrowsNoContentWhenNoTriples_RdfXml() throws Exception {
        var response =
                dispatch(
                        "application/rdf+xml",
                        TypedRdfRdfModelMessageBodyReaderTests.class.getResourceAsStream(
                                "rdfxml--no-triples.xml"));

        assertEquals(SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void readFrom_ReturnsIri_RdfXml() throws Exception {
        var response =
                dispatch(
                        "application/rdf+xml",
                        TypedRdfRdfModelMessageBodyReaderTests.class.getResourceAsStream(
                                "rdfxml--valid-iri.xml"));

        assertEquals("test-id", response.getContentAsString());
        assertEquals(SC_OK, response.getStatus());
    }

    @Test
    public void readFrom_ThrowsNoContentWhenEmpty_JsonLd() throws Exception {
        var response = dispatch("application/ld+json", InputStream.nullInputStream());

        assertEquals(SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void readFrom_ThrowsNoContentWhenNoTriples_JsonLd() throws Exception {
        var response =
                dispatch(
                        "application/ld+json",
                        TypedRdfRdfModelMessageBodyReaderTests.class.getResourceAsStream(
                                "jsonld--no-triples.json"));

        assertEquals(SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void readFrom_DeserializesJsonLd() throws Exception {
        var response =
                dispatch(
                        "application/ld+json",
                        TypedRdfRdfModelMessageBodyReaderTests.class.getResourceAsStream(
                                "jsonld--valid-iri.json"));

        assertEquals("test-id", response.getContentAsString());
        assertEquals(SC_OK, response.getStatus());
    }

    @Path("/")
    public static class RdfModelResource {
        @RdfType("http://www.w3.org/2004/02/skos/core#Concept")
        public static class RdfModelTest implements RdfModel {
            private final RdfModelContext context;

            @RdfConstructor
            public RdfModelTest(RdfModelContext context) {
                this.context = context;
            }

            @Override
            public RdfModelContext getContext() {
                return context;
            }

            String id() {
                return getResource().getProperty(DCTerms.identifier).getLiteral().getString();
            }
        }

        @GET
        @Consumes("application/rdf+xml")
        public Response rdfXml(RdfModelTest model) {
            return respondWithSubjectUris(model);
        }

        @GET
        @Consumes("application/ld+json")
        public Response jsonLd(RdfModelTest model) {
            return respondWithSubjectUris(model);
        }

        private static Response respondWithSubjectUris(RdfModelTest model) {
            return Response.ok(model.id()).build();
        }
    }
}
