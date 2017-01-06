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

    public String getAwsAccessKeyId() {return System.getenv("AWS_ACCESS_KEY_ID");}

    public String getAwsSecretAccessKey() {return System.getenv("AWS_SECRET_ACCESS_KEY");}

    public String getAwsSesHost() {return System.getenv("AWS_SES_HOST");}

    public String getAwsSesTransport() {return System.getenv("AWS_SES_TRANSPORT");}

    public int getAwsSesPort() {return Integer.parseInt(System.getenv("AWS_SES_PORT"));}

    public String getAwsSesPolicy() {return System.getenv("AWS_SES_POLICY");}
}
