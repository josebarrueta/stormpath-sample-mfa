package com.stormpath.sample.api.challenge;

import com.stormpath.sdk.account.Account;

/**
 * @since 0.1
 */
public interface ChallengeTokenVerifier {

    //TODO: Throw a proper exception for token verification
    void verifyChallengeToken(Account account, String token) throws IllegalStateException;

}
