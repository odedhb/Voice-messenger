package com.robinlabs.voca;

import android.content.Context;

public class App extends android.app.Application {

    private static App instance;

    static Task task;

    static int userTries;


    public App() {
        instance = this;
        task = new Task();
    }

    public static Context getContext() {
        return instance;
    }

    public static void cleanStatus() {
        task = new Task();
    }

    public static String getTryAgainText() {
        userTries++;
        if (userTries < 3) {
            return "Say something like: TEXT John THAT I'm on my way.";
        }

        if (userTries < 5) {
            return "Specifically use the words .TEXT. and .THAT. in your sentence.";
        }

        return "try again";
    }
}