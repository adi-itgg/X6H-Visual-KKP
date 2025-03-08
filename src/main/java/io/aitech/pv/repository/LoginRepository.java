package io.aitech.pv.repository;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;

public interface LoginRepository {

    Future<Row> findUserByEmail(String email);

}
