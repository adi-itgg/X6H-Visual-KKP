package io.aitech.pv.repository.impl;

import io.aitech.pv.repository.ClassRepository;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClassRepositoryImpl implements ClassRepository {

    private final Pool pool;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public ClassRepositoryImpl(Pool pool) {
        this.pool = pool;
    }

    @Override
    public Future<RowSet<Row>> fetchAll(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return pool.preparedQuery("""
                    SELECT c.id,
                           c.name,
                           t.name as teacher_name,
                           c.max_capacity,
                           c.schedule,
                           c.duration,
                           i.name as biaya
                    FROM m_class c
                    JOIN m_teacher t ON c.teacher_id = t.id
                    LEFT JOIN m_invoice i ON c.invoice_id = i.id
                    """).execute();
        }

        keyword = "%" + keyword.toLowerCase() + "%";
        return pool.preparedQuery("""
                SELECT c.id,
                       c.name,
                       t.name as teacher_name,
                       c.max_capacity,
                       c.schedule,
                       c.duration,
                       i.name as biaya
                FROM m_class c
                JOIN m_teacher t ON c.teacher_id = t.id
                LEFT JOIN m_invoice i ON c.invoice_id = i.id
                WHERE lower(c.name) like ? OR lower(t.name) like ?
                """).execute(Tuple.of(keyword, keyword));
    }

    @Override
    public Future<Void> save(List<Object> params) {
        Tuple tuple = Tuple.tuple();
        for (int i = 1; i < params.size(); i++) {
            tuple.addValue(params.get(i));
        }
        tuple.addValue(params.getFirst());
        return pool.preparedQuery("""
                UPDATE m_class
                    SET name = ?,
                        teacher_id = ?,
                        max_capacity = ?,
                        schedule = ?,
                        duration = ?,
                        invoice_id = ?,
                        updated_at = CURRENT_TIMESTAMP
                    WHERE id = ?
                """).execute(tuple)
                .mapEmpty();
    }

    @Override
    public Future<Void> delete(Object id) {
        return pool.preparedQuery("DELETE FROM m_class WHERE id = ?")
                .execute(Tuple.of(id))
                .mapEmpty();
    }

    @Override
    public Future<RowSet<Row>> fetchTeacherItems() {
        return pool.preparedQuery("SELECT id, name FROM m_teacher")
                .execute();
    }

    @Override
    public Future<RowSet<Row>> fetchInvoiceItems() {
        return pool.preparedQuery("SELECT id, name FROM m_invoice")
                .execute();
    }

    @Override
    public Future<Void> addClass(String name, Long teacherId, int capacity, LocalTime scheduleTime, LocalTime durationTime, Long invoiceId) {
        return pool.preparedQuery("""
                INSERT INTO m_class(name, teacher_id, max_capacity, schedule, duration, invoice_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """).execute(Tuple.of(name, teacherId, capacity, timeFormatter.format(scheduleTime), timeFormatter.format(durationTime), invoiceId))
                .mapEmpty();
    }
}
