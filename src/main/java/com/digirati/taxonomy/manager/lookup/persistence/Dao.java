package com.digirati.taxonomy.manager.lookup.persistence;

import java.util.Optional;

interface Dao<T> {

	Optional<T> create(T toCreate);

	Optional<T> read(long primaryKey);

	Optional<T> update(T toUpdate);

	boolean delete(long primaryKey);
}
