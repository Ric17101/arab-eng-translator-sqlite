package com.eng.arab.translator.androidtranslator.dictinary;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;



public class DictionarySuggestion implements SearchSuggestion {

    public static final Creator<DictionarySuggestion> CREATOR = new Creator<DictionarySuggestion>() {
        @Override
        public DictionarySuggestion createFromParcel(Parcel in) {
            return new DictionarySuggestion(in);
        }

        @Override
        public DictionarySuggestion[] newArray(int size) {
            return new DictionarySuggestion[size];
        }
    };
    private String mDictionaryWord;
    private boolean mIsHistory = false;

    public DictionarySuggestion(String suggestion) {
        //this.mDictionaryWord = suggestion.toLowerCase();
        this.mDictionaryWord = suggestion;
    }

    public DictionarySuggestion(Parcel source) {
        this.mDictionaryWord = source.readString();
        this.mIsHistory = source.readInt() != 0;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }

    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    @Override
    public String getWord() {
        return mDictionaryWord;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDictionaryWord);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}