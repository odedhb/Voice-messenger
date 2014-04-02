package com.robinlabs.voca;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by oded on 12/8/13.
 */
public class TextToSpeechListener implements TextToSpeech.OnInitListener {

    private final MainActivity mainActivity;
    TextToSpeech tts;

    TextToSpeechListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        tts = new TextToSpeech(mainActivity, this);
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);
            tts.setSpeechRate(0.8f);

            int uResult = tts.setOnUtteranceProgressListener(new PolyUtteranceProgressListener(mainActivity));

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initialization Failed!");
            Toast.makeText(mainActivity, "TTS Initialization Failed!", Toast.LENGTH_SHORT).show();
        }

    }

    public void speakOut(String show, String read) {

//        AudioManager am = (AudioManager) mainActivity.getSystemService(Context.AUDIO_SERVICE);

//        am.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put(TextToSpeech.Engine.KEY_FEATURE_NETWORK_SYNTHESIS, String.valueOf(true));
        hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, show);
//        hashMap.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
        tts.speak(read, TextToSpeech.QUEUE_FLUSH, hashMap);

    }

    public void stop() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
//            tts.shutdown();
        }
    }

    public void shutDown() {
        if (tts != null) {
            tts.shutdown();
        }
    }

}
