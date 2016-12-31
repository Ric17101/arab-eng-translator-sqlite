package com.eng.arab.translator.androidtranslator.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eng.arab.translator.androidtranslator.alphabet.AlphabetModel;
import com.eng.arab.translator.androidtranslator.alphabet.AlphabetSuggestion;
import com.eng.arab.translator.androidtranslator.dictinary.DictionaryModel;
import com.eng.arab.translator.androidtranslator.dictinary.DictionarySuggestion;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
	private SQLiteOpenHelper openHelper;
	private SQLiteDatabase database;
	private static DatabaseAccess instance;
//	private static final String TABLE_LANGUAGE = "language";
	private static final String TABLE_LANGUAGE = "dictionary";
	private static final String TABLE_DICTIONARY = "dictionary";
	private static final String TABLE_ALPHABET = "alphabets";
	private static final String TABLE_MONTHS   = "months";
	/**
	 * Private constructor to aboid object creation from outside classes.
	 *
	 * @param context
	 */
    public DatabaseAccess(Context context) {
		this.openHelper = new DatabaseOpenHelper(context);
	}

	/**
	 * Return a singleton instance of DatabaseAccess.
	 *
	 * @param context the Context
	 * @return the instance of DabaseAccess
	 */
	public static DatabaseAccess getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseAccess(context);
		}
		return instance;
	}

	/**
	 * Open the database connection.
	 */
	public void open() {
		this.database = openHelper.getWritableDatabase();
	}

	/**
	 * Close the database connection.
	 */
	public void close() {
		if (database != null) {
			this.database.close();
		}
	}

	/**
	 * Read all quotes from the database.
	 *
	 * @return a List of quotes
	 */
	public List<String> getQuotes() {
		ArrayList<String> list = new ArrayList<>();
		Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_LANGUAGE, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			list.add(cursor.getString(0));
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}

    // Getting All Data
    public List<TranslateModel> getAllTranslations() {
		ArrayList<TranslateModel> translationList = new ArrayList<TranslateModel>();
        String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY;

        Cursor cursor = database.rawQuery(selectQuery, null);
        TranslateModel translation = new TranslateModel();
        if (cursor.moveToFirst()) {
            do {
                translation.setID(Integer.parseInt(cursor.getString(0)));
				translation.setArabic(cursor.getString(1));
				translation.setEnglish(cursor.getString(2));
				translation.setType(cursor.getString(3));
				translation.setDefinition(cursor.getString(4));
				translation.setPronunciation(cursor.getString(5));
                translationList.add(translation);
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        // return data list
        return translationList;
    }

	// Getting Arabic word Data for ListView
	public List<String> getArabicTranslations() {
		ArrayList<String> translationList = new ArrayList<String>();
		String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY;

		Cursor cursor = database.rawQuery(selectQuery, null);
		String translation;
		if (cursor.moveToFirst()) {
			do {
				translation = cursor.getString(1);
//				translation.setID(Integer.parseInt(cursor.getString(0)));
//				translation.setEnglish(cursor.getString(1));
//				translation.setArabic(cursor.getString(2));
//				translation += "    ";
//				translation += cursor.getString(1);
//				translation += "    ";
				//translation += cursor.getString(2);
//				translation += "    ";
//				translation += cursor.getString(3);
				translationList.add(translation);
			} while (cursor.moveToNext());
		}
		if (!cursor.isClosed()) {
			cursor.close();
		}
		// return data list
		return translationList;
	}

	// Getting Details Data WORD
	public List<TranslateModel> getAllDetailsByWord_OLD(String word) {
		ArrayList<TranslateModel> translationList = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY + " WHERE arabic=\"" + word +"\"";

		Cursor cursor = database.rawQuery(selectQuery, null);
		TranslateModel alpha = new TranslateModel();
		if (cursor.moveToFirst()) {
			do {
				alpha.setID(Integer.parseInt(cursor.getString(0)));
				alpha.setArabic(cursor.getString(1));
				alpha.setEnglish(cursor.getString(2));
				alpha.setType(cursor.getString(3));
				alpha.setDefinition(cursor.getString(4));
				alpha.setPronunciation(cursor.getString(5));
				translationList.add(alpha);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}
		return translationList;
	}

	// Getting Details Data ALPHABET
	public List<TranslateModel> getAllDetailsByWord(String word) {
		ArrayList<TranslateModel> translationList = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY + " WHERE arabic=\"" + word +"\"";

		Cursor cursor = database.rawQuery(selectQuery, null);
		TranslateModel alpha = new TranslateModel();
		if (cursor.moveToFirst()) {
			do {
				alpha.setID(Integer.parseInt(cursor.getString(0)));
				alpha.setArabic(cursor.getString(1));
				alpha.setType(cursor.getString(3));
				alpha.setEnglish(cursor.getString(2));
				alpha.setDefinition(cursor.getString(4));
				alpha.setPronunciation(cursor.getString(5));
				translationList.add(alpha);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}
		return translationList;
	}

	public TranslateModel getTranslationToArabic(String word) {
		String selectQuery = "SELECT *, LENGTH(arabic) - LENGTH(REPLACE(arabic, ' ', '')) as LEN " +
				" FROM " + TABLE_DICTIONARY + " WHERE arabic=\"" + word +"\" or arabic LIKE '%" + word + "'";

		Cursor cursor = database.rawQuery(selectQuery, null);
		TranslateModel alpha = new TranslateModel();
		if (cursor.moveToFirst()) {
			do {
				alpha.setID(Integer.parseInt(cursor.getString(0)));
				alpha.setArabic(cursor.getString(1));
				alpha.setType(cursor.getString(3));
				alpha.setEnglish(cursor.getString(2));
				alpha.setDefinition(cursor.getString(4));
				alpha.setPronunciation(cursor.getString(5));
				alpha.setLength(Integer.parseInt(cursor.getString(6)));
				break;
			} while (cursor.moveToNext());
		}

		Log.d("CLSS",alpha.toString());

		if (!cursor.isClosed()) {
			cursor.close();
		}
		return alpha;
	}

	public TranslateModel getTranslationToArabic_OLD(String word) {
		Cursor cursor = database.query(TABLE_DICTIONARY, new String[] { "id",
						"english", "arabic", "type", "definition", "pronunciation"}, "arabic" + "=?",
				new String[] { word }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		TranslateModel translation = new TranslateModel(Integer.parseInt(
				cursor.getString(0)),
				cursor.getString(1),
				cursor.getString(2),
				cursor.getString(3),
				cursor.getString(4),
				cursor.getString(5));
		// return data
		if (!cursor.isClosed()) {
			cursor.close();
		}
		return translation;
	}

	/****************** DICTIOANRY QUERY ******************/
	// Getting All Data
	public List<DictionarySuggestion> getDictionary() {
		ArrayList<DictionarySuggestion> translationList = new ArrayList<DictionarySuggestion>();

		Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_DICTIONARY,null);
		if (cursor.moveToFirst()) {
			do {
				String translation = cursor.getString(1); // Get only letters from DB
				translationList.add(new DictionarySuggestion(translation));
			} while (cursor.moveToNext());
		}
		if (!cursor.isClosed()) {
			cursor.close();
		}
		// return data list
		return translationList;
	}

	public List<DictionaryModel> getAllDictionary() {
		ArrayList<DictionaryModel> translationList = new ArrayList<DictionaryModel>();
		String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY;
		Cursor cursor = database.rawQuery(selectQuery, null);
        DictionaryModel alpha = new DictionaryModel();

		if (cursor.moveToFirst()) {
			do {
                alpha.setID(Integer.parseInt(cursor.getString(0)));
                alpha.setArabic(cursor.getString(1));
                alpha.setEnglish(cursor.getString(2));
                alpha.setType(cursor.getString(3));
                alpha.setDefinition(cursor.getString(4));
                alpha.setPronunciation(cursor.getString(5));
				translationList.add(alpha);

			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}
		// return data list
		return translationList;
	}

	// Getting Details Data of all ALPHABET
	public List<DictionaryModel> getAllDictionaryDetailsOfWords() {
		ArrayList<DictionaryModel> translationList = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY;

		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				DictionaryModel alpha = new DictionaryModel();
				alpha.setID(Integer.parseInt(cursor.getString(0)));
				alpha.setArabic(cursor.getString(1));
				alpha.setEnglish(cursor.getString(2));
				alpha.setType(cursor.getString(3));
				alpha.setDefinition(cursor.getString(4));
				alpha.setPronunciation(cursor.getString(5));
				translationList.add(alpha);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}
		return translationList;
	}

	// Getting using LIKE Operator Details Data ALPHABET
	public List<DictionaryModel> getLikeDictionaryDetailsByWord(String word) {
		ArrayList<DictionaryModel> translationList = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY + " WHERE arabic LIKE '%" + word +"%'";

		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				DictionaryModel alpha = new DictionaryModel();
				alpha.setID(Integer.parseInt(cursor.getString(0)));
				alpha.setArabic(cursor.getString(1));
				alpha.setEnglish(cursor.getString(2));
				alpha.setType(cursor.getString(3));
				alpha.setDefinition(cursor.getString(4));
				alpha.setPronunciation(cursor.getString(5));
				translationList.add(alpha);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}
		return translationList;
	}

    // Getting Details Data ALPHABET
    public List<DictionaryModel> getAllDictionaryDetailsByWord(String word) {
        ArrayList<DictionaryModel> translationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY + " WHERE arabic=\"" + word +"\"";

        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DictionaryModel alpha = new DictionaryModel();
                alpha.setID(Integer.parseInt(cursor.getString(0)));
                alpha.setArabic(cursor.getString(1));
                alpha.setEnglish(cursor.getString(2));
                alpha.setType(cursor.getString(3));
                alpha.setDefinition(cursor.getString(4));
                alpha.setPronunciation(cursor.getString(5));
                translationList.add(alpha);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        return translationList;
    }

	/****************** ALPHABETS QUERY ******************/
	// Getting All Data
	public List<AlphabetSuggestion> getAlphabets() {
		ArrayList<AlphabetSuggestion> translationList = new ArrayList<AlphabetSuggestion>();

		String selectQuery = "SELECT * FROM " + TABLE_ALPHABET;
		//Cursor cursor = database.rawQuery(selectQuery, null);
		Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_ALPHABET,null);
		if (cursor.moveToFirst()) {
            do {
                String translation = cursor.getString(2); // Get only letters from DB
                translationList.add(new AlphabetSuggestion(translation));
            } while (cursor.moveToNext());
        }
		if (!cursor.isClosed()) {
			cursor.close();
		}
		// return data list
		return translationList;
	}

	public List<AlphabetModel> getAllAlphabets() {
		ArrayList<AlphabetModel> translationList = new ArrayList<AlphabetModel>();
		String selectQuery = "SELECT * FROM " + TABLE_ALPHABET;
		Cursor cursor = database.rawQuery(selectQuery, null);
//		Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_ALPHABET,null);
		AlphabetModel alpha = new AlphabetModel();
		if (cursor.moveToFirst()) {
			do {
				alpha.setID(Integer.parseInt(cursor.getString(0)));
				alpha.setPronunciation(cursor.getString(1));
				alpha.setLetter(cursor.getString(2));
				alpha.setExample(cursor.getString(3));
				alpha.setVideoFileName(cursor.getString(4));
				translationList.add(alpha);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}
		// return data list
		return translationList;
	}

	// ALPHABET NOT WORKING
	public List<String> getAlphabets_OLD() {
		ArrayList<String> list = new ArrayList<String>();
		Cursor cursor = database.rawQuery("SELECT letter FROM " + TABLE_ALPHABET, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			list.add(cursor.getString(0));
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}

    // Getting Details Data of all ALPHABET
    public List<AlphabetModel> getAllDetailsOfLetters() {
		ArrayList<AlphabetModel> translationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ALPHABET;

        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
			do {
				AlphabetModel alpha = new AlphabetModel();
				alpha.setID(Integer.parseInt(cursor.getString(0)));
                alpha.setPronunciation(cursor.getString(1));
                alpha.setLetter(cursor.getString(2));
                alpha.setExample(cursor.getString(3));
                alpha.setVideoFileName(cursor.getString(4));
                translationList.add(alpha);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        return translationList;
    }

	// Getting Details Data ALPHABET
	public List<AlphabetModel> getAllDetailsByLetter(String letter) {
		ArrayList<AlphabetModel> translationList = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_ALPHABET + " WHERE letter=\"" + letter +"\"";

		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				AlphabetModel alpha = new AlphabetModel();
				alpha.setID(Integer.parseInt(cursor.getString(0)));
				alpha.setPronunciation(cursor.getString(1));
				alpha.setLetter(cursor.getString(2));
				alpha.setExample(cursor.getString(3));
				alpha.setVideoFileName(cursor.getString(4));
				translationList.add(alpha);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}
		return translationList;
	}

	/****************** MONTHS QUERY ******************/
	// Getting All Data
	public List<String> getMonts() {
		ArrayList<String> translationList = new ArrayList<String>();
		String selectQuery = "SELECT * FROM " + TABLE_MONTHS;
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String translation = cursor.getString(1); // Get only letters from DB
				translationList.add(translation);
			} while (cursor.moveToNext());
		}
		if (!cursor.isClosed()) {
			cursor.close();
		}
		// return data list
		return translationList;
	}

	// Getting Details Data ALPHABET
	public List<MonthModel> getAllDetailsByMonth(String month) {
		ArrayList<MonthModel> translationList = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_MONTHS + " WHERE arabic_month=\"" + month +"\" OR " + " english_month=\"" + month +"\"";

		Cursor cursor = database.rawQuery(selectQuery, null);
		MonthModel mon = new MonthModel();
		if (cursor.moveToFirst()) {
			do {
				mon.setID(Integer.parseInt(cursor.getString(0)));
				mon.setArabicMonth(cursor.getString(1));
				mon.setEnglishMonth(cursor.getString(2));
				mon.setExample(cursor.getString(3));
				translationList.add(mon);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}
		return translationList;
	}

}