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

package com.eng.arab.translator.androidtranslator.translate;

import android.content.Context;
import android.text.Html;
import android.util.Log;

import com.eng.arab.translator.androidtranslator.model.DatabaseAccess;
import com.eng.arab.translator.androidtranslator.model.TranslateModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by keir on 11/26/2016.
 * Translation of Senctence 7/30/2016
 */

public class TranslatorClass {

    private Context context;

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
        return sentence
                .replaceAll("١", "1")
                .replaceAll("٢", "2")
                .replaceAll("٣", "3")
                .replaceAll("١", "4")
                .replaceAll("٢", "5")
                .replaceAll("٣", "6")
                .replaceAll("١", "7")
                .replaceAll("٢", "8")
                .replaceAll("٣", "9")
                .replaceAll("١", "0");
    }

    /**
     * Description:
     * [^-?0-9]+
     * + Between one and unlimited times, as many times as possible, giving back as needed
     * -? One of the characters “-?”
     * 0-9 A character in the range between “0” and “9”
     * Sample sentence: "qwerty-1qwerty-2 455 f0gfg 4";
     * Output:
     * [-1, -2, 455, 0, 4]
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
                numWord = NumberToWords.convert(Integer.parseInt(sourceAStr[i]));
                srcSenctence = srcSenctence.replaceAll(sourceAStr[i], " " + numWord + " ");
            }
        }
        return srcSenctence.replace("  ", " "); // Replace the spaces with two to one space only
        //return srcSenctence.replaceAll("<font>","<font color='#EE0000'>");
    }

    /**
     * For Debugging Purposes to Display the Contents of Array
     */
    String fileArrayToString(String[] f){
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
        //Log.d("DBARAB",fileArrayToString(newAString.toString().split(" ")));

        db.close();

        return newAString.toString().split(" ");
    }

    /** Get the Translation as List from DB */
    private String[] getArrayOfArrabic(String[] strList) {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();

        String[] newAString = new String[255];
        int continueWithSpaces = 0;
        int counter = strList.length;
        int flagInverse = 0;
        //for (String str : strList) {
        for (int i = 0; i < counter; i++) {
            if (!isProbablyArabicOnFirstChar(strList[i]) && i == 0) { // If first Character is not Arabic then do nothing
                newAString[i] = strList[i];
                continue;
            }

            TranslateModel tm = db.getTranslationToArabic(strList[i]);
            continueWithSpaces = tm.getLength();
            if (continueWithSpaces > 0) { // Continue and skip the translation if and only if data has spaces
                continueWithSpaces -= continueWithSpaces;
                newAString[i] = db.getTranslationToArabic(strList[i - continueWithSpaces]).getEnglish();
                flagInverse = 1;
            } else {
                if (flagInverse == 0) { // NOT WORKING YET
                    if (tm.getEnglish() == null) {
                        // if query has no result then retain the string
                        // Should not execute getTRansation... here only once is enough
                        //newAString.append("<font>[" + str + "]</font>");
                        newAString[i] = strList[i];
                    } else {
                        newAString[i] = tm.getEnglish(); // Or can be store to an array for less query on DB side
                    }
                } else {
                    newAString[i] = "";
                }
                flagInverse = 0;
            }
        }
        //Log.d("DBARAB", fileArrayToString(newAString.toString().split(" ")));

        db.close();

        return newAString;
    }

    /**
     * NOT YET USED
     */
    public class ReplaceSpecialArabicCharacUtil {
        public List<Integer> getValidAsciiValues() {
            List<Integer> validAsciiValues = new ArrayList<Integer>();
            for (int i = 193; i <= 218; i++) {
                validAsciiValues.add(i);
            }
            for (int i = 225; i <= 234; i++) {
                validAsciiValues.add(i);
            }

            validAsciiValues.add(32);
            validAsciiValues.add(38);
            validAsciiValues.add(40);
            validAsciiValues.add(41);
            validAsciiValues.add(47);
            validAsciiValues.add(247);
            validAsciiValues.add(248);
            validAsciiValues.add(249);
            validAsciiValues.add(250);

            return validAsciiValues;
        }

        public void removeSpecialArabicCharacters(String name_a) {

            //replace_mult_spaces(name_a)
            int stringLenth = name_a.length();
            int pos = 0; //the Java index is 0-based (starts from 0)
            while (pos < stringLenth) {
                char nextChar = name_a.substring(pos, pos + 1).toCharArray()[0];
                int asciiValue = (int) nextChar;
                if (getValidAsciiValues().contains(asciiValue)) {
                    pos++;
                } else {
                    throw new AssertionError("The string contains invalid characters");
                }
            }
            name_a = name_a.replaceAll("ې", " ې ");
            if (name_a.substring(stringLenth).equals('ي')) {
                name_a = name_a.substring(0, stringLenth - 2);
            }
            name_a = name_a.replaceAll(" ", "ه  ");
            if (name_a.substring(stringLenth).equals("ة")) {
                name_a = name_a.substring(0, stringLenth - 2);
            }

            name_a = name_a.replace('ا', 'أ');
            name_a = name_a.replace('ا', 'إ');
            name_a = name_a.replace('ا', 'آ');
            name_a = name_a.replace((char) 250, 'ل');
            name_a = name_a.replace((char) 247, 'ل');
            name_a = name_a.replace((char) 248, 'ل');
            name_a = name_a.replace((char) 249, 'ل');
            name_a = name_a.replace((char) 63, 'ل');

            name_a.replace(" ابن ", " بن ");
            if (name_a.substring(0, 5).equals("'عبد ال")) {
                name_a = name_a.substring(6);
            }


            name_a.replaceAll(" عبد ال", " عبدال");
            if (name_a.substring(0, 3).equals("'ابن")) {
                name_a = name_a.substring(4);
            }
            if (name_a.substring(-4).equals("ابن))")) {
                name_a = name_a.substring(0, name_a.length() - 4);
            }
        }
    }

    /* PROCESS???
        1st arg of string
         - get value from DB of the conversion
         - retain word with char[0] that is special characters
         - loop then replace the convertion
         - replace arabic numbers to English word numbers
     */
}
