package com.robinlabs.voca;

import android.app.Activity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by oded on 4/6/14.
 */
public class ParseSpeech {

    private final Activity activity;

    ParseSpeech(Activity activity) {
        this.activity = activity;
    }


    void parse(ArrayList<String> matches) {

        NameMatcher nameMatcher = new NameMatcher(activity);

        String str = matches.get(0);
        Pattern pattern = Pattern.compile("text (.*?) that");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {

            String name = matcher.group(1);
            String message = str.replace("text " + name + " that ", "");

            nameMatcher.match(name);
            App.task.currentText = message;
        }

        if (Meaning.equals(Meaning.OK, str)) {
            App.task.readyToSend = true;
        } else if (Meaning.equals(Meaning.NO, str) || Meaning.equals(Meaning.STOP, str)) {
            App.task = new Task();
        }


    }

}
