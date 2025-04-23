package io.aitech.pv.config;

public class MySQLConfiguration {

    private String host;
    private int port;
    private String database;
    private String user;
    private String password;
    private int pipeliningLimit;
    private int poolSize;

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String database() {
        return database;
    }

    public String user() {
        return user;
    }

    public String password() {
        return password;
    }

    public int pipeliningLimit() {
        return pipeliningLimit;
    }

    public int poolSize() {
        return poolSize;
    }

}
