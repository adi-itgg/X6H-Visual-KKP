package io.aitech.pv.repository.impl;

import io.aitech.pv.repository.TeacherRepository;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.List;

public class TeacherRepositoryImpl implements TeacherRepository {

    private final Pool pool;

    public TeacherRepositoryImpl(Pool pool) {
        this.pool = pool;
    }

    @Override
    public Future<RowSet<Row>> fetchAll(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return pool.preparedQuery("""
                    SELECT id, nip, name, gender, education, phone_number, address
                    FROM m_teacher
                    """).execute();
        }
        keyword = "%" + keyword.toLowerCase() + "%";
        return pool.preparedQuery("""
                SELECT id, nip, name, gender, education, phone_number, address
                FROM m_teacher
                WHERE lower(name) like ? or phone_number like ?
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
                        UPDATE m_teacher
                            SET nip = ?,
                                name = ?,
                                gender = ?,
                                education = ?,
                                phone_number = ?,
                                address = ?,
                                updated_at = CURRENT_TIMESTAMP
                        WHERE id = ?
                        """).execute(tuple)
                .mapEmpty();
    }

    @Override
    public Future<Void> delete(Object id) {
        return pool.preparedQuery("DELETE FROM m_teacher WHERE id = ?")
                .execute(Tuple.of(id))
                .mapEmpty();
    }

    @Override
    public Future<Void> addTeacher(String nip, String name, char gender, String education, String phoneNumber, String address) {
        return pool.preparedQuery("""
                        INSERT INTO m_teacher (nip, name, gender, education, phone_number, address)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """).execute(Tuple.of(nip, name, gender, education, phoneNumber, address))
                .mapEmpty();
    }
}
