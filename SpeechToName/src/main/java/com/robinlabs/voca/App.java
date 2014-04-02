package com.robinlabs.voca;

import android.content.Context;

public class App extends android.app.Application {

    private static App instance;

    public static Contact currentContact;
    public static String currentText;
    public static boolean readyToSend;
    public static boolean messageSent;


    public App() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    public static void cleanStatus() {
        currentContact = null;
        currentText = null;
        readyToSend = false;
        messageSent = false;
    }
}