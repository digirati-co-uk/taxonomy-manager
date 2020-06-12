package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptReference;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.mapper.ConceptRecordMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.record.mapper.ConceptSchemeRecordMapper;
import org.json.JSONArray;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Optional;
import java.util.UUID;

public class ConceptSchemeDao {

    private final ConceptSchemeRecordMapper conceptSchemeRecordMapper = new ConceptSchemeRecordMapper();

    private final JdbcTemplateEx jdbcTemplate;

    private final ConceptRecordMapper conceptRecordMapper = new ConceptRecordMapper();

    public ConceptSchemeDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplateEx(dataSource);
    }

    /**
     * Given a {@code uuid} of a concept scheme, lookup and find the concept scheme record
     * and all of the top-level concepts.
     *
     * @param uuid The identity of the concept scheme to be looked up.
     * @return A complete {@link ConceptSchemeDataSet} containing a {@link ConceptRecord}
     *     and collection of {@link ConceptReference}s representing top-level concepts.
     */
    public Optional<ConceptSchemeDataSet> loadDataSet(UUID uuid) {
        Object[] recordArgs = {uuid};
        int[] recordTypes = {Types.OTHER};

        var record = jdbcTemplate.queryForOptional("SELECT * FROM get_concept_scheme(?)",
                recordArgs,
                recordTypes,
                conceptSchemeRecordMapper);

        if (record.isEmpty()) {
            return Optional.empty();
        }

        var relationshipRecords = jdbcTemplate.query(
                "SELECT * FROM get_concept_scheme_top_concepts(?)",
                recordArgs,
                recordTypes,
                conceptRecordMapper);

        return Optional.of(new ConceptSchemeDataSet(record.get(), relationshipRecords));
    }

    /**
     * Store a {@link ConceptSchemeDataSet}, updating or creating the underlying database record
     * and removing/creating any concept relationships that were removed/added from the concept scheme.
     *
     * @param dataset The {@code ConceptSchemeDataSet} to store.
     * @return {@code true} if storing the dataset caused changes in the datastore, {@code false} otherwise.
     */
    public UUID storeDataSet(ConceptSchemeDataSet dataset) {
        ConceptSchemeRecord record = dataset.getRecord();

        Object[] recordArgs = {record.getUuid(), record.getSource(), DaoUtils.createRdfPlainLiteral(record.getTitle())};
        int[] recordTypes = {Types.OTHER, Types.VARCHAR, Types.OTHER};

        record = jdbcTemplate.queryForObject("SELECT * FROM create_or_update_concept_scheme(?, ?, ?)",
                recordArgs,
                recordTypes,
                conceptSchemeRecordMapper);

        UUID uuid = record.getUuid();

        JSONArray uuidArray = dataset.getTopConceptsJson();
        Object[] conceptArgs = {uuid, uuidArray};
        int[] conceptTypes = {Types.OTHER, Types.OTHER};

        jdbcTemplate.update("CALL update_concept_scheme_top_concepts(?, ?)", conceptArgs, conceptTypes);
        return uuid;
    }

    public void deleteDataSet(UUID uuid) {
        Object[] recordArgs = {uuid};
        int[] recordTypes = {Types.OTHER};

        jdbcTemplate.update("CALL delete_concept_scheme(?)", recordArgs, recordTypes);
    }
}
