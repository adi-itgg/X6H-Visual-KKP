package io.aitech.pv.repository.impl;

import io.aitech.pv.repository.InvoiceRepository;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.List;

public class InvoiceRepositoryImpl implements InvoiceRepository {

    private final Pool pool;

    public InvoiceRepositoryImpl(Pool pool) {
        this.pool = pool;
    }

    @Override
    public Future<RowSet<Row>> fetchAll(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return pool.preparedQuery("""
                    SELECT id, name, amount
                    FROM m_invoice
                    """).execute();
        }
        keyword = "%" + keyword.toLowerCase() + "%";
        return pool.preparedQuery("""
                SELECT id, name, amount
                FROM m_invoice
                WHERE lower(name) like ?
                """).execute(Tuple.of(keyword));
    }

    @Override
    public Future<Void> save(List<Object> params) {
        if (params.getFirst().equals("-")) {
            return pool.preparedQuery("""
                    INSERT INTO m_invoice (name, amount) VALUES (?, ?)
                    """).execute(Tuple.tuple(params.subList(1, params.size()))).mapEmpty();
        }
        return pool.preparedQuery("""
                UPDATE m_invoice SET amount = ?, name = ?
                WHERE id = ?
                """).execute(Tuple.tuple(params.reversed())).mapEmpty();
    }

    @Override
    public Future<Void> delete(Object id) {
        return pool.preparedQuery("DELETE FROM m_invoice WHERE id = ?")
                .execute(Tuple.of(id))
                .mapEmpty();
    }

}
