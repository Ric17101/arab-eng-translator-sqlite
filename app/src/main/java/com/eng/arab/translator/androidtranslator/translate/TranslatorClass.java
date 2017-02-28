package com.eng.arab.translator.androidtranslator.translate;

import android.app.Application;
import android.content.Context;
import android.text.Html;
import android.util.Log;

import com.eng.arab.translator.androidtranslator.alphabet.AlphabetModel;
import com.eng.arab.translator.androidtranslator.dictinary.DictionaryWrapper;
import com.eng.arab.translator.androidtranslator.model.DatabaseAccess;
import com.eng.arab.translator.androidtranslator.model.TranslateModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by keir on 11/26/2016.
 * Translation of Senctence 7/30/2016
 */

public class TranslatorClass {

    private Context context;
//    private Context context;
    /**
     * Test if First Character on the String is in Arabic Alphabet
     *
     * @param s
     * @return TRUE || FALSE
     */
    public static boolean isProbablyArabicOnFirstChar(String s) {
        if (s == "" || s.isEmpty()) // Test ig empty to make sure no error will be compared to the arab
            return false;
        char[] c = s.toCharArray();
//        int ch = s.codePointAt(1); // 1st Char as HEX
        char ch = c[0]; // 1st Char as HEX
        //the arabic letter 'لا' is special case having the range from 0xFE70 to 0xFEFF
        if (ch >= 0x0600 && ch <= 0x06E0)
            return true;
        if (ch >= 0x600 && ch <= 0x6ff)
            return true;
        return ch >= 0xFE70 && ch <= 0xFEFF;
    }

    public void setContext(Context c) {
        this.context = c;
    }

    public List<TranslateModel> DBAccess(String mode)
    {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();
        List<TranslateModel> qoutes;
        if (mode == "all")
            qoutes = db.getAllTranslations();
        else
            qoutes = null;
        db.close();
        return qoutes;
    }

    public String translateSentence(String srcSenctence) {
        List<TranslateModel> languages;
        StringBuffer bufferCol1 = new StringBuffer();
        StringBuffer bufferCol2 = new StringBuffer();
        try {
            languages = DBAccess("all");
            for (TranslateModel lang : languages) // Loop all the database equivalence
            {
                bufferCol1.append("Id: " + lang.getID() + "\n");
                bufferCol1.append("English: " + lang.getEnglish()
                        + "\n");
                bufferCol2.append("Id: " + lang.getID() + "\n");
                bufferCol2.append("Arabic: " + lang.getArabic()
                        + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            bufferCol1.append(e);
        }
        return bufferCol2.toString();
    }

    private int countSpace(String strToken) {
        int spaceCount = 0;
        int i = 0;
        while( i < strToken.length() ){
            if( strToken.charAt(i) == ' ' ) {
                spaceCount++;
            }
            i++;
        }
        return spaceCount;
    }

    /******FIRST ATTEMPT******/
    public String searchWord(String srcSenctence) {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();
        String searchTextWord = srcSenctence;

        StringBuffer buffer = new StringBuffer();
        int index = 0;
        String strcat = " ";

        StringTokenizer stk = new StringTokenizer(searchTextWord, " ");
        int counter = new StringTokenizer(searchTextWord, " \n").countTokens(); // Number of entered words
        String[] arrayTextResult = new String[counter];
        String[] arrayCatAfter = new String[counter];
        System.out.println("counter: " + counter);
        String arrayWord = " ";
        // By word DECODING using space as an index
        int iCount = 0;
        int spaceCount = 0;
        TranslateModel languages1 = new TranslateModel();
        while (stk.hasMoreTokens()) // Assign Words into Array then use it to query on the DB
        {
            strcat = " ";
            arrayWord = stk.nextToken();
            if (arrayWord.contains("\n") || arrayWord.contains("<br/>")) {
                strcat = "<br/>";
            }
//            arrayWord = stk.nextToken("\n");
            try { // Getting data from the database
                System.out.println("index[" + index + "] = " + arrayWord);
                languages1 = db.getTranslationToArabic(arrayWord);
                if (isNumber(arrayWord)) { // To check if the text is a number
                    arrayTextResult[index] =  arabicNumberFormatter(arrayWord);
                }
                else {
                    if (index == 0) {
                        arrayTextResult[index] = (languages1.getArabic().substring(0, 1).toUpperCase()
                                + languages1.getArabic().substring(1).toLowerCase());
                    }
                    else if (languages1.getArabic() == null) { // this is like Exception error
                        arrayTextResult[index] = (arrayWord);
                    }
                    else{
                        arrayTextResult[index] = (languages1.getArabic());
                    }
                }
            } catch (Exception e) {
                if (isNumber(arrayWord)) { // To check if the text is a number
                    arrayTextResult[index] = (arabicNumberFormatter(arrayWord));
                }
                else { // This will change the color (RED) of the invalid word and enclosed it with []
                    arrayTextResult[index] = ("<font color='#EE0000'>[" + arrayWord + "]</font>");
                }
            } finally {
                if (arrayWord.contains("\n") || arrayWord.contains("<br/>") || arrayTextResult.equals('\n')) {
//                    strcat = "<br/>";
//                    strcat = " ";
                }
                else {
//                    strcat = "<br/>";
//                    strcat = "-";
                }
                arrayCatAfter[index] = strcat;
            }
//                index += spaceCount;
            index++;
            System.out.println("end: " + index);
        } // End of WHile loop -->

        db.close();
        for (int i = 0; i < index; i++) // Data Rendering
        {
            buffer.append(arrayTextResult[i] + arrayCatAfter[i]);
        }
////        System.out.println("BUFFER = " + buffer.toString());
////        textResultCol2.setText(Html.fromHtml(buffer.toString()));
////        textResultCol1.setText(searchTextWord);
        return String.valueOf(Html.fromHtml(buffer.toString()));
//        return "";
    }

    /******SECOND ATTEMPT******/
    public String searchWord2(String srcSenctence) {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();
        String searchTextWord = srcSenctence;

        StringBuffer buffer = new StringBuffer();
        int index = 0;
        String strcat = " ";
        String[] stk = searchTextWord.split("(?<=\n)\\b");
        int spltCounter = stk.length;
        String[] arrayTextResult = new String[spltCounter];
        String[] arrayCatAfter = new String[spltCounter];

        TranslateModel languages1;
        new TranslateModel();
        for (String arrayWord: stk)
        {
            System.out.println("index[" + index + "] = " + arrayWord);
            strcat = arrayWord;

            try { // Getting data from the database
                System.out.println("indexss[" + index + "] = " + arrayWord);
                if (isNumber(arrayWord)) { // To check if the text is a number
                    arrayTextResult[index] = arabicNumberFormatter(arrayWord);
                }
                else {
                    languages1 = db.getTranslationToArabic(arrayWord);
                    if (index == 0) {
                        arrayTextResult[index] = languages1.getArabic().substring(0, 1).toUpperCase()
                                + languages1.getArabic().substring(1).toLowerCase();
                    }
                    else if (languages1.getArabic() == null) { // this is like Exception error
                        arrayTextResult[index] = arrayWord;
                    }
                    else{
                        arrayTextResult[index] = languages1.getArabic();
                    }
                }
            } catch (Exception e) {
                if (isNumber(arrayWord)) { // To check if the text is a number
                    arrayTextResult[index] = arabicNumberFormatter(arrayWord);
                }
                else { // This will change the color (RED) of the invalid word and enclosed it with []
                    arrayTextResult[index] = "<font color='#EE0000'>[" + arrayWord + "]</font>";
                }
            } finally {
                if (arrayWord.contains("\n") || arrayWord.contains(" ") || arrayTextResult.equals('\n')) {
                    strcat = "<br/>";
//                    strcat = "";
                }
                else {
                    strcat = " ";
//                    strcat = "\n";
                }
                arrayCatAfter[index] = strcat;
                index++;
            }
        }
        db.close();
        for (int i = 0; i < index; i++) // Data Rendering
        {
            buffer.append(arrayTextResult[i] + arrayCatAfter[i]);
        }
        System.out.print("ARRAY: " + String.valueOf(Html.fromHtml(buffer.toString())));
        return String.valueOf(Html.fromHtml(buffer.toString()));
//        return "";
    } // End of searchWord() -->

    /**
     * Test if the String arg can be converted into String
     *
     * @param num
     * @return TRUE || FALSE
     */
    private boolean isNumber(String num)
    {
        boolean bool;
        try {
            Integer.parseInt(num);
            bool = true;
        } catch (NumberFormatException e) {
            bool = false;
        }
        return bool;
    }

    private String arabicNumberFormatter(String array) // Format Numeric number into ARABIC number Symbol
    {
        // String fulltext =
        // array.replaceAll("1","١").replaceAll("2","٢").replaceAll("3","٣");
        // arrayTextResult[index] = fulltext;
        // or
        int myNum = 0;
        NumberFormat nf = null;
        try {
            nf = NumberFormat.getInstance(new Locale("ar", "EG"));
            myNum = Integer.parseInt(array);
        } catch (NumberFormatException nfe) {
        }
        return nf.format(myNum);
    }

    public String arabicNumberFormatter2(String sentence){
        //java.lang.String.replaceAll()
        return sentence.
                replaceAll("٢", "1").
                replaceAll("٢", "2").
                replaceAll("٣", "3").
                replaceAll("١", "4").
                replaceAll("٢", "5").
                replaceAll("٣", "6").
                replaceAll("١", "7").
                replaceAll("٢", "8").
                replaceAll("٣", "9").
                replaceAll("١", "0");
    }

    /**
     * Description:
     *      [^-?0-9]+
     *      + Between one and unlimited times, as many times as possible, giving back as needed
     *      -? One of the characters “-?”
     *      0-9 A character in the range between “0” and “9”
     * Sample sentence: "qwerty-1qwerty-2 455 f0gfg 4";
     * Output:
     *      [-1, -2, 455, 0, 4]
     *
     * @param sentence
     * @return String[]
     */
    private String[] arabicNumberFormatterToWord(String sentence) {
        String str = "qwerty-1qwerty-2 455 f0gfg 4";
        str = str.replaceAll("[^-?0-9]+", " ");
        return str.trim().split(" ");
    }

    public String stripBeginningNonAlphaNumeric(String word) {
        return word.replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]", "");
    }

    /******THIRD ATTEMPT******/
    public String searchWord3(String srcSenctence) {
        String regex = "\\W+"; // (?<=\n)\b" // Regex for spaces to split
        String numWord;
        String withoutSpacesInTheBeginning = srcSenctence;
        String[] sourceAStr = withoutSpacesInTheBeginning.split(regex);
        String[] newList = getArrayOfArrabic(sourceAStr);

        int strLen = sourceAStr.length;
        for (int i = 0; i < strLen; i++/*, inv--*/) // Replacing the Data by Value
        {
            if (srcSenctence.contains(sourceAStr[i])) // if src has data from string array, then replace
            {
                //srcSenctence = srcSenctence.replaceAll(sourceAStr[i], " " + newList[i] + " "); // add space for the converted words
                srcSenctence = srcSenctence.replaceFirst(sourceAStr[i], " " + newList[i] + " "); // add space for the converted words
            } /*else { // Replace Per letter

            }*/

            if (isNumber(sourceAStr[i])) { // If word is Interger then replace with WordNumber
                numWord = NumberToWordsUtil.convert(Integer.parseInt(sourceAStr[i]));
                srcSenctence = srcSenctence.replaceFirst(sourceAStr[i], " " + numWord + " ");
            }
        }

        //if (getIfSourceTextIsAword(withoutSpacesInTheBeginning))
        //    Log.d("ffff", newList[0].toString()); //return newList[newList.length-1];
        return srcSenctence.replace("  ", " "); // Replace the spaces with two to one space only
        //return srcSenctence.replaceAll("<font>","<font color='#EE0000'>");
    }

    public String replaceWordAndNumbersOrLetters(String srcSenctence) {
        String regex = "\\W+";
        String numWord;
        String withoutSpacesInTheBeginning = srcSenctence;
        String[] sourceAStr = withoutSpacesInTheBeginning.split(regex);
        String[] newList = getArrayOfArrabic(sourceAStr);

        int strLen = sourceAStr.length;
        for (int i = 0; i < strLen; i++/*, inv--*/) // Replacing the Data by Value
        {
            if (srcSenctence.contains(sourceAStr[i])) // if src has data from string array, then replace
            {
                srcSenctence = srcSenctence.replaceFirst(sourceAStr[i], newList[i]);
            } /*else { // Replace Per letter

            }*/

            if (isNumber(sourceAStr[i])) { // If word is Interger then replace with WordNumber
                numWord = NumberToWordsUtil.convert(Integer.parseInt(sourceAStr[i]));
                srcSenctence = srcSenctence.replace(sourceAStr[i], numWord);
            }
        }
        return srcSenctence;
    }

    public String searchWordIfSourceIsWord(String srcSenctence) {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();

        String[] words = srcSenctence.split("\\W+");
        int size = words.length;
        for (int i = 0; i < size; i++) {
            db.open();
            if (db.getDictionaryWordArabicToEnglishIfExist(words[i])) {
                srcSenctence = srcSenctence.replace(words[i], db.getDictionaryEnglishWord(words[i]));
            } else if (db.getOneWordArabicToEnglishIfExist(words[i])) {
                srcSenctence = srcSenctence.replace(words[i], db.getOneSentenceWordArabicToEnglish(words[i]));
            } else {
                // translated.append(words[i] + " ");
                srcSenctence = srcSenctence.replace(words[i], replaceWordAndNumbersOrLetters(words[i]));
            }
        }

        db.close();
        return srcSenctence;
        //return translated.toString();
    }


    public String searchWordIfSourceIsWordWithSpaces(String srcSenctence) {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();

        String[] words = srcSenctence.split("\\W+");
        int size = words.length;
        for (int i = 0; i < size; i++) {
            db.open();
            if (db.getDictionaryWordArabicToEnglishIfExist(words[i])) {
                srcSenctence = srcSenctence.replace(words[i], db.getDictionaryEnglishWord(words[i]));
            } else if (db.getOneWordArabicToEnglishIfExist(words[i])) {
                srcSenctence = srcSenctence.replace(words[i], db.getOneSentenceWordArabicToEnglish(words[i]));
            } else {
                // translated.append(words[i] + " ");
                srcSenctence = srcSenctence.replace(words[i], replaceWordAndNumbersOrLetters(words[i]));
            }
        }

        db.close();
        return srcSenctence;
    }

    /**
     * For Debugging Purposes to Display the Contents of Array
     */
    private String fileArrayToString(String[] f) {
        String output = "";
        String delimiter = "\n"; // Can be new line \n tab \t etc...
        for (int i = 0; i < f.length; i++) {
            output = output + f[i] + delimiter;
        }

        return output;
    }

    /* Get the Translation as List from DB */
    private String[] getArrayOfArrabic_OLD(String[] strList) {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();

        StringBuilder newAString = new StringBuilder(255);
        int _continueWithSpaces = 0;
        int counter = strList.length - 1;
        //for (String str : strList) {
        for (int i = 0; i < counter; i++) {
            TranslateModel tm = db.getTranslationToArabic(strList[i]);
            _continueWithSpaces = tm.getLength();
            if (_continueWithSpaces > 0) { // Continue and skip the translation if and only if data has spaces
                _continueWithSpaces -= _continueWithSpaces;
                //continue;
                newAString.append("--");

            } else {
                if (tm.getEnglish() == null) {// Should not execute getTRansation... here only once is enough
                    //newAString.append("<font>[" + str + "]</font>");
                    newAString.append(strList[i]);
                } else {
                    newAString.append(tm.getEnglish()); // Or can be store to an array for less query on DB side
                }
            }
            newAString.append(" ");
        }

        db.close();

        return newAString.toString().split(" ");
    }

    /** Get the Translation as List from DB */
    private String[] getArrayOfArrabic(String[] strList) {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();

        String[] newAString = new String[255];
        int continueWithSpaces = 0;
        int strLen = strList.length;

        //for (String str : strList) {
        for (int i = 0; i < strLen; i++) {
            if (!isProbablyArabicOnFirstChar(strList[i]) && i == 0) { // If first Character is not Arabic then do nothing
                newAString[i] = strList[i];
                continue;
            }

            db.open();
            TranslateModel tm = db.getTranslationToArabic(strList[i]);
            continueWithSpaces = tm.getLength();
            if (continueWithSpaces > 0) { // Continue and skip the translation if and only if data has spaces
                continueWithSpaces -= continueWithSpaces;
                newAString[i] = db.getTranslationToArabic(strList[i - continueWithSpaces]).getEnglish();
            } else {
                if (getIfSourceTextIsAword2(strList[i])) {
                    /** Another test of the esult from tm, if above has no results */
                    db.open();
                    newAString[i] = db.getDictionaryArabicToEnglish(strList[i]);
                    Log.d("getArrayOfArrabic", "getIfSourceTextIsAword2 " + newAString[i]);
                    if (newAString[i] == null)
                        newAString[i] = db.getOneSentenceWordArabicToEnglish(strList[i]);
                } else if (tm.getEnglish() == null /*|| !getIfSourceTextIsAword2(strList[i])*/) { /** IF WORD is not on the WORD/ SENTENE WORD then Translate per letter */
//                    String cleanWord = new ReplaceSpecialArabicCharacUtil().removeSpecialArabicCharactersClean(strList[i]);
//                    newAString[i] = getArabicPerLetter(cleanWord)[0]; /** Get only the first letter if only if it has '-' from DB*/
                    //newAString[i] = strList[i];
                    /** LETTER PER LETTER TRANSLATION */
                    newAString[i] = getArabicPerLetter(strList[i]);
                    Log.d("getArrayOfArrabic", "null");
                } else {
                    newAString[i] = tm.getEnglish(); // Or can be store to an array for less query on DB side
                    Log.d("getArrayOfArrabic", "else");
                }

            }

            /*db.open();
            if (!db.getWordArabicToEnglishIfExist2(strList[i])) {
                db.open();
                newAString[i] = getArabicPerLetter(strList[i]);
                Log.d("getArrayOfArrabic", "getIfSourceTextIsAword2asd");
            }*/
        }
        Log.d("DBARAB", fileArrayToString(newAString.toString().split(" ")));

        db.close();

        return newAString;
    }

    /**
     * Translator Suggestions
     *
     */
    public String searchWordSuggestion(String srcSenctence) {
        String regex = "\\W+"; //(?<=\n)\b" // Regex for spaces to split
        String numWord;
        String withoutSpacesInTheBeginning = srcSenctence.trim();
        String[] sourceAStr = withoutSpacesInTheBeginning.split(regex);
        String[] newList = getArrayOfArrabic(sourceAStr);

        for (int i = 0; i < sourceAStr.length; i++/*, inv--*/) // Replacing the Data by Value
        {
            if (srcSenctence.contains(sourceAStr[i])) // if src has data from string array, then replace
            {
                srcSenctence = srcSenctence.replaceAll(sourceAStr[i], " " + newList[i] + " "); // add space for the converted words
            }

            if (isNumber(sourceAStr[i])) { // If word is Interger then replace with WordNumber
                numWord = NumberToWordsUtil.convert(Integer.parseInt(sourceAStr[i]));
                srcSenctence = srcSenctence.replaceAll(sourceAStr[i], " " + numWord + " ");
            }
        }
        return srcSenctence.replace("  ", " ");
    }

    /**
     * Translator Suggestions
     * Get Format Suggestion
     */
    public String[] getArrayOfArrabicSuggestion(String strList) {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();

        //TranslateModel tm = db.getTranslationToArabic(strList[i]); // From Dictionary
        // Get the Types and Store it on the String Array Delimited by '-'
        List<SentenceFormatWrapper> sfw = db.getAllFormats(); // All formats

        StringBuilder formats = new StringBuilder();
        StringBuilder wordTypes = new StringBuilder();
        int counter = sfw.size();
        for (int i = 0; i < counter; i++) {
            formats.append(sfw.get(i).getArabicFormat());
            formats.append(" ");
            //wordTypes.append(db.getTranslationToArabic(sfw.get(i).get());
        }

        String[] x = formats.toString().split(" ");
        for (int i = 0; i < counter; i++) {
            Log.d("TAGArray", "getArrayOfArrabicSuggestion: " + x[i]);
        }

        db.close();

        return formats.toString().split("-");
    }

    public String[] getArabicSentence(String srcSentence) {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();
        List<SentenceWrapper> osw = db.getOneSentenceToArabic(srcSentence); // All formats

        StringBuilder formats = new StringBuilder();
        int counter = osw.size();
        for (int i = 0; i < counter; i++) {
            formats.append(osw.get(i).getArabicSentence());
            formats.append("-");
        }

        String[] x = formats.toString().split("-");
        for (int i = 0; i < counter; i++) {
            Log.d("TAGArray", "getArabicSentence: " + x[i]);
        }

        db.close();

        return formats.toString().split("-");
    }

    public String getArabicPerLetter(String srcWord) {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();

        StringBuilder formats = new StringBuilder();
        for (int j = 0; j < srcWord.length(); j++) {

            List<AlphabetModel> osw = db.getArabicLetterToEnglish(srcWord.charAt(j) + ""); // All formats

            int strLen = osw.size();
            for (int i = 0; i < strLen; i++) {
                if (osw.get(i).getPronunciation().contains("-")) {
                    formats.append(osw.get(i).getPronunciation().split("-")[1]);
                } else {
                    formats.append(osw.get(i).getPronunciation());
                }
                // formats.append("-");
            }
        }

        String[] x = formats.toString().split("-");
        Log.d("getArabicPerLetter", "Arabic Letter to English: " + x.toString());

        db.close();

        return formats.toString();
    }

    public String translateArabicSentenceToEnglishWithWords(String srcSentence) {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();
        List<SentenceWrapper> allSentence = db.getAllArabicToEnglishSentences(); // All formats

        int counter = allSentence.size();
        for (int i = 0; i < counter; i++) {
            if (srcSentence.contains(allSentence.get(i).getArabicSentence())) { // OR use  this // if (srcSentence.trim().matches(".*"+ allSentence.get(i).getArabicSentence().trim() +".*")) {
                srcSentence = srcSentence.replaceAll(allSentence.get(i).getArabicSentence(), allSentence.get(i).getEnglishSentence());
                // break;
            }
        }

        db.close();

        return srcSentence;
    }

    public String translateArabicSentenceToEnglishWithSpaces(String srcSentence) {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();
        List<DictionaryWrapper> allWords = db.getAllArabicToEnglishWithArabic2Or3Spaces(); // All formats

        int counter = allWords.size();
        for (int i = 0; i < counter; i++) {
            if (srcSentence.contains(allWords.get(i).getArabic())) {
                srcSentence = srcSentence.replaceAll(allWords.get(i).getArabic(),
                        allWords.get(i).getEnglish());
                Log.d("SRCTENCE", "OK " + srcSentence);
            }
        }

        db.close();

        return srcSentence;
    }

    public List<SentenceWrapper> getArabicSentenceList(String srcSentence) {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();

        List<SentenceWrapper> osw = db.getOneSentenceToArabic(srcSentence); // All formats

        db.close();
        return osw; //appendWordLetterSuggestionSpecialCase(osw);
    }

    /***
     * TODO: APply this to alphaber/word/dictionary
     *
     * @param list
     * @return
     */
    private List<SentenceWrapper> appendWordLetterSuggestionSpecialCase(List<SentenceWrapper> list) {
        SentenceWrapper o = new SentenceWrapper();

        int count = list.size();
        for (int i = 0; i < count; i++) { /** LOOP THRU the suggestion and ADD another senter suggestion as per spcl cases*/
            SentenceWrapper sw = list.get(i);
            if (sw.getArabicSentence().contains("أ")) {
                // e

                sw.getEnglishSentence().replaceAll("e", "e");
                list.add(sw);

                sw.getEnglishSentence().replaceAll("o", "e");
                list.add(sw);

                sw.getEnglishSentence().replaceAll("i", "e");
                list.add(sw);
                // a

                sw.getEnglishSentence().replaceAll("e", "a");
                list.add(sw);

                sw.getEnglishSentence().replaceAll("o", "a");
                list.add(sw);

                sw.getEnglishSentence().replaceAll("i", "a");
                list.add(sw);
                //o
                sw.getEnglishSentence().replaceAll("a", "o");
                list.add(sw);

                sw.getEnglishSentence().replaceAll("e", "o");
                list.add(sw);

                sw.getEnglishSentence().replaceAll("i", "o");
                list.add(sw);
                //i
                sw.getEnglishSentence().replaceAll("a", "i");
                list.add(sw);

                sw.getEnglishSentence().replaceAll("e", "i");
                list.add(sw);

                sw.getEnglishSentence().replaceAll("o", "i");
                list.add(sw);
            }

            if (sw.getArabicSentence().contains("ب")) {
                //b
                sw.getEnglishSentence().replaceAll("v", "b");
                list.add(sw);

                sw.getEnglishSentence().replaceAll("p", "b");
                list.add(sw);
                //v
                sw.getEnglishSentence().replaceAll("b", "v");
                list.add(sw);

                sw.getEnglishSentence().replaceAll("p", "v");
                list.add(sw);
                //p
                sw.getEnglishSentence().replaceAll("b", "p");
                list.add(sw);

                sw.getEnglishSentence().replaceAll("v", "p");
                list.add(sw);
            }

            if (sw.getArabicSentence().contains("ك")) {
                //c
                sw.getEnglishSentence().replaceAll("k", "c");
                list.add(sw);
                // k
                sw.getEnglishSentence().replaceAll("c", "k");
                list.add(sw);
            }

            if (sw.getArabicSentence().contains("ي")) {
                // u
                sw.getEnglishSentence().replaceAll("y", "u");
                list.add(sw);
                // y
                sw.getEnglishSentence().replaceAll("u", "y");
                list.add(sw);
            }

        }

        o.setID(1);
        list.add(o);
        return list;
    }

    /**
     * Arabic Format with dash '-'
     *
     * @return
     */
    public String[] getArrayOfArabicFormat() {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();

        List<SentenceFormatWrapper> sfw = db.getAllFormats(); // All formats
        StringBuilder formats = new StringBuilder();
        int counter = sfw.size();
        for (int i = 0; i < counter; i++) {
            formats.append(sfw.get(i).getArabicFormat());
            formats.append(" ");
        }

        db.close();

        return formats.toString().split(" ");
    }

    /**
     * English Format with dash '-'
     *
     * @return
     */
    public String[] getArrayOfEnglishFormat() {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();

        List<SentenceFormatWrapper> sfw = db.getAllFormats(); // All formats
        StringBuilder formats = new StringBuilder();
        int counter = sfw.size();
        for (int i = 0; i < counter; i++) {
            formats.append(sfw.get(i).getEnglishFormat());
            formats.append(" ");
        }

        db.close();

        return formats.toString().split(" ");
    }

    public String getSentenceTranslationToEnglish(String sentenceArabic) {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();

        String sfw = db.getOneSentenceArabicToEnglish(sentenceArabic);
        db.close();

        return sfw;
    }

    public boolean getIfSourceTextIsAword(String sentenceArabic) {
        DatabaseAccess db = DatabaseAccess.getInstance(context);
        db.open();

        boolean sfw = db.getWordArabicToEnglishIfExist(sentenceArabic);
        db.close();

        return sfw;
    }

    public boolean getIfSourceTextIsAword2(String sentenceArabic) {
        DatabaseAccess db = DatabaseAccess.getInstance(context);
        db.open();

        String[] sentence = sentenceArabic.split("\\W+");
        boolean sfw = db.getWordArabicToEnglishIfExist2(sentence[0]);
        if (!sfw) {
            for (int x = 1; sentence.length > x; x++) {
                sfw = db.getWordArabicToEnglishIfExist2(sentence[x]);
                if (sfw)
                    break;
            }
            Log.d("SFW", "getWordArabicToEnglishIfExist2 " + sfw);
        }
        db.close();

        return sfw;
    }

    public boolean getIfSourceTextIsADictionaryWord(String sentenceArabic) {
        DatabaseAccess db = DatabaseAccess.getInstance(context);
        db.open();

        boolean sfw = db.getDictionaryWordArabicToEnglishIfExist(sentenceArabic);
        db.close();

        return sfw;
    }

    public boolean getIfSourceTextIsASentenceOneWord(String sentenceArabic) {
        DatabaseAccess db = DatabaseAccess.getInstance(context);
        db.open();

        boolean sfw = db.getOneWordArabicToEnglishIfExist(sentenceArabic);
        db.close();

        return sfw;
    }

    public boolean getIfSourceTextIsASentense(String sentenceArabic) {
        DatabaseAccess db = DatabaseAccess.getInstance(context);
        db.open();

        boolean sfw = db.getSentenceArabicToEnglishIfExist(sentenceArabic);
        db.close();

        return sfw;
    }

    private String[] getArrayOfArrabicSentenceFormat(String[] strList) {
        DatabaseAccess db = DatabaseAccess.getInstance(context);
        db.open();

        List<SentenceFormatWrapper> sfw = db.getAllFormats(); // All formats
        StringBuilder formats = new StringBuilder();

        int counter = strList.length - 1;
        for (int i = 0; i < counter; i++) { // Get the Types and Store it on the String Array Delimited by '-'
            formats.append(sfw.get(i).getArabicFormat());
            formats.append("-");
        }

        db.close();

        return formats.toString().split("-");
    }



    /* PROCESS???
        1st arg of string
         - get value from DB of the conversion
         - retain word with char[0] that is special characters
         - loop then replace the convertion
         - replace arabic numbers to English word numbers
     */
}
