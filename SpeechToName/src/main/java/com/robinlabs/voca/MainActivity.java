package com.robinlabs.voca;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.robinlabs.SpeechToName.R;

import java.util.ArrayList;

public class MainActivity extends Activity {

    NameMatcher nameMatcher;

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1987;

    Voice voice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameMatcher = new NameMatcher(this);
        voice = new Voice(this);

        findViewById(R.id.who_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voice.listen();
            }
        });

        findViewById(R.id.recipient_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.currentContact = null;
                App.readyToSend = false;
                App.messageSent = false;
                //recognizeVoice();
            }
        });
        findViewById(R.id.message_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.currentText = null;
                App.readyToSend = false;
                App.messageSent = false;
                //recognizeVoice();
            }
        });

        //recognizeVoice();
    }

    protected void onSpeech(ArrayList<String> matches) {
        if (App.currentContact == null) {
            nameMatcher.match(matches);
        } else if (App.currentText == null) {
            App.currentText = matches.get(0);
            refreshView();
        } else if (Meaning.equals(Meaning.OK, matches.get(0))) {
            App.readyToSend = true;
        } else if (Meaning.equals(Meaning.NO, matches.get(0))) {
            App.currentText = null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();
    }

    private void refreshView() {

        if (App.currentContact != null) {
            ((TextView) findViewById(R.id.recipient_name)).setText(App.currentContact.name);
        }

        if (App.currentText != null) {
            ((TextView) findViewById(R.id.message_text)).setText(App.currentText);
        }

        String speech = null;
        if (App.currentContact == null && App.currentText == null) {
            speech = "Who would you like to text?";
        } else if (App.currentContact != null && App.currentText == null) {
            speech = "Say your message for " + App.currentContact.name;
        } else if (App.currentContact != null && App.currentText != null && !App.readyToSend) {
            speech = App.currentText + " should I send it?";
        } else if (App.readyToSend && !App.messageSent) {
            speech = "Message sent";
            (new Texting()).send(App.currentContact.phoneNumber, App.currentText);
            App.messageSent = true;
        }

        voice.speakOut(speech, speech);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        App.cleanStatus();
    }
}