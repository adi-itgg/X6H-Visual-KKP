package io.aitech.pv.repository;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public interface DashboardRepository {
    Future<Long> countAllStudent();

    Future<Long> countAllTeacher();

    Future<Long> countAllMoney();

    Future<RowSet<Row>> fetchAll();
}
