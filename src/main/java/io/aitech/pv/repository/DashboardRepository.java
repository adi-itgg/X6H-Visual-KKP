package io.aitech.pv.repository;

import io.vertx.core.Future;

public interface DashboardRepository {
    Future<Long> countAllStudent();

    Future<Long> countAllTeacher();

    Future<Long> countAllMoney();
}
