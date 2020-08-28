package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.common.taxonomy.ConceptRelationshipType;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.mapper.ConceptRecordMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.record.mapper.ConceptRelationshipRecordMapper;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.stream.Stream;

/**
 * A data access object that can retrieve and store {@link ConceptDataSet}s in an underlying
 * PostgreSQL database.
 */
public class ConceptDao {
    private final JdbcTemplateEx jdbcTemplate;
    private final ConceptRecordMapper recordMapper = new ConceptRecordMapper();
    private final ConceptRelationshipRecordMapper relationshipRecordMapper = new ConceptRelationshipRecordMapper();

    public ConceptDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplateEx(dataSource);
    }

    public Stream<ConceptRecord> loadAllRecords() {
        return jdbcTemplate.query("SELECT * FROM get_all_concepts()", recordMapper).stream();
    }

    public Collection<ConceptRecord> getConceptsByPartialLabel(String label, String languageKey) {
        Object[] args = {label, languageKey};
        return jdbcTemplate.query("SELECT * FROM get_concepts_by_partial_label(?, ?)", args, recordMapper);
    }

    /**
     * Find all the records that have a {@code UUID} value in the collection of {@code uuids} given.
     *
     * @param uuids A collection of UUIDs representing {@link ConceptRecord}s.
     */
    public List<ConceptRecord> findAllRecords(Collection<UUID> uuids) {
        Array uuidArray;

        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            uuidArray = conn.createArrayOf("uuid", uuids.toArray(UUID[]::new));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Object[] conceptArgs = {uuidArray};
        int[] conceptTypes = {Types.ARRAY};

        return jdbcTemplate.query(
                "SELECT * FROM get_concepts_by_uuids(?)",
                conceptArgs,
                conceptTypes,
                recordMapper);
    }

    /**
     * Find all the records that are related to the record identified by {@code uuid},
     * with the given relationship {@code type}.
     *  @param uuid The identifier of the source record.
     * @param type The type of relationship to search for.
     * @param depth
     */
    public List<ConceptRecord> findRelatedRecords(UUID uuid, ConceptRelationshipType type, int depth) {
        Object[] conceptArgs = {uuid, type.toString().toLowerCase(), depth};
        int[] conceptTypes = {Types.OTHER, Types.OTHER, Types.INTEGER};

        return jdbcTemplate.query(
                "SELECT * FROM get_concept_semantic_relations_recursive(?, ?, ?)",
                conceptArgs,
                conceptTypes,
                recordMapper);
    }

    /**
     * Given a {@code uuid} of a concept, lookup and find the concept record and all of its
     * associated relationships.
     *
     * @param uuid The identity of the concept to be looked up.
     * @return A complete {@link ConceptDataSet}.
     */
    public Optional<ConceptDataSet> loadDataSet(UUID uuid) {
        Object[] recordArgs = {uuid};
        int[] recordTypes = {Types.OTHER};

        var record = jdbcTemplate.queryForOptional("SELECT * FROM get_concept(?)", recordArgs, recordTypes, recordMapper);
        if (record.isEmpty()) {
            return Optional.empty();
        }
        var relationshipRecords = jdbcTemplate.query(
                "SELECT * FROM get_concept_relationships(?)",
                recordArgs,
                recordTypes,
                relationshipRecordMapper);

        return Optional.of(new ConceptDataSet(record.get(), relationshipRecords));
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
            record.getSource(),
            DaoUtils.createRdfPlainLiteral(record.getPreferredLabel()),
            DaoUtils.createRdfPlainLiteral(record.getAltLabel()),
            DaoUtils.createRdfPlainLiteral(record.getHiddenLabel()),
            DaoUtils.createRdfPlainLiteral(record.getNote()),
            DaoUtils.createRdfPlainLiteral(record.getChangeNote()),
            DaoUtils.createRdfPlainLiteral(record.getEditorialNote()),
            DaoUtils.createRdfPlainLiteral(record.getExample()),
            DaoUtils.createRdfPlainLiteral(record.getHistoryNote()),
            DaoUtils.createRdfPlainLiteral(record.getScopeNote()),
            DaoUtils.createRdfPlainLiteral(record.getDefinition()),
        };

        int[] recordTypes = new int[recordArgs.length];
        Arrays.fill(recordTypes, Types.OTHER);

        // @FIXME gtierney: 12 positional parameters is garbage. Fix this sometime.
        jdbcTemplate.update("CALL update_concept(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", recordArgs, recordTypes);

        int[] relationTypes = {Types.OTHER, Types.VARCHAR, Types.OTHER};
        Object[] relationArgs = {record.getUuid(), record.getSource(), dataset.getRelationshipRecordsJson()};

        jdbcTemplate.update("CALL update_concept_semantic_relations(?, ?, ?)", relationArgs, relationTypes);
    }

    public void deleteDataSet(UUID uuid) {
        Object[] recordArgs = {uuid};
        int[] recordTypes = {Types.OTHER};

        jdbcTemplate.update("CALL delete_concept(?)", recordArgs, recordTypes);
    }
}
