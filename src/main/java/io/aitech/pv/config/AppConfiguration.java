package io.aitech.pv.config;

public class AppConfiguration {

    private String appName;
    private MySQLConfiguration mysql;


    public String appName() {
        return appName;
    }

    public MySQLConfiguration mysql() {
        return mysql;
    }

}
