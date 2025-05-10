package io.aitech.pv.repository.impl;

import io.aitech.pv.repository.DashboardRepository;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;

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
}
