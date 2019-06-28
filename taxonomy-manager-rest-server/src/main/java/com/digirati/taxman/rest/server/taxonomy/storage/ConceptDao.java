package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.mapper.ConceptRecordMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.record.mapper.ConceptRelationshipRecordMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.sql.RowMappingSpliterator;
import org.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * A data access object that can retrieve and store {@link ConceptDataSet}s in an underlying
 * PostgreSQL database.
 */
public class ConceptDao {
    private final JdbcTemplate jdbcTemplate;
    private final ConceptRecordMapper recordMapper = new ConceptRecordMapper();
    private final ConceptRelationshipRecordMapper relationshipRecordMapper = new ConceptRelationshipRecordMapper();

    public ConceptDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Load every concept scheme in the database and stream them from the database server in batches.
     */
    public Stream<ConceptRecord> loadAllRecords() {
        // @TODO - jdbcTemplate.setFetchSize(...)
        ResultSetExtractor<Stream<ConceptRecord>> extractor = rs -> RowMappingSpliterator.stream(recordMapper, rs);

        return jdbcTemplate.query("SELECT * FROM get_all_concepts()", extractor);
    }

    /**
     * Given a {@code uuid} of a concept, lookup and find the concept record and all of its
     * associated relationships.
     *
     * @param uuid The identity of the concept to be looked up.
     * @return A complete {@link ConceptDataSet}.
     */
    public ConceptDataSet loadDataSet(UUID uuid) {
        Object[] recordArgs = {uuid};
        int[] recordTypes = {Types.OTHER};

        var record = jdbcTemplate.queryForObject("SELECT * FROM get_concept(?)", recordArgs, recordTypes, recordMapper);
        var relationshipRecords = jdbcTemplate.query(
                "SELECT * FROM get_concept_relationships(?)",
                recordArgs,
                recordTypes,
                relationshipRecordMapper);

        return new ConceptDataSet(record, relationshipRecords);
    }

    /**
     * Store a {@link ConceptDataSet}, updating or creating the underlying concept record and
     * removing/creating any relationships that were removed/added to the concept.
     *
     * @param dataset The {@code ConceptDataSet} to store.
     */
    public void storeDataSet(ConceptDataSet dataset) {
        var record = dataset.getRecord();

        Object[] recordArgs = {
                record.getUuid(),
                new JSONObject(record.getPreferredLabel()),
                new JSONObject(record.getAltLabel()),
                new JSONObject(record.getHiddenLabel()),
                new JSONObject(record.getNote()),
                new JSONObject(record.getChangeNote()),
                new JSONObject(record.getEditorialNote()),
                new JSONObject(record.getExample()),
                new JSONObject(record.getHistoryNote()),
                new JSONObject(record.getScopeNote())
        };

        int[] recordTypes = new int[recordArgs.length];
        Arrays.fill(recordTypes, Types.OTHER);

        jdbcTemplate.update("CALL update_concept(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", recordArgs, recordTypes);

        int[] relationTypes = {Types.OTHER, Types.OTHER};
        Object[] relationArgs = {record.getUuid(), dataset.getRelationshipRecordsJson()};

        jdbcTemplate.update("CALL update_concept_semantic_relations(?, ?)", relationArgs, relationTypes);
    }
}
