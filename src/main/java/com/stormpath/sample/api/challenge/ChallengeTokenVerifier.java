package com.stormpath.sample.api.challenge;

import com.stormpath.sdk.account.Account;

/**
 * @since 0.1
 */
public interface ChallengeTokenVerifier {

    void verifyChallengeToken(Account account, String token);

}
