/*
 * Copyright (c) 2016 Richard C.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eng.arab.translator.androidtranslator.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arlib.floatingsearchview.util.Util;
import com.eng.arab.translator.androidtranslator.R;
import com.eng.arab.translator.androidtranslator.number.NumberWrapper;

import java.util.ArrayList;
import java.util.List;

public class NumberListAdapter extends RecyclerView.Adapter<NumberListAdapter.ViewHolder> {

    private static final String LOG_TAG = "RecyclerViewAdapter";
    private final int CHECK_CODE = 0x1;
    private final int SHORT_DURATION = 1000;
    private List<NumberWrapper> mDataSet = new ArrayList<>();
    private NumberWrapper numberSuggestion;
    private CallbackInterface mCallback;
    private int mLastAnimatedItemPosition = -1;
    private int mLasItemPosition = 0;
    private Context mContext;
    /*private TextToSpeech engine;
    private double pitch = 1.0;
    private double speed = 1.0;*/
    //TTS object
    private TextToSpeech _speaker;
    private OnItemClickListener mItemsOnClickListener;
    private int counterOnBindViewHolder = 0;

    public NumberListAdapter(Context context) {
        mContext = context;

        // .. Attach the interface
        try {
            mCallback = (CallbackInterface) context;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
            Log.e("MyAdapter", "Must implement the CallbackInterface in the Activity", ex);
        }
    }

    public NumberListAdapter(List<NumberWrapper> numberList) {
        this.mDataSet = numberList;
        notifyDataSetChanged();
    }

    public NumberListAdapter(TextToSpeech speaker) {
        _speaker = speaker;
    }

    public void swapData(List<NumberWrapper> mNewDataSet) {
        mDataSet = mNewDataSet;
        notifyDataSetChanged();
    }

    public void setItemsOnClickListener(OnItemClickListener onClickListener) {
        this.mItemsOnClickListener = onClickListener;
    }

    @Override
    public NumberListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_number_list_item, parent, false);
        mContext = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NumberListAdapter.ViewHolder holder, final int position) {
        numberSuggestion = mDataSet.get(holder.getAdapterPosition());
        holder.mNumberName.setText(numberSuggestion.getNumber());
        holder.mNumberValue.setText(numberSuggestion.getExample());
//        Log.i("TAG BindViewHolder1: ", colorSuggestion.getLetter());
//        int color = Color.parseColor(colorSuggestion.getHex());
        Log.d(LOG_TAG, "onBindViewHolder (" + ++counterOnBindViewHolder + ") = " + numberSuggestion.getExample());
        int color = Color.BLACK;
        holder.mNumberName.setTextColor(color);
        holder.mNumberValue.setTextColor(color);
        holder.textViewPronunciation.setText("\\" + numberSuggestion.getPronunciation() + "\\");
        holder.textViewWordType.setText("letter");
        //holder.buttonPlayVideo.

        if (mLastAnimatedItemPosition < position) {
            animateItem(holder.itemView);
            mLastAnimatedItemPosition = holder.getAdapterPosition(); // or mLastAnimatedItemPosition = position;
        }

        /*if(mItemsOnClickListener != null){
            holder.buttonSpeakAlphabet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    *//*mItemsOnClickListener.onClick(mDataSet.get(position));
                    Toast.makeText(v.getContext(), "Item " + position, Toast.LENGTH_SHORT).show();
*//*
                    if(mCallback != null){
                        mCallback.onHandleSelection(position, mDataSet.get(position));
                        Toast.makeText(v.getContext(), "Item " + position, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }*/

        // Use your listener to pass back the data used FOR callback
        /*holder.buttonSpeakAlphabet.onClick(new View.onClick() {
            @Override
            public void onClick(View v) {
                if(mCallback != null){
                    mCallback.onHandleSelection(position, mDataSet.get(position));
                    Toast.makeText(v.getContext(), "Item " + position, Toast.LENGTH_SHORT).show();
                }
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    private void animateItem(View view) {
        view.setTranslationY(Util.getScreenHeight((Activity) view.getContext()));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

    public interface CallbackInterface {

        /**
         * Callback invoked when clicked
         *
         * @param position - the position
         * @param text     - the text to pass back
         */
        void onHandleSelection(int position, NumberWrapper text, int mode);
    }

    public interface OnItemClickListener {
        void onClick(NumberWrapper colorWrapper);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mNumberName;
        public final TextView mNumberValue;
        public final View mTextContainer;
        public final TextView textViewPronunciation;
        public final TextView textViewWordType;
        //public final ImageButton buttonSpeakAlphabet;
        public final ImageButton buttonPlayVideo;

        public ViewHolder(View view) {
            super(view);

            mNumberName = (TextView) view.findViewById(R.id.number_name);
            mNumberValue = (TextView) view.findViewById(R.id.number_value);
            textViewPronunciation = (TextView) view.findViewById(R.id.textViewPronunciation_number);
            textViewWordType = (TextView) view.findViewById(R.id.textViewWordType_number);
            mTextContainer = view.findViewById(R.id.text_number_container);
            /*buttonSpeakAlphabet = (ImageButton) view.findViewById(R.id.buttonSpeak_alphabet);
            buttonSpeakAlphabet.setOnClickListener(this);*/
            buttonPlayVideo = (ImageButton) view.findViewById(R.id.buttonPlayVideo_number);
            buttonPlayVideo.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            /*if (v.getId() == buttonSpeakAlphabet.getId()) {
                if (mCallback != null){
                    mCallback.onHandleSelection(getAdapterPosition(), mDataSet.get(getAdapterPosition()), 1);
                }
            } else*/
            if (v.getId() == buttonPlayVideo.getId()) {
                //Toast.makeText(v.getContext(), "TEST: " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                if (mCallback != null) {
                    mCallback.onHandleSelection(getAdapterPosition(), mDataSet.get(getAdapterPosition()), 2);
                }
            }

        }

        /*@Override
        public void onClick(View v) {
            if (v.getId() == buttonSpeakAlphabet.getId()) {
                //speakWords("TEST");
                //_speaker.speak("TEST");
                *//*HashMap<String, String> hash = new HashMap<String,String>();
                hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));

                _speaker.speak("TEST", TextToSpeech.QUEUE_ADD, hash);*//*

                Toast.makeText(v.getContext(), "TEST", Toast.LENGTH_SHORT).show();
                *//*TextToSpeech tts = new TextToSpeech(mContext.getApplicationContext(), null);
                tts.setLanguage(Locale.US);
                tts.speak("Text to say aloud", TextToSpeech.QUEUE_ADD, null);*//*
            }
        }*/
    }

    /**********TTS INIT***********/
/*    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d("SpeechOnInit", "Success!");
            engine.setLanguage(Locale.US);
        }
    }

    private void speakWords(String speak) {
//        engine.setPitch((float) 1.0);
//        engine.setSpeechRate((float) 1.0);
//        engine.speak(speak, TextToSpeech.QUEUE_FLUSH, null, null);
        engine.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
    }*/

}
