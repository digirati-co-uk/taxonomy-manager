package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.common.taxonomy.ConceptRelationshipType;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRelationshipRecord;
import com.digirati.taxman.rest.server.testing.DatabaseTestExtension;
import com.digirati.taxman.rest.server.testing.annotation.TestDataSource;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;


@ExtendWith(DatabaseTestExtension.class)
@Tag("integration")
public class ConceptDaoTests {

    @TestDataSource
    DataSource dataSource;

    private static Arguments labelPropertiesArgs(String name, LabelSetter setter, LabelGetter getter) {
        return Arguments.of(name, setter, getter);
    }

    private static Stream<Arguments> labelPropertiesProvider() {
        return Stream.of(
                labelPropertiesArgs("preferredLabel", ConceptRecord::setPreferredLabel, ConceptRecord::getPreferredLabel),
                labelPropertiesArgs("altLabel", ConceptRecord::setAltLabel, ConceptRecord::getAltLabel),
                labelPropertiesArgs("hiddenLabel", ConceptRecord::setHiddenLabel, ConceptRecord::getHiddenLabel),
                labelPropertiesArgs("note", ConceptRecord::setNote, ConceptRecord::getNote),
                labelPropertiesArgs("changeNote", ConceptRecord::setChangeNote, ConceptRecord::getChangeNote),
                labelPropertiesArgs("editorialNote", ConceptRecord::setEditorialNote, ConceptRecord::getEditorialNote),
                labelPropertiesArgs("example", ConceptRecord::setExample, ConceptRecord::getExample),
                labelPropertiesArgs("historyNote", ConceptRecord::setHistoryNote, ConceptRecord::getHistoryNote),
                labelPropertiesArgs("scopeNote", ConceptRecord::setScopeNote, ConceptRecord::getScopeNote)
        );
    }

    @MethodSource("labelPropertiesProvider")
    @ParameterizedTest(name = "{index} - {0}")
    public void shouldStoreLabels(@SuppressWarnings("unused") String name, LabelSetter setter, LabelGetter getter) {
        var dao = new ConceptDao(dataSource);
        var uuid = UUID.fromString("3828f4e5-ad0d-402c-978a-e2b9939332c7");

        var record = new ConceptRecord(uuid);
        var expectedLabels = ArrayListMultimap.<String, String>create();
        expectedLabels.put("en", "value");

        setter.setLabels(record, expectedLabels);
        dao.storeDataSet(new ConceptDataSet(record));

        var storedRecord = dao.loadDataSet(uuid).getRecord();
        var storedLabels = getter.getLabels(storedRecord);

        Assertions.assertEquals(expectedLabels, storedLabels);
    }

    @MethodSource("labelPropertiesProvider")
    @ParameterizedTest(name = "{index} - {0}")
    public void shouldUpdateLabels(@SuppressWarnings("unused") String name, LabelSetter setter, LabelGetter getter) {
        var dao = new ConceptDao(dataSource);
        var uuid = UUID.fromString("3828f4e5-ad0d-402c-978a-e2b9939332c7");
        var record = new ConceptRecord(uuid);
        var label = ArrayListMultimap.<String, String>create();
        label.put("en", "value");

        setter.setLabels(record, label);

        dao.storeDataSet(new ConceptDataSet(record));

        var expectedLabels = ArrayListMultimap.<String, String>create();
        expectedLabels.put("en", "value");
        expectedLabels.put("fr", "valeur");
        setter.setLabels(record, expectedLabels);

        dao.storeDataSet(new ConceptDataSet(record));

        var storedRecord = dao.loadDataSet(uuid).getRecord();
        var storedLabels = getter.getLabels(storedRecord);

        Assertions.assertEquals(expectedLabels, storedLabels);
    }

    @Test
    public void storeDataSet_shouldStoreRelationships() {
        var dao = new ConceptDao(dataSource);

        var uuidA = UUID.fromString("3828f4e5-ad0d-402c-978a-e2b9939332c7");
        var uuidB = UUID.fromString("f0ea2717-1114-46f4-bc51-a25985571a01");
        var relationships = List.of(new ConceptRelationshipRecord(uuidA, uuidB, null, ConceptRelationshipType.BROADER, false));

        dao.storeDataSet(new ConceptDataSet(new ConceptRecord(uuidB)));
        dao.storeDataSet(new ConceptDataSet(new ConceptRecord(uuidA), relationships));

        Assertions.assertEquals(relationships, dao.loadDataSet(uuidA).getRelationshipRecords());
    }

    @Test
    public void search_ShouldRetrieveConceptsByPartialLabel() {
        var one = new ConceptRecord(UUID.randomUUID());
        one.getPreferredLabel().put("en", "one");
        one.getPreferredLabel().put("fr", "un");

        var two = new ConceptRecord(UUID.randomUUID());
        two.getPreferredLabel().put("en", "two");

        var eleven = new ConceptRecord(UUID.randomUUID());
        eleven.getPreferredLabel().put("en", "eleven");
        eleven.getAltLabel().put("fr", "onze");

        var hundred = new ConceptRecord(UUID.randomUUID());
        hundred.getPreferredLabel().put("en", "a hundred");
        hundred.getHiddenLabel().put("en", "one hundred");

        var dao = new ConceptDao(dataSource);
        dao.storeDataSet(new ConceptDataSet(one));
        dao.storeDataSet(new ConceptDataSet(two));
        dao.storeDataSet(new ConceptDataSet(eleven));
        dao.storeDataSet(new ConceptDataSet(hundred));

        var expected = List.of(one, hundred);
        var actual = dao.getConceptsByPartialLabel("on", "en");

        Assertions.assertEquals(expected, actual);
    }

    @FunctionalInterface
    private interface LabelGetter {
        Multimap<String, String> getLabels(ConceptRecord record);
    }

    @FunctionalInterface
    private interface LabelSetter {
        void setLabels(ConceptRecord record, Multimap<String, String> labels);
    }
}
