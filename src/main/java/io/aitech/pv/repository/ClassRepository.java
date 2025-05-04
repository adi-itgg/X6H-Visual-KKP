package io.aitech.pv.repository;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import java.time.LocalTime;


public interface ClassRepository extends BaseMasterRepository {


    Future<RowSet<Row>> fetchTeacherItems();

    Future<RowSet<Row>> fetchInvoiceItems();

    Future<Void> addClass(String name, Long teacherId, int capacity, LocalTime scheduleTime, LocalTime durationTime, Long invoiceId);

}
