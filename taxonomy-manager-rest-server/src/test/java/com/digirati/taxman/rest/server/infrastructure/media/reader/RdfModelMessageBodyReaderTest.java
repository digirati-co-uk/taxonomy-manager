package com.digirati.taxman.rest.server.infrastructure.media.reader;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
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
import java.util.stream.Collectors;

import static org.jboss.resteasy.spi.HttpResponseCodes.SC_BAD_REQUEST;
import static org.jboss.resteasy.spi.HttpResponseCodes.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RdfModelMessageBodyReaderTest {
    private static final Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
    private static final ResourceClass rootResourceClass =
            new ResourceBuilder().buildRootResource(RdfModelResource.class).buildClass();

    @BeforeAll
    public static void setup() {
        var providerFactory = dispatcher.getProviderFactory();
        providerFactory.registerProvider(RdfModelMessageBodyReader.class);

        var registry = dispatcher.getRegistry();
        registry.addResourceFactory(new POJOResourceFactory(rootResourceClass));
    }

    @Path("/")
    public static class RdfModelResource {
        @GET
        @Consumes("application/rdf+xml")
        public Response rdfXml(Model model) {
            return respondWithSubjectUris(model);
        }

        @GET
        @Consumes("application/ld+json")
        public Response jsonLd(Model model) {
            return respondWithSubjectUris(model);
        }

        private static Response respondWithSubjectUris(Model model) {
            var uris =
                    Streams.stream(model.listSubjects())
                            .map(Resource::getURI)
                            .sorted()
                            .collect(Collectors.joining(","));

            return Response.ok(uris).build();
        }
    }

    /**
     * Mock request dispatcher method for fake test requests. Returns the {@link MockHttpResponse}
     * after being handled by the {@link RdfModelMessageBodyReader} and {@link RdfModelResource}.
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
                        RdfModelMessageBodyReaderTest.class.getResourceAsStream(
                                "rdfxml--no-triples.xml"));

        assertEquals(SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void readFrom_ReturnsIri_RdfXml() throws Exception {
        var response =
                dispatch(
                        "application/rdf+xml",
                        RdfModelMessageBodyReaderTest.class.getResourceAsStream(
                                "rdfxml--valid-iri.xml"));

        assertEquals(SC_OK, response.getStatus());
        assertEquals("http://example.org/test", response.getContentAsString());
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
                        RdfModelMessageBodyReaderTest.class.getResourceAsStream(
                                "jsonld--no-triples.json"));

        assertEquals(SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void readFrom_ReturnsIri_JsonLd() throws Exception {
        var response =
                dispatch(
                        "application/ld+json",
                        RdfModelMessageBodyReaderTest.class.getResourceAsStream(
                                "jsonld--valid-iri.json"));

        assertEquals(SC_OK, response.getStatus());
        assertEquals("http://example.org/test", response.getContentAsString());
    }
}
