package io.aitech.pv.config;

public class AppConfiguration {

    private String appName;
    private PostgresConfiguration postgres;


    public String appName() {
        return appName;
    }

    public PostgresConfiguration postgres() {
        return postgres;
    }

}
