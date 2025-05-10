package io.aitech.pv.repository.impl;

import io.aitech.pv.repository.TransactionRepository;
import io.vertx.core.Future;
import io.vertx.sqlclient.*;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class TransactionRepositoryImpl implements TransactionRepository {

    private final Pool pool;

    public TransactionRepositoryImpl(Pool pool) {
        this.pool = pool;
    }

    @Override
    public Future<RowSet<Row>> fetchAll(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return pool.preparedQuery("""
                    SELECT b.id, s.name, p.name as parent_name, b.total_amount, b.created_at
                    FROM t_bill_student b
                    JOIN m_student s ON b.student_id = s.id
                    JOIN m_parent p ON b.parent_id = p.id
                    ORDER BY b.created_at DESC
                    """).execute();
        }

        keyword = "%" + keyword.toLowerCase() + "%";
        return pool.preparedQuery("""
                SELECT b.id, s.name, p.name as parent_name, b.total_amount, b.created_at
                FROM t_bill_student b
                JOIN m_student s ON b.student_id = s.id
                JOIN m_parent p ON b.parent_id = p.id
                WHERE s.name like ?
                ORDER BY b.created_at DESC
                """).execute(Tuple.of(keyword));
    }

    @Override
    public Future<RowSet<Row>> fetchStudentItems() {
        return pool.preparedQuery("SELECT id, name FROM m_student")
                .execute();
    }

    @Override
    public Future<RowSet<Row>> fetchInvoiceItems() {
        return pool.preparedQuery("SELECT id, name, amount FROM m_invoice")
                .execute();
    }

    @Override
    public Future<RowSet<Row>> transaction(Function<SqlConnection, Future<RowSet<Row>>> function) {
        return pool.withTransaction(function);
    }

    @Override
    public Future<Long> findParentIdByStudentId(long studentId) {
        return pool.preparedQuery("SELECT parent_id FROM m_student WHERE id = ? LIMIT 1")
                .execute(Tuple.of(studentId))
                .map(rows -> {
                    Iterator<Row> iterator = rows.iterator();
                    if (iterator.hasNext()) {
                        return iterator.next().getLong(0);
                    }
                    return null;
                });
    }

    @Override
    public Future<Void> addBillStudentTx(SqlConnection connection, long studentId, Long parentId, long totalAmount) {
        return Objects.requireNonNullElse(connection, pool).preparedQuery("""
                        INSERT INTO t_bill_student (student_id, parent_id, total_amount)
                        VALUES (?, ?, ?)
                        """).execute(Tuple.of(studentId, parentId, totalAmount))
                .map(rows -> {
                    assert rows.rowCount() > 0;
                    return null;
                });
    }

    @Override
    public Future<Void> addBillDetailStudentTx(SqlConnection connection, Long id, List<Long> invoiceIds) {
        List<Future<Object>> futures = invoiceIds.stream().map(invoiceId -> {
            return Objects.requireNonNullElse(connection, pool).preparedQuery("""
                    SELECT id, name, amount FROM m_invoice WHERE id = ? LIMIT 1
                    """).execute(Tuple.of(invoiceId)).compose(rows -> {
                Row row = rows.iterator().next();
                return Objects.requireNonNullElse(connection, pool).preparedQuery("""
                        INSERT INTO t_bill_student_detail (bill_student_id, invoice_id, name, amount)
                        VALUES (?, ?, ?, ?)
                        """).execute(Tuple.of(id, row.getValue(0), row.getValue(1), row.getValue(2))).map(r -> {
                    assert r.rowCount() > 0;
                    return null;
                });
            });

        }).toList();
        return Future.all(futures).mapEmpty();
    }

    @Override
    public Future<Long> getIdBillStudent(SqlConnection connection) {
        return Objects.requireNonNullElse(connection, pool).preparedQuery("SELECT last_insert_id()")
                .execute().map(rows -> rows.iterator().next().getLong(0));
    }

    @Override
    public Future<RowSet<Row>> fetchDetailsAll(Long transactionId) {
        return pool.preparedQuery("""
                        SELECT t.id, t.name, t.amount, t.created_date FROM t_bill_student_detail t
                        WHERE t.bill_student_id = ?
                        """)
                .execute(Tuple.of(transactionId));
    }

}
