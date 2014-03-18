package com.stormpath.sample.api.sms;

import com.stormpath.sdk.lang.Assert;

/**
 * @since 0.1
 */
public class SendSmsRequest {

    private final String messageBody;

    private final String phoneNumber;

    public SendSmsRequest(String messageBody, String phoneNumber) {
        Assert.hasText(messageBody, "messageBody cannot be null or empty.");
        Assert.hasText(phoneNumber, "phoneNumber cannot be null or empty.");
        this.messageBody = messageBody;
        this.phoneNumber = phoneNumber;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
