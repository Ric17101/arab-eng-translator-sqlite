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

package com.eng.arab.translator.androidtranslator.alphabet;


import android.content.Context;
import android.util.Log;
import android.widget.Filter;

import com.eng.arab.translator.androidtranslator.model.DatabaseAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AlphabetDataHelper {

    private static Context mContext;

    public AlphabetDataHelper(Context context) {
        mContext = context;
        sAlphabetSuggestions =
                new ArrayList<>(new DatabaseAccess(mContext.getApplicationContext()).getAlphabets());
    }

    //private static final String COLORS_FILE_NAME = "colors.json";
    private static DatabaseAccess db;
    private static List<AlphabetModel> sAlphabetWrappers = new ArrayList<AlphabetModel>();

    /*private static List<AlphabetSuggestion> sAlphabetSuggestions =
            new ArrayList<>(new DatabaseAccess(mContext.getApplicationContext()).getAlphabets());*/

    private static List<AlphabetSuggestion> sAlphabetSuggestions_ =
            new ArrayList<>(Arrays.asList(
                    new AlphabetSuggestion("green"),
                    new AlphabetSuggestion("blue"),
                    new AlphabetSuggestion("pink"),
                    new AlphabetSuggestion("purple"),
                    new AlphabetSuggestion("brown"),
                    new AlphabetSuggestion("gray"),
                    new AlphabetSuggestion("Granny Smith Apple"),
                    new AlphabetSuggestion("Indigo"),
                    new AlphabetSuggestion("Periwinkle"),
                    new AlphabetSuggestion("Mahogany"),
                    new AlphabetSuggestion("Maize"),
                    new AlphabetSuggestion("Mahogany"),
                    new AlphabetSuggestion("Outer Space"),
                    new AlphabetSuggestion("Melon"),
                    new AlphabetSuggestion("Yellow"),
                    new AlphabetSuggestion("Orange"),
                    new AlphabetSuggestion("Red"),
                    new AlphabetSuggestion("Orchid")));

    //private static List<AlphabetSuggestion> sAlphabetSuggestions = null;
    private static List<AlphabetSuggestion> sAlphabetSuggestions =
            new ArrayList<>(Arrays.asList(
                    new AlphabetSuggestion(""))
            );

    public static List<AlphabetSuggestion> getAlphabetSuggestions(Context context) {
        return sAlphabetSuggestions;
    }
    //private static Context mContext;

    /*//public static Context getContect() {
        return mContext;
    }*/

//    public void setContext(Context c) {
//        mContext= c;
//    }
/*
    private static void getList() {
        List l = new DatabaseAccess(getContect()).getAlphabets();
        String letter = null;
        for (letter : l;;){

        }

    }*/

    public interface OnFindAlphabetListener {
        void onResults(List<AlphabetModel> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<AlphabetSuggestion> results);
    }


    public static List<AlphabetSuggestion> getHistory(Context context, int count) {

        List<AlphabetSuggestion> suggestionList = new ArrayList<>();
        AlphabetSuggestion colorSuggestion;
        for (int i = 0; i < sAlphabetSuggestions.size(); i++) {
            colorSuggestion = sAlphabetSuggestions.get(i);
            colorSuggestion.setIsHistory(false);
            //suggestionList.add(colorSuggestion);
//            if (suggestionList.size() == count) {
//                break;
//            }
        }
        return suggestionList;
    }

    public static List<AlphabetSuggestion> getHistory_my(Context context, int count) {
        List<AlphabetSuggestion> suggestionList = new ArrayList<>();
        AlphabetSuggestion alphabetSuggestion;

        // Initialze DB
        db = DatabaseAccess.getInstance(context.getApplicationContext());
        db.open();
        List<AlphabetSuggestion> alpha = db.getAlphabets();
        db.close();

        for (int i = 0; i < alpha.size(); i++) {
            alphabetSuggestion = alpha.get(i);
            alphabetSuggestion.setIsHistory(true);
            suggestionList.add(alphabetSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    private static void resetSuggestionsHistory() {
        for (AlphabetSuggestion alphabetSuggestion : sAlphabetSuggestions) {
            alphabetSuggestion.setIsHistory(false);
        }
    }

    /* Render all Data Initially on the List NOT WORKING*/
    public static void viewAllAlphabets(final Context context, final OnFindAlphabetListener listener) {
        initAlphabetWrapperList(context);
        new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<AlphabetModel> suggestionList = new ArrayList<>();
                List<AlphabetModel> alphaberList = loadJson(context);

                for (AlphabetModel alphabet : alphaberList) {
                    suggestionList.add(alphabet);
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
                    listener.onResults((List<AlphabetModel>) results.values);
                }
            }
        };
    }


    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
//        mContext = context;
//        db = DatabaseAccess.getInstance(context.getApplicationContext());
//        db.open();
//        List<AlphabetSuggestion> alpha = db.getAlphabets();
//        db.close();
//        sAlphabetSuggestions = alpha;
        //Set History
        /*sAlphabetSuggestions = new ArrayList<>(Collections.singletonList(
                new AlphabetSuggestion(query)));*/
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                AlphabetDataHelper.resetSuggestionsHistory();
                List<AlphabetSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (AlphabetSuggestion suggestion : sAlphabetSuggestions) {
                        if (suggestion.getWord().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<AlphabetSuggestion>() {
                    @Override
                    public int compare(AlphabetSuggestion lhs, AlphabetSuggestion rhs) {
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
                    listener.onResults((List<AlphabetSuggestion>) results.values);
                }
            }
        }.filter(query);

    }


    public static void findAlphabets(Context context, String query, final OnFindAlphabetListener listener) {
        initAlphabetWrapperList(context);
        filterData(context, query);
        //Set History
        sAlphabetSuggestions = new ArrayList<>(Collections.singletonList(
                new AlphabetSuggestion(query)));
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<AlphabetModel> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (AlphabetModel alphabet : sAlphabetWrappers) {
                        Log.i("TAG BindViewHolder1: ", alphabet.toString());
                        if (alphabet.getLetter().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(alphabet);
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
                    listener.onResults((List<AlphabetModel>) results.values);
                }
            }
        }.filter(query);

    }

    /*
        Setting data of sAlphabetWrappers onSearch
    */
    private static void filterData(Context context, String query) {
        //Getting Letter Details
        DatabaseAccess db;
        db = DatabaseAccess.getInstance(context.getApplicationContext());
        db.open();
        sAlphabetWrappers = db.getAllDetailsByLetter(query);

        db.close();
    }


    private static void initAlphabetWrapperList(Context context) {
        if (sAlphabetWrappers.isEmpty()) {
            sAlphabetWrappers = loadJson(context);
        }
    }

    private static List<AlphabetModel> loadJson(Context context) {
        DatabaseAccess db;
        db = DatabaseAccess.getInstance(context.getApplicationContext());
        db.open();
        List<AlphabetModel> alphabets = db.getAllAlphabets();
        db.close();

        return alphabets;
        /*String jsonString;

        try {
            InputStream is = context.getAssets().open(COLORS_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return jsonString;*/
    }
}
