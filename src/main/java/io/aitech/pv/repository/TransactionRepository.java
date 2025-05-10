package io.aitech.pv.repository;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;

import java.awt.*;
import java.util.List;
import java.util.function.Function;

public interface TransactionRepository {

    Future<RowSet<Row>> fetchAll(String keyword);

    Future<RowSet<Row>> fetchStudentItems();

    Future<RowSet<Row>> fetchInvoiceItems();

    Future<RowSet<Row>> transaction(Function<SqlConnection, Future<RowSet<Row>>> function);

    Future<Long> findParentIdByStudentId(long studentId);

    Future<Void> addBillStudentTx(SqlConnection connection, long studentId, Long parentId, long totalAmount);

    Future<Void> addBillDetailStudentTx(SqlConnection connection, Long id, List<Long> invoiceIds);

    Future<Long> getIdBillStudent(SqlConnection connection);
}
