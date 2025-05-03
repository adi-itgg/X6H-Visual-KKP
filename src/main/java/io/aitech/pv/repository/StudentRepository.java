package io.aitech.pv.repository;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import java.time.LocalDate;

public interface StudentRepository {

    Future<RowSet<Row>> fetchAll(String keyword);

    Future<Void> save(Long id, String name, String gender, String birthPlace, LocalDate birthDate, String address);

    Future<RowSet<Row>> fetchParentNames();

    Future<Void> addStudent(String name, char gender, LocalDate bornDate, String bornPlace, String address, Long parentId);
}
