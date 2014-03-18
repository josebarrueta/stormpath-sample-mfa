package com.stormpath.sample.impl.challenge;

import com.stormpath.sample.api.challenge.ChallengeTokenVerifier;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.lang.Assert;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * @since 0.1
 */
public class DefaultChallengeTokenVerifier implements ChallengeTokenVerifier {

    @Value("${challengeToken.timeToLive.inSeconds}")
    private int challengeTokenTimeToLiveInSeconds;

    @Override
    public void verifyChallengeToken(Account account, String token) {
        Assert.notNull(account, "account cannot be null.");
        Assert.hasText(token, "token cannot be null or empty.");

        //Retrieve the account customData
        CustomData customData = account.getCustomData();

        Map<String, Object> challengeTokenInfo = (Map<String, Object>) customData.get("challengeTokenInfo");

        Assert.notEmpty(challengeTokenInfo, "There isn't challengeTokenInfo for this account.");

        String challengeToken = (String) challengeTokenInfo.get("challengeToken");

        if (!token.equals(challengeToken)) {
            //Throw proper exception
            throw new IllegalStateException("The submitted token doesn't match.");
        }

        //Check if the token is still valid
        Long createAtInMillis = (Long) challengeTokenInfo.get("createdAtInMillis");

        DateTime createdAt = new DateTime(createAtInMillis, DateTimeZone.UTC);

        Duration duration = new Duration(createdAt, DateTime.now());

        if (challengeTokenTimeToLiveInSeconds < duration.toStandardSeconds().getSeconds()) {
            //Throw proper exception
            throw new IllegalStateException("The submitted token is not longer valid.");
        }

    }
}
