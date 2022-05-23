package com.digirati.taxman.rest.server.infrastructure.config;

import com.digirati.taxman.common.rdf.PersistentProjectScopedModel;
import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.rest.server.infrastructure.web.WebContextHolder;
import com.digirati.taxman.rest.server.taxonomy.ConceptModelRepository;
import com.digirati.taxman.rest.server.taxonomy.identity.AbstractIdResolver;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.apache.jena.rdf.model.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@ApplicationScoped
public class RdfConfig {

    private final ThreadLocal<Boolean> isInRecursiveCall =
            ThreadLocal.withInitial(() -> false);

    @ApplicationScoped
    public static class InlineConceptIdResolver extends AbstractIdResolver {

        public InlineConceptIdResolver() {
            super("/v0.1/concept/:id:");
        }

        @Inject
        WebContextHolder contextHolder;

        @Override
        public UriInfo getUriInfo() {
            return contextHolder.uriInfo();
        }
    }

    @Inject
    InlineConceptIdResolver conceptIdResolver;

    @Inject
    ConceptModelRepository concepts;

    private static final Model m = ModelFactory.createDefaultModel();

    /**
     * The namespace of the SKOS vocabulary as a string
     */
    public static final String uri = "http://crugroup.com/commodities#";
    public static final Property inCommodityGroup = m.createProperty(uri + "inCommodityGroup");
    public static final Property isCommodityGroup = m.createProperty(uri + "isCommodityGroup");
    public static final Property inTopicGroup = m.createProperty(uri + "inTopicGroup");
    public static final Property isTopicGroup = m.createProperty(uri + "isTopicGroup");
    public static final Property inRegionGroup = m.createProperty(uri + "inRegionGroup");
    public static final Property isRegionGroup = m.createProperty(uri + "isRegionGroup");

    private static final Map<String, String> CONCEPT_TO_REGION_GROUP = Map.ofEntries(
            Map.entry("9a4326b2-6613-44bb-ba53-926beae2bb4a", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("81ad65da-8f51-4fc4-aff0-d239035a759e", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("bf176e22-d1dc-4f6a-a013-ca9e0be1401f", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("3f20063f-500f-4680-84fd-e3f1c8d818d6", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("bc8e6db8-8f98-4dac-bd81-e323c4ff1789", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("7e860b06-236e-4a69-a2c1-7cbfc9e25c88", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("8f817330-d9a1-48e1-afef-6a62144260db", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("82613fe6-04e3-4364-885c-1d679a6aecce", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("8713a060-3679-4edf-b84d-77d306f5b07a", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("13af6d00-616c-417d-aca1-8c994b2dd548", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("6dee9071-cc5c-471d-ae60-e8bf820ffdc1", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("e6573f5f-2b1f-45f1-9cf2-978be165298d", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("b30768cf-703c-460e-bc02-883665905044", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("d330f9e1-9789-4a50-8b1a-343e9d49c9ab", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("66fb80cc-f6fd-4e0e-8fc0-15db382fdce6", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("ddf42c97-3586-4cb2-82a6-07c4f89d8170", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("10722a9a-7ffd-432f-bde0-84dcc7d42ba5", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("3ca8c618-7f9c-4d7b-94b9-349a89398ea9", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("4433e5c7-1e99-4103-ba11-066d5dc8866b", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("b30e4942-8403-46c8-a9fb-1cb7b4001150", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("4332016a-38b7-4bda-a913-d87ed0a98012", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("7a0b5be3-9a7d-4d14-8dd6-784cdd84bf47", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("316ab5b2-767c-41e8-a1ea-edfd8dd103c1", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("f6e04371-aea7-49a3-9489-6296352ba053", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("5eb863d8-bf27-4983-bcb6-628c7cb206f2", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("4cfd2a66-6ed9-483f-82e2-96d9bed8d342", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("41cb5490-569f-40ca-af8d-c17a4e72c91d", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("bdc3c63c-d1d6-4b18-b223-408ac9db624f", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("9bf69406-d321-47ad-8c19-f8d62f474f09", "5b804657-b531-4aac-928b-da40bdd1bc00"),
            Map.entry("431f6c9c-1097-4bbc-bbf6-ac02d45888ea", "f96ca93e-209e-4b22-937d-f40bd1647c90"),
            Map.entry("60a1f9cc-78be-44b8-8a19-46056ec4cf65", "f96ca93e-209e-4b22-937d-f40bd1647c90"),
            Map.entry("01e542c4-ad8f-47ee-b274-45a0e7bb269e", "f96ca93e-209e-4b22-937d-f40bd1647c90"),
            Map.entry("353f836d-1fd3-45f4-b60b-12d7e2652515", "f96ca93e-209e-4b22-937d-f40bd1647c90"),
            Map.entry("e6bd6264-0a6d-4099-bfd8-b08938481329", "f96ca93e-209e-4b22-937d-f40bd1647c90"),
            Map.entry("7fb764ba-8fe2-4ff9-b079-4ac1f78527ed", "f96ca93e-209e-4b22-937d-f40bd1647c90"),
            Map.entry("07e15900-b64d-4d61-a92e-24df4634c04c", "f96ca93e-209e-4b22-937d-f40bd1647c90"),
            Map.entry("1245ca55-ac64-4ac9-88a8-926140ef9b6f", "f96ca93e-209e-4b22-937d-f40bd1647c90"),
            Map.entry("da508947-316d-49c8-af67-28f06ef87937", "f96ca93e-209e-4b22-937d-f40bd1647c90"),
            Map.entry("7674d503-8c0b-445a-9003-274db3e913ae", "f96ca93e-209e-4b22-937d-f40bd1647c90"),
            Map.entry("1c68f75d-c9c7-490d-b807-2b81d172384c", "f96ca93e-209e-4b22-937d-f40bd1647c90"),
            Map.entry("5fb7e7d4-6c44-4fbb-8546-fb8b912a1136", "f96ca93e-209e-4b22-937d-f40bd1647c90"),
            Map.entry("54ec04de-e55a-49a7-9762-c7b0e348abcd", "f96ca93e-209e-4b22-937d-f40bd1647c90"),
            Map.entry("ca80d8fb-8e19-4c70-8c6a-211d0a18f237", "f96ca93e-209e-4b22-937d-f40bd1647c90"),
            Map.entry("7e7c937a-2a38-4770-bcb8-1755fd14d8cf", "f96ca93e-209e-4b22-937d-f40bd1647c90"),
            Map.entry("d35bfa87-ae7e-4be4-9a4b-71a020998361", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("57817306-2474-46bc-a49b-d8d22b78d1d4", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("50da4554-46ad-4042-96de-c06d0b14e1fd", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("18077f46-dd90-4154-8aae-cc835185795f", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("fdee04f6-6c15-4d5c-b917-1ec972cddf25", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("9059df25-352a-40de-9af7-84a08e308f4f", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("715915cf-0ba1-4bc8-8782-838ef7516ae5", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("eddafcc0-849a-4305-bcec-1fac7395963c", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("6bda1372-6b6e-4ad3-8379-0e40795e0231", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("17593f93-676e-4f0c-8e85-f968fb95d688", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("6a5d98d9-bc5b-4257-93e5-6990a1d0edce", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("82a4a13c-8ca3-41f9-ac88-c2da580da3b2", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("32acce7f-cc8d-4837-9b9f-fe258682fdc3", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("3b8848b4-f1bd-4935-9878-f7cee84714c7", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("53571213-bfee-4b52-95c3-a410c3e981c0", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("abbef132-ee2c-453a-9e02-a378477f8e84", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("0aa6848e-c151-416b-b94b-5920eb00698e", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("fa2417f2-ff6b-4744-b37c-0c8a52c368bb", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("99b778ee-22e3-4fd1-a058-12ba7e8e3e90", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("d695c185-11ee-43f2-bc00-d0dfdb2b4640", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("316fe1ff-fa8c-46a6-bba4-00071437f68c", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("986c2ebf-ba95-45d7-8038-a6c4b779cb55", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("1ce48a34-ff0b-4974-a1fb-e2b2def41a18", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("64e30e80-aa8f-4d58-9918-48764f02c653", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("780d4eaa-40b0-40c4-886f-00799d3c39d3", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("e4bbf6e1-b476-458b-8941-32f11e44089f", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("06cb7e0a-2b1a-4f90-95b0-695266483cb5", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("3b490bbe-56c7-4d0f-b90e-e9f0381c1ceb", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("999f9f29-7d8d-4405-a87c-39e238567757", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("14c68ca9-43bf-4d37-bd3d-f369b742b4d4", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("af112a1e-e5c9-408b-ac70-c5a52f3d944f", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("af4f1d3e-7fbb-4fe9-9c49-65fe648fadb5", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("c460338c-34c6-4ed6-80a5-ecc6d70062e6", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("b3d30f9b-8197-4fa8-bdf9-5b33f7711b33", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("c65fd4e7-d499-4707-ab6f-ecf3f0f1a7bc", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("88bedf13-b860-4d75-99c7-fab99541baab", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("760bab04-0ac6-4a8c-99dc-d372c1eec6f7", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("de098db5-cbbe-4d4d-ac07-5e2689cd4a84", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("9983e630-eba8-45e2-9000-99a4df7a3423", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("842a6b96-311d-41e6-bd9d-cb3b2c6c5926", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("ef1bbe3b-d17b-4611-a580-23f85a62f178", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("ed87baa6-49b6-4192-9ba1-197527b44c5a", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("63ecc2b5-2021-4e95-9107-d3bfdeccb989", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("c4a07dfe-351a-4b89-b25c-f1a682b8f086", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("a3b6a8a6-b4e0-49af-9cc7-aeef7f3e404b", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("4d5b318d-b067-4a1b-ae3a-dfae2c771d24", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("cee43f98-976b-4411-81fa-a93b333d912e", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("6fde3b58-e4fc-404d-81d0-fa6210e07576", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("807cf5e4-8921-482a-a048-aa1f2be253d4", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("bc8da527-5af1-4ef8-a183-c951c2036ed4", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("9aee0b8a-35ed-4038-9bdb-0f8b56cf6b93", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("fab527a5-c60f-499b-8bf3-d85d62dc1b67", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("04c2d13d-55d0-4271-8ca6-dcff2b27d674", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("d00289eb-415e-42b9-b5d9-f5a0f4d814b0", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("c2a424e8-a113-4d21-b851-cd0bb35701b9", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("d9c761e8-0ccb-4f1a-a3a9-b37624110600", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("c138e0c2-8eb7-4d3a-a913-8ca29d6248d0", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("07df8d64-a83d-4451-bacf-e079a94e8ceb", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("11d9a2fe-5076-4685-a0d1-897fc85e23eb", "66174e1a-8413-4781-8805-7cdb4e935612"),
            Map.entry("3b865a83-b22d-4dea-8021-f6da7fa505b7", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("775b7474-5624-4a20-99e8-a92efc3351c1", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("e2b9244f-2ed4-4b56-814c-4daaf23c1f6e", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("bf28e7da-db57-4ddf-ab08-d3de8c62ad92", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("86f5a140-545a-4dce-8282-fd8663759cd8", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("eb732311-6f03-45af-aa2a-8c97648d1053", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("5fcd88f1-778f-4b12-bec6-775b582bdd9a", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("4faa3709-2e1a-40a4-96e9-779c3e8b7cba", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("28459c14-2585-4ce5-ba2d-4828d1d28882", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("9ddef94c-719e-44c3-9758-e4b677bfd971", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("75772427-4afc-4f75-baa0-9288ee5f1545", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("0d2ff8c1-5b4c-4300-a711-b4852aabb9f8", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("a3926915-30bd-4e75-b9b6-28040eb5fe11", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("1024b33a-4a59-4fdb-950f-b7131566209c", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("d22d1f01-d20e-4937-9a73-cc8f7ca0f5d0", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("9f192b89-e0e5-4124-937c-2880a06818e2", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("517e9bd8-f329-4c8f-bf2e-a19568d96db3", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("fb970e04-24e4-4bb7-9f5c-0bf048ee6423", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("ddc7719f-bb40-40e6-a397-3b160a7b640a", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("6e72f440-0b3b-423f-899e-c62b63b991aa", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("9e3a3432-00c2-4c64-8f8d-8a87db233dab", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("685347e7-6117-4102-988c-2be46f341233", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("88663d81-c572-4e9c-b4e2-85890df6c8a2", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("f4ff1413-7677-4e4c-81a6-af6bc05df57a", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("03241bfc-20e3-4b90-af98-a90c892e8e8f", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("2d668b9a-b938-437d-a7b6-0821cd79830e", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("f5970d66-e1e0-45a5-88f6-b9218498c6a2", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("7ef29179-243f-43a5-90aa-e9de5aa46483", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("22ef13a6-8775-4e2d-acdc-6feaff5c4be4", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("e3f859cf-fe4e-4f7f-af61-eb11d76eb95b", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("c7fd45bc-dde1-40d1-9045-2b7b959fc13d", "580a27f3-1538-4caa-a4ad-e07807f95f07"),
            Map.entry("ad9ffb07-44ce-4f8c-8f5a-1f9533b14b27", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("529fca0a-f50d-432b-8a50-48d3becd392f", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("552b6e89-4550-45ac-8ce2-274dd17505b4", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("118ac75b-a2ca-433e-8adb-de20d33a8046", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("bc537447-9850-49a1-986b-b6c0f8377203", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("f3cf1665-3503-4023-975e-83d288d55522", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("b280b104-7a63-44c0-b127-cb87752a4a6e", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("425e7588-b528-49aa-a628-2e93bfccc5c3", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("1b26a114-fefa-4c6e-9877-fd918390650e", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("c54461af-9cd0-492f-bcb1-3e365fd04136", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("8824dee9-2797-4ccb-bcba-c4d12b926e58", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("6ab594a7-6a5f-4c6f-91f7-ba0366e8fe55", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("0ea7c8c3-2c87-49f4-9e77-e87eeb60d684", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("9fa7f15d-cbca-4169-b290-ee80da651dd2", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("a7071a0d-99f5-4325-b554-aa6c4c521da1", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("c4c11ef0-5d88-424a-8ed4-85911da1f2dd", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("e8c09803-4177-4208-a654-408193baf8dd", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("4ac9f679-54c1-42ae-8d7c-5c6a05cd23d5", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("b2404ad2-8142-46ca-8342-068aff37bacc", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("bb31581e-c46c-454d-8735-51c700807355", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("1e522428-18c6-4d5b-8dc5-37637e2a8e7d", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("44e5d43e-98d5-4efa-a219-547ebd2afb38", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("c68c5a0f-e666-4478-bd11-ef7b1fa0635f", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("8fcb5792-8695-45bd-b225-bf543af314df", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("a38839ae-2f49-4a4c-a07d-45a55daa05ec", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("04d47953-9639-4e74-9056-a543eafae944", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("a5410520-cdd3-4421-971e-c6a4d04e9db2", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("a88f7269-6df3-41a1-95bf-5e9cb6a385fd", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("cb339cb8-84f6-4b57-be14-7ee9dff654a8", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("7bf3a940-f73e-4f20-82f8-9f575c783bfc", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("0af1a1c8-3554-4e71-8e61-7cf7e3fdb80d", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("6ac4b7ac-b795-4521-886d-776e4358406e", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("c68a0bf9-c1fd-45a6-8473-5a1d6483dc9f", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("1ee7f68e-c412-4c18-9b30-28a8e82e7c49", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("ff374813-9e35-4a85-b14d-096a7887292d", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("89343099-0864-42fa-91c0-2034201b621c", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("503631e6-b713-4cbc-96ae-627d4a44519b", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("bf8424a9-89da-4d20-a8c6-4633443591b0", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("b407e6b4-8344-4c99-a3ba-573709d19a58", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("6ff251ee-ca93-4a71-a3c3-871df83131c6", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("81c047ce-d83a-407e-829d-9d48cf3a327c", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("3b948629-9e16-49df-915f-cf8055523f4e", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("a4335f54-da0a-4e1e-9c88-f2318ad21405", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("163c2ba0-a821-4c04-8dde-7bbd395b6723", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("ca4b8145-758a-42e9-be27-c7ec4c8679c8", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("1132517f-c20c-4d3f-a181-49dc5e0b107a", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("7bd12503-edb0-4647-835f-afea0e8e45c9", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("8434e99f-d5e7-4883-83b2-16d9456de0e5", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("51073cf2-0e78-431e-9786-18f15bc1400d", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("10640b8a-b363-4253-aa95-e87954dad471", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("ea502395-3336-401d-8063-61d1871d68e1", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("47f42c80-7289-47e2-a10e-648d5dff604e", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("84afe0a2-49ff-40a1-bd6d-ed3824615747", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("c415c592-2079-4cd5-8b2a-73aac5779c5c", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("362120b5-43fd-4635-a7b1-59c041f0e8b9", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("b6dffb2d-3bd8-49b3-9c38-08314e7049f8", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("846fafd3-9539-4893-97f5-6c86a4ad7d0c", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("e183ec58-a607-4250-9464-f7120ab0716d", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("436a13ad-9213-40b1-a64a-190d82986975", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("437216ca-cb9d-439c-9f69-8eba5712c7d1", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("ec11ec06-a6ea-4354-a4ee-a3ed8527bae7", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("40013e00-3f6d-47df-83e6-d149b217449f", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("a1d34f06-0308-4a27-a563-a45a52cfb9ed", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("94ed9f31-f2b9-45fb-9a00-b89df54bc121", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("463fc913-62b3-4fa0-8bce-57dc8ec9fee3", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("9242e7ae-cedc-46f1-ba3f-5c430fd766b5", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("02fcc7ca-8372-4060-a3b5-2ebdfe35e00d", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("92dd3c5c-1480-4d2b-9356-a7df06579168", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("b7a60ec1-12a3-4094-95dc-a18a318f9a76", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("eb8fb49b-dda2-4b25-9199-0400fd164034", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("a6e519aa-cdf6-4e18-a4f9-bc9b6322cfbd", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("1a134e68-e0b0-4748-911f-b2d383e62c39", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("a515b7c9-7ab7-4d14-b4e6-b2125e0e250a", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("b591b90a-5adc-4165-a323-ae8845b36718", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("80824d71-2c33-4c72-9e85-392811fee13e", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("b9d443ce-f458-411e-af52-0c461a63a89a", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("ececb7fb-0630-4bfd-9feb-b99c8b0b6d8c", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("310acf91-90ee-4499-8641-358e8695fdcd", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("ed56ec22-26b6-4fac-8cc7-cfdf7e23fdcb", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("31cdd75a-1a28-4474-8a8f-80ec23795a47", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("9745142d-38b0-4550-a8a0-e8f6aeaa58a6", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("e08747ae-1c2e-4ddc-ba6b-cd1e2c10b88e", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("4307db6f-5a8d-405b-add7-82c062164835", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("3a814539-efb9-4999-a4b2-6b251db9ab88", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("0200f598-8922-4ee6-a79b-d2462bf2b548", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("d886800d-3ef6-4046-91a9-fca08999fcee", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("55159a95-8b4d-47c9-9ebf-cef482fd329f", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("0327ed33-7ecd-4da0-bce3-a35653f01ae0", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("bae55d4a-5b50-4be9-b0b4-237d944969df", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("b0e95e29-6d69-4510-ab2a-29922f9f3523", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("bd4cbeea-8419-4869-99ec-cc8f7f289352", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("b8148b7c-0942-4a93-b2ca-3b58ee025ded", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("59b92b89-13c1-4ce7-be19-074696fcd4a7", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("9f1c0ef4-97df-4a3d-b9ca-ce96b57f1e77", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("4f87fd50-bb9d-432d-aee0-a13166de22e8", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("c44c3263-6517-46c3-b2d9-e015091b40bd", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("e680945d-23d1-4add-8808-17fa59c01468", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("43e153a3-f5a1-40c9-9b05-8b025eebb568", "69b1198a-95dc-46fc-8879-52fb9a7d8095")
    );
    private static final Map<String, String> CONCEPT_TO_COMMODITY_GROUP = Map.ofEntries(
            Map.entry("ad482083-7bcf-443e-b8bb-a4bba0a47a47", "ee975f52-6db1-4ed4-b1ac-c5b39e0e472c"),
            Map.entry("53d5d55d-8999-4bee-aa4b-cee87643e8b4", "ee975f52-6db1-4ed4-b1ac-c5b39e0e472c"),
            Map.entry("aa0dd981-a489-4d4d-83aa-61122f62ef5b", "ee975f52-6db1-4ed4-b1ac-c5b39e0e472c"),
            Map.entry("0d498a9c-3a8e-48c8-8e93-7f7cbfba1169", "ee975f52-6db1-4ed4-b1ac-c5b39e0e472c"),
            Map.entry("4c762487-e337-42fa-9cf7-0f99d5c61127", "ee975f52-6db1-4ed4-b1ac-c5b39e0e472c"),
            Map.entry("4978b1b1-4947-4db7-b3bf-da89ec99e4ec", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("da017418-9c64-4f0b-ab0b-ed91879dde61", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("559a85bf-5cf8-4627-9e30-a2e5b0331981", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("4d61204c-df2f-4bb9-b8ca-fcd0d48eb197", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("65a7dcf3-7439-4582-91b7-d97a89a37540", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("46ec4a36-cfec-42a7-b0c6-0ce815d1ce22", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("0d7d339a-00c5-44c6-b0f4-d6c634a887da", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("52721b19-5c4f-4d80-bd41-3c1582a5c885", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("e43f211c-2531-47ae-beb5-c5805bc2c0f1", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("44601d3a-fc54-410b-b3cc-e7374f6e51c6", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("44a2557b-7141-49ab-bc47-665a8df75c4a", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("3527b657-845f-4d8a-94e7-173db9c2f7cc", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("d4eb2403-81cb-4047-b11d-90d9fe3d9317", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("191c66b8-d0ba-43d9-8de2-11866b65fa6f", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("ba3355ac-8e42-40dc-9659-464fb4f86ee2", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("96762cde-dd24-4339-a157-5ab314141524", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("2e8aa8d4-457d-45a6-9b71-315bbcac2465", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("0bcd7119-8b96-4986-912f-6b2200e530c8", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("bb3e3206-a40f-4c04-a1e0-0641202b0b53", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("45ffb098-ebad-4aed-b6bc-030363a453e5", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("9babc2a2-4721-4566-a8b3-23d830530e26", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("9f5659fb-b13f-4194-82ca-03c12caf1729", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("6c0c77fd-83c6-40ff-ba5d-201a753c66cf", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("a1e395d6-ad6a-448d-b61a-7e697cec6fc2", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("6135578d-d1b7-4dad-b2db-0e6b475eeb32", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("b730779b-a473-4fa1-ac6d-361af4e8d5ca", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("2707c92b-f300-4263-95ba-bed882df8d27", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("38e25244-2d82-46d9-bce4-cbe8478938ad", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("0657bfa8-8a36-42af-b5b4-414ec6ea0f6b", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("93aa3914-e5d9-4ffe-ac2d-72bd85727c09", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("f86610d1-897c-4f11-b750-5fb1d9a29429", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("152909dc-dc93-45c7-87aa-725dedfd2d36", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("a50c5f74-efda-4cdb-8221-984ac88d28a2", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("8b527223-42a8-47e1-9047-138dd732de8a", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("d5861356-7091-4edd-91b3-3d28e0ad8943", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("09e23659-d6a6-4fb6-9715-8765219ea455", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("9daeb7e5-50de-47ae-a4a0-dd122ea44026", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("009b52a6-fc3f-47e9-8c82-b1fab17303f7", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("24382d8f-9dff-486c-8f47-703746ff5ae7", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("f7561f54-a962-4ade-8e3e-1f5b546f7664", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("53a8fca4-3da1-4205-9282-eaa59bfb8553", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("67eeda7d-bd87-4938-af2d-c6da39c1e3d8", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("5e4ad9ec-0328-469a-a257-0035e735e381", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("ec074599-46dc-4ac9-8087-c730c6a241f2", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("05dbfc76-3df0-462c-93bc-7b38d907285c", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("4fbef814-2818-4460-a48e-08f1a0f1d94b", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("753a69ee-a748-4ed7-9093-461cd1d1bdbc", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("e6a9bda9-1e0f-4734-a3f3-43f921c70eb8", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("c739e42b-308d-47ee-9a06-0c2241e5da18", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("2a972949-e8f3-4127-8dc8-cb2f8002873a", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("bb7afe54-2cc6-459b-86db-be751e8778fa", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("0f6d5e2c-4ce9-450f-ac7c-f5eff0f820df", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("813922c8-a1dc-4fa8-a5fc-9ece789692f3", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("271a2f7e-91bb-401a-a2f5-f96ff857581b", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("8ed5be59-6518-4ad0-9509-ab1f801a00e0", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("2808254b-2e02-4916-9fa2-a3195160c8fd", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("b5ed86c9-792e-4395-950d-32e50195c3db", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("462c95df-e8b2-41c9-aede-a18b6fa87cb2", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("c2780bc6-46d2-4c1d-85ca-3d8685521c4d", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("3d80b05e-8b02-46a2-a3c7-53b56275bbb3", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("e34dbda6-c221-4dda-822e-7bf1ddd90bcb", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("85d084ef-7ff3-4f67-b790-6cc37b8ff972", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("90297e9c-3517-4f37-b87e-6c6d6ef94c52", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("c9af9aa7-db38-40e9-bcf0-32cc70814e8e", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("3c8f720a-b51c-4cbc-b93a-5a87b6041934", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("f8d50ebc-6e3f-49ed-ae04-76886af4588d", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("43f75d64-9e53-4324-8c06-f590729590c5", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("2bf19ce1-2bed-4e25-81af-1cdd0e851b99", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("ce7c3caf-bb74-4eae-9499-74863be22614", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("6ae5a196-92f9-4eda-9dfa-43409e37a184", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("79d4fab5-4fb5-493a-8701-3ec79520a483", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("e015737e-9e0e-41bd-8a6c-5289147208c2", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("1be785f8-5aaa-45d3-a66b-88f501c5eca9", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("0f014161-c5e1-4740-a68e-17531fef5a2e", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("533f9e63-ca2a-475a-8c22-a52d2a54a9d5", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("fffa8784-9607-4370-988c-f194764e5470", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("3ccefd21-8a26-4fce-81f6-8c3f9eeec8b1", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("adf3e6a6-25da-4ef0-ab66-5f4c425b08c8", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("2a99a8e7-a62a-42e4-8ec6-556ed1692912", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("9a069274-51e9-4978-865a-4c41fda46099", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("a59d031f-4062-4129-9c6d-5fa8fc90c0a8", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("63203942-0f75-4158-8452-19628052322c", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("ad2dd6e9-2cd8-44b0-a90f-d124cf591c53", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("24d7d829-8769-4dba-97d4-01c9efd56e97", "afee7042-0263-4674-9e27-49ca952b14ea")
    );
    private static final Map<String, String> CONCEPT_TO_TOPIC_GROUP = Map.ofEntries(
            Map.entry("5d564ee5-243d-4339-a0ea-78a524b54904", "3c42ecb8-4871-4091-b1f1-d31598335fa8"),
            Map.entry("5f94f4e0-d94b-41d5-aa03-54420cb0084f", "3c42ecb8-4871-4091-b1f1-d31598335fa8"),
            Map.entry("aa544db1-8957-437b-9496-c7ca5d1b0c9d", "3c42ecb8-4871-4091-b1f1-d31598335fa8"),
            Map.entry("b4b9b4bb-cff0-423d-9297-6b822166a76a", "3c42ecb8-4871-4091-b1f1-d31598335fa8"),
            Map.entry("cfe6b5d2-575c-4e1d-aaf8-824380d20f14", "3c42ecb8-4871-4091-b1f1-d31598335fa8"),
            Map.entry("774c3a96-911e-4874-b139-71240970b179", "d5529814-3067-4264-a0f9-7036afb28071"),
            Map.entry("b1cd9724-e148-45b8-be66-469f691735eb", "d5529814-3067-4264-a0f9-7036afb28071"),
            Map.entry("da5f4762-eb4d-4a43-9a92-a68b42fd85ed", "d5529814-3067-4264-a0f9-7036afb28071"),
            Map.entry("da46d791-3933-47dc-af74-bb1066483c54", "d5529814-3067-4264-a0f9-7036afb28071"),
            Map.entry("61fda965-158b-4fe3-a138-9a46cac2aa32", "63cb1a12-17da-49b3-afff-842a349715b8"),
            Map.entry("0db990c0-ecf8-467d-98bd-ba7a62a57223", "63cb1a12-17da-49b3-afff-842a349715b8"),
            Map.entry("c72ad343-4af2-4125-b84c-4285ca9526b4", "63cb1a12-17da-49b3-afff-842a349715b8"),
            Map.entry("137ccbd4-ac91-411f-9c14-c525c5d0b709", "63cb1a12-17da-49b3-afff-842a349715b8"),
            Map.entry("f7fd83b2-f3d5-4b2e-ad68-79b26274a8ca", "63cb1a12-17da-49b3-afff-842a349715b8"),
            Map.entry("77a2d771-e741-4ba9-9fdc-5796dd430ac9", "af1822e6-89c6-4555-a111-b62f3ae42b5d"),
            Map.entry("59d533b6-22e7-4f73-9eec-1f5ff01ba0ef", "af1822e6-89c6-4555-a111-b62f3ae42b5d"),
            Map.entry("a79a53fb-517d-4a58-a657-35a027f3bf3c", "af1822e6-89c6-4555-a111-b62f3ae42b5d"),
            Map.entry("edb5c004-e080-4419-ac04-cd0955ea9869", "af1822e6-89c6-4555-a111-b62f3ae42b5d"),
            Map.entry("cb98ac6e-ad03-425e-9084-d33e643c0299", "af1822e6-89c6-4555-a111-b62f3ae42b5d"),
            Map.entry("c26e3e2b-12e5-46fa-aeb5-fba8d9f76e0b", "02727ef0-03af-4891-9328-9ef102ea0d61"),
            Map.entry("365e395d-76cf-4fa3-aa28-ed32be087825", "02727ef0-03af-4891-9328-9ef102ea0d61"),
            Map.entry("46226672-742d-45bc-a288-fc2262ba0a10", "4e586a9d-4bdf-4c14-9346-a8a1b2779882"),
            Map.entry("bf1df009-f486-473c-ba30-56404903dd41", "4e586a9d-4bdf-4c14-9346-a8a1b2779882"),
            Map.entry("01772c23-40bf-45d3-aab3-f56e7cca1e0c", "4e586a9d-4bdf-4c14-9346-a8a1b2779882"),
            Map.entry("859a67ed-24ae-46b9-b18d-8526868b2193", "4e586a9d-4bdf-4c14-9346-a8a1b2779882"),
            Map.entry("3d05f898-4777-4a24-b86c-29697a6f16ea", "4e586a9d-4bdf-4c14-9346-a8a1b2779882"),
            Map.entry("c2e521d9-6489-4d42-be76-c1682ba9af74", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("10a5e74c-1ef6-42a7-b822-81f7aa2e12d5", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("62ecbb19-0e3a-4cf2-a6d8-872dc41530fa", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("b8e5a815-affe-471e-ae55-979ee5c7af11", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("01f9e230-8ee6-4183-8b67-ddc351bfb5b6", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("e929a269-f1d8-4e88-abd4-be019683f5cd", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("35825d8a-6bc3-48fb-ae7a-997e0763ea94", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("1b2416a5-4b5d-401d-941d-5f89de7df7a1", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("2d434129-6a91-4033-9461-23cd5ec94ca9", "631d4525-55e6-4ecb-9aef-0b7c775232ca")
    );

    public static final String X_PROJECT_SLUG = "x-project-slug";

    public static void decorateProjectScopedModels(RdfModel model, Resource resource, Multimap<String, String> attributes) {
        if (!(model instanceof PersistentProjectScopedModel) || !attributes.containsKey(X_PROJECT_SLUG)) {
            return;
        }

        try {
            var projectIdValue = Iterables.getOnlyElement(attributes.get(X_PROJECT_SLUG));
            var projectScopedModel = (PersistentProjectScopedModel) model;

            projectScopedModel.setProjectId(projectIdValue);
        } catch (NoSuchElementException ex) {
            throw new WebApplicationException("X-Project-Slug must be present in the request headers");
        }
    }


    @Produces
    public RdfModelFactory rdfModelFactory() {
        return new RdfModelFactory(List.of(
                RdfConfig::decorateProjectScopedModels,
                this::decorareCommodityGroupModels
        ));
    }

    private void decorareCommodityGroupModels(RdfModel rdfModel, Resource resource, Multimap<String, String> stringStringMultimap) {
        if (!(rdfModel instanceof ConceptModel)) {
            return;
        }

        boolean isRecursive = isInRecursiveCall.get();
        if (!isRecursive) {
            isInRecursiveCall.set(true);

            ConceptModel model = (ConceptModel) rdfModel;
            String id = resource.getURI();
            if (id != null) {
                conceptIdResolver.resolve(URI.create(id)).map(UUID::toString).ifPresent(uuidKey -> {
                    handleGroup(resource, uuidKey, CONCEPT_TO_COMMODITY_GROUP, inCommodityGroup, isCommodityGroup);
                    handleGroup(resource, uuidKey, CONCEPT_TO_TOPIC_GROUP, inTopicGroup, isTopicGroup);
                    handleGroup(resource, uuidKey, CONCEPT_TO_REGION_GROUP, inRegionGroup, isRegionGroup);
                });
            }

            isInRecursiveCall.set(false);
        }
    }

    private void handleGroup(Resource reource, String uuid, Map<String, String> grouping, Property groupProperty, Property inverseProperty) {
        if (grouping.containsKey(uuid)) {
            String groupId = grouping.get(uuid);
            Model model = reource.getModel();
            UUID groupUuid = UUID.fromString(groupId);
            String groupUri = conceptIdResolver.resolve(groupUuid).toString();
            Resource groupResourceRef = ResourceFactory.createResource(groupUri);

            if (!model.containsResource(groupResourceRef)) {
                concepts.find(groupUuid)
                        .ifPresent(groupConcept -> model.add(groupConcept.getResource().getModel()));
            }

            reource.addProperty(groupProperty, model.createResource(groupUri));
        } else if (grouping.containsValue(uuid)) {
            reource.addLiteral(inverseProperty, true);
        }
    }
}
