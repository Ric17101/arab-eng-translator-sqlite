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

package com.eng.arab.translator.androidtranslator.number;

import android.os.Parcel;
import android.os.Parcelable;

public class NumberWrapper implements Parcelable {

    public static final Creator<NumberWrapper> CREATOR = new Creator<NumberWrapper>() {
        @Override
        public NumberWrapper createFromParcel(Parcel in) {
            return new NumberWrapper(in);
        }

        @Override
        public NumberWrapper[] newArray(int size) {
            return new NumberWrapper[size];
        }
    };
    //private variables
    public int _id;
    public String _number;
    public String _pronunciation;
    public String _example;
    public String _video_file;

    // Empty constructor
    public NumberWrapper() {
    }

    protected NumberWrapper(Parcel in) {
        _id = in.readInt();
        _number = in.readString();
        _pronunciation = in.readString();
        _example = in.readString();
        _video_file = in.readString();
    }

    // constructor
    public NumberWrapper(int id, String number, String pronunciation, String example, String video_file) {
        this._id = id;
        this._number = number;
        this._pronunciation = pronunciation;
        this._example = example;
        this._video_file = video_file;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(_number);
        dest.writeString(_pronunciation);
        dest.writeString(_example);
        dest.writeString(_video_file);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "ID : " + this._id + "\nNumber : " + this._number + "\nPronunciation : "
                + this._pronunciation + "\nDescription : " + this._example + "\n" + this._video_file;
    }

    // get set ID
    public int getID() {
        return this._id;
    }

    public void setID(int id) {
        this._id = id;
    }

    // get set Letter
    public String getNumber() {
        return this._number;
    }

    public void setNumber(String num) {
        this._number = num;
    }

    // get set Pronunciation
    public String getPronunciation() {
        return this._pronunciation;
    }

    public void setPronunciation(String arabic) {
        this._pronunciation = arabic;
    }

    // get set Example
    public String getExample() {
        return this._example;
    }

    public void setExample(String structure) {
        this._example = structure;
    }

    // get set Videfile Name
    public String getVideoFileName() {
        return this._video_file;
    }

    public void setVideoFileName(String data) {
        this._video_file = data;
    }


}