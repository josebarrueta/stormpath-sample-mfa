package com.stormpath.sample.api.sms;

/**
 * @since 0.1
 */
public interface SmsSender {

    public void send(SendSmsRequest sendSmsRequest);

}
