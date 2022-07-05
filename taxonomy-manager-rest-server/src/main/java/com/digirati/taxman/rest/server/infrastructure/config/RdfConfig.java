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
import java.util.*;

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
    public static final Set<String> ADDITIONAL_TOPIC_GROUPS = Set.of("3f2cc76f-3065-4d47-904f-af4b53b96b05", "314d5a4f-1097-463d-9634-07792bcba566", "700913a2-62c9-41d9-9011-1a16aea0f0db");
    public static final Set<String> ADDITIONAL_REGION_GROUPS = Set.of("cf28b330-7a1f-46ac-8c6b-dcb2eeccc7dc");
    private static final Map<String, String> CONCEPT_TO_REGION_GROUP = Map.ofEntries(
            Map.entry("d9c761e8-0ccb-4f1a-a3a9-b37624110600", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("fab527a5-c60f-499b-8bf3-d85d62dc1b67", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("fa2417f2-ff6b-4744-b37c-0c8a52c368bb", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("b3d30f9b-8197-4fa8-bdf9-5b33f7711b33", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("07df8d64-a83d-4451-bacf-e079a94e8ceb", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("c138e0c2-8eb7-4d3a-a913-8ca29d6248d0", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("af112a1e-e5c9-408b-ac70-c5a52f3d944f", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("50da4554-46ad-4042-96de-c06d0b14e1fd", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("de098db5-cbbe-4d4d-ac07-5e2689cd4a84", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("a3b6a8a6-b4e0-49af-9cc7-aeef7f3e404b", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("9aee0b8a-35ed-4038-9bdb-0f8b56cf6b93", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("fdee04f6-6c15-4d5c-b917-1ec972cddf25", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("06cb7e0a-2b1a-4f90-95b0-695266483cb5", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("0aa6848e-c151-416b-b94b-5920eb00698e", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("14c68ca9-43bf-4d37-bd3d-f369b742b4d4", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("18077f46-dd90-4154-8aae-cc835185795f", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("bc8da527-5af1-4ef8-a183-c951c2036ed4", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("9059df25-352a-40de-9af7-84a08e308f4f", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("ef1bbe3b-d17b-4611-a580-23f85a62f178", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("d695c185-11ee-43f2-bc00-d0dfdb2b4640", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("316fe1ff-fa8c-46a6-bba4-00071437f68c", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("eddafcc0-849a-4305-bcec-1fac7395963c", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("c460338c-34c6-4ed6-80a5-ecc6d70062e6", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("780d4eaa-40b0-40c4-886f-00799d3c39d3", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("c65fd4e7-d499-4707-ab6f-ecf3f0f1a7bc", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("999f9f29-7d8d-4405-a87c-39e238567757", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("d35bfa87-ae7e-4be4-9a4b-71a020998361", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("88bedf13-b860-4d75-99c7-fab99541baab", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("3b8848b4-f1bd-4935-9878-f7cee84714c7", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("986c2ebf-ba95-45d7-8038-a6c4b779cb55", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("82a4a13c-8ca3-41f9-ac88-c2da580da3b2", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("c2a424e8-a113-4d21-b851-cd0bb35701b9", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("04c2d13d-55d0-4271-8ca6-dcff2b27d674", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("64e30e80-aa8f-4d58-9918-48764f02c653", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("57817306-2474-46bc-a49b-d8d22b78d1d4", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("af4f1d3e-7fbb-4fe9-9c49-65fe648fadb5", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("32acce7f-cc8d-4837-9b9f-fe258682fdc3", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("807cf5e4-8921-482a-a048-aa1f2be253d4", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("abbef132-ee2c-453a-9e02-a378477f8e84", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("99b778ee-22e3-4fd1-a058-12ba7e8e3e90", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("1ce48a34-ff0b-4974-a1fb-e2b2def41a18", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("842a6b96-311d-41e6-bd9d-cb3b2c6c5926", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("ed87baa6-49b6-4192-9ba1-197527b44c5a", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("c4a07dfe-351a-4b89-b25c-f1a682b8f086", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("6a5d98d9-bc5b-4257-93e5-6990a1d0edce", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("53571213-bfee-4b52-95c3-a410c3e981c0", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("3b490bbe-56c7-4d0f-b90e-e9f0381c1ceb", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("e4bbf6e1-b476-458b-8941-32f11e44089f", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("760bab04-0ac6-4a8c-99dc-d372c1eec6f7", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("715915cf-0ba1-4bc8-8782-838ef7516ae5", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("11d9a2fe-5076-4685-a0d1-897fc85e23eb", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("17593f93-676e-4f0c-8e85-f968fb95d688", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("4d5b318d-b067-4a1b-ae3a-dfae2c771d24", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("cee43f98-976b-4411-81fa-a93b333d912e", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("9983e630-eba8-45e2-9000-99a4df7a3423", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("d00289eb-415e-42b9-b5d9-f5a0f4d814b0", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("6fde3b58-e4fc-404d-81d0-fa6210e07576", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("6bda1372-6b6e-4ad3-8379-0e40795e0231", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("63ecc2b5-2021-4e95-9107-d3bfdeccb989", "1ceb32aa-0450-41a5-9958-544b628b7c2f"),
            Map.entry("7e860b06-236e-4a69-a2c1-7cbfc9e25c88", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("ddf42c97-3586-4cb2-82a6-07c4f89d8170", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("41cb5490-569f-40ca-af8d-c17a4e72c91d", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("7a0b5be3-9a7d-4d14-8dd6-784cdd84bf47", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("3f20063f-500f-4680-84fd-e3f1c8d818d6", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("5eb863d8-bf27-4983-bcb6-628c7cb206f2", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("4433e5c7-1e99-4103-ba11-066d5dc8866b", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("9bf69406-d321-47ad-8c19-f8d62f474f09", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("d330f9e1-9789-4a50-8b1a-343e9d49c9ab", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("e6573f5f-2b1f-45f1-9cf2-978be165298d", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("10722a9a-7ffd-432f-bde0-84dcc7d42ba5", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("bc8e6db8-8f98-4dac-bd81-e323c4ff1789", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("8f817330-d9a1-48e1-afef-6a62144260db", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("4332016a-38b7-4bda-a913-d87ed0a98012", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("3ca8c618-7f9c-4d7b-94b9-349a89398ea9", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("66fb80cc-f6fd-4e0e-8fc0-15db382fdce6", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("b30768cf-703c-460e-bc02-883665905044", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("bdc3c63c-d1d6-4b18-b223-408ac9db624f", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("316ab5b2-767c-41e8-a1ea-edfd8dd103c1", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("8713a060-3679-4edf-b84d-77d306f5b07a", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("81ad65da-8f51-4fc4-aff0-d239035a759e", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("4cfd2a66-6ed9-483f-82e2-96d9bed8d342", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("f6e04371-aea7-49a3-9489-6296352ba053", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("b30e4942-8403-46c8-a9fb-1cb7b4001150", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("bf176e22-d1dc-4f6a-a013-ca9e0be1401f", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("13af6d00-616c-417d-aca1-8c994b2dd548", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("9a4326b2-6613-44bb-ba53-926beae2bb4a", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("6dee9071-cc5c-471d-ae60-e8bf820ffdc1", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("82613fe6-04e3-4364-885c-1d679a6aecce", "9a2c5b3b-63b5-4f90-81a2-a17c06a725c1"),
            Map.entry("e6bd6264-0a6d-4099-bfd8-b08938481329", "7c0a2418-5d56-48ea-a7a8-f2570b8cf0f4"),
            Map.entry("1245ca55-ac64-4ac9-88a8-926140ef9b6f", "7c0a2418-5d56-48ea-a7a8-f2570b8cf0f4"),
            Map.entry("da508947-316d-49c8-af67-28f06ef87937", "7c0a2418-5d56-48ea-a7a8-f2570b8cf0f4"),
            Map.entry("7674d503-8c0b-445a-9003-274db3e913ae", "7c0a2418-5d56-48ea-a7a8-f2570b8cf0f4"),
            Map.entry("7e7c937a-2a38-4770-bcb8-1755fd14d8cf", "7c0a2418-5d56-48ea-a7a8-f2570b8cf0f4"),
            Map.entry("431f6c9c-1097-4bbc-bbf6-ac02d45888ea", "7c0a2418-5d56-48ea-a7a8-f2570b8cf0f4"),
            Map.entry("07e15900-b64d-4d61-a92e-24df4634c04c", "7c0a2418-5d56-48ea-a7a8-f2570b8cf0f4"),
            Map.entry("54ec04de-e55a-49a7-9762-c7b0e348abcd", "7c0a2418-5d56-48ea-a7a8-f2570b8cf0f4"),
            Map.entry("ca80d8fb-8e19-4c70-8c6a-211d0a18f237", "7c0a2418-5d56-48ea-a7a8-f2570b8cf0f4"),
            Map.entry("60a1f9cc-78be-44b8-8a19-46056ec4cf65", "7c0a2418-5d56-48ea-a7a8-f2570b8cf0f4"),
            Map.entry("1c68f75d-c9c7-490d-b807-2b81d172384c", "7c0a2418-5d56-48ea-a7a8-f2570b8cf0f4"),
            Map.entry("5fb7e7d4-6c44-4fbb-8546-fb8b912a1136", "7c0a2418-5d56-48ea-a7a8-f2570b8cf0f4"),
            Map.entry("353f836d-1fd3-45f4-b60b-12d7e2652515", "7c0a2418-5d56-48ea-a7a8-f2570b8cf0f4"),
            Map.entry("01e542c4-ad8f-47ee-b274-45a0e7bb269e", "7c0a2418-5d56-48ea-a7a8-f2570b8cf0f4"),
            Map.entry("7fb764ba-8fe2-4ff9-b079-4ac1f78527ed", "7c0a2418-5d56-48ea-a7a8-f2570b8cf0f4"),
            Map.entry("28459c14-2585-4ce5-ba2d-4828d1d28882", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("d22d1f01-d20e-4937-9a73-cc8f7ca0f5d0", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("2c987e6c-05df-473a-acb0-832850ae2212", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("75772427-4afc-4f75-baa0-9288ee5f1545", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("ddc7719f-bb40-40e6-a397-3b160a7b640a", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("f4ff1413-7677-4e4c-81a6-af6bc05df57a", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("1024b33a-4a59-4fdb-950f-b7131566209c", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("c7fd45bc-dde1-40d1-9045-2b7b959fc13d", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("685347e7-6117-4102-988c-2be46f341233", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("a3926915-30bd-4e75-b9b6-28040eb5fe11", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("4faa3709-2e1a-40a4-96e9-779c3e8b7cba", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("2d668b9a-b938-437d-a7b6-0821cd79830e", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("eb732311-6f03-45af-aa2a-8c97648d1053", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("22ef13a6-8775-4e2d-acdc-6feaff5c4be4", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("3b865a83-b22d-4dea-8021-f6da7fa505b7", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("775b7474-5624-4a20-99e8-a92efc3351c1", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("e3f859cf-fe4e-4f7f-af61-eb11d76eb95b", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("517e9bd8-f329-4c8f-bf2e-a19568d96db3", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("93864c48-fdca-44a5-b042-eb806e2aa28b", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("9ddef94c-719e-44c3-9758-e4b677bfd971", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("88663d81-c572-4e9c-b4e2-85890df6c8a2", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("9f192b89-e0e5-4124-937c-2880a06818e2", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("bf28e7da-db57-4ddf-ab08-d3de8c62ad92", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("9e3a3432-00c2-4c64-8f8d-8a87db233dab", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("03241bfc-20e3-4b90-af98-a90c892e8e8f", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("86f5a140-545a-4dce-8282-fd8663759cd8", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("fb970e04-24e4-4bb7-9f5c-0bf048ee6423", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("6e72f440-0b3b-423f-899e-c62b63b991aa", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("f5970d66-e1e0-45a5-88f6-b9218498c6a2", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("5fcd88f1-778f-4b12-bec6-775b582bdd9a", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("e2b9244f-2ed4-4b56-814c-4daaf23c1f6e", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("7ef29179-243f-43a5-90aa-e9de5aa46483", "f37a883d-03d9-4f1f-ab32-e1c2473ad33f"),
            Map.entry("a5410520-cdd3-4421-971e-c6a4d04e9db2", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("529fca0a-f50d-432b-8a50-48d3becd392f", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("1ee7f68e-c412-4c18-9b30-28a8e82e7c49", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("44e5d43e-98d5-4efa-a219-547ebd2afb38", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("47dec8b1-81fe-4d34-b3a7-37e48cc2adb3", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("425e7588-b528-49aa-a628-2e93bfccc5c3", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("1e522428-18c6-4d5b-8dc5-37637e2a8e7d", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("04d47953-9639-4e74-9056-a543eafae944", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("c54461af-9cd0-492f-bcb1-3e365fd04136", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("a7071a0d-99f5-4325-b554-aa6c4c521da1", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("c68a0bf9-c1fd-45a6-8473-5a1d6483dc9f", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("bb31581e-c46c-454d-8735-51c700807355", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("8fcb5792-8695-45bd-b225-bf543af314df", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("b2404ad2-8142-46ca-8342-068aff37bacc", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("b407e6b4-8344-4c99-a3ba-573709d19a58", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("a88f7269-6df3-41a1-95bf-5e9cb6a385fd", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("0262a1c1-30ba-4d16-a18d-ef29e25ae6e2", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("8824dee9-2797-4ccb-bcba-c4d12b926e58", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("c68c5a0f-e666-4478-bd11-ef7b1fa0635f", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("0ea7c8c3-2c87-49f4-9e77-e87eeb60d684", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("118ac75b-a2ca-433e-8adb-de20d33a8046", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("1b26a114-fefa-4c6e-9877-fd918390650e", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("a38839ae-2f49-4a4c-a07d-45a55daa05ec", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("b280b104-7a63-44c0-b127-cb87752a4a6e", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("0af1a1c8-3554-4e71-8e61-7cf7e3fdb80d", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("ad9ffb07-44ce-4f8c-8f5a-1f9533b14b27", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("c4c11ef0-5d88-424a-8ed4-85911da1f2dd", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("bc537447-9850-49a1-986b-b6c0f8377203", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("f3cf1665-3503-4023-975e-83d288d55522", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("7bf3a940-f73e-4f20-82f8-9f575c783bfc", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("81c047ce-d83a-407e-829d-9d48cf3a327c", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("b0696e6b-6aaf-4e84-b6ef-faea5134399b", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("552b6e89-4550-45ac-8ce2-274dd17505b4", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("89343099-0864-42fa-91c0-2034201b621c", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("9fa7f15d-cbca-4169-b290-ee80da651dd2", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("4ac9f679-54c1-42ae-8d7c-5c6a05cd23d5", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("3b948629-9e16-49df-915f-cf8055523f4e", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("6ff251ee-ca93-4a71-a3c3-871df83131c6", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("6ab594a7-6a5f-4c6f-91f7-ba0366e8fe55", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("6ac4b7ac-b795-4521-886d-776e4358406e", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("ff374813-9e35-4a85-b14d-096a7887292d", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("503631e6-b713-4cbc-96ae-627d4a44519b", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("e8c09803-4177-4208-a654-408193baf8dd", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("bf8424a9-89da-4d20-a8c6-4633443591b0", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("cb339cb8-84f6-4b57-be14-7ee9dff654a8", "423b0903-142e-4db1-9712-b75681d85957"),
            Map.entry("92dd3c5c-1480-4d2b-9356-a7df06579168", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("ec11ec06-a6ea-4354-a4ee-a3ed8527bae7", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("43e153a3-f5a1-40c9-9b05-8b025eebb568", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("ca4b8145-758a-42e9-be27-c7ec4c8679c8", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("ececb7fb-0630-4bfd-9feb-b99c8b0b6d8c", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("a515b7c9-7ab7-4d14-b4e6-b2125e0e250a", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("437216ca-cb9d-439c-9f69-8eba5712c7d1", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("b591b90a-5adc-4165-a323-ae8845b36718", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("31cdd75a-1a28-4474-8a8f-80ec23795a47", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("94ed9f31-f2b9-45fb-9a00-b89df54bc121", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("a4335f54-da0a-4e1e-9c88-f2318ad21405", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("bae55d4a-5b50-4be9-b0b4-237d944969df", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("463fc913-62b3-4fa0-8bce-57dc8ec9fee3", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("02fcc7ca-8372-4060-a3b5-2ebdfe35e00d", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("3a814539-efb9-4999-a4b2-6b251db9ab88", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("ed56ec22-26b6-4fac-8cc7-cfdf7e23fdcb", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("0f2246af-5c43-4105-a3e2-1a0ce2b7698c", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("c415c592-2079-4cd5-8b2a-73aac5779c5c", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("163c2ba0-a821-4c04-8dde-7bbd395b6723", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("b8148b7c-0942-4a93-b2ca-3b58ee025ded", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("51073cf2-0e78-431e-9786-18f15bc1400d", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("0327ed33-7ecd-4da0-bce3-a35653f01ae0", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("47f42c80-7289-47e2-a10e-648d5dff604e", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("b6dffb2d-3bd8-49b3-9c38-08314e7049f8", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("ea502395-3336-401d-8063-61d1871d68e1", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("8434e99f-d5e7-4883-83b2-16d9456de0e5", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("10640b8a-b363-4253-aa95-e87954dad471", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("436a13ad-9213-40b1-a64a-190d82986975", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("e183ec58-a607-4250-9464-f7120ab0716d", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("b0e95e29-6d69-4510-ab2a-29922f9f3523", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("310acf91-90ee-4499-8641-358e8695fdcd", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("e680945d-23d1-4add-8808-17fa59c01468", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("0200f598-8922-4ee6-a79b-d2462bf2b548", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("55159a95-8b4d-47c9-9ebf-cef482fd329f", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("1a134e68-e0b0-4748-911f-b2d383e62c39", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("362120b5-43fd-4635-a7b1-59c041f0e8b9", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("9745142d-38b0-4550-a8a0-e8f6aeaa58a6", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("b7a60ec1-12a3-4094-95dc-a18a318f9a76", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("4307db6f-5a8d-405b-add7-82c062164835", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("0f9dbb6b-3acf-4279-932e-77b4dbad713f", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("c44c3263-6517-46c3-b2d9-e015091b40bd", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("9242e7ae-cedc-46f1-ba3f-5c430fd766b5", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("4f87fd50-bb9d-432d-aee0-a13166de22e8", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("40013e00-3f6d-47df-83e6-d149b217449f", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("846fafd3-9539-4893-97f5-6c86a4ad7d0c", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("84afe0a2-49ff-40a1-bd6d-ed3824615747", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("bd4cbeea-8419-4869-99ec-cc8f7f289352", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("80824d71-2c33-4c72-9e85-392811fee13e", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("9f1c0ef4-97df-4a3d-b9ca-ce96b57f1e77", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("a1d34f06-0308-4a27-a563-a45a52cfb9ed", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("a6e519aa-cdf6-4e18-a4f9-bc9b6322cfbd", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("1132517f-c20c-4d3f-a181-49dc5e0b107a", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("59b92b89-13c1-4ce7-be19-074696fcd4a7", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("e08747ae-1c2e-4ddc-ba6b-cd1e2c10b88e", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("7bd12503-edb0-4647-835f-afea0e8e45c9", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("eb8fb49b-dda2-4b25-9199-0400fd164034", "4f2e0943-58f7-48b2-abf3-0f2212c735a2"),
            Map.entry("b9d443ce-f458-411e-af52-0c461a63a89a", "4f2e0943-58f7-48b2-abf3-0f2212c735a2")
    );

    private static final Map<String, String> CONCEPT_TO_COMMODITY_GROUP = Map.ofEntries(
            Map.entry("4c762487-e337-42fa-9cf7-0f99d5c61127", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("0d498a9c-3a8e-48c8-8e93-7f7cbfba1169", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("6c0c77fd-83c6-40ff-ba5d-201a753c66cf", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("6135578d-d1b7-4dad-b2db-0e6b475eeb32", "a6d3b095-fd28-45e8-b751-75bf1da0a0f7"),
            Map.entry("5e4ad9ec-0328-469a-a257-0035e735e381", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("009b52a6-fc3f-47e9-8c82-b1fab17303f7", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("38e25244-2d82-46d9-bce4-cbe8478938ad", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("09e23659-d6a6-4fb6-9715-8765219ea455", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("39f1239b-406c-49ce-8622-bb4ccf53f509", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("ec074599-46dc-4ac9-8087-c730c6a241f2", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("f7561f54-a962-4ade-8e3e-1f5b546f7664", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("82c719b2-c9c2-4fe2-8ecb-2f4c9bbfc671", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("895ba986-c839-4157-8f85-ce42c65153d1", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("67eeda7d-bd87-4938-af2d-c6da39c1e3d8", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("135aea45-62c5-4bfc-8d81-f162b2f46c54", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("ea23a2ae-bc12-4f7d-8e8d-3f3d2deaebed", "a05ddfe0-0c41-4f9f-b0d7-4ba3f0e61bc1"),
            Map.entry("53d5d55d-8999-4bee-aa4b-cee87643e8b4", "a05ddfe0-0c41-4f9f-b0d7-4ba3f0e61bc1"),
            Map.entry("aa0dd981-a489-4d4d-83aa-61122f62ef5b", "a05ddfe0-0c41-4f9f-b0d7-4ba3f0e61bc1"),
            Map.entry("2d6b24eb-3f06-478b-9d35-9fe50a044a82", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("e34dbda6-c221-4dda-822e-7bf1ddd90bcb", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("3b66b278-7266-4634-b833-9bebf675ff2d", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("2bf19ce1-2bed-4e25-81af-1cdd0e851b99", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("0585357a-945e-4644-b9d2-b32c2d0228c4", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("3c8f720a-b51c-4cbc-b93a-5a87b6041934", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("ab885354-7b97-4188-aaaa-b6e9c945c93e", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("2b4c279b-0bcb-4521-8639-7f1c27029906", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("8a06d645-f6f3-4b85-b711-182c6a246030", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("e1a431cc-5b65-487a-9547-a41f7b76f1e8", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("f8d50ebc-6e3f-49ed-ae04-76886af4588d", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("273a383f-c1aa-4bb2-a5a2-da8a2a4dc889", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("db845d01-9e16-498b-910a-145bde692071", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("e43f211c-2531-47ae-beb5-c5805bc2c0f1", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("44601d3a-fc54-410b-b3cc-e7374f6e51c6", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("44a2557b-7141-49ab-bc47-665a8df75c4a", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("3527b657-845f-4d8a-94e7-173db9c2f7cc", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("d4eb2403-81cb-4047-b11d-90d9fe3d9317", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("4895b48b-24c1-4872-bad2-d8a80db1fbcc", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("0f6d5e2c-4ce9-450f-ac7c-f5eff0f820df", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("bb7afe54-2cc6-459b-86db-be751e8778fa", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("2808254b-2e02-4916-9fa2-a3195160c8fd", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("6a373b49-3953-4787-af3d-a98ad7887483", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("813922c8-a1dc-4fa8-a5fc-9ece789692f3", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("dc6053c1-a3fc-439b-9064-dc110373c1de", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("42222e1e-8d69-4a2f-b01d-c2162e4278b1", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("9a2fd733-c75a-4684-a108-30202548c9df", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("fd988edb-bbc6-4789-a92b-5f73e1c8e4d7", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("4fbef814-2818-4460-a48e-08f1a0f1d94b", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("753a69ee-a748-4ed7-9093-461cd1d1bdbc", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("462c95df-e8b2-41c9-aede-a18b6fa87cb2", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("05dbfc76-3df0-462c-93bc-7b38d907285c", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("9babc2a2-4721-4566-a8b3-23d830530e26", "a6d3b095-fd28-45e8-b751-75bf1da0a0f7"),
            Map.entry("bb160d15-4b4c-4cb0-a6ed-16e1b833ba3d", "a6d3b095-fd28-45e8-b751-75bf1da0a0f7"),
            Map.entry("07237b9f-52ce-4971-9f9b-91eb0a541b60", "a6d3b095-fd28-45e8-b751-75bf1da0a0f7"),
            Map.entry("e08f947c-eb38-4c06-8395-b241f273e45e", "a6d3b095-fd28-45e8-b751-75bf1da0a0f7"),
            Map.entry("6fc94ce2-5c49-4774-82f6-6d85ad1f1bf7", "a6d3b095-fd28-45e8-b751-75bf1da0a0f7"),
            Map.entry("28ffbba3-d9e7-4ef7-bfec-20cce915bd99", "a6d3b095-fd28-45e8-b751-75bf1da0a0f7"),
            Map.entry("9c6fcdcf-cc37-4d71-b332-fa6f3f861e91", "a6d3b095-fd28-45e8-b751-75bf1da0a0f7"),
            Map.entry("8804499a-7084-4269-999d-82f31991aa74", "a6d3b095-fd28-45e8-b751-75bf1da0a0f7"),
            Map.entry("af299475-bffd-4f3e-b7fa-2f530e802171", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("93aa3914-e5d9-4ffe-ac2d-72bd85727c09", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("152909dc-dc93-45c7-87aa-725dedfd2d36", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("0657bfa8-8a36-42af-b5b4-414ec6ea0f6b", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("7ed44b39-d791-435d-b01a-24eb10788dee", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("2707c92b-f300-4263-95ba-bed882df8d27", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("a1e395d6-ad6a-448d-b61a-7e697cec6fc2", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("93f3444a-1173-423f-a5d3-7610872f8b34", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("068e4b7d-afc3-4681-b958-3f28b9985fe5", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("d250c3a4-7981-4bf7-b128-88f67c932f30", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("24d7d829-8769-4dba-97d4-01c9efd56e97", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("f9d1ac5e-0e16-4aa8-9f2b-6525f9b90a22", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("64dd6317-bf81-4744-ac45-274105711159", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("2a99a8e7-a62a-42e4-8ec6-556ed1692912", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("5f63be2c-19b1-4940-b492-f834708fdc92", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("19fa65d0-07a9-4538-b5f6-426b99c2ee74", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("0f014161-c5e1-4740-a68e-17531fef5a2e", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("73f56547-4ee8-4740-8a7f-7cda6bc1b2a5", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("4978b1b1-4947-4db7-b3bf-da89ec99e4ec", "2c92873a-9225-4774-b8be-263482f1a7b4"),
            Map.entry("da017418-9c64-4f0b-ab0b-ed91879dde61", "2c92873a-9225-4774-b8be-263482f1a7b4"),
            Map.entry("559a85bf-5cf8-4627-9e30-a2e5b0331981", "2c92873a-9225-4774-b8be-263482f1a7b4"),
            Map.entry("4d61204c-df2f-4bb9-b8ca-fcd0d48eb197", "2c92873a-9225-4774-b8be-263482f1a7b4"),
            Map.entry("65a7dcf3-7439-4582-91b7-d97a89a37540", "2c92873a-9225-4774-b8be-263482f1a7b4"),
            Map.entry("46ec4a36-cfec-42a7-b0c6-0ce815d1ce22", "2c92873a-9225-4774-b8be-263482f1a7b4"),
            Map.entry("0d7d339a-00c5-44c6-b0f4-d6c634a887da", "2c92873a-9225-4774-b8be-263482f1a7b4"),
            Map.entry("9ef873c7-35e6-4208-b541-e7fcc86490ff", "2c92873a-9225-4774-b8be-263482f1a7b4"),
            Map.entry("3d00f2fc-9fa3-4fd6-bce4-8be603ca5c37", "2c92873a-9225-4774-b8be-263482f1a7b4"),
            Map.entry("e3044ee1-d666-4fc5-8d31-1dcdafc4f85a", "2c92873a-9225-4774-b8be-263482f1a7b4"),
            Map.entry("5bf8e6df-7eba-4958-8cee-e34dd56fba9e", "2c92873a-9225-4774-b8be-263482f1a7b4"),
            Map.entry("9db63908-83ad-4125-b5b0-539015ef689e", "2c92873a-9225-4774-b8be-263482f1a7b4"),
            Map.entry("b86f36f1-aeb5-4c43-887d-13101241ccd9", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("c24a391a-6d6d-408e-994b-b8f35af718ea", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("c739e42b-308d-47ee-9a06-0c2241e5da18", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("43e8be98-1bde-4af9-b470-9b759731712d", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("b01f0958-1765-4389-92c7-82dc75f5c0e6", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("3f027298-6521-44f6-9699-9b9a53794e06", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("1dc38e3e-1055-4252-8359-fac65c8e779b", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("eff8d319-935e-40be-a8ab-e5681435fb81", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("4fd89c4d-1555-4e04-97c7-314b08d4453f", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("97fc7397-2d30-432f-ac42-157f7071a054", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("b5ed86c9-792e-4395-950d-32e50195c3db", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("98cadd31-6ff4-4be1-9595-f8b7625cca43", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("6bf8420c-7dd8-4ae8-9194-6b9cd02e39f7", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("d8c8d9c5-7645-4932-8de0-90f6c43a4f44", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("9e1ec3ec-2bb5-43c4-8197-1f416869f936", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("18a2920e-6973-4547-bbd0-94207e3156fd", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("f86610d1-897c-4f11-b750-5fb1d9a29429", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("a50c5f74-efda-4cdb-8221-984ac88d28a2", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("b730779b-a473-4fa1-ac6d-361af4e8d5ca", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("8b527223-42a8-47e1-9047-138dd732de8a", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("5cd9196e-9695-437d-818f-4c1f94b33363", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("578e0fab-7740-420d-9ce9-96fe10e8cc70", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("f29af709-b683-46ef-80d6-875557c722ae", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("3af7f017-cbfa-4357-80d6-1d06c88e5532", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("e876a608-2ff0-41be-b954-56d6375755ac", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("7b584c3c-f84b-4cc7-b0e0-06b1ff224df0", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("02227e73-1988-4914-a155-92b1e0828889", "afee7042-0263-4674-9e27-49ca952b14ea")
    );

    private static final Map<String, String> CONCEPT_TO_TOPIC_GROUP = Map.ofEntries(
            Map.entry("56eb5d77-4969-4a2c-917e-e96e56e67054", "700913a2-62c9-41d9-9011-1a16aea0f0db"),
            Map.entry("c2e521d9-6489-4d42-be76-c1682ba9af74", "7e30edfe-5ae3-45f9-8470-bb8002ecb705"),
            Map.entry("2d434129-6a91-4033-9461-23cd5ec94ca9", "7e30edfe-5ae3-45f9-8470-bb8002ecb705"),
            Map.entry("1b2416a5-4b5d-401d-941d-5f89de7df7a1", "7e30edfe-5ae3-45f9-8470-bb8002ecb705"),
            Map.entry("952b9840-d6e2-45af-abd0-e26b8beefece", "7e30edfe-5ae3-45f9-8470-bb8002ecb705"),
            Map.entry("62ecbb19-0e3a-4cf2-a6d8-872dc41530fa", "7e30edfe-5ae3-45f9-8470-bb8002ecb705"),
            Map.entry("e929a269-f1d8-4e88-abd4-be019683f5cd", "7e30edfe-5ae3-45f9-8470-bb8002ecb705"),
            Map.entry("7036ef5d-f4b5-4e22-998a-e7ad50d87cf5", "7e30edfe-5ae3-45f9-8470-bb8002ecb705"),
            Map.entry("10a5e74c-1ef6-42a7-b822-81f7aa2e12d5", "7e30edfe-5ae3-45f9-8470-bb8002ecb705"),
            Map.entry("6cd0a479-d7fe-4aff-9cc8-68bb6b8f673f", "7e30edfe-5ae3-45f9-8470-bb8002ecb705"),
            Map.entry("b8e5a815-affe-471e-ae55-979ee5c7af11", "7e30edfe-5ae3-45f9-8470-bb8002ecb705"),
            Map.entry("9ded81b3-9be0-46d6-8d9e-4a3ed358da2b", "d5529814-3067-4264-a0f9-7036afb28071"),
            Map.entry("8c0b258f-03a9-40a4-b3f1-4d893b889978", "d5529814-3067-4264-a0f9-7036afb28071"),
            Map.entry("6a7419c6-c83a-465c-abb9-d0070abbf3d5", "d5529814-3067-4264-a0f9-7036afb28071"),
            Map.entry("2da88375-4460-49d3-88e9-05d167b1ebc2", "d5529814-3067-4264-a0f9-7036afb28071"),
            Map.entry("c1a64943-5cdf-404f-9f7a-a97787ff66e9", "af1822e6-89c6-4555-a111-b62f3ae42b5d"),
            Map.entry("93d152ec-1501-4812-a779-e954b8881a6a", "af1822e6-89c6-4555-a111-b62f3ae42b5d"),
            Map.entry("a79a53fb-517d-4a58-a657-35a027f3bf3c", "af1822e6-89c6-4555-a111-b62f3ae42b5d"),
            Map.entry("edb5c004-e080-4419-ac04-cd0955ea9869", "af1822e6-89c6-4555-a111-b62f3ae42b5d"),
            Map.entry("cb98ac6e-ad03-425e-9084-d33e643c0299", "af1822e6-89c6-4555-a111-b62f3ae42b5d"),
            Map.entry("76fdb23a-def4-4871-b07a-b403045722c1", "02727ef0-03af-4891-9328-9ef102ea0d61"),
            Map.entry("848ab468-7878-44c3-9ea6-aa28c823a755", "02727ef0-03af-4891-9328-9ef102ea0d61"),
            Map.entry("a2d787ff-7518-455e-becc-c2b0a59e1ab4", "63cb1a12-17da-49b3-afff-842a349715b8"),
            Map.entry("6dba1518-a754-465f-9070-f012382f82bb", "63cb1a12-17da-49b3-afff-842a349715b8"),
            Map.entry("65dff035-ca97-46e6-abbd-dbfd4adba79b", "63cb1a12-17da-49b3-afff-842a349715b8"),
            Map.entry("21858ff6-9a6a-4b21-90e4-feb0a4da1f0c", "63cb1a12-17da-49b3-afff-842a349715b8"),
            Map.entry("f7fd83b2-f3d5-4b2e-ad68-79b26274a8ca", "63cb1a12-17da-49b3-afff-842a349715b8"),
            Map.entry("da716ef7-f6a6-47d1-9ac5-f29ece2a9a1d", "a99f3a29-887f-4cf6-aa6c-ba5d93eb36d8"),
            Map.entry("bed342e9-81c2-4dab-b5fd-7b7f3d03b24c", "a99f3a29-887f-4cf6-aa6c-ba5d93eb36d8"),
            Map.entry("964248ff-3169-45c4-9999-d83ba66ae00c", "a99f3a29-887f-4cf6-aa6c-ba5d93eb36d8"),
            Map.entry("a7a31496-ca13-4d63-b25f-8dafce7e1eea", "a99f3a29-887f-4cf6-aa6c-ba5d93eb36d8"),
            Map.entry("990dfee2-0958-4549-be18-65493728b609", "a99f3a29-887f-4cf6-aa6c-ba5d93eb36d8"),
            Map.entry("2541966a-aa99-4040-b274-8fcc374410a7", "4e586a9d-4bdf-4c14-9346-a8a1b2779882"),
            Map.entry("d6d7663b-b795-4ccd-9b5a-0e90331557d2", "4e586a9d-4bdf-4c14-9346-a8a1b2779882"),
            Map.entry("34b5131a-c240-40fb-8b66-90f7694b2591", "4e586a9d-4bdf-4c14-9346-a8a1b2779882"),
            Map.entry("d308b6fe-699a-4e03-81d3-2c9b5ab28f20", "4e586a9d-4bdf-4c14-9346-a8a1b2779882"),
            Map.entry("3d05f898-4777-4a24-b86c-29697a6f16ea", "4e586a9d-4bdf-4c14-9346-a8a1b2779882")
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
                    handleGroup(resource, uuidKey, CONCEPT_TO_COMMODITY_GROUP, inCommodityGroup, isCommodityGroup, Set.of());
                    handleGroup(resource, uuidKey, CONCEPT_TO_TOPIC_GROUP, inTopicGroup, isTopicGroup, ADDITIONAL_TOPIC_GROUPS);
                    handleGroup(resource, uuidKey, CONCEPT_TO_REGION_GROUP, inRegionGroup, isRegionGroup, ADDITIONAL_REGION_GROUPS);
                });
            }

            isInRecursiveCall.set(false);
        }
    }

    private void handleGroup(Resource resource, String uuid, Map<String, String> grouping, Property groupProperty, Property inverseProperty, Set<String> additionalGroupIds) {
        if (grouping.containsKey(uuid) && !grouping.containsValue(uuid)) {
            String groupId = grouping.get(uuid);
            Model model = resource.getModel();
            UUID groupUuid = UUID.fromString(groupId);
            String groupUri = conceptIdResolver.resolve(groupUuid).toString();
            Resource groupResourceRef = ResourceFactory.createResource(groupUri);

            if (!model.containsResource(groupResourceRef)) {
                concepts.find(groupUuid)
                        .ifPresent(groupConcept -> model.add(groupConcept.getResource().getModel()));
            }

            resource.addProperty(groupProperty, model.createResource(groupUri));
        } else if (grouping.containsValue(uuid)) {
            resource.addLiteral(inverseProperty, true);
        }

        if (additionalGroupIds.contains(uuid)) {
            resource.addLiteral(inverseProperty, true);
        }
    }
}
