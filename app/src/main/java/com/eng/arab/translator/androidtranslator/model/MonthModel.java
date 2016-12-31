package com.eng.arab.translator.androidtranslator.model;

public class MonthModel {

	//private variables
	public int _id;
	public String _arabic_month;
	public String _english_month;
	public String _example;

	// Empty constructor
	public MonthModel(){

	}
	// constructor
	public MonthModel(int id, String arabic_month, String english_month, String example){
		this._id = id;
		this._english_month = arabic_month;
		this._english_month = english_month;
		this._example = example;
	}

	public String toString(){
		return "ID : "+ this._id + "\nLetter : " + this._arabic_month + "\nPronunciation : "
				+ this._english_month + "\nDescription : " + this._example;
	}
	// get set ID
	public int getID(){
		return this._id;
	}
	public void setID(int id){
		this._id = id;
	}

	// get set Letter
	public String getArabicMonth(){
		return this._arabic_month;
	}
	public void setArabicMonth(String english){
		this._arabic_month = english;
	}

	// get set Pronunciation
	public String getEnglishMonth(){
		return this._english_month;
	}
	public void setEnglishMonth(String arabic){
		this._english_month = arabic;
	}

    // get set Example
	public String getExample(){
	return this._example;
}
	public void setExample(String structure){
		this._example= structure;
	}
}