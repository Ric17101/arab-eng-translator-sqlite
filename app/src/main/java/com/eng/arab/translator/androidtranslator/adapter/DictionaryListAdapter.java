package com.eng.arab.translator.androidtranslator.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.util.Util;
import com.eng.arab.translator.androidtranslator.R;
import com.eng.arab.translator.androidtranslator.ShowDetailsDictionary;
import com.eng.arab.translator.androidtranslator.dictinary.DictionaryModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DictionaryListAdapter extends RecyclerView.Adapter<DictionaryListAdapter.ViewHolder>
        implements TextToSpeech.OnInitListener {

    private List<DictionaryModel> mDataSet = new ArrayList<>();
    private DictionaryModel alphabetSuggestion;
    private int mLastAnimatedItemPosition = -1;
    private int mLasItemPosition = 0;
    private Context mContext;

    //TTS object
    private TextToSpeech engine;
    private double pitch = 1.0;
    private double speed = 1.0;

    public DictionaryListAdapter() { }

    public DictionaryListAdapter(List<DictionaryModel> wordList) {
        this.mDataSet = wordList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onClick(DictionaryModel colorWrapper);
    }

    private OnItemClickListener mItemsOnClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView dictionaryName;
        public final CardView cv_word_item;

        public ViewHolder(View view) {
            super(view);
            //tts_init();
            dictionaryName = (TextView) view.findViewById(R.id.dictionary_name);
            cv_word_item = (CardView) view.findViewById(R.id.cv_word_item);
            cv_word_item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // speakWords("TEST");
            //Toast.makeText(v.getContext(), "TEST", Toast.LENGTH_SHORT).show();
            v.getContext().startActivity(new Intent(v.getContext(), ShowDetailsDictionary.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            //overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            Intent intent = new Intent(v.getContext(), ShowDetailsDictionary.class);
            intent.putExtra("MONTH", dictionaryName.getText().toString());
            v.getContext().startActivity(intent);

            // Set up animation to the this view
            Activity a = (Activity) v.getContext();
            a.overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    public void swapData(List<DictionaryModel> mNewDataSet) {
        mDataSet = mNewDataSet;
        notifyDataSetChanged();
    }

    public void setItemsOnClickListener(OnItemClickListener onClickListener){
        this.mItemsOnClickListener = onClickListener;
    }

    @Override
    public DictionaryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_dictionary_list_item, parent, false);
        mContext = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DictionaryListAdapter.ViewHolder holder, final int position) {
        alphabetSuggestion = mDataSet.get(holder.getAdapterPosition());
        holder.dictionaryName.setText(alphabetSuggestion.getArabic());

        if (mLastAnimatedItemPosition < position) {
            animateItem(holder.itemView);
            mLastAnimatedItemPosition = holder.getAdapterPosition(); // or mLastAnimatedItemPosition = position;
        }

        if(mItemsOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemsOnClickListener.onClick(mDataSet.get(position));
                    Toast.makeText(v.getContext(), "Item " + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
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

    /**********TTS INIT***********/
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d("Speech", "Success!");
            engine.setLanguage(Locale.UK);
        }
    }

    private void speakWords(String speak) {
        engine.setPitch((float) 1.0);
        engine.setSpeechRate((float) 1.0);
        engine.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
//        engine.speak(speak, null,null);
//        engine.speak(speak, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
