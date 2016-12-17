package com.manywho.services.provisioning.factories;

import org.sql2o.Sql2o;

public class Sql2oFactory {
    public Sql2o create(String database) {
        return create(database, "postgres", "");
    }

    public Sql2o create(String database, String username, String password) {
        return new Sql2o("jdbc:postgresql://localhost/" + database, username, password);
    }
}
