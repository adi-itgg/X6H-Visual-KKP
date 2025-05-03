package io.aitech.pv.repository.impl;

import io.aitech.pv.repository.ParentRepository;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.List;

public class ParentRepositoryImpl implements ParentRepository {

    private final Pool pool;

    public ParentRepositoryImpl(Pool pool) {
        this.pool = pool;
    }

    @Override
    public Future<RowSet<Row>> fetchAll(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return pool.preparedQuery("""
                    SELECT id, nik, name, job, phone_number, address
                    FROM m_parent
                    """).execute();
        }
        keyword = "%" + keyword.toLowerCase() + "%";
        return pool.preparedQuery("""
                SELECT id, nik, name, job, phone_number, address
                FROM m_parent
                WHERE lower(name) like ? or phone_number like ?
                """).execute(Tuple.of(keyword, keyword));
    }

    @Override
    public Future<Void> save(List<Object> params) {
        return pool.preparedQuery("""
                        UPDATE m_parent
                            SET nik = ?,
                                name = ?,
                                job = ?,
                                phone_number = ?,
                                address = ?,
                                updated_at = CURRENT_TIMESTAMP
                        WHERE id = ?
                        """).execute(Tuple.tuple(params))
                .mapEmpty();
    }

    @Override
    public Future<Void> delete(Object id) {
        return pool.preparedQuery("""
                        DELETE FROM m_parent
                        WHERE id = ?
                        """).execute(Tuple.of(id))
                .mapEmpty();
    }
}
