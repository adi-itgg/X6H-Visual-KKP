package io.aitech.pv.repository.impl;

import io.aitech.pv.repository.DashboardRepository;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class DashboardRepositoryImpl implements DashboardRepository {

    private final Pool pool;

    public DashboardRepositoryImpl(Pool pool) {
        this.pool = pool;
    }


    @Override
    public Future<Long> countAllStudent() {
        return pool.preparedQuery("SELECT count(1) FROM m_student").execute().map(rows -> rows.iterator().next().getLong(0));
    }

    @Override
    public Future<Long> countAllTeacher() {
        return pool.preparedQuery("SELECT count(1) FROM m_teacher").execute().map(rows -> rows.iterator().next().getLong(0));
    }

    @Override
    public Future<Long> countAllMoney() {
        return pool.preparedQuery("SELECT sum(total_amount) FROM t_bill_student").execute().map(rows -> {
            if (!rows.iterator().hasNext()) {
                return 0L;
            }
            return rows.iterator().next().getLong(0);
        });
    }

    @Override
    public Future<RowSet<Row>> fetchAll() {
        return pool.preparedQuery("""
                SELECT s.name,
                       s.gender,
                       p.name                                         as parent_name,
                       COALESCE(ts.enter_year, '-')                   as enter_year,
                       COALESCE(c.name, '-')                          as class_name,
                       COALESCE(sum(COALESCE(bs.total_amount, 0)), 0) as total_amount,
                       COALESCE(bs.created_at, '-')                   as created_at
                FROM m_student s
                         JOIN m_parent p ON s.parent_id = p.id
                         LEFT JOIN t_student ts ON s.id = ts.student_id
                         LEFT JOIN m_class c ON ts.class_id = c.id
                         LEFT JOIN t_bill_student bs ON p.id = bs.parent_id AND s.id = bs.student_id
                GROUP BY s.name, s.gender, p.name, ts.enter_year, c.name, bs.created_at
                ORDER BY bs.created_at DESC
                """).execute();
    }
}
