package com.manywho.services.provisioning.providers;

import com.google.inject.Provider;
import org.sql2o.Sql2o;

public class Sql2oProvider implements Provider<Sql2o> {
    @Override
    public Sql2o get() {
        return new Sql2o("jdbc:postgresql://localhost/service-provisioning", "postgres", "");
    }
}
