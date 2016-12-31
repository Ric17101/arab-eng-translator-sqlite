package com.eng.arab.translator.androidtranslator.alphabet;

import android.os.Parcel;
import android.os.Parcelable;

public class AlphabetModel implements Parcelable {

	//private variables
	public int _id;
	public String _letter;
	public String _pronunciation;
	public String _example;
	public String _video_file;

	// Empty constructor
	public AlphabetModel(){
	}

	protected AlphabetModel(Parcel in) {
		_id = in.readInt();
		_letter = in.readString();
		_pronunciation = in.readString();
		_example = in.readString();
		_video_file = in.readString();
	}

	public static final Creator<AlphabetModel> CREATOR = new Creator<AlphabetModel>() {
		@Override
		public AlphabetModel createFromParcel(Parcel in) {
			return new AlphabetModel(in);
		}

		@Override
		public AlphabetModel[] newArray(int size) {
			return new AlphabetModel[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeString(_letter);
		dest.writeString(_pronunciation);
		dest.writeString(_example);
		dest.writeString(_video_file);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	// constructor
	public AlphabetModel(int id, String letter, String pronunciation, String example, String video_file){
		this._id = id;
		this._letter = letter;
		this._pronunciation = pronunciation;
		this._example = example;
		this._video_file = video_file;
	}

	public String toString(){
		return "ID : "+ this._id + "\nLetter : " + this._letter + "\nPronunciation : "
				+ this._pronunciation + "\nDescription : " + this._example +"\n" + this._video_file;
	}
	// get set ID
	public int getID(){
		return this._id;
	}
	public void setID(int id){
		this._id = id;
	}

	// get set Letter
	public String getLetter(){
		return this._letter;
	}
	public void setLetter(String english){
		this._letter = english;
	}

	// get set Pronunciation
	public String getPronunciation(){
		return this._pronunciation;
	}
	public void setPronunciation(String arabic){
		this._pronunciation = arabic;
	}

	// get set Example
	public String getExample(){
		return this._example;
	}
	public void setExample(String structure){
		this._example= structure;
	}

	// get set Videfile Name
	public String getVideoFileName(){ return this._video_file; }
	public void setVideoFileName(String data){
		this._video_file = data;
	}


}