package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptReference;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.mapper.ConceptReferenceMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.record.mapper.ConceptSchemeRecordMapper;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Types;
import java.util.UUID;

public class ConceptSchemeDao {

    private final ConceptSchemeRecordMapper recordMapper = new ConceptSchemeRecordMapper();
    private final ConceptReferenceMapper referenceMapper = new ConceptReferenceMapper();

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public ConceptSchemeDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    /**
     * Given a {@code uuid} of a concept scheme, lookup and find the concept scheme record
     * and all of the top-level concepts.
     *
     * @param uuid The identity of the concept scheme to be looked up.
     * @return A complete {@link ConceptSchemeDataSet} containing a {@link ConceptRecord}
     *     and collection of {@link ConceptReference}s representing top-level concepts.
     */
    public ConceptSchemeDataSet loadDataSet(UUID uuid) {
        Object[] recordArgs = {uuid};
        int[] recordTypes = {Types.OTHER};

        var record = jdbcTemplate.queryForObject("SELECT * FROM get_concept_scheme(?)",
                recordArgs,
                recordTypes,
                recordMapper);

        var relationshipRecords = jdbcTemplate.query(
                "SELECT * FROM get_concept_scheme_top_concepts(?)",
                recordArgs,
                recordTypes,
                referenceMapper);

        return new ConceptSchemeDataSet(record, relationshipRecords);
    }

    /**
     * Store a {@link ConceptSchemeDataSet}, updating or creating the underlying database record
     * and removing/creating any concept relationships that were removed/added from the concept scheme.
     *
     * @param dataset The {@code ConceptSchemeDataSet} to store.
     * @return {@code true} if storing the dataset caused changes in the datastore, {@code false} otherwise.
     */
    public boolean storeDataSet(ConceptSchemeDataSet dataset) {
        ConceptSchemeRecord record = dataset.getRecord();
        UUID uuid = record.getUuid();
        Object[] recordArgs = {uuid, record.getSource(), new JSONObject(record.getTitle())};
        int[] recordTypes = {Types.OTHER, Types.VARCHAR, Types.OTHER};

        boolean changed = jdbcTemplate.update("CALL create_or_update_concept_scheme(?, ?, ?)", recordArgs, recordTypes) > 0;

        UUID[] uuids = dataset.getTopConcepts()
                .stream()
                .map(ConceptReference::getId)
                .toArray(UUID[]::new);

        Array uuidArray = DaoUtils.createArrayOf(uuids, "uuid", dataSource);
        Object[] conceptArgs = {uuid, uuidArray};
        int[] conceptTypes = {Types.OTHER, Types.ARRAY};

        changed |= jdbcTemplate.update("CALL update_concept_scheme_top_concepts(?, ?)", conceptArgs, conceptTypes) > 0;
        return changed;
    }
}
