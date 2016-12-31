package com.eng.arab.translator.androidtranslator.translate;

import android.content.Context;
import android.text.Html;
import android.util.Log;

import com.eng.arab.translator.androidtranslator.model.DatabaseAccess;
import com.eng.arab.translator.androidtranslator.model.TranslateModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by keir on 11/26/2016.
 */

/*********************************************************************
 *****************Translation of Senctence 7/30/2016******************
 *********************************************************************/

public class TranslatorClass {

    private Context context;

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

    private int countSpace(String strToken)
    {
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
    } // End of searchWord() -->

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

    private boolean isNumber(String num)
    {
        String text = num;
        boolean bool = false;
        try {
            int nums = Integer.parseInt(text);
            Log.i("", nums + " is a number");
            bool = true;
        } catch (NumberFormatException e) {
            Log.i("", text + " is not a number");
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

    /**
     * Test if First Character on the String is in Arabic Alphabet
     * @param s
     * @return TRUE || FALSE
     */
    public static boolean isProbablyArabicOnFirstChar(String s) {
        char[] c = s.toCharArray();
        //the arabic letter 'لا' is special case having the range from 0xFE70 to 0xFEFF
        if (c[0] >= 0x0600 && c[0] <= 0x06E0)
            return true;
        if (c[0] >= 0x600 && c[0] <= 0x6ff)
            return true;
        return c[0] >= 0xFE70 && c[0] <= 0xFEFF;

    }

    public String arabicNumberFormatter2(String sentence){
        return sentence
                .replaceAll("1","١")
                .replaceAll("2","٢")
                .replaceAll("3","٣")
                .replaceAll("4","١")
                .replaceAll("5","٢")
                .replaceAll("6","٣")
                .replaceAll("7","١")
                .replaceAll("8","٢")
                .replaceAll("9","٣")
                .replaceAll("0","١");
    }



    /******THIRD ATTEMPT******/
    public String searchWord3(String srcSenctence) {
        /*if (srcSenctence.equals(""))
            return "";*/
        String regex = "\\W+"; //(?<=\n)\b"
        String[] sourceAStr = (srcSenctence.trim()).split(regex);
        /*if (isProbablyArabicOnFirstChar(srcSenctence.split(regex)[0])) {
            sourceAStr[0] = sourceAStr[0].substring(0, 1) + " "; // Add space if it is English Char
            sourceAStr = sourceAStr.toString().split(regex); // Reformat again the value and now with space
        }*/

        Log.d("SPLIT", fileArrayToString(sourceAStr));
        String[] newList = getArrayOfArrabic(sourceAStr);
        Log.d("ARAB", fileArrayToString(newList));

        //int inv = sourceAStr.length - 1;
        for (int i = 0; i < sourceAStr.length; i++/*, inv--*/)
        {
            if (srcSenctence.contains(sourceAStr[i]))
            {
                srcSenctence = srcSenctence.replaceAll(sourceAStr[i], newList[i]);
            }
        }
        return srcSenctence;
        //return srcSenctence.replaceAll("<font>","<font color='#EE0000'>");
    }

    /** For Debugging Purposes to Display  the Contents */
    String fileArrayToString(String[] f){
        String output = "";
        String delimiter = "\n"; // Can be new line \n tab \t etc...
        for (int i=0; i<f.length; i++)
        {
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
        Log.d("DBARAB",fileArrayToString(newAString.toString().split(" ")));

        db.close();

        return newAString.toString().split(" ");
    }

    /* Get the Translation as List from DB */
    private String[] getArrayOfArrabic(String[] strList) {
        DatabaseAccess db = DatabaseAccess.getInstance(this.context);
        db.open();

        String[] newAString = new String[255];
        int _continueWithSpaces = 0;
        int counter = strList.length;
        int flag_inverse = 0;
        //for (String str : strList) {
        for (int i = 0; i < counter; i++) {
            TranslateModel tm = db.getTranslationToArabic(strList[i]);
            _continueWithSpaces = tm.getLength();
            if (_continueWithSpaces > 0) { // Continue and skip the translation if and only if data has spaces
                _continueWithSpaces -= _continueWithSpaces;
                //continue;
                //newAString[i] = ("--");
                newAString[i] = db.getTranslationToArabic(strList[i-_continueWithSpaces]).getEnglish();
                //newAString[i - _continueWithSpaces] = "-";
                flag_inverse = 1;
                Log.i("counter", _continueWithSpaces+"");
            } else {
                if (flag_inverse == 0) { // NOT WORKING YET
                    if (tm.getEnglish() == null) {// Should not execute getTRansation... here only once is enough
                        //newAString.append("<font>[" + str + "]</font>");
                        newAString[i] = strList[i];
                    } else {
                        newAString[i] = tm.getEnglish(); // Or can be store to an array for less query on DB side
                    }
                } else {
                    newAString[i] = "";
                }
                flag_inverse = 0;
            }
        }
        Log.d("DBARAB", fileArrayToString(newAString.toString().split(" ")));

        db.close();

        return newAString;
    }
}
