package com.eng.arab.translator.androidtranslator.number;


import android.content.Context;
import android.util.Log;
import android.widget.Filter;

import com.eng.arab.translator.androidtranslator.model.DatabaseAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NumberDataHelper {

    private static Context mContext;
    //private static final String COLORS_FILE_NAME = "colors.json";
    private static DatabaseAccess db;
    private static List<NumberWrapper> sAlphabetWrappers = new ArrayList<NumberWrapper>();
    private static List<NumberSuggestion> sNumberSuggestions_ =
            new ArrayList<>(Arrays.asList(
                    new NumberSuggestion("green"),
                    new NumberSuggestion("blue"),
                    new NumberSuggestion("pink"),
                    new NumberSuggestion("purple"),
                    new NumberSuggestion("brown"),
                    new NumberSuggestion("gray"),
                    new NumberSuggestion("Granny Smith Apple"),
                    new NumberSuggestion("Indigo"),
                    new NumberSuggestion("Periwinkle"),
                    new NumberSuggestion("Mahogany"),
                    new NumberSuggestion("Maize"),
                    new NumberSuggestion("Mahogany"),
                    new NumberSuggestion("Outer Space"),
                    new NumberSuggestion("Melon"),
                    new NumberSuggestion("Yellow"),
                    new NumberSuggestion("Orange"),
                    new NumberSuggestion("Red"),
                    new NumberSuggestion("Orchid")));

    /*private static List<NumberSuggestion> sNumberSuggestions =
            new ArrayList<>(new DatabaseAccess(mContext.getApplicationContext()).getAlphabets());*/
    //private static List<NumberSuggestion> sNumberSuggestions = null;
    private static List<NumberSuggestion> sNumberSuggestions =
            new ArrayList<>(Arrays.asList(
                    new NumberSuggestion(""))
            );

    public NumberDataHelper(Context context) {
        mContext = context;
        sNumberSuggestions =
                new ArrayList<>(new DatabaseAccess(mContext.getApplicationContext()).getNumbers());
    }

    public static List<NumberSuggestion> getAlphabetSuggestions(Context context) {
        return sNumberSuggestions;
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

    public static List<NumberSuggestion> getHistory(Context context, int count) {

        List<NumberSuggestion> suggestionList = new ArrayList<>();
        NumberSuggestion colorSuggestion;
        for (int i = 0; i < sNumberSuggestions.size(); i++) {
            colorSuggestion = sNumberSuggestions.get(i);
            colorSuggestion.setIsHistory(false);
            //suggestionList.add(colorSuggestion);
//            if (suggestionList.size() == count) {
//                break;
//            }
        }
        return suggestionList;
    }

    public static List<NumberSuggestion> getHistory_my(Context context, int count) {
        List<NumberSuggestion> suggestionList = new ArrayList<>();
        NumberSuggestion numberSuggestion;

        // Initialze DB
        db = DatabaseAccess.getInstance(context.getApplicationContext());
        db.open();
        List<NumberSuggestion> alpha = db.getNumbers();
        db.close();

        for (int i = 0; i < alpha.size(); i++) {
            numberSuggestion = alpha.get(i);
            numberSuggestion.setIsHistory(true);
            suggestionList.add(numberSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    private static void resetSuggestionsHistory() {
        for (NumberSuggestion numberSuggestion : sNumberSuggestions) {
            numberSuggestion.setIsHistory(false);
        }
    }

    /* Render all Data Initially on the List NOT WORKING*/
    public static void viewAllAlphabets(final Context context, final OnFindNumberListener listener) {
        initNumberWrapperList(context);
        new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<NumberWrapper> suggestionList = new ArrayList<>();
                List<NumberWrapper> alphaberList = loadJson(context);

                for (NumberWrapper alphabet : alphaberList) {
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
                    listener.onResults((List<NumberWrapper>) results.values);
                }
            }
        };
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
//        mContext = context;
//        db = DatabaseAccess.getInstance(context.getApplicationContext());
//        db.open();
//        List<NumberSuggestion> alpha = db.getAlphabets();
//        db.close();
//        sNumberSuggestions = alpha;
        //Set History
        /*sNumberSuggestions = new ArrayList<>(Collections.singletonList(
                new NumberSuggestion(query)));*/
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                NumberDataHelper.resetSuggestionsHistory();
                List<NumberSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (NumberSuggestion suggestion : sNumberSuggestions) {
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
                Collections.sort(suggestionList, new Comparator<NumberSuggestion>() {
                    @Override
                    public int compare(NumberSuggestion lhs, NumberSuggestion rhs) {
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
                    listener.onResults((List<NumberSuggestion>) results.values);
                }
            }
        }.filter(query);

    }

    public static void findNumbers(Context context, String query, final OnFindNumberListener listener) {
        initNumberWrapperList(context);
        filterData(context, query);
        //Set History
        sNumberSuggestions = new ArrayList<>(Collections.singletonList(
                new NumberSuggestion(query)));
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<NumberWrapper> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (NumberWrapper alphabet : sAlphabetWrappers) {
                        Log.i("TAG BindViewHolder1: ", alphabet.toString());
                        if (alphabet.getNumber().toUpperCase()
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
                    listener.onResults((List<NumberWrapper>) results.values);
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
        sAlphabetWrappers = db.getAllDetailsByNumber(query);

        db.close();
    }

    private static void initNumberWrapperList(Context context) {
        if (sAlphabetWrappers.isEmpty()) {
            sAlphabetWrappers = loadJson(context);
        }
    }

    private static List<NumberWrapper> loadJson(Context context) {
        DatabaseAccess db;
        db = DatabaseAccess.getInstance(context.getApplicationContext());
        db.open();
        List<NumberWrapper> alphabets = db.getAllNumbers();
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


    public interface OnFindNumberListener {
        void onResults(List<NumberWrapper> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<NumberSuggestion> results);
    }
}
