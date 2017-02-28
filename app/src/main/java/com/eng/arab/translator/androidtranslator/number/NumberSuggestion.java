package com.eng.arab.translator.androidtranslator.number;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;


public class NumberSuggestion implements SearchSuggestion {

    public static final Creator<NumberSuggestion> CREATOR = new Creator<NumberSuggestion>() {
        @Override
        public NumberSuggestion createFromParcel(Parcel in) {
            return new NumberSuggestion(in);
        }

        @Override
        public NumberSuggestion[] newArray(int size) {
            return new NumberSuggestion[size];
        }
    };
    private String mAlphabetName;
    private boolean mIsHistory = false;

    public NumberSuggestion(String suggestion) {
        this.mAlphabetName = suggestion/*.toLowerCase()*/;
    }

    public NumberSuggestion(Parcel source) {
        this.mAlphabetName = source.readString();
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
        return mAlphabetName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAlphabetName);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}