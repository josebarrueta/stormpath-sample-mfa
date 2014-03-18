package com.stormpath.sample.impl.challenge;

import com.stormpath.sample.api.challenge.TokenGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Sample code to generate an eight digit random number.
 *
 * @since 0.1
 */
@Component
public class SampleTokenGenerator implements TokenGenerator {

    private final SecureRandom secureRandom;

    private final static int NUM_BYTES = 100000000;

    public SampleTokenGenerator() {
        secureRandom = new SecureRandom();
    }

    @Override
    public String generateToken() {
        return String.format("%08d", secureRandom.nextInt(NUM_BYTES));
    }
}
