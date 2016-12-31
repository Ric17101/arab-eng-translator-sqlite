package com.eng.arab.translator.androidtranslator.model;

public class TranslateModel {

	//private variables
	public int _id;
	public String _english;
	public String _arabic;
	public String _type;
	public String _definition;
	public String _pronunciation;
	public int _len;

	// Empty constructor
	public TranslateModel(){

	}
	// constructor
	public TranslateModel(int id, String english, String arabic, String type, String definition, String pronunciation){
		this._id = id;
		this._english = english;
		this._arabic = arabic;
		this._type = type;
		this._type = definition;
		this._pronunciation = pronunciation;
	}

	// constructor
	public TranslateModel(int id, String english, String _arabic){
		this._id = id;
		this._english = english;
		this._arabic = _arabic;
	}

	// constructor
	public TranslateModel(String english, String arabic){
		this._english = english;
		this._arabic = arabic;
	}
	public String toString(){
		return "ID : "+ this._id + "\nEnglish : " + this._english + "\nArabic : " + this._arabic
				+ "\ntype : " + this._type + "\nDefinition : " + this._definition
				+ "\nPronunciation : " + this._pronunciation + "\nLength : " + this._len;
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
	public void setPronunciation(String type){ this._pronunciation = type; }

	public int getLength(){ return this._len; }
	public void setLength(int len){ this._len = len; }
}