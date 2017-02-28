package com.eng.arab.translator.androidtranslator.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eng.arab.translator.androidtranslator.alphabet.AlphabetModel;
import com.eng.arab.translator.androidtranslator.alphabet.AlphabetSuggestion;
import com.eng.arab.translator.androidtranslator.alphabet.AlphabetWrapper;
import com.eng.arab.translator.androidtranslator.dictinary.DictionaryWrapper;
import com.eng.arab.translator.androidtranslator.dictinary.DictionarySuggestion;
import com.eng.arab.translator.androidtranslator.number.NumberSuggestion;
import com.eng.arab.translator.androidtranslator.number.NumberWrapper;
import com.eng.arab.translator.androidtranslator.translate.SentenceFormatWrapper;
import com.eng.arab.translator.androidtranslator.translate.SentenceWordWrapper;
import com.eng.arab.translator.androidtranslator.translate.SentenceWrapper;

import java.util.ArrayList;
import java.util.List;

import static com.eng.arab.translator.androidtranslator.R.string.word;

public class DatabaseAccess {
//	private static final String TABLE_LANGUAGE = "language";
	private static final String TABLE_LANGUAGE = "dictionary";
	private static final String TABLE_DICTIONARY = "dictionary";
	private static final String TABLE_ALPHABET = "alphabets";
    private static final String TABLE_NUMBER = "numbers";
	private static final String TABLE_SENTENCE_FORMAT = "sentence_formats";
	private static final String TABLE_ONE_SENTENCE = "one_sentence";
	private static final String TABLE_ONE_SENTENCE_WORD = "one_sentence_word";
	private static final String TABLE_MONTHS   = "months";
    private static DatabaseAccess instance;
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;

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

	/**
	 * GEt the arabic word even with spaces
	 *
	 * @param word
	 * @return
	 */
	public TranslateModel getTranslationToArabic(String word) {
//		String selectQuery = "SELECT *, LENGTH(arabic) - LENGTH(REPLACE(arabic, ' ', '')) as LEN " +
//				" FROM " + TABLE_DICTIONARY + " WHERE arabic=\"" + word +"\" or arabic LIKE '%" + word + "'";

		String selectQuery = "SELECT *, LENGTH(arabic) - LENGTH(REPLACE(arabic, ' ', '')) as LEN " +
				" FROM " + TABLE_DICTIONARY + " WHERE arabic=\"" + word +"\"";

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

    public List<DictionaryWrapper> getAllDictionary() {
        ArrayList<DictionaryWrapper> translationList = new ArrayList<DictionaryWrapper>();
        String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY;
		Cursor cursor = database.rawQuery(selectQuery, null);
        DictionaryWrapper alpha = new DictionaryWrapper();

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
    public List<DictionaryWrapper> getAllDictionaryDetailsOfWords() {
        ArrayList<DictionaryWrapper> translationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY;

		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
                DictionaryWrapper alpha = new DictionaryWrapper();
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
    public List<DictionaryWrapper> getLikeDictionaryDetailsByWord(String word) {
        ArrayList<DictionaryWrapper> translationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY + " WHERE arabic LIKE '%" + word +"%'";

		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
                DictionaryWrapper alpha = new DictionaryWrapper();
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
    public List<DictionaryWrapper> getAllDictionaryDetailsByWord(String word) {
        ArrayList<DictionaryWrapper> translationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY + " WHERE arabic=\"" + word +"\"";

        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DictionaryWrapper alpha = new DictionaryWrapper();
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

	// Getting Details Data MONTH
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


    /******************
     * ALPHABETS QUERY
     ******************/
    // Getting All Data
    public List<NumberSuggestion> getNumbers() {
        ArrayList<NumberSuggestion> translationList = new ArrayList<NumberSuggestion>();

        String selectQuery = "SELECT * FROM " + TABLE_NUMBER;
        //Cursor cursor = database.rawQuery(selectQuery, null);
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NUMBER, null);
        if (cursor.moveToFirst()) {
            do {
                String translation = cursor.getString(2); // Get only letters from DB
                translationList.add(new NumberSuggestion(translation));
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        // return data list
        return translationList;
    }

    public List<NumberWrapper> getAllNumbers() {
        ArrayList<NumberWrapper> translationList = new ArrayList<NumberWrapper>();
        String selectQuery = "SELECT * FROM " + TABLE_NUMBER;
        Cursor cursor = database.rawQuery(selectQuery, null);
        NumberWrapper alpha = new NumberWrapper();
        if (cursor.moveToFirst()) {
            do {
                alpha.setID(Integer.parseInt(cursor.getString(0)));
                alpha.setPronunciation(cursor.getString(1));
                alpha.setNumber(cursor.getString(2));
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

    // Getting Details Data NUMBER
    public List<NumberWrapper> getAllDetailsByNumber(String number) {
        ArrayList<NumberWrapper> translationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NUMBER + " WHERE number=\"" + number + "\"";

        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                NumberWrapper alpha = new NumberWrapper();
                alpha.setID(Integer.parseInt(cursor.getString(0)));
                alpha.setPronunciation(cursor.getString(1));
                alpha.setNumber(cursor.getString(2));
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

	// Getting Details Data of all NUMBER
	public List<NumberWrapper> getAllDetailsOfNumbers() {
        ArrayList<NumberWrapper> translationList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NUMBER;

        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                NumberWrapper alpha = new NumberWrapper();
                alpha.setID(Integer.parseInt(cursor.getString(0)));
                alpha.setPronunciation(cursor.getString(1));
                alpha.setNumber(cursor.getString(2));
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

	/**************************
	 * SENTENCE FORMAT QUERY
	 **************************/
	// Getting Details Data of all ALPHABET
	public List<SentenceFormatWrapper> getAllFormats() {
		ArrayList<SentenceFormatWrapper> formatList = new ArrayList<>();
		String selectQuery = "SELECT * FROM " + TABLE_SENTENCE_FORMAT;

		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				SentenceFormatWrapper alpha = new SentenceFormatWrapper();
				alpha.setID(Integer.parseInt(cursor.getString(0)));
				alpha.setArabicFormat(cursor.getString(1));
				alpha.setEnglishFormat(cursor.getString(2));
				formatList.add(alpha);
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}
		return formatList;
	}
	/**************************
	 * ONE SENTENCE QUERY
	 **************************/
	/**
	 * arabic word can be an english or arabic word
	 *
	 * @param strWord
	 * @return
	 */
	public List<SentenceWrapper> getOneSentenceToArabic(String strWord) {
		ArrayList<SentenceWrapper> setenceList = new ArrayList<SentenceWrapper>();
		String selectQuery = "SELECT * FROM " + TABLE_ONE_SENTENCE
				+ " WHERE sentence_arabic=\"" + strWord
				+ "\" OR sentence_arabic LIKE '%" + strWord + "%' "
				+ " OR sentence_english LIKE '%" + getDictionaryArabicToEnglish(strWord) + "%'"
				//+ " OR sentence_english LIKE '%" + getOneSentenceWordArabicToEnglish(getDictionaryArabicToEnglish(strWord)) + "%'"
				+ " OR sentence_english LIKE '%" + getOneSentenceWordArabicToEnglish(strWord) + "%'";

		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				SentenceWrapper sen = new SentenceWrapper();
				sen.setID(Integer.parseInt(cursor.getString(0)));
				sen.setArabicSentence(cursor.getString(1));
				sen.setArabicFormat(cursor.getString(2));
				sen.setEnglishSentence(cursor.getString(3));
				sen.setEnglishFormat(cursor.getString(4));
				sen.setGender(Integer.parseInt(cursor.getString(5)));
				setenceList.add(sen);
				//break; // Only one result being get
			} while (cursor.moveToNext());
		}

		return getOneSentenceToArabicByWord(strWord, setenceList, cursor);
	}

	private List<SentenceWrapper> getOneSentenceToArabicByWord(String strSentence,
															   List<SentenceWrapper> setenceList, Cursor cursor) {

		String[] sentenceWOrd = strSentence.split(" ");
		String selectQuery;
		for (String word : sentenceWOrd) {
			selectQuery = "SELECT * FROM " + TABLE_ONE_SENTENCE
					+ " WHERE sentence_arabic=\"" + word + "\""
					+ " OR sentence_arabic LIKE '%" + word + "%' "
					+ " OR sentence_english LIKE '%" + getDictionaryArabicToEnglish(word.trim()) + "%'"
					+ " OR sentence_english LIKE '%" + getOneSentenceWordArabicToEnglish(word.trim()) + "%'";

			cursor = database.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				int x = 0;
				do {
					if (x == setenceList.size()) {
						break;
					}
					SentenceWrapper sen = new SentenceWrapper();
					sen.setID(Integer.parseInt(cursor.getString(0)));
					sen.setArabicSentence(cursor.getString(1));
					sen.setArabicFormat(cursor.getString(2));
					sen.setEnglishSentence(cursor.getString(3));
					sen.setEnglishFormat(cursor.getString(4));
					sen.setGender(Integer.parseInt(cursor.getString(5)));
					if (setenceList.get(x).getID() == sen.getID())
						; // Dont add aother list
					else
						setenceList.add(sen);
					//break; // Only one result being get\
					x++;
				} while (cursor.moveToNext());
			}
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}

		Log.d("setenceList", setenceList.toString());
		return setenceList;
	}

	public List<AlphabetModel> getArabicLetterToEnglish(String strArabicLetter) {
		ArrayList<AlphabetModel> setenceList = new ArrayList<AlphabetModel>();
		String selectQuery = "SELECT * FROM " + TABLE_ALPHABET + " WHERE letter=\"" + strArabicLetter + "\"";

		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				AlphabetModel sen = new AlphabetModel();
				sen.setID(Integer.parseInt(cursor.getString(0)));
				sen.setPronunciation(cursor.getString(1));
				sen.setLetter(cursor.getString(2));
				sen.setExample(cursor.getString(3));
				sen.setVideoFileName(cursor.getString(4));
				setenceList.add(sen);
				break; // Only one result being get
			} while (cursor.moveToNext());
		}

		Log.d("LETTER", setenceList.toString());

		if (!cursor.isClosed()) {
			cursor.close();
		}
		return setenceList;
	}

	public List<SentenceWrapper> getAllArabicToEnglishSentences() {
		ArrayList<SentenceWrapper> setenceList = new ArrayList<SentenceWrapper>();
		String selectQuery = "SELECT * FROM " + TABLE_ONE_SENTENCE;

		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				SentenceWrapper sen = new SentenceWrapper();
				sen.setID(Integer.parseInt(cursor.getString(0)));
				sen.setArabicSentence(cursor.getString(1));
				sen.setArabicFormat(cursor.getString(2));
				sen.setEnglishSentence(cursor.getString(3));
				sen.setEnglishFormat(cursor.getString(4));
				sen.setGender(Integer.parseInt(cursor.getString(5) == null ? "0" : cursor.getString(5)));
				setenceList.add(sen);
			} while (cursor.moveToNext());
		}

		Log.d("SENTENCES", setenceList.size() + "");

		if (!cursor.isClosed()) {
			cursor.close();
		}
		return setenceList;
	}

	public List<DictionaryWrapper> getAllArabicToEnglishWithArabic2Or3Spaces() {
		ArrayList<DictionaryWrapper> setenceList = new ArrayList<DictionaryWrapper>();

		String selectQuery =
				"SELECT english_word AS english, arabic_word AS arabic FROM " +
						TABLE_ONE_SENTENCE_WORD + " WHERE arabic_word LIKE '% %'" +
						" UNION " +
						"SELECT english, arabic FROM " +
						TABLE_DICTIONARY + " WHERE arabic LIKE '% %'";

		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				DictionaryWrapper sen = new DictionaryWrapper();
				sen.setEnglish(cursor.getString(0));
				sen.setArabic(cursor.getString(1));
				setenceList.add(sen);
			} while (cursor.moveToNext());
		}

		Log.d("UNION_SENTENCE", setenceList.toString() + "");

		if (!cursor.isClosed()) {
			cursor.close();
		}
		return setenceList;
	}

	/**
	 * Get the English meaning of Arabic word: for getOneSentenceToArabic(...)
	 *
	 * @param arabicWord
	 * @return
	 */
	public String getOneSentenceWordArabicToEnglish(String arabicWord) {
//		String selectQuery = "SELECT * FROM " + TABLE_ONE_SENTENCE_WORD
//				+ " WHERE arabic_word=\"" + arabicWord +"\" OR arabic_word LIKE '%" + arabicWord + "%'";

		String selectQuery = "SELECT * FROM " + TABLE_ONE_SENTENCE_WORD
				+ " WHERE arabic_word=\"" + arabicWord + "\""; // OR arabic_word LIKE '%" + arabicWord + "%'";

		Cursor cursor = database.rawQuery(selectQuery, null);
		SentenceWordWrapper sen = new SentenceWordWrapper();
		if (cursor.moveToFirst()) {
			do {
				sen.setID(Integer.parseInt(cursor.getString(0)));
				sen.setEnglishWord(cursor.getString(1));
				sen.setArabicWord(cursor.getString(2));
				sen.setType(cursor.getString(3));
				break;
			} while (cursor.moveToNext());
		}
		/*Log.d("CLSS", sen.toString());*/
		if (!cursor.isClosed()) {
			cursor.close();
		}
		return sen.getEnglishWord();
	}

	public String getOneSentenceWordArabicWord(String arabicWord) {
//		String selectQuery = "SELECT * FROM " + TABLE_ONE_SENTENCE_WORD
//				+ " WHERE arabic_word=\"" + arabicWord +"\" OR arabic_word LIKE '%" + arabicWord + "%'";

		String selectQuery = "SELECT * FROM " + TABLE_ONE_SENTENCE_WORD
				+ " WHERE arabic_word=\"" + arabicWord + "\""; // OR arabic_word LIKE '%" + arabicWord + "%'";

		Cursor cursor = database.rawQuery(selectQuery, null);
		SentenceWordWrapper sen = new SentenceWordWrapper();
		if (cursor.moveToFirst()) {
			do {
				sen.setID(Integer.parseInt(cursor.getString(0)));
				sen.setEnglishWord(cursor.getString(1));
				sen.setArabicWord(cursor.getString(2));
				sen.setType(cursor.getString(3));
				break;
			} while (cursor.moveToNext());
		}
		/*Log.d("CLSS", sen.toString());*/
		if (!cursor.isClosed()) {
			cursor.close();
		}
		return sen.getArabicWord();
	}

	public String getDictionaryArabicToEnglish(String arabicWord) {
//		String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY
//				+ " WHERE arabic=\"" + arabicWord +"\" OR arabic LIKE '%" + arabicWord + "%'";
		String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY
				+ " WHERE arabic=\"" + arabicWord + "\"";


		Cursor cursor = database.rawQuery(selectQuery, null);
		DictionaryWrapper dw = new DictionaryWrapper();
		if (cursor.moveToFirst()) {
			do {
				dw.setID(Integer.parseInt(cursor.getString(0)));
				dw.setArabic(cursor.getString(1));
				dw.setEnglish(cursor.getString(2));
				dw.setType(cursor.getString(3));
				dw.setDefinition(cursor.getString(4));
				dw.setPronunciation(cursor.getString(5));
				break;
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}
		return dw.getArabic();
	}

	public String getDictionaryEnglishWord(String arabicWord) {
		String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY
				+ " WHERE arabic=\"" + arabicWord + "\"";


		Cursor cursor = database.rawQuery(selectQuery, null);
		DictionaryWrapper dw = new DictionaryWrapper();
		if (cursor.moveToFirst()) {
			do {
				dw.setID(Integer.parseInt(cursor.getString(0)));
				dw.setArabic(cursor.getString(1));
				dw.setEnglish(cursor.getString(2));
				dw.setType(cursor.getString(3));
				dw.setDefinition(cursor.getString(4));
				dw.setPronunciation(cursor.getString(5));
				break;
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}
		return dw.getEnglish();
	}

	public boolean getOneSentenceWordArabicToEnglish2(String arabicWord) {

		String selectQuery = "SELECT * FROM " + TABLE_ONE_SENTENCE_WORD
				+ " WHERE arabic_word=\"" + arabicWord + "\""; // OR arabic_word LIKE '%" + arabicWord + "%'";

		boolean exists = false;
		Cursor cursor = database.rawQuery(selectQuery, null);
		SentenceWordWrapper sen = new SentenceWordWrapper();
		if (cursor.moveToFirst()) {
			exists = true;
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}
		return exists;
	}

	public boolean getDictionaryArabicToEnglish2(String arabicWord) {
		String selectQuery = "SELECT * FROM " + TABLE_DICTIONARY
				+ " WHERE arabic=\"" + arabicWord + "\"";

		boolean exists = false;

		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			exists = true;
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}

		return exists;
	}

	public String getOneSentenceArabicToEnglish(String arabicSentence) {
		String selectQuery = "SELECT * FROM " + TABLE_ONE_SENTENCE
				+ " WHERE sentence_arabic=\"" + arabicSentence + "\""; //+"\" or sentence_arabic LIKE '%" + arabicSentence + "%'";

		Cursor cursor = database.rawQuery(selectQuery, null);
		SentenceWrapper sen = new SentenceWrapper();
		if (cursor.moveToFirst()) {
			do {
				sen.setID(Integer.parseInt(cursor.getString(0)));
				sen.setArabicSentence(cursor.getString(1));
				sen.setArabicFormat(cursor.getString(2));
				//sen.setEnglishSentence("<font color='#0000FF'>" + cursor.getString(3) + "</font>");
				sen.setEnglishSentence(cursor.getString(3));
				sen.setEnglishFormat(cursor.getString(4));
				break; // One result will be rendered
			} while (cursor.moveToNext());
		}

		if (!cursor.isClosed()) {
			cursor.close();
		}
		return (sen.getEnglishSentence() == null ? "" : sen.getEnglishSentence());
	}


	/**
	 * TEST EXISTENCE from the Database
	 *
	 * @param arabicSentence
	 * @return
	 */
	public boolean getSentenceArabicToEnglishIfExist(String arabicSentence) {
		if (getOneSentenceArabicToEnglish(arabicSentence) == "null" || getOneSentenceArabicToEnglish(arabicSentence) == "") {
			return false;
		}
		return true;
	}

	public boolean getWordArabicToEnglishIfExist(String arabicWord) {
		if (getOneSentenceWordArabicToEnglish(arabicWord) == getDictionaryArabicToEnglish(arabicWord)) {
			return true;
		}
		return false;
	}

	public boolean getWordArabicToEnglishIfExist2(String arabicWord) {
		if (getOneWordArabicToEnglishIfExist(arabicWord) || getDictionaryWordArabicToEnglishIfExist(arabicWord)) {
			return true;
		}
		return false;
	}

	public boolean getOneWordArabicToEnglishIfExist(String arabicWord) {
		/*if (getOneSentenceWordArabicToEnglish2(arabicWord)) {
            return true;
        }
        return false;*/
		return getOneSentenceWordArabicToEnglish2(arabicWord);
	}

	public boolean getDictionaryWordArabicToEnglishIfExist(String arabicWord) {
		return getDictionaryArabicToEnglish2(arabicWord);
	}
}