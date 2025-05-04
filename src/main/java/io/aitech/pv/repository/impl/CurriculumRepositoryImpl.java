package io.aitech.pv.repository.impl;

import io.aitech.pv.repository.CurriculumRepository;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.List;


public class CurriculumRepositoryImpl implements CurriculumRepository {

    private final Pool pool;

    public CurriculumRepositoryImpl(Pool pool) {
        this.pool = pool;
    }

    @Override
    public Future<RowSet<Row>> fetchAll(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return pool.preparedQuery("""
                    SELECT id, name, duration
                    FROM m_curriculum
                    """).execute();
        }
        keyword = "%" + keyword.toLowerCase() + "%";
        return pool.preparedQuery("""
                SELECT id, name, duration
                FROM m_curriculum
                WHERE lower(name) like ?
                """).execute(Tuple.of(keyword));
    }

    @Override
    public Future<Void> save(List<Object> params) {
        if (params.getFirst().equals("-")) {
            return pool.preparedQuery("""
                    INSERT INTO m_curriculum (name, duration) VALUES (?, ?)
                    """).execute(Tuple.tuple(params.subList(1, params.size()))).mapEmpty();
        }
        return pool.preparedQuery("""
                UPDATE m_curriculum
                    SET duration = ?, name = ?
                    WHERE id = ?
                """).execute(Tuple.tuple(params.reversed())).mapEmpty();
    }

    @Override
    public Future<Void> delete(Object id) {
        return pool.preparedQuery("DELETE FROM m_curriculum WHERE id = ?").execute(Tuple.of(id)).mapEmpty();
    }
}
