package io.aitech.pv.repository;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import java.util.List;

public interface BaseMasterRepository {

    Future<RowSet<Row>> fetchAll(String keyword);

    Future<Void> save(List<Object> params);

    Future<Void> delete(Object id);

}
