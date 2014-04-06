package com.robinlabs.voca;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by oded on 12/8/13.
 */
public class Voice extends UtteranceProgressListener implements TextToSpeech.OnInitListener, RecognitionListener, TextToSpeech.OnUtteranceCompletedListener {

    private final MainActivity mainActivity;
    TextToSpeech tts;
    SpeechRecognizer sr = null;
    boolean useFreeForm = true;

    /*****************************************************************************************************/
    /************************* initialization/finalization ***********************************************/
    /*****************************************************************************************************/
    Voice(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        tts = new TextToSpeech(mainActivity, this);

        sr = SpeechRecognizer.createSpeechRecognizer(mainActivity, findSpeechRecognizerComponent());
        if (sr != null)
            sr.setRecognitionListener(this);
    }

    public void shutDown() {
        if (tts != null) {
            tts.shutdown();
        }
        if (sr != null) {
            runInMainUiThread(new Runnable() {
                @Override
                public void run() {
                    sr.destroy();
                    sr = null;
                }
            });
        }
    }

    /*****************************************************************************************************/
    /********** text to speech events ********************************************************************/
    /*****************************************************************************************************/
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);
            tts.setSpeechRate(0.8f);

            int uResult = tts.setOnUtteranceProgressListener(this);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initialization Failed!");
            Toast.makeText(mainActivity, "TTS Initialization Failed!", Toast.LENGTH_SHORT).show();
        }

    }

    /*****************************************************************************************************/
    /********** text to speech ***************************************************************************/
    /*****************************************************************************************************/
    public void speakOut(String show, String read) {

        runInMainUiThread(new Runnable() {
            @Override
            public void run() {
                sr.stopListening();
            }
        });

        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put(TextToSpeech.Engine.KEY_FEATURE_NETWORK_SYNTHESIS, String.valueOf(true));
        hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, show);
//        hashMap.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
        tts.speak(read, TextToSpeech.QUEUE_FLUSH, hashMap);

    }

    /*****************************************************************************************************/
    /********** speech to text ***************************************************************************/
    /*****************************************************************************************************/
    public static boolean inMainGuiThread() {
        Looper ml=Looper.myLooper();
        return ml!=null&&ml==Looper.getMainLooper();
    }

    public static void runInMainUiThread(Runnable r) {
        if (inMainGuiThread())
            r.run();
        else
            new Handler(Looper.getMainLooper()).post(r);
    }

    public void listen() {
        // prepare intent
        final Intent vri = findSpeechRecognizerIntent();//new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        vri.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, MainActivity.class.getPackage().getName());

        if (useFreeForm) {
            vri.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            vri.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            vri.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        }
        else {
            vri.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
            vri.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
            vri.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        }

        // vri.putExtra(RecognizerIntent.EXTRA_LANGUAGE, newLang);

        if (sr != null)
            runInMainUiThread(new Runnable() {
                @Override
                public void run() {
                    sr.startListening(vri);
                }
            });
    }

    protected ComponentName findSpeechRecognizerComponent() {
        PackageManager pm = mainActivity.getPackageManager();
        Intent it = new Intent("android.speech.RecognitionService");
        List<ResolveInfo> ss=pm.queryIntentServices(it, PackageManager.GET_SERVICES);
        if ((ss != null) && (ss.size() > 0)) {
            for (ResolveInfo ri:ss) {
                ServiceInfo si=ri.serviceInfo;
                if (si.name.contains("google")) return new ComponentName(si.packageName,si.name);
            }
            ServiceInfo si=ss.get(0).serviceInfo;
            return new ComponentName(si.packageName,si.name);
        }

        return null;
    }

    protected Intent findSpeechRecognizerIntent() {

        PackageManager pm = mainActivity.getPackageManager();

        Intent it = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        it.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, MainActivity.class
                .getPackage().getName());

        List<ResolveInfo> rsi = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if ((rsi != null) && (rsi.size() > 0)) {
            for (ResolveInfo ri : rsi) {
                ActivityInfo ai = ri.activityInfo;
                if (ai.name!=null&&ai.name.contains(".google.")) {
                    String s = ai.loadLabel(pm).toString().toLowerCase();

                    if (s.contains("voice search")) {
                        it.setClassName(ai.packageName, ai.name);
                        return it;
                    }
                }
            }
            ActivityInfo ai = rsi.get(0).activityInfo;
            it.setPackage(ai.packageName);
            // it.setClassName(ai.packageName, ai.name);
            return it;
        }

        return null;
    }

    /*************************************************************************************************/
    /************ events from SpeechToText ***********************************************************/
    /*************************************************************************************************/
    @Override
    public void onReadyForSpeech(Bundle params) {
        mainActivity.isListening(true);
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //mainActivity.
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
        mainActivity.isListening(false);
    }

    @Override
    public void onError(int error) {
        mainActivity.isListening(false);
    }

    @Override
    public void onResults(Bundle results) {
        mainActivity.isListening(false);
        if (results!=null) {
            ArrayList<String> list = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (list != null && list.size() > 0)
                mainActivity.onSpeech(list);
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        if (partialResults!=null) {
            ArrayList<String> list = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (list != null && list.size() > 0)
                mainActivity.onPartialSpeech(list);
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }

    /************************************************************************************************************************/
    /********************** Listening TTS completion ************************************************************************/
    /************************************************************************************************************************/
    @Override
    public void onUtteranceCompleted(String utteranceId) {
        Log.d("TTS", "start speak");

    }


    @Override
    public void onStart(String utteranceId) {

    }

    @Override
    public void onDone(String utteranceId) {
        listen();
    }

    @Override
    public void onError(String utteranceId) {

    }
}
