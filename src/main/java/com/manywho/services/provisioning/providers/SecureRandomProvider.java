package com.manywho.services.provisioning.providers;

import com.google.inject.Provider;

import javax.inject.Singleton;
import java.security.SecureRandom;

@Singleton
public class SecureRandomProvider implements Provider<SecureRandom> {
    @Override
    public SecureRandom get() {
        return new SecureRandom();
    }
}
