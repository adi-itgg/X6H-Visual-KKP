package io.aitech.pv.repository;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

public class LoginRepositoryImpl implements LoginRepository {

    private final Pool pool;

    public LoginRepositoryImpl(Pool pool) {
        this.pool = pool;
    }


    @Override
    public Future<Row> findUserByEmail(String email) {
        return pool.preparedQuery("SELECT * FROM m_user WHERE lower(email) = ? LIMIT 1")
                .execute(Tuple.of(email.toLowerCase()))
                .map(rows -> {
                    if (rows.size() > 0) {
                        return rows.iterator().next();
                    }
                    return null;
                });
    }

}
