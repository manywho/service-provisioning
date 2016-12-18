package com.manywho.services.provisioning;

import javax.inject.Singleton;

@Singleton
public class ApplicationConfiguration {

    public String getDatabaseHostname() {
        return System.getenv("DATABASE_HOSTNAME");
    }

    public String getDatabaseSuperuserPassword() {
        return System.getenv("DATABASE_SUPERUSER_PASSWORD");
    }

    public String getDatabaseSuperuserUsername() {
        return System.getenv("DATABASE_SUPERUSER_USERNAME");
    }
}
