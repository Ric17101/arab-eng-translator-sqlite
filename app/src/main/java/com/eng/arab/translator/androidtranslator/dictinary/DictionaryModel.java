package com.eng.arab.translator.androidtranslator.dictinary;

import android.os.Parcel;
import android.os.Parcelable;

public class DictionaryModel implements Parcelable {

	//private variables
	public int _id;
	public String _english;
	public String _arabic;
	public String _type;
	public String _definition;
	public String _pronunciation;

	// Empty constructor
	public DictionaryModel(){
	}

	protected DictionaryModel(Parcel in) {
		_id = in.readInt();
		_english = in.readString();
		_arabic = in.readString();
		_type = in.readString();
		_definition = in.readString();
		_pronunciation = in.readString();
	}

	public static final Creator<DictionaryModel> CREATOR = new Creator<DictionaryModel>() {
		@Override
		public DictionaryModel createFromParcel(Parcel in) {
			return new DictionaryModel(in);
		}

		@Override
		public DictionaryModel[] newArray(int size) {
			return new DictionaryModel[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeString(_english);
		dest.writeString(_arabic);
		dest.writeString(_type);
		dest.writeString(_definition);
		dest.writeString(_pronunciation);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	// constructor
	public DictionaryModel(int id, String english, String arabic, String type, String definition, String pronunciation){
		this._id = id;
		this._english = english;
		this._arabic = arabic;
		this._type = type;
		this._type = definition;
		this._pronunciation = pronunciation;
	}


	public String toString(){
		return "ID : "+ this._id + "\nEnglish : " + this._english + "\nArabic : "
				+ this._arabic + "\ntype : " + this._type;
	}

	// getting ID
	public int getID(){
		return this._id;
	}
	public void setID(int id){
		this._id = id;
	}

	// getting english
	public String getEnglish(){
		return this._english;
	}
	public void setEnglish(String english){
		this._english = english;
	}

	// getting arabic
	public String getArabic(){
		return this._arabic;
	}
	public void setArabic(String arabic){
		this._arabic = arabic;
	}

	public String getType(){ return this._type; }
	public void setType(String type){
		this._type = type;
	}

	public String getDefinition(){
		return this._definition;
	}
	public void setDefinition(String definition){
		this._definition = definition;
	}

	public String getPronunciation(){
		return this._pronunciation;
	}
	public void setPronunciation(String type){
		this._pronunciation = type;
	}

}