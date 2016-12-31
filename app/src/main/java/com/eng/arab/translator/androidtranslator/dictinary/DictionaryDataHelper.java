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

package com.eng.arab.translator.androidtranslator.dictinary;

import android.content.Context;
import android.widget.Filter;
import android.widget.Toast;

import com.eng.arab.translator.androidtranslator.model.DatabaseAccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DictionaryDataHelper {

    private static DatabaseAccess db;
    private static List<DictionaryModel> sDictionaryWrappers = new ArrayList<DictionaryModel>();
    private static List<DictionarySuggestion> sDictionarySuggestions = null;

    public interface OnFindWordListener {
        void onResults(List<DictionaryModel> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<DictionarySuggestion> results);
    }

    public static List<DictionarySuggestion> getHistory(Context context, int count) {
        List<DictionarySuggestion> suggestionList = new ArrayList<>();
        DictionarySuggestion DictionarySuggestion;

        // Initialze DB
        db = DatabaseAccess.getInstance(context.getApplicationContext());
        db.open();
        List<DictionarySuggestion> word = db.getDictionary();
        db.close();

        for (int i = 0; i < word.size(); i++) {
            DictionarySuggestion = word.get(i);
            DictionarySuggestion.setIsHistory(false);
            /*suggestionList.add(DictionarySuggestion);
            if (suggestionList.size() == count) {
                break;
            }*/
        }
        return suggestionList;
    }

    private static void resetSuggestionsHistory() {
        for (DictionarySuggestion DictionarySuggestion : sDictionarySuggestions) {
            DictionarySuggestion.setIsHistory(false);
        }
    }

    /* Render all Data Initially on the List NOT WORKING*/
    public static void viewAllWords(final Context context, final OnFindWordListener listener) {
        initDictionaryWrapperList(context);
        new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<DictionaryModel> suggestionList = new ArrayList<>();
                List<DictionaryModel> wordList = loadJson(context);

                for (DictionaryModel word : wordList) {
                    suggestionList.add(word);
                }

                FilterResults results = new FilterResults();
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
//                    listener.onResults(loadJson(context));
                    listener.onResults((List<DictionaryModel>) results.values);
                }
            }
        };
    }


    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        sDictionarySuggestions = new ArrayList<>(Collections.singletonList(
                new DictionarySuggestion(query)));
        Toast.makeText(context, "ITEM " + query, Toast.LENGTH_SHORT).show();
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                DictionaryDataHelper.resetSuggestionsHistory();
                List<DictionarySuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (DictionarySuggestion suggestion : sDictionarySuggestions) {
                        if (suggestion.getWord()/*.toUpperCase()*/
                                .startsWith(constraint.toString()/*.toUpperCase()*/)) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<DictionarySuggestion>() {
                    @Override
                    public int compare(DictionarySuggestion lhs, DictionarySuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<DictionarySuggestion>) results.values);
                }
            }
        }.filter(query);

    }


    public static void findWords(Context context, String query, final OnFindWordListener listener) {
        initDictionaryWrapperList(context);
        filterData(context, query);
        //Set History
        sDictionarySuggestions = new ArrayList<>(Collections.singletonList(
                new DictionarySuggestion(query)));
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<DictionaryModel> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (DictionaryModel word : sDictionaryWrappers) {
                        if (word.getArabic()/*.toUpperCase()*/
                                .startsWith(constraint.toString()/*.toUpperCase()*/)) {

                            suggestionList.add(word);
                        }
                    }

                }

                FilterResults results = new FilterResults();
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<DictionaryModel>) results.values);
                }
            }
        }.filter(query);

    }

    /*
        Setting data of sDictionaryWrappers onSearch
    */
    private static void filterData(Context context, String query) {
        //Getting Letter Details
        DatabaseAccess db;
        db = DatabaseAccess.getInstance(context.getApplicationContext());
        db.open();
        sDictionaryWrappers = db.getAllDictionaryDetailsByWord(query);

        db.close();
    }

    private static void initDictionaryWrapperList(Context context) {
        if (sDictionaryWrappers.isEmpty()) {
            sDictionaryWrappers = loadJson(context);
        }
    }

    private static List<DictionaryModel> loadJson(Context context) {
        DatabaseAccess db;
        db = DatabaseAccess.getInstance(context.getApplicationContext());
        db.open();
        List<DictionaryModel> words = db.getAllDictionary();
        db.close();

        return words;
    }
}
