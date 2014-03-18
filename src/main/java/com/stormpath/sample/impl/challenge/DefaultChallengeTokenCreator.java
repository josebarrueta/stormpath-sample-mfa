package com.stormpath.sample.impl.challenge;

import com.stormpath.sample.api.challenge.ChallengeTokenCreator;
import com.stormpath.sample.api.challenge.TokenGenerator;
import com.stormpath.sample.api.sms.SendSmsRequest;
import com.stormpath.sample.api.sms.SmsSender;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.lang.Assert;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 0.1
 */
@Component
public class DefaultChallengeTokenCreator implements ChallengeTokenCreator {

    private final static String SMS_BODY_TEMPLATE = "Hi, %s here is your verification code: %s";

    @Autowired
    private SmsSender smsSender;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Override
    public void createChallengeToken(Account account) {
        Assert.notNull(account);

        String challengeToken = tokenGenerator.generateToken();

        CustomData customData = account.getCustomData();

        // Here we assume that the application has the user phone number and
        // store it in the account customData with "phone" as the property name.
        String phoneNumber = (String) customData.get("phone");

        String messageBody = String.format(SMS_BODY_TEMPLATE, account.getGivenName(), challengeToken);

        //Send the message by SMS.
        smsSender.send(new SendSmsRequest(messageBody, phoneNumber));

        Map<String, Object> challengeTokenInfo = new HashMap<String, Object>();

        Long createdAtInMillis = DateTime.now(DateTimeZone.UTC).getMillis();

        challengeTokenInfo.put("challengeToken", challengeToken);
        challengeTokenInfo.put("createdAtInMillis", createdAtInMillis);


        //The new challenge will be saved in the account customData with
        //and can be accessed using the "challengeTokenInfo" key.
        customData.put("challengeTokenInfo", challengeTokenInfo);
        customData.save();
    }
}
