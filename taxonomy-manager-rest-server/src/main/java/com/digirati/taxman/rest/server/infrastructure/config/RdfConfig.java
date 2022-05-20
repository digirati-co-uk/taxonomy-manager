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
            Map.entry("b6dffb2d-3bd8-49b3-9c38-08314e7049f8", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("436a13ad-9213-40b1-a64a-190d82986975", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("9745142d-38b0-4550-a8a0-e8f6aeaa58a6", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("b0e95e29-6d69-4510-ab2a-29922f9f3523", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("463fc913-62b3-4fa0-8bce-57dc8ec9fee3", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("362120b5-43fd-4635-a7b1-59c041f0e8b9", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("e08747ae-1c2e-4ddc-ba6b-cd1e2c10b88e", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("310acf91-90ee-4499-8641-358e8695fdcd", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("ca4b8145-758a-42e9-be27-c7ec4c8679c8", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("1132517f-c20c-4d3f-a181-49dc5e0b107a", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("55159a95-8b4d-47c9-9ebf-cef482fd329f", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("0200f598-8922-4ee6-a79b-d2462bf2b548", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("ececb7fb-0630-4bfd-9feb-b99c8b0b6d8c", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("8434e99f-d5e7-4883-83b2-16d9456de0e5", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("a6e519aa-cdf6-4e18-a4f9-bc9b6322cfbd", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("846fafd3-9539-4893-97f5-6c86a4ad7d0c", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("31cdd75a-1a28-4474-8a8f-80ec23795a47", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("02fcc7ca-8372-4060-a3b5-2ebdfe35e00d", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("ea502395-3336-401d-8063-61d1871d68e1", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("51073cf2-0e78-431e-9786-18f15bc1400d", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("e183ec58-a607-4250-9464-f7120ab0716d", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("10640b8a-b363-4253-aa95-e87954dad471", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("437216ca-cb9d-439c-9f69-8eba5712c7d1", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("bd4cbeea-8419-4869-99ec-cc8f7f289352", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("9242e7ae-cedc-46f1-ba3f-5c430fd766b5", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("bae55d4a-5b50-4be9-b0b4-237d944969df", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("43e153a3-f5a1-40c9-9b05-8b025eebb568", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("80824d71-2c33-4c72-9e85-392811fee13e", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("9f1c0ef4-97df-4a3d-b9ca-ce96b57f1e77", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("59b92b89-13c1-4ce7-be19-074696fcd4a7", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("ec11ec06-a6ea-4354-a4ee-a3ed8527bae7", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("163c2ba0-a821-4c04-8dde-7bbd395b6723", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("b7a60ec1-12a3-4094-95dc-a18a318f9a76", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("a515b7c9-7ab7-4d14-b4e6-b2125e0e250a", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("b591b90a-5adc-4165-a323-ae8845b36718", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("c44c3263-6517-46c3-b2d9-e015091b40bd", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("40013e00-3f6d-47df-83e6-d149b217449f", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("c415c592-2079-4cd5-8b2a-73aac5779c5c", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("94ed9f31-f2b9-45fb-9a00-b89df54bc121", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("7bd12503-edb0-4647-835f-afea0e8e45c9", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("e680945d-23d1-4add-8808-17fa59c01468", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("0327ed33-7ecd-4da0-bce3-a35653f01ae0", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("3a814539-efb9-4999-a4b2-6b251db9ab88", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("a4335f54-da0a-4e1e-9c88-f2318ad21405", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("4307db6f-5a8d-405b-add7-82c062164835", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("a1d34f06-0308-4a27-a563-a45a52cfb9ed", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("ed56ec22-26b6-4fac-8cc7-cfdf7e23fdcb", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("b9d443ce-f458-411e-af52-0c461a63a89a", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("47f42c80-7289-47e2-a10e-648d5dff604e", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("84afe0a2-49ff-40a1-bd6d-ed3824615747", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("92dd3c5c-1480-4d2b-9356-a7df06579168", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("b8148b7c-0942-4a93-b2ca-3b58ee025ded", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("1a134e68-e0b0-4748-911f-b2d383e62c39", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("eb8fb49b-dda2-4b25-9199-0400fd164034", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("4f87fd50-bb9d-432d-aee0-a13166de22e8", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("d886800d-3ef6-4046-91a9-fca08999fcee", "69b1198a-95dc-46fc-8879-52fb9a7d8095"),
            Map.entry("c4c11ef0-5d88-424a-8ed4-85911da1f2dd", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("f3cf1665-3503-4023-975e-83d288d55522", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("ad9ffb07-44ce-4f8c-8f5a-1f9533b14b27", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("6ff251ee-ca93-4a71-a3c3-871df83131c6", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("b407e6b4-8344-4c99-a3ba-573709d19a58", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("552b6e89-4550-45ac-8ce2-274dd17505b4", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("81c047ce-d83a-407e-829d-9d48cf3a327c", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("9fa7f15d-cbca-4169-b290-ee80da651dd2", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("c54461af-9cd0-492f-bcb1-3e365fd04136", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("3b948629-9e16-49df-915f-cf8055523f4e", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("b280b104-7a63-44c0-b127-cb87752a4a6e", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("a88f7269-6df3-41a1-95bf-5e9cb6a385fd", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("425e7588-b528-49aa-a628-2e93bfccc5c3", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("7bf3a940-f73e-4f20-82f8-9f575c783bfc", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("c68a0bf9-c1fd-45a6-8473-5a1d6483dc9f", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("4ac9f679-54c1-42ae-8d7c-5c6a05cd23d5", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("118ac75b-a2ca-433e-8adb-de20d33a8046", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("ff374813-9e35-4a85-b14d-096a7887292d", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("503631e6-b713-4cbc-96ae-627d4a44519b", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("89343099-0864-42fa-91c0-2034201b621c", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("8fcb5792-8695-45bd-b225-bf543af314df", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("a5410520-cdd3-4421-971e-c6a4d04e9db2", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("1ee7f68e-c412-4c18-9b30-28a8e82e7c49", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("8824dee9-2797-4ccb-bcba-c4d12b926e58", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("b2404ad2-8142-46ca-8342-068aff37bacc", "84b2461c-b1a6-4869-82e9-a75776d3bc04"),
            Map.entry("1b26a114-fefa-4c6e-9877-fd918390650e", "580a27f3-1538-4caa-a4ad-e07807f95f07")
    );

    private static final Map<String, String> CONCEPT_TO_TOPIC_GROUP = Map.ofEntries(
            Map.entry("cb98ac6e-ad03-425e-9084-d33e643c0299", "af1822e6-89c6-4555-a111-b62f3ae42b5d"),
            Map.entry("edb5c004-e080-4419-ac04-cd0955ea9869", "af1822e6-89c6-4555-a111-b62f3ae42b5d"),
            Map.entry("a79a53fb-517d-4a58-a657-35a027f3bf3c", "af1822e6-89c6-4555-a111-b62f3ae42b5d"),
            Map.entry("59d533b6-22e7-4f73-9eec-1f5ff01ba0ef", "af1822e6-89c6-4555-a111-b62f3ae42b5d"),
            Map.entry("77a2d771-e741-4ba9-9fdc-5796dd430ac9", "af1822e6-89c6-4555-a111-b62f3ae42b5d"),
            Map.entry("365e395d-76cf-4fa3-aa28-ed32be087825", "02727ef0-03af-4891-9328-9ef102ea0d61"),
            Map.entry("c26e3e2b-12e5-46fa-aeb5-fba8d9f76e0b", "02727ef0-03af-4891-9328-9ef102ea0d61"),
            Map.entry("da46d791-3933-47dc-af74-bb1066483c54", "d5529814-3067-4264-a0f9-7036afb28071"),
            Map.entry("da5f4762-eb4d-4a43-9a92-a68b42fd85ed", "d5529814-3067-4264-a0f9-7036afb28071"),
            Map.entry("b1cd9724-e148-45b8-be66-469f691735eb", "d5529814-3067-4264-a0f9-7036afb28071"),
            Map.entry("774c3a96-911e-4874-b139-71240970b179", "d5529814-3067-4264-a0f9-7036afb28071"),
            Map.entry("f7fd83b2-f3d5-4b2e-ad68-79b26274a8ca", "63cb1a12-17da-49b3-afff-842a349715b8"),
            Map.entry("137ccbd4-ac91-411f-9c14-c525c5d0b709", "63cb1a12-17da-49b3-afff-842a349715b8"),
            Map.entry("c72ad343-4af2-4125-b84c-4285ca9526b4", "63cb1a12-17da-49b3-afff-842a349715b8"),
            Map.entry("0db990c0-ecf8-467d-98bd-ba7a62a57223", "63cb1a12-17da-49b3-afff-842a349715b8"),
            Map.entry("61fda965-158b-4fe3-a138-9a46cac2aa32", "63cb1a12-17da-49b3-afff-842a349715b8"),
            Map.entry("3d05f898-4777-4a24-b86c-29697a6f16ea", "4e586a9d-4bdf-4c14-9346-a8a1b2779882"),
            Map.entry("859a67ed-24ae-46b9-b18d-8526868b2193", "4e586a9d-4bdf-4c14-9346-a8a1b2779882"),
            Map.entry("01772c23-40bf-45d3-aab3-f56e7cca1e0c", "4e586a9d-4bdf-4c14-9346-a8a1b2779882"),
            Map.entry("bf1df009-f486-473c-ba30-56404903dd41", "4e586a9d-4bdf-4c14-9346-a8a1b2779882"),
            Map.entry("46226672-742d-45bc-a288-fc2262ba0a10", "4e586a9d-4bdf-4c14-9346-a8a1b2779882"),
            Map.entry("3ed0d673-1040-449b-a553-f8fb938a86f3", "1564b19a-053f-48e7-bd0e-fd61ded61b78"),
            Map.entry("6e614cf5-7031-413b-819f-3a81eb07e478", "a92c89ff-1d42-4c58-acf0-50370f706388"),
            Map.entry("cfe6b5d2-575c-4e1d-aaf8-824380d20f14", "3c42ecb8-4871-4091-b1f1-d31598335fa8"),
            Map.entry("b4b9b4bb-cff0-423d-9297-6b822166a76a", "3c42ecb8-4871-4091-b1f1-d31598335fa8"),
            Map.entry("aa544db1-8957-437b-9496-c7ca5d1b0c9d", "3c42ecb8-4871-4091-b1f1-d31598335fa8"),
            Map.entry("5f94f4e0-d94b-41d5-aa03-54420cb0084f", "3c42ecb8-4871-4091-b1f1-d31598335fa8"),
            Map.entry("5d564ee5-243d-4339-a0ea-78a524b54904", "3c42ecb8-4871-4091-b1f1-d31598335fa8"),
            Map.entry("62ecbb19-0e3a-4cf2-a6d8-872dc41530fa", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("e929a269-f1d8-4e88-abd4-be019683f5cd", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("10a5e74c-1ef6-42a7-b822-81f7aa2e12d5", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("c2e521d9-6489-4d42-be76-c1682ba9af74", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("2d434129-6a91-4033-9461-23cd5ec94ca9", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("b8e5a815-affe-471e-ae55-979ee5c7af11", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("1b2416a5-4b5d-401d-941d-5f89de7df7a1", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("01f9e230-8ee6-4183-8b67-ddc351bfb5b6", "631d4525-55e6-4ecb-9aef-0b7c775232ca"),
            Map.entry("35825d8a-6bc3-48fb-ae7a-997e0763ea94", "631d4525-55e6-4ecb-9aef-0b7c775232ca")
    );

    private static final Map<String, String> CONCEPT_TO_COMMODITY_GROUP = Map.<String, String>ofEntries(
            Map.entry("24d7d829-8769-4dba-97d4-01c9efd56e97", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("2a99a8e7-a62a-42e4-8ec6-556ed1692912", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("0f014161-c5e1-4740-a68e-17531fef5a2e", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("1be785f8-5aaa-45d3-a66b-88f501c5eca9", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("a59d031f-4062-4129-9c6d-5fa8fc90c0a8", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("9a069274-51e9-4978-865a-4c41fda46099", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("ad2dd6e9-2cd8-44b0-a90f-d124cf591c53", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("3ccefd21-8a26-4fce-81f6-8c3f9eeec8b1", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("63203942-0f75-4158-8452-19628052322c", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("fffa8784-9607-4370-988c-f194764e5470", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("533f9e63-ca2a-475a-8c22-a52d2a54a9d5", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("adf3e6a6-25da-4ef0-ab66-5f4c425b08c8", "afee7042-0263-4674-9e27-49ca952b14ea"),
            Map.entry("a1e395d6-ad6a-448d-b61a-7e697cec6fc2", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("2707c92b-f300-4263-95ba-bed882df8d27", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("0657bfa8-8a36-42af-b5b4-414ec6ea0f6b", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("152909dc-dc93-45c7-87aa-725dedfd2d36", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("93aa3914-e5d9-4ffe-ac2d-72bd85727c09", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("9f5659fb-b13f-4194-82ca-03c12caf1729", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("7ae34142-4467-4b27-9755-b25b41ab538f", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("a83e11a2-3d5d-4d6f-b9db-a1bd4d0589a9", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("5cd9196e-9695-437d-818f-4c1f94b33363", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("9ade1882-574b-4998-96b2-b60b08ba9be1", "6051c49f-56a8-41d0-a250-a4cb8c3f4084"),
            Map.entry("09e23659-d6a6-4fb6-9715-8765219ea455", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("f7561f54-a962-4ade-8e3e-1f5b546f7664", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("67eeda7d-bd87-4938-af2d-c6da39c1e3d8", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("009b52a6-fc3f-47e9-8c82-b1fab17303f7", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("5e4ad9ec-0328-469a-a257-0035e735e381", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("ec074599-46dc-4ac9-8087-c730c6a241f2", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("24382d8f-9dff-486c-8f47-703746ff5ae7", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("53a8fca4-3da1-4205-9282-eaa59bfb8553", "7ed44b39-d791-435d-b01a-24eb10788dee"),
            Map.entry("f29af709-b683-46ef-80d6-875557c722ae", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("e876a608-2ff0-41be-b954-56d6375755ac", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("578e0fab-7740-420d-9ce9-96fe10e8cc70", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("2bf19ce1-2bed-4e25-81af-1cdd0e851b99", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("e34dbda6-c221-4dda-822e-7bf1ddd90bcb", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("ce7c3caf-bb74-4eae-9499-74863be22614", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("c9af9aa7-db38-40e9-bcf0-32cc70814e8e", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("90297e9c-3517-4f37-b87e-6c6d6ef94c52", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("85d084ef-7ff3-4f67-b790-6cc37b8ff972", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("08a2045a-133b-474f-adee-803735503c88", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("e015737e-9e0e-41bd-8a6c-5289147208c2", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("43f75d64-9e53-4324-8c06-f590729590c5", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("79d4fab5-4fb5-493a-8701-3ec79520a483", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("6ae5a196-92f9-4eda-9dfa-43409e37a184", "a962a29a-e4d6-40c5-8bde-2a549d292e8a"),
            Map.entry("9f953234-fa55-4b79-91b5-f85e752858b6", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("9e1ec3ec-2bb5-43c4-8197-1f416869f936", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("98cadd31-6ff4-4be1-9595-f8b7625cca43", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("d8c8d9c5-7645-4932-8de0-90f6c43a4f44", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("b5ed86c9-792e-4395-950d-32e50195c3db", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("e6a9bda9-1e0f-4734-a3f3-43f921c70eb8", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("813922c8-a1dc-4fa8-a5fc-9ece789692f3", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("bb7afe54-2cc6-459b-86db-be751e8778fa", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("05dbfc76-3df0-462c-93bc-7b38d907285c", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("d68e6c53-85b1-4aa5-80fe-9838519cbc47", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("2808254b-2e02-4916-9fa2-a3195160c8fd", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("4fbef814-2818-4460-a48e-08f1a0f1d94b", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("c24a391a-6d6d-408e-994b-b8f35af718ea", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("4895b48b-24c1-4872-bad2-d8a80db1fbcc", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("6bf8420c-7dd8-4ae8-9194-6b9cd02e39f7", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("b86f36f1-aeb5-4c43-887d-13101241ccd9", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("3f027298-6521-44f6-9699-9b9a53794e06", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("c739e42b-308d-47ee-9a06-0c2241e5da18", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("4fd89c4d-1555-4e04-97c7-314b08d4453f", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("c2780bc6-46d2-4c1d-85ca-3d8685521c4d", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("3d80b05e-8b02-46a2-a3c7-53b56275bbb3", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("8ed5be59-6518-4ad0-9509-ab1f801a00e0", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("271a2f7e-91bb-401a-a2f5-f96ff857581b", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("c45a8a93-41e5-40c1-b5cf-0a6bddd993bb", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("26fa29d9-5ef0-4ca0-8396-57a2cedd13a3", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("4687652b-015d-4143-814a-17bd99746eec", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("a88013a0-ba45-4495-8eba-e755170f80ef", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("f56daefb-737a-4219-9c26-dec52c915898", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("93f3cd46-d8dc-4842-855d-a20e2310e2b7", "4bc49c2e-f6f3-4c5c-a7e5-47cd7915e38e"),
            Map.entry("d4eb2403-81cb-4047-b11d-90d9fe3d9317", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("3527b657-845f-4d8a-94e7-173db9c2f7cc", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("44a2557b-7141-49ab-bc47-665a8df75c4a", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("44601d3a-fc54-410b-b3cc-e7374f6e51c6", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("e43f211c-2531-47ae-beb5-c5805bc2c0f1", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("52721b19-5c4f-4d80-bd41-3c1582a5c885", "76081f5b-3c45-4b38-b0c0-56ffef958038"),
            Map.entry("0d7d339a-00c5-44c6-b0f4-d6c634a887da", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("46ec4a36-cfec-42a7-b0c6-0ce815d1ce22", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("65a7dcf3-7439-4582-91b7-d97a89a37540", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("4d61204c-df2f-4bb9-b8ca-fcd0d48eb197", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("559a85bf-5cf8-4627-9e30-a2e5b0331981", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("da017418-9c64-4f0b-ab0b-ed91879dde61", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("4978b1b1-4947-4db7-b3bf-da89ec99e4ec", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("b57c8082-3155-41a3-afa2-2a1422eb2334", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("c4f18b5e-c86a-4a74-bdd6-0da1f1498966", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("29d11cb8-2cc5-4195-b3ce-b23bae37d2f9", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("9de0f13a-ef08-4857-8abc-c652a0221f84", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("9db63908-83ad-4125-b5b0-539015ef689e", "3be0f489-5a31-40c7-b97a-0051c55306b5"),
            Map.entry("9babc2a2-4721-4566-a8b3-23d830530e26", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("45ffb098-ebad-4aed-b6bc-030363a453e5", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("bb3e3206-a40f-4c04-a1e0-0641202b0b53", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("0bcd7119-8b96-4986-912f-6b2200e530c8", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("2e8aa8d4-457d-45a6-9b71-315bbcac2465", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("96762cde-dd24-4339-a157-5ab314141524", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("ba3355ac-8e42-40dc-9659-464fb4f86ee2", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("191c66b8-d0ba-43d9-8de2-11866b65fa6f", "dba9c66c-6839-4587-a754-2926608f2af3"),
            Map.entry("aa0dd981-a489-4d4d-83aa-61122f62ef5b", "ee975f52-6db1-4ed4-b1ac-c5b39e0e472c"),
            Map.entry("53d5d55d-8999-4bee-aa4b-cee87643e8b4", "ee975f52-6db1-4ed4-b1ac-c5b39e0e472c"),
            Map.entry("558fdfd7-a1dd-4d36-8268-fcefe167fd9b", "ee975f52-6db1-4ed4-b1ac-c5b39e0e472c"),
            Map.entry("ad482083-7bcf-443e-b8bb-a4bba0a47a47", "ee975f52-6db1-4ed4-b1ac-c5b39e0e472c")

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
