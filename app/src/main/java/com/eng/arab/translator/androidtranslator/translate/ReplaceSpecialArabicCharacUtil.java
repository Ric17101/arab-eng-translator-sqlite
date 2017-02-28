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

/**
 * Created by Richard on 18/02/2017.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * NOT YET USED
 */
public class ReplaceSpecialArabicCharacUtil {

    private List<Integer> getValidAsciiValues() {
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

    public String removeSpecialArabicCharactersClean(String name_a) {

        //replace_mult_spaces(name_a)
        int stringLenth = name_a.length();
        int pos = 0; //the Java index is 0-based (starts from 0)
        while (pos < stringLenth) {
            char nextChar = name_a.substring(pos, pos + 1).toCharArray()[0];
            int asciiValue = (int) nextChar;
            if (getValidAsciiValues().contains(asciiValue)) {
                pos++;
            } else {
                // throw new AssertionError("The string contains invalid characters");
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

        return name_a;
    }
}
