package io.aitech.pv.repository;

import io.vertx.core.Future;

public interface TeacherRepository extends BaseMasterRepository {


    Future<Void> addTeacher(String nip, String name, char gender, String education, String phoneNumber, String address);

}
