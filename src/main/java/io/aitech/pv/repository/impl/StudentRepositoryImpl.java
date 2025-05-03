package io.aitech.pv.repository.impl;

import io.aitech.pv.repository.StudentRepository;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.time.LocalDate;
import java.util.List;

public class StudentRepositoryImpl implements StudentRepository {

    private final Pool pool;

    public StudentRepositoryImpl(Pool pool) {
        this.pool = pool;
    }


    @Override
    public Future<RowSet<Row>> fetchAll(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return pool.preparedQuery("""
                        SELECT s.id,
                           s.name,
                           s.gender,
                           s.birth_place,
                           s.birth_date,
                           s.address,
                           p.name as parent_name
                    FROM m_student s
                    JOIN m_parent p ON s.parent_id = p.id
                    """).execute();
        }
        keyword = "%" + keyword.toLowerCase() + "%";
        return pool.preparedQuery("""
                    SELECT s.id,
                       s.name,
                       s.gender,
                       s.birth_place,
                       s.birth_date,
                       s.address,
                       p.name as parent_name
                FROM m_student s
                JOIN m_parent p ON s.parent_id = p.id
                WHERE lower(s.name) like ? OR p.name like ?
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
                        UPDATE m_student
                            SET name = ?,
                                gender = ?,
                                birth_place = ?,
                                birth_date = ?,
                                address = ?,
                                updated_at = CURRENT_TIMESTAMP
                        WHERE id = ?
                        """).execute(tuple)
                .mapEmpty();
    }

    @Override
    public Future<Void> delete(Object id) {
        return pool.preparedQuery("""
                        DELETE FROM m_student
                        WHERE id = ?
                        """).execute(Tuple.of(id))
                .mapEmpty();
    }

    @Override
    public Future<RowSet<Row>> fetchParentNames() {
        return pool.preparedQuery("""
                SELECT id, name FROM m_parent
                """).execute();
    }

    @Override
    public Future<Void> addStudent(String name, char gender, LocalDate bornDate, String bornPlace, String address, Long parentId) {
        return pool.preparedQuery("""
                        INSERT INTO m_student (name, gender, birth_place, birth_date, address, parent_id)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """).execute(Tuple.of(name, gender, bornPlace, bornDate, address, parentId))
                .mapEmpty();
    }

}
