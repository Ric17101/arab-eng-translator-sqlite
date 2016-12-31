package com.eng.arab.translator.androidtranslator.activity;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;

public class Speaker implements TextToSpeech.OnInitListener {
    private TextToSpeech _tts;
    private boolean _ready = false;
    private boolean _allowed = false;

    public Speaker(Context context){
        _tts = new TextToSpeech(context, this);
    }

    public void setSpeedRate(float speechrate) {
        _tts.setSpeechRate(speechrate);
    }

    public boolean isAllowed(){
        return _allowed;
    }

    public void allow(boolean allowed){
        _allowed = allowed;
    }

    public TextToSpeech getTTS(){
        return this._tts;
    }

    @Override
    public void onInit(int status) {
        System.out.println("I'm in onInit from Speaker");
        if(status == TextToSpeech.SUCCESS){
            _tts.setLanguage(Locale.US);
            _ready = true;
        } else{
            _ready = false;
        }
    }

    public void speak(String text) {
//        if(_ready && _allowed) {
            HashMap<String, String> hash = new HashMap<String,String>();
            hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));
            _tts.speak(text, TextToSpeech.QUEUE_ADD, hash);
            System.out.println("speackIsWorkingBaby");
//        }
    }

    public boolean isSpeaking() {
        return _tts.isSpeaking();
    }

    public void pause(int duration){
        _tts.playSilence(duration, TextToSpeech.QUEUE_ADD, null);
    }

    public void stop() {
        _tts.stop();
    }

    public void destroy(){
        _tts.shutdown();
    }
}