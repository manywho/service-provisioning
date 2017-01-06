package com.manywho.services.provisioning.factories;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.manywho.services.provisioning.ApplicationConfiguration;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AmazonFactory {

    private ApplicationConfiguration applicationConfiguration;
    private AmazonIdentityManagementClient amazonIdentityManagementClient;
    private BasicAWSCredentials basicAWSCredentials;

    @Inject
    public AmazonFactory(ApplicationConfiguration applicationConfiguration){
        this.applicationConfiguration = applicationConfiguration;
        this.amazonIdentityManagementClient = null;
        this.basicAWSCredentials = null;
    }

    public BasicAWSCredentials createBasicAwsCredentials(){
        if (basicAWSCredentials == null) {
            basicAWSCredentials = new BasicAWSCredentials(applicationConfiguration.getAwsSesManagerAccessKeyId(), applicationConfiguration.getAwsSesManagerSecretAccessKey());
        }

        return basicAWSCredentials;
    }

    public AmazonIdentityManagementClient getAmazonIdentityManagementClient() {
        if(amazonIdentityManagementClient == null) {
            amazonIdentityManagementClient = new AmazonIdentityManagementClient(this.createBasicAwsCredentials());
        }

        return amazonIdentityManagementClient;
    }
}
