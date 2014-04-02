package com.robinlabs.voca;

import java.util.HashMap;

/**
 * Created by oded on 2/18/14.
 */
public class Meaning {


    public static final int NO = 101;
    public static final int OK = 102;
    public static final int TEACH = 103;
    public static final int NEXT = 104;
    public static final int STOP = 105;


    static boolean equals(int meaning, String speech) {

        Integer foundMeaning = meaningsMap().get(speech);

        if (foundMeaning == null) return false;

        if (meaning == foundMeaning) return true;

        return false;
    }

    static boolean contains(int meaning, String speech) {

        for (String phrase : meaningsMap().keySet()) {
            if (speech.contains(phrase) && meaningsMap().get(phrase) == meaning) {
                return true;
            }
        }

        return false;
    }

    private static HashMap<String, Integer> meaningsMap() {
        HashMap<String, Integer> meaningsMap = new HashMap<String, Integer>();

        meaningsMap.put("no", NO);
        meaningsMap.put("nope", NO);

        meaningsMap.put("yes", OK);
        meaningsMap.put("ok", OK);
        meaningsMap.put("okay", OK);

        meaningsMap.put("teach", TEACH);
        meaningsMap.put("learn", TEACH);
        meaningsMap.put("something new", TEACH);

        meaningsMap.put("don't know", NEXT);
        meaningsMap.put("next", NEXT);
        meaningsMap.put("never mind", NEXT);
        meaningsMap.put("nevermind", NEXT);
        meaningsMap.put("whatever", NEXT);
        meaningsMap.put("none", NEXT);
        meaningsMap.put("doesn't matter", NEXT);

        meaningsMap.put("stop", STOP);
        meaningsMap.put("shut up", STOP);
        meaningsMap.put("exit", STOP);

        return meaningsMap;
    }
}
