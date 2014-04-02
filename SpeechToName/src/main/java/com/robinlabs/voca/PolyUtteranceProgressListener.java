package com.robinlabs.voca;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by oded on 12/10/13.
 */
public class PolyUtteranceProgressListener extends UtteranceProgressListener {
    private MainActivity mainActivity;

    PolyUtteranceProgressListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    public void onStart(final String utteranceId) {
        Log.d("TTS", "start speak");

/*
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) mainActivity.findViewById(R.id.words)).setText(TheBrainer.instance.currentNeed.toScreen());
            }
        });*/

    }

    @Override
    public void onDone(final String utteranceId) {
        Log.d("TTS", "done speak");

        if(App.messageSent)return;

        gatherSpeech(utteranceId);

    }

    @Override
    public void onError(String utteranceId) {
        Log.d("TTS", "error utterance");
    }



    public Intent getRecognizeIntent(String promptToUse, int maxResultsToReturn)
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResultsToReturn);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, promptToUse);
        return intent;
    }

    public void gatherSpeech(String prompt)
    {
        Intent recognizeIntent = getRecognizeIntent(prompt,50);
        try
        {
            mainActivity.startActivityForResult(recognizeIntent, MainActivity.VOICE_RECOGNITION_REQUEST_CODE);
        }
        catch (ActivityNotFoundException actNotFound)
        {
            Log.w("voice", "did not find the speech activity, not doing it");
        }
    }


}
