package com.robinlabs.voca;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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

        findViewById(R.id.who_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voice.listen();
            }
        });

        findViewById(R.id.recipient_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.task.currentContact = null;
                App.task.readyToSend = false;
                //recognizeVoice();
            }
        });
        findViewById(R.id.message_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.task.currentText = null;
                App.task.readyToSend = false;
                //recognizeVoice();
            }
        });


        ((TextView) findViewById(R.id.help_text)).setText(Html.fromHtml("<b>TEXT</b> John <b>THAT</b> I'm on my way"));

        //recognizeVoice();
    }

    protected void onSpeech(ArrayList<String> matches) {
        parseSpeech.parse(matches);
        whatNext();
        refreshView();
    }

    private void whatNext() {
        String speech = null;
        if (App.task.currentContact == null && App.task.currentText == null) {
            speech = "Say something like: text John that I'm on my way";
        } else if (App.task.currentContact != null && App.task.currentText != null && !App.task.readyToSend) {
            speech = App.task.currentText + " to " + App.task.currentContact.name + " should I send it?";
        } else if (App.task.readyToSend) {
            speech = "Message sent";
            (new Texting()).send(App.task.currentContact.phoneNumber, App.task.currentText);
            App.task = new Task();
            refreshView();
        }

        voice.speakOut(speech, speech);
    }


    protected void onPartialSpeech(ArrayList<String> matches) {
        Log.d("speech_match", matches.get(0));
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

        if (App.task.currentContact != null) {
            ((TextView) findViewById(R.id.recipient_name)).setText(App.task.currentContact.name);
        } else {
            ((TextView) findViewById(R.id.recipient_name)).setText("");
        }

        if (App.task.currentText != null) {
            ((TextView) findViewById(R.id.message_text)).setText(App.task.currentText);
        } else {
            ((TextView) findViewById(R.id.message_text)).setText("");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        App.cleanStatus();
    }

    public void isListening(boolean isListening) {
        if (isListening) {
            ((ImageButton) findViewById(R.id.who_button)).setImageResource(R.drawable.mic_button_on);
        } else {
            ((ImageButton) findViewById(R.id.who_button)).setImageResource(R.drawable.mic_button_off);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        voice = new Voice(this);
    }

    @Override
    protected void onStop() {
        voice.shutDown();
        voice = null;
        super.onStop();
    }

}
