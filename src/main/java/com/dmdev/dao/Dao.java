package com.dmdev.dao;

import com.dmdev.entity.BaseEntity;
import com.dmdev.util.Generated;

import java.util.List;
import java.util.Optional;

@Generated
public interface Dao<K, T extends BaseEntity<K>> {

    default T upsert(T entity) {
        return entity.getId() != null
                ? update(entity)
                : insert(entity);
    }

    List<T> findAll();

    Optional<T> findById(K id);

    boolean delete(K id);

    T update(T entity);

    T insert(T entity);
}
