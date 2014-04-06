package com.robinlabs.voca;

import android.content.Context;

public class App extends android.app.Application {

    private static App instance;

    static Task task;


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
}