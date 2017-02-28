package com.eng.arab.translator.androidtranslator.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.eng.arab.translator.androidtranslator.MainActivity;
import com.eng.arab.translator.androidtranslator.R;
import com.eng.arab.translator.androidtranslator.ShowDetailsDictionary;
import com.eng.arab.translator.androidtranslator.adapter.DictionaryListAdapter;
import com.eng.arab.translator.androidtranslator.dictinary.DictionaryDataHelper;
import com.eng.arab.translator.androidtranslator.dictinary.DictionaryWrapper;
import com.eng.arab.translator.androidtranslator.dictinary.DictionarySuggestion;
import com.eng.arab.translator.androidtranslator.model.DatabaseAccess;

import java.util.List;

public class DictionaryViewActivity extends AppCompatActivity {
    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private static final int REQUEST_CODE = 1234;
    private final String TAG = "BlankFragment";
    private FloatingSearchView mSearchView;

    private RecyclerView mSearchResultsList;
    private DictionaryListAdapter mSearchResultsAdapter;

    private boolean mIsDarkSearchTheme = false;
    private String mLastQuery = "";

    public DictionaryViewActivity() {
        // Required empty pubclic constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dictionary_search);

        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_dictionary_view);
        mSearchResultsList = (RecyclerView) findViewById(R.id.search_results_dictionary_list);

        setupFloatingSearch();
        setupResultsList();

        populateCardList();
    }

    private void populateCardList() {
        List<DictionaryWrapper> results = getAllData(getApplicationContext());

        mSearchResultsList.setHasFixedSize(true);
        mSearchResultsList.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultsList.setItemAnimator(new DefaultItemAnimator());
        mSearchResultsAdapter.swapData(results);
    }

    /**
        Setting data of sAlphabetWrappers onSearch
        CALLED at AlphabetViewActivity
    */
    public List<DictionaryWrapper> getAllData(Context context) {
        DatabaseAccess db;
        db = DatabaseAccess.getInstance(context.getApplicationContext());
        db.open();
        List<DictionaryWrapper> list = db.getAllDictionaryDetailsOfWords();
        db.close();
        return list;
    }

    /*
        Setting data of sAlphabetWrappers onSearch using the LIKE operator per Keywor of the word
        CALLED at AlphabetViewActivity
    */
    public List<DictionaryWrapper> getSuggestionData(Context context, String keyWordSearch) {
        DatabaseAccess db;
        db = DatabaseAccess.getInstance(context.getApplicationContext());
        db.open();
        List<DictionaryWrapper> list = db.getLikeDictionaryDetailsByWord(keyWordSearch);
        db.close();
        return list;
    }

    public void showWordDetails(View v) {
        startActivity(new Intent(DictionaryViewActivity.this, ShowDetailsDictionary.class)
                /*.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)*/.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupFloatingSearch() {
        mSearchView.setOnHomeActionClickListener(
                new FloatingSearchView.OnHomeActionClickListener() {
                    @Override
                    public void onHomeClicked() {

                    }
                });

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.
                    DictionaryDataHelper.findSuggestions(getApplicationContext(), newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new DictionaryDataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<DictionarySuggestion> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary
                                    mSearchView.swapSuggestions(results);

                                    //let the users know that the background
                                    //process has completed
                                    mSearchView.hideProgress();
                                }
                            });
                    }

                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                DictionarySuggestion wordSuggestion = (DictionarySuggestion) searchSuggestion;
                DictionaryDataHelper.findWords(getApplicationContext(), wordSuggestion.getWord(),
                        new DictionaryDataHelper.OnFindWordListener() {

                            @Override
                            public void onResults(List<DictionaryWrapper> results) {
                                mSearchResultsAdapter.swapData(results);
                            }

                        });
                Log.d(TAG, "onSuggestionClicked()");

                mLastQuery = searchSuggestion.getWord();
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;

                DictionaryDataHelper.findWords(getApplicationContext(), query,
                        new DictionaryDataHelper.OnFindWordListener() {

                            @Override
                            public void onResults(List<DictionaryWrapper> results) {
                                mSearchResultsAdapter.swapData(results);
                            }

                        });
                Log.d(TAG, "onSearchAction()");
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
//                NumberDataHelper cdh = new NumberDataHelper();
//                cdh.setContext(getApplicationContext());
                mSearchView.swapSuggestions(DictionaryDataHelper.getHistory(getApplicationContext(), 5));

                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle(mLastQuery);

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getWord());

                Log.d(TAG, "onFocusCleared()");
            }
        });


        //handle menu clicks the same way as you would
        //in a regular activity
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

                if (item.getItemId() == R.id.action_refresh_list) {
                    populateCardList();
                } else if (item.getItemId() == R.id.action_voice_rec) {
                    Intent voiceRecognize = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    voiceRecognize.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
                    voiceRecognize.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    voiceRecognize.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG");
                    voiceRecognize.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the letter in ARABIC...");
                    /*voiceRecognize.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true); */
                    voiceRecognize.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
                    startActivityForResult(voiceRecognize, REQUEST_CODE);
                } else {

                    //just print action
                    Toast.makeText(getApplicationContext().getApplicationContext(), item.getTitle(),
                        Toast.LENGTH_SHORT).show();
                }

            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {
                startActivity(new Intent(DictionaryViewActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                Log.d(TAG, "onHomeClicked()");
            }
        });

        /*
         * Here you have access to the left icon and the text of a given suggestion
         * item after as it is bound to the suggestion list. You can utilize this
         * callback to change some properties of the left icon and the text. For example, you
         * can load the left icon images using your favorite image loading library, or change text color.
         *
         *
         * Important:
         * Keep in mind that the suggestion list is a RecyclerView, so views are reused for different
         * items in the list.
         */
        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {
                DictionarySuggestion alphabetSuggestion = (DictionarySuggestion) item;

                String textColor = mIsDarkSearchTheme ? "#ffffff" : "#000000";
                String textLight = mIsDarkSearchTheme ? "#bfbfbf" : "#787878";
                String text = "";

                if (alphabetSuggestion.getIsHistory()) {
                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_history_black_24dp, null));
                    Util.setIconColor(leftIcon, Color.parseColor(textColor));
                    leftIcon.setAlpha(.36f);
                } else {
                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_lightbulb_outline_black_24dp, null));
                    Util.setIconColor(leftIcon, Color.parseColor(textColor));
                    leftIcon.setAlpha(.36f);
                    /*leftIcon.setAlpha(0.0f);
                    leftIcon.setImageDrawable(null);*/

                    /*List<DictionaryWrapper> results = getSuggestionData(getApplicationContext(), mSearchView.getQuery());
                    Toast.makeText(getApplicationContext().getApplicationContext(), results.toString(),
                            Toast.LENGTH_SHORT).show();

                    textView.setTextColor(Color.parseColor(textColor));
                    text = alphabetSuggestion.getWord()
                            .replaceFirst(mSearchView.getQuery(),
                                    "<font color=\"" + textLight + "\">" + results.get(itemPosition)._arabic + "</font>");*/
                }
                textView.setTextColor(Color.parseColor(textColor));
                text = alphabetSuggestion.getWord()
                        .replaceFirst(mSearchView.getQuery(),
                                "<font color=\"" + textLight + "\">" + mSearchView.getQuery() + "</font>");
                textView.setText(Html.fromHtml(text));
            }

        });

        //listen for when suggestion list expands/shrinks in order to move down/up the
        //search results list
        mSearchView.setOnSuggestionsListHeightChanged(new FloatingSearchView.OnSuggestionsListHeightChanged() {
            @Override
            public void onSuggestionsListHeightChanged(float newHeight) {
                mSearchResultsList.setTranslationY(newHeight);
            }
        });
    }

    private void setupResultsList() {
        mSearchResultsAdapter = new DictionaryListAdapter();
        mSearchResultsList.setAdapter(mSearchResultsAdapter);
        mSearchResultsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

}
