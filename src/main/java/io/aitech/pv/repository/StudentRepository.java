package io.aitech.pv.repository;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import java.time.LocalDate;

public interface StudentRepository extends BaseMasterRepository {

    Future<RowSet<Row>> fetchParentNames();

    Future<Void> addStudent(String name, char gender, LocalDate bornDate, String bornPlace, String address, Long parentId);
}
