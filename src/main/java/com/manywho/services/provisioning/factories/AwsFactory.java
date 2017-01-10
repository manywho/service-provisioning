package com.manywho.services.provisioning.factories;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.manywho.services.provisioning.ApplicationConfiguration;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AwsFactory {
    private final ApplicationConfiguration applicationConfiguration;

    private AmazonIdentityManagement iamClient;
    private AWSCredentials credentials;

    @Inject
    public AwsFactory(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public AWSCredentials createBasicCredentials() {
        if (credentials == null) {
            credentials = new BasicAWSCredentials(
                    applicationConfiguration.getAwsAccessKey(),
                    applicationConfiguration.getAwsSecretKey()
            );
        }

        return credentials;
    }

    public AmazonIdentityManagement getIamClient() {
        if (iamClient == null) {
            iamClient = new AmazonIdentityManagementClient(createBasicCredentials());
        }

        return iamClient;
    }
}
