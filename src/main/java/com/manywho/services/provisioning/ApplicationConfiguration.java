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

    public String getAwsAccessKey() {return System.getenv("AWS_ACCESS_KEY");}

    public String getAwsSecretKey() {return System.getenv("AWS_SECRET_KEY");}

    public String getAwsTenantGroup() {return System.getenv("AWS_TENANT_GROUP");}

    public String getSmtpHostname() {
        return System.getenv("SMTP_HOSTNAME");
    }

    public Integer getSmtpPort() {
        return Integer.valueOf(System.getenv("SMTP_PORT"));
    }
}
