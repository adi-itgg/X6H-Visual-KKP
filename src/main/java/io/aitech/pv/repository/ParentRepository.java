package io.aitech.pv.repository;

import io.vertx.core.Future;

public interface ParentRepository extends BaseMasterRepository {

    Future<Void> addParent(String nik, String name, String job, String address, String phoneNumber);

}
