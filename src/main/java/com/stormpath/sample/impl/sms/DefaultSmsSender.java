package com.stormpath.sample.impl.sms;

import com.stormpath.sample.api.sms.SendSmsRequest;
import com.stormpath.sample.api.sms.SmsSender;
import com.stormpath.sdk.lang.Assert;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Sms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 0.1
 */
@Component
public class DefaultSmsSender implements SmsSender {

    private final static Logger logger = LoggerFactory.getLogger(SmsSender.class);

    private final TwilioRestClient smsClient;

    private final String fromPhoneNumber;

    @Autowired
    public DefaultSmsSender(@Value("${sms.developerKey.id}") String smsClientId,
                            @Value("${sms.developerKey.secret}") String smsClientSecret,
                            @Value("${sms.fromPhoneNumber}") String fromPhoneNumber) {

        Assert.hasText(smsClientId, "smsClientId cannot be null or empty.");
        Assert.hasText(smsClientSecret, "smsClientSecret cannot be null or empty.");
        Assert.hasText(fromPhoneNumber, "fromPhoneNumber cannot be null or empty.");

        this.smsClient = new TwilioRestClient(smsClientId, smsClientSecret);
        this.fromPhoneNumber = fromPhoneNumber;
    }

    private Map<String, String> createMessageBody(String body, String toPhoneNumber) {
        Map<String, String> params = new HashMap<String, String>();

        params.put("Body", body);
        params.put("To", toPhoneNumber);
        params.put("From", fromPhoneNumber);

        return params;
    }

    @Override
    public void send(SendSmsRequest sendSmsRequest) {
        Assert.notNull(sendSmsRequest, "sendSmsRequest cannot be null");
        Map<String, String> messages = createMessageBody(sendSmsRequest.getMessageBody(), sendSmsRequest.getPhoneNumber());
        SmsFactory messageFactory = smsClient.getAccount().getSmsFactory();
        try {
            Sms message = messageFactory.create(messages);

            //Store the message info.
            if (logger.isInfoEnabled()) {
                logger.info("The message sent info sid: {} and sent on: {}", message.getSid(), message.getDateSent());
            }
        } catch (TwilioRestException e) {
            logger.error("Twilio error {} for phone number {}", e.getMessage(), sendSmsRequest.getPhoneNumber());
            throw new IllegalStateException("Unable to send challenge.");
        }
    }
}