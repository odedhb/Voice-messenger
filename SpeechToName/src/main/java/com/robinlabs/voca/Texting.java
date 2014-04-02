package com.robinlabs.voca;

import android.telephony.SmsManager;

/**
 * Created by oded on 3/31/14.
 */
public class Texting {


    public void send(String phoneNumber, String message) {
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
