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

package com.eng.arab.translator.androidtranslator.alphabet;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;



public class AlphabetSuggestion implements SearchSuggestion {

    private String mAlphabetName;
    private boolean mIsHistory = false;

    public AlphabetSuggestion(String suggestion) {
        this.mAlphabetName = suggestion/*.toLowerCase()*/;
    }

    public AlphabetSuggestion(Parcel source) {
        this.mAlphabetName = source.readString();
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
        return mAlphabetName;
    }

    public static final Creator<AlphabetSuggestion> CREATOR = new Creator<AlphabetSuggestion>() {
        @Override
        public AlphabetSuggestion createFromParcel(Parcel in) {
            return new AlphabetSuggestion(in);
        }

        @Override
        public AlphabetSuggestion[] newArray(int size) {
            return new AlphabetSuggestion[size];
        }
    };

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