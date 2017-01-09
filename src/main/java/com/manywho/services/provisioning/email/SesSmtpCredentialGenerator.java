package com.manywho.services.provisioning.email;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class SesSmtpCredentialGenerator {
    private static final byte VERSION = 0x02; // Version number. Do not modify.
    private static final String MESSAGE = "SendRawEmail"; // Used to generate the HMAC signature. Do not modify.

    public String getSmtpPassword(String key) {

        // Get the AWS secret access key from environment variable AWS_SECRET_ACCESS_KEY.
        if (key == null) {
            System.out.println("Error: Cannot find environment variable AWS_SECRET_ACCESS_KEY.");
            System.exit(0);
        }

        // Create an HMAC-SHA256 key from the raw bytes of the AWS secret access key.
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");

        try {
            // Get an HMAC-SHA256 Mac instance and initialize it with the AWS secret access key.
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);

            // Compute the HMAC signature on the input data bytes.
            byte[] rawSignature = mac.doFinal(MESSAGE.getBytes());

            // Prepend the version number to the signature.
            byte[] rawSignatureWithVersion = new byte[rawSignature.length + 1];
            byte[] versionArray = {VERSION};
            System.arraycopy(versionArray, 0, rawSignatureWithVersion, 0, 1);
            System.arraycopy(rawSignature, 0, rawSignatureWithVersion, 1, rawSignature.length);

            // To get the final SMTP password, convert the HMAC signature to base 64.
            return DatatypeConverter.printBase64Binary(rawSignatureWithVersion);

        } catch (Exception ex) {
            System.out.println("Error generating SMTP password: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}