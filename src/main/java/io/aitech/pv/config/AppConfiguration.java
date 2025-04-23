package io.aitech.pv.config;

public class AppConfiguration {

    private String appName;
    private PostgresConfiguration postgres;
    private MySQLConfiguration mysql;


    public String appName() {
        return appName;
    }

    public PostgresConfiguration postgres() {
        return postgres;
    }

    public MySQLConfiguration mysql() {
        return mysql;
    }

}
