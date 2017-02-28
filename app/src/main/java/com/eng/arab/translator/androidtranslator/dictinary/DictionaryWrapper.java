package com.eng.arab.translator.androidtranslator.dictinary;

import android.os.Parcel;
import android.os.Parcelable;

public class DictionaryWrapper implements Parcelable {

	public static final Creator<DictionaryWrapper> CREATOR = new Creator<DictionaryWrapper>() {
		@Override
		public DictionaryWrapper createFromParcel(Parcel in) {
			return new DictionaryWrapper(in);
		}

		@Override
		public DictionaryWrapper[] newArray(int size) {
			return new DictionaryWrapper[size];
		}
	};
	//private variables
	public int _id;
	public String _english;
	public String _arabic;
	public String _type;
	public String _definition;
	public String _pronunciation;

	// Empty constructor
	public DictionaryWrapper() {
	}

	protected DictionaryWrapper(Parcel in) {
		_id = in.readInt();
		_arabic = in.readString();
		_english = in.readString();
		_type = in.readString();
		_definition = in.readString();
		_pronunciation = in.readString();
	}

	// constructor
	public DictionaryWrapper(int id, String english, String arabic, String type, String definition, String pronunciation) {
		this._id = id;
		this._english = english;
		this._arabic = arabic;
		this._type = type;
		this._type = definition;
		this._pronunciation = pronunciation;
	}

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