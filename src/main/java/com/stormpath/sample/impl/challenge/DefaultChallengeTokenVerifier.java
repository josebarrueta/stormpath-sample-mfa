package com.stormpath.sample.impl.challenge;

import com.stormpath.sample.api.challenge.ChallengeTokenVerifier;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.lang.Assert;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @since 0.1
 */
@Component
public class DefaultChallengeTokenVerifier implements ChallengeTokenVerifier {

    @Value("${challengeToken.timeToLive.inSeconds}")
    private int challengeTokenTimeToLiveInSeconds;

    @Override
    public void verifyChallengeToken(Account account, String submittedToken) throws IllegalStateException {
        Assert.notNull(account, "account cannot be null.");
        Assert.hasText(submittedToken, "token cannot be null or empty.");

        //Retrieve the account customData
        CustomData customData = account.getCustomData();

        Map<String, Object> challengeTokenInfo = (Map<String, Object>) customData.get("challengeTokenInfo");

        Assert.notEmpty(challengeTokenInfo, "There isn't challengeTokenInfo for this account.");

        String storedToken = (String) challengeTokenInfo.get("challengeToken");

        //Check if the token is still valid
        Long createAtInMillis = (Long) challengeTokenInfo.get("createdAtInMillis");

        DateTime createdAt = new DateTime(createAtInMillis, DateTimeZone.UTC);

        try {
            verifyToken(submittedToken, storedToken, createdAt);
        } finally {
            //Clear challenge token info, since it should be valid just once.
            customData.remove("challengeTokenInfo");
            customData.save();
        }
    }

    private void verifyToken(String submittedToken, String storedToken, DateTime createdAt) {

        if (!submittedToken.equals(storedToken)) {
            //Throw proper exception
            throw new IllegalStateException("The submitted token doesn't match.");
        }

        Duration duration = new Duration(createdAt, DateTime.now());

        if (challengeTokenTimeToLiveInSeconds < duration.toStandardSeconds().getSeconds()) {
            //Throw proper exception
            throw new IllegalStateException("The submitted token is not longer valid.");
        }

    }
}
