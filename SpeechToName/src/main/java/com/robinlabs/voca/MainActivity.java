package com.robinlabs.voca;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.robinlabs.SpeechToName.R;

import java.util.ArrayList;

public class MainActivity extends Activity {

    NameMatcher nameMatcher;

    Voice voice;
    private ParseSpeech parseSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parseSpeech = new ParseSpeech(this);

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
                //recognizeVoice();
            }
        });
        findViewById(R.id.message_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.currentText = null;
                App.readyToSend = false;
                //recognizeVoice();
            }
        });

        //recognizeVoice();
    }

    protected void onSpeech(ArrayList<String> matches) {
        whatNext();
    }

    private void whatNext() {
        String speech = null;
        if (App.currentContact == null && App.currentText == null) {
            speech = "Say something like: text John that I'm on my way";
        } else if (App.currentContact != null && App.currentText != null && !App.readyToSend) {
            speech = App.currentText + " to " + App.currentContact.name + " should I send it?";
        } else if (App.readyToSend) {
            speech = "Message sent";
            (new Texting()).send(App.currentContact.phoneNumber, App.currentText);
            finish();
        }

        voice.speakOut(speech, speech);
    }


    protected void onPartialSpeech(ArrayList<String> matches) {
        parseSpeech.parse(matches);
        refreshView();
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
        voice.listen();
    }

    private void refreshView() {

        if (App.currentContact != null) {
            ((TextView) findViewById(R.id.recipient_name)).setText(App.currentContact.name);
        }

        if (App.currentText != null) {
            ((TextView) findViewById(R.id.message_text)).setText(App.currentText);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        App.cleanStatus();
    }
}
