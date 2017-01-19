package com.manywho.services.provisioning.email;

import com.amazonaws.services.identitymanagement.model.*;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.ListVerifiedEmailAddressesResult;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.provisioning.ApplicationConfiguration;
import com.manywho.services.provisioning.ServiceConfiguration;
import com.manywho.services.provisioning.factories.AwsFactory;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

public class ProvisionSmtpCommand implements ActionCommand<ServiceConfiguration, ProvisionSmtp, ProvisionSmtp.Input, ProvisionSmtp.Output> {
    private final ApplicationConfiguration applicationConfiguration;
    private final AwsFactory awsFactory;

    @Inject
    public ProvisionSmtpCommand(ApplicationConfiguration applicationConfiguration, AwsFactory awsFactory) {
        this.applicationConfiguration = applicationConfiguration;
        this.awsFactory = awsFactory;
    }

    @Override
    public ActionResponse<ProvisionSmtp.Output> execute(ServiceConfiguration configuration, ServiceRequest request, ProvisionSmtp.Input input) {
        // remove whitespaces and * that have a special meaning in amazon and can be used as a reg exp
        String cleanEmail = input.getTenantEmail().replace("*", "").trim();

        validateEmailAddress (cleanEmail);

        // Create a new policyDocument with the policy template and the email address of the user
        String policyDocument = getPolicyString(cleanEmail);

        // We want to create an IAM user that is scoped to tenants
        String iamUsername = "tenant-" + input.getTenant();

        // Create the IAM user with no privileges
        CreateUserResult createUserResult = awsFactory.getIamClient().createUser(new CreateUserRequest(iamUsername));

        // Add policy to user
        addPolicyEmailRestrictionToUser(createUserResult.getUser().getUserName(), policyDocument);

        // Add the IAM user to the desired SES group
        awsFactory.getIamClient().addUserToGroup(new AddUserToGroupRequest(applicationConfiguration.getAwsTenantGroup(), iamUsername));

        // Create and get access key credentials for the IAM user
        AccessKey accessKey = awsFactory.getIamClient().createAccessKey(new CreateAccessKeyRequest(iamUsername))
                .getAccessKey();

        // Get the SMTP username and password
        String username = accessKey.getAccessKeyId();
        String password = generateSmtpPassword(accessKey.getSecretAccessKey());

        // send a verification link to the user by email
        verifyEmailAddress(cleanEmail);

        return new ActionResponse<>(new ProvisionSmtp.Output(
                applicationConfiguration.getSmtpHostname(),
                "tls",
                applicationConfiguration.getSmtpPort(),
                username,
                password
        ));
    }

    private void validateEmailAddress(String tenantEmail) {
        EmailValidator emailValidator = new EmailValidator();

        if(! emailValidator.isValid(tenantEmail, null)) {
            throw new RuntimeException("The email is not valid");
        }
    }

    private void addPolicyEmailRestrictionToUser(String userName, String policyDocument) {
        CreatePolicyRequest createPolicyRequest = new CreatePolicyRequest();
        createPolicyRequest.setPolicyName(userName);
        createPolicyRequest.setPolicyDocument(policyDocument);
        CreatePolicyResult createPolicyResult =  awsFactory.getIamClient().createPolicy(createPolicyRequest);

        AttachUserPolicyRequest attachUserPolicyRequest = new AttachUserPolicyRequest();
        attachUserPolicyRequest.setPolicyArn(createPolicyResult.getPolicy().getArn());
        attachUserPolicyRequest.setUserName(userName);
        awsFactory.getIamClient().attachUserPolicy(attachUserPolicyRequest);

    }


    public String getPolicyString(String tenantEmail) {
        ObjectMapper mapper = new ObjectMapper();
        String policyTemplate = "{\"Version\": \"2012-10-17\", \"Statement\": [{\"Effect\": \"Allow\", \"Action\": [\"ses:SendEmail\", \"ses:SendRawEmail\"],\"Resource\":\"*\", \"Condition\": {\"StringEquals\": {\"ses:FromAddress\": \"to-replace\"}}}]}";
        try {
            JsonNode root = mapper.readTree(policyTemplate);
            JsonNode fromRestriction = root.path("Statement").path(0).path("Condition").path("StringEquals");
            ((ObjectNode) fromRestriction).put("ses:FromAddress", tenantEmail);

            return root.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void verifyEmailAddress(String address) {
        AmazonSimpleEmailService ses = new AmazonSimpleEmailServiceClient(awsFactory.createBasicCredentials());
        ListVerifiedEmailAddressesResult verifiedEmails = ses.listVerifiedEmailAddresses();
        if (verifiedEmails.getVerifiedEmailAddresses().contains(address)){
            return;
        }

        ses.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress(address));
    }

    /**
     * This method is needed to generate an SMTP password to be used with SES, from the given IAM secret key. Based on
     * example code from the SES documentation.
     *
     * @see <a href="https://docs.aws.amazon.com/ses/latest/DeveloperGuide/smtp-credentials.html#smtp-credentials-convert">SES docmentation</a>
     * @param key the secret key to generate an SMTP password from
     * @return the SMTP password
     */
    private String generateSmtpPassword(String key) {
        if (key == null) {
            throw new RuntimeException("A key must be supplied when generating SMTP credentials");
        }

        // Create an HMAC-SHA256 key from the raw bytes of the AWS secret access key.
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");

        try {
            // Get an HMAC-SHA256 Mac instance and initialize it with the AWS secret access key.
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);

            // Compute the HMAC signature on the input data bytes.
            byte[] rawSignature = mac.doFinal("SendRawEmail".getBytes());

            // Prepend the version number to the signature.
            byte[] rawSignatureWithVersion = new byte[rawSignature.length + 1];
            byte[] versionArray = {0x02};
            System.arraycopy(versionArray, 0, rawSignatureWithVersion, 0, 1);
            System.arraycopy(rawSignature, 0, rawSignatureWithVersion, 1, rawSignature.length);

            // To get the final SMTP password, convert the HMAC signature to base 64.
            return DatatypeConverter.printBase64Binary(rawSignatureWithVersion);
        } catch (Exception ex) {
            throw new RuntimeException("There was an error generating the SMTP password: " + ex.getMessage());
        }
    }
}
