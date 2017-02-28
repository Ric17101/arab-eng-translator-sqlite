package com.eng.arab.translator.androidtranslator.translate;

public class SentenceFormatWrapper {

    // private variables
    public int _id;
    public String _arabicFormat;
    public String _englishFormat;

    // Empty constructor
    public SentenceFormatWrapper() {
    }

    // constructor
    public SentenceFormatWrapper(int id, String arabicFormat, String englishFormat) {
        this._id = id;
        this._arabicFormat = arabicFormat;
        this._englishFormat = englishFormat;
    }

    public String toString() {
        return "ID : " + this._id
                + "\nArabic Format : " + this._arabicFormat
                + "\nEnglish Format : " + this._englishFormat;
    }

    // get set ID
    public int getID() {
        return this._id;
    }

    public void setID(int id) {
        this._id = id;
    }

    // get set ArabicFormat
    public String getArabicFormat() {
        return this._arabicFormat;
    }

    public void setArabicFormat(String arabicFormat) {
        this._arabicFormat = arabicFormat;
    }

    // get set EnglishFormat
    public String getEnglishFormat() {
        return this._englishFormat;
    }

    public void setEnglishFormat(String englishFormat) {
        this._englishFormat = englishFormat;
    }

}