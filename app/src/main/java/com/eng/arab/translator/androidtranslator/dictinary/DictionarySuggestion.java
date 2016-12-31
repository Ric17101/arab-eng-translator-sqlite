package com.eng.arab.translator.androidtranslator.dictinary;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Copyright (C) 2015 Ari C.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class DictionarySuggestion implements SearchSuggestion {

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

    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }

    @Override
    public String getWord() {
        return mDictionaryWord;
    }

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