package com.manywho.services.provisioning.providers;

import com.google.inject.Provider;

import java.security.SecureRandom;

public class SecureRandomProvider implements Provider<SecureRandom> {
    @Override
    public SecureRandom get() {
        return new SecureRandom();
    }
}
