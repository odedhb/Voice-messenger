package com.robinlabs.voca;

import android.content.Context;
import android.view.Gravity;

/**
 * Created by oded on 3/31/14.
 */
public class Toast {


    public static void makeText(Context ctx, String text) {
        android.widget.Toast t = android.widget.Toast.makeText(ctx, text, android.widget.Toast.LENGTH_SHORT);
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 64);
        t.show();
    }

}
