package com.eng.arab.translator.androidtranslator.translate;

import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eng.arab.translator.androidtranslator.R;
import com.eng.arab.translator.androidtranslator.activity.Speaker;
import com.eng.arab.translator.androidtranslator.adapter.TranslateSuggestionListAdapter;

import java.util.List;

public class TranslateViewActivity extends AppCompatActivity implements OnClickListener, TranslateSuggestionListAdapter.CallbackInterface {
    private static final String STATE_SRC_CARD_VISIBILITY = "SRC_CARD_VISIBILITY";
    private static final String STATE_TRG_CARD_VISIBILITY = "TRG_CARD_VISIBILITY";
    private static final String STATE_SRC_TEXT = "SRC_TEXT";
    private static final String STATE_TRG_TEXT = "TRG_TEXT";
    private static final String SHARED_PREF_SRC_TEXT = "SRC_TEXT";
    private static final String SHARED_PREF_TRG_TEXT = "TRG_TEXT";
    private static final int SETTINGS_ACTIVITY_ID = 1;
    //TTS object
    private static Speaker _speaker;
    private final int CHECK_CODE = 0x1;
    private final int SHORT_DURATION = 1000;
    /**
     * <strong>public void showAToast (String st)</strong></br>
     * this little method displays a toast on the screen.</br>
     * it checks if a toast is currently visible</br>
     * if so </br>
     * ... it "sets" the new text</br>
     * else</br>
     * ... it "makes" the new text</br>
     * and "shows" either or
     *
     * @param st the string to be toasted
     */
    Toast toast = null;
    private int keyboard_flag = 0;
    private boolean editing = false;
    private SharedPreferences preferences;
    private LinearLayout mainPanel;
    private Spinner actionBarSpinner;
    private CardView srcCard;
    private Toolbar srcToolbar;
    private LinearLayout srcContent;
    private EditText srcText;
    private Button translateButton;
    private CardView trgCard;
    private Toolbar trgToolbar;
    private LinearLayout trgContent;
    private ScrollView trgTextScroll;
    private TextView trgText;
    //TTS object
    private TextToSpeech myTTS;
    //status check code
    private int MY_DATA_CHECK_CODE = 0;
    private String TextToSpeak;
    private String SuggestionWord = "";
    private ListView lvSuggestion;
    private CardView src_cardSuggestions;
    private RecyclerView mSearchResultsList;
    private TranslateSuggestionListAdapter mSearchResultsAdapter;
    private List<SentenceWrapper> results = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_translator);
        Init_ToolBarReturnButton();

        // preferences = PreferenceManager.getDefaultSharedPreferences(this);

        initTranslatorPanel();
        //checkTTS();
        new TTSTask().execute("");
        loadValueSP();

        // Suggestions Init
        src_cardSuggestions = (CardView) findViewById(R.id.src_cardSuggestions);
        setSuggestionVisibility(View.GONE);

        setupSuggestionList();
    }

    private void setupSuggestionList() {
        mSearchResultsList = (RecyclerView) findViewById(R.id.search_results_dictionary_list);
        setupResultsList();
    }

    private void initTranslatorPanel() {
        mainPanel = (LinearLayout) findViewById(R.id.activity_translator_translate);

        srcCard = (CardView) findViewById(R.id.src_card);
        srcToolbar = (Toolbar) findViewById(R.id.src_toolbar);
        srcToolbar.inflateMenu(R.menu.src_card);
        srcToolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_paste:
                                srcText.setText(readFromClipboard());
                                trgText.setTextColor(Color.BLACK);// Set Default Color
                                showAToast("Pasted");
                                break;
                            /*case R.id.action_share:
                                if (keyboard_flag == 0) {
                                    hideSoftKeyboard();
                                    keyboard_flag = 1;
                                }
                                else keyboard_flag = 0;
                                Toast.makeText(TranslateViewActivity.this,"Share",Toast.LENGTH_SHORT).show();
                                break;*/
                            case R.id.action_clear:
                                showAToast("Cleared");
                                srcText.setText("");
                                trgText.setTextColor(Color.BLACK);// Set Default Color
                                //trgText.setText("");
                                break;
                        }
                        return true;
                    }
                });
        srcToolbar.setOnClickListener(this);
        srcToolbar.setTitle("ARABIC");/**UPPER Text to translate*/
        srcContent = (LinearLayout) findViewById(R.id.src_translate_content);

        translateButton = (Button) findViewById(R.id.main_translate_button);
        translateButton.setOnClickListener(this);

        trgCard = (CardView) findViewById(R.id.translate_trg_card);
        trgToolbar = (Toolbar) findViewById(R.id.translate_trg_toolbar);
        trgToolbar.inflateMenu(R.menu.trg_card);
        trgToolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.action_trg_audio:
                                //Toast.makeText(TranslateViewActivity.this,"TRG AUDIO",Toast.LENGTH_SHORT).show();
                                speakEnglishTranslation();
                                break;
                            case R.id.action_copy:
                                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboard.setText(trgText.getText());
                                showAToast("English translation copied");
                                trgText.setTextColor(Color.BLUE); // Set color as Higlight
                                break;
                            /*case R.id.action_share:
                                Toast.makeText(TranslateViewActivity.this,"Share",Toast.LENGTH_SHORT).show();
                                final Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_TEXT, trgText.getText().toString());
                                startActivity(Intent.createChooser(intent, getResources().getText(R.string.share_with)));
                                break;*/
                        }
                        return true;
                    }
                });
        trgToolbar.setOnClickListener(this);
        trgToolbar.setTitle("ENGLISH");/**LOWER Translated*/
        trgContent = (LinearLayout) findViewById(R.id.trg_content);

        srcText = (EditText) findViewById(R.id.src_text);

        trgTextScroll = (ScrollView) findViewById(R.id.trg_text_scroll);
        trgText = (TextView) findViewById(R.id.trg_text);

        mainPanel.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            private int trgCardPreEditingVisibility;

            @Override
            public void onGlobalLayout() {
                final int heightDiffPx = mainPanel.getRootView().getHeight() - mainPanel.getHeight();
                final int heightDiffDp = (int) (heightDiffPx / (getResources().getDisplayMetrics().densityDpi / 160f));
                if (heightDiffDp > 150 && !editing) { // if more than 150 dps, its probably a keyboard...
                    editing = true;
                    trgCardPreEditingVisibility = trgCard.getVisibility();
                    trgCard.setVisibility(View.GONE);
//                    updateSrcToolbar();
                } else if (heightDiffDp < 150 && editing) {
                    editing = false;
                    trgCard.setVisibility(trgCardPreEditingVisibility);
                    srcText.clearFocus();
//                    updateSrcToolbar();
                }
            }

        });

        final Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction()) && "text/plain".equals(intent.getType())) {
            srcText.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
        }
        //snackBar();
    }

    private void snackBar(View view) {
        Snackbar.make(view, "Dictionary", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void Init_ToolBarReturnButton() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar_translate); // Adding Home Button on Toolbar
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        storeValueSP();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(STATE_SRC_CARD_VISIBILITY, srcCard.getVisibility());
        savedInstanceState.putInt(STATE_TRG_CARD_VISIBILITY, trgCard.getVisibility());
        savedInstanceState.putString(STATE_SRC_TEXT, Html.toHtml(srcText.getText()));
        if (trgText.getEditableText() != null) {
            savedInstanceState.putString(STATE_TRG_TEXT, Html.toHtml(trgText.getEditableText()));
        }
        storeValueSP();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int srcCardVisibility = savedInstanceState.getInt(STATE_SRC_CARD_VISIBILITY);
        final int trgCardVisibility = savedInstanceState.getInt(STATE_TRG_CARD_VISIBILITY);
        if (srcCardVisibility == View.VISIBLE && trgCardVisibility == View.GONE) {
            trgCard.setVisibility(View.GONE);
        } else if (srcCardVisibility == View.GONE && trgCardVisibility == View.VISIBLE) {
            srcCard.setVisibility(View.GONE);
        }
        srcText.setText(Html.fromHtml(savedInstanceState.getString(STATE_SRC_TEXT)));
        trgText.setText(Html.fromHtml(savedInstanceState.getString(STATE_TRG_TEXT)));
    }

    private void hideSoftKeyboard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(srcText.getApplicationWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (_speaker != null)
            _speaker.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (_speaker != null)
            _speaker.destroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch (item.getItemId()) {
            case android.R.id.home:
                storeValueSP();
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String removeHTMLTagsFromString(String strVal) {
        return strVal.replace("<font color='#0000FF'>", "") // String Clean up
                .replace("</font>", "");
    }

    @Override
    public void onClick(View view) {
        if (view == translateButton) {
            trgText.setTextColor(Color.BLACK); // Set Default Color
            hideSoftKeyboard();

            runOnUiThread(new Runnable() { // To manage and run separately from the Main Thread but this will run in UX Level
                TranslatorClass tc = null;

                public void run() {
                    try {
                        translateSourceTextBox();
                    } catch (final Exception ex) {
                        Log.i("---", "Exception in thread");
                    }
                }

                private void translateSourceTextBox() {
                    if (!srcText.getText().toString().equals("")) { // TEST Blank
                        tc = new TranslatorClass();
                        tc.setContext(getApplicationContext());

                        String sourceString = removeHTMLTagsFromString(srcText.getText().toString()).trim();

                        new TranslationFromTask().execute(sourceString);
                        if (srcText.getText().toString().isEmpty()) { /** If Empty Source EditText */
                            trgText.setText("");
                        }

                        /** TRANSLATION SUGGESTION **/
                        // GEt the first word in the Sentence // word has 2word
                        /*if (!SourceTextIsOnTheSuggestionList()) { // SuggestionWord != srcText.getText().toString()
                            if (sourceTextIsAword(sourceString)) {
                                populateCardList(sourceString.split(" ")[0]);
                            } else {
                                populateCardList(sourceString);
                            }
                            setSuggestionVisibility(View.VISIBLE);
                            SuggestionWord = srcText.getText().toString(); // not used??
                        }*/

                        if (suggestionHasValue()) { // If List has suggestion
                            if (suggestionListHasNoVlue(sourceString)) {
                                setSuggestionVisibility(View.GONE);
                                Log.d("CLSS", "lvSuggestion != null");
                            } else { // Test if Text
                                setSuggestionVisibility(View.VISIBLE);

                                if (isNotOnTheSuggestionList()) {
                                    trgText.setText("");
                                }
                            }

                            if (sameResultWithSourceText()) // Same input and result
                                trgText.setText("");
                        } else {
                            setSuggestionVisibility(View.VISIBLE);
//                            setSuggestionVisibility(View.GONE);
                        }
                    } else {
                        if (sourceTextIsEmpty()) {
                            //EditText is empty
                            setSuggestionVisibility(View.GONE);
                            Log.d("CLSS", "srcText.length() == 0");
                        }
                    }
                }

                private String translateSourceSentence(String sourceString) {
                    /** TRANSLATION OF SENTENCE **/
                    String translatedSentence = "";

                    if (sourceTextIsASentence(sourceString)) {
                        Log.d("translatedSentence1", "getIfSourceTextIsASentence");
                        translatedSentence = tc.getSentenceTranslationToEnglish(sourceString);
                    } else {
                        if (tc.getIfSourceTextIsAword2(sourceString)/* == false*/) {
                            Log.d("translatedSentence2", "getIfSourceTextIsAword");
                            translatedSentence = tc.translateArabicSentenceToEnglishWithWords(sourceString);
                            translatedSentence = tc.translateArabicSentenceToEnglishWithSpaces(translatedSentence);
                            translatedSentence = tc.searchWordIfSourceIsWordWithSpaces(translatedSentence);
                            translatedSentence = tc.searchWordIfSourceIsWord(translatedSentence);
                        } else {
                            /** REPLACE number with arabic equivalent then SearchWord */
                            Log.d("translatedSentence3", "else");
                            translatedSentence = tc.translateArabicSentenceToEnglishWithWords(sourceString);
                            translatedSentence = tc.translateArabicSentenceToEnglishWithSpaces(translatedSentence);
                            translatedSentence = tc.searchWordIfSourceIsWordWithSpaces(translatedSentence);
                            // translatedSentence = tc.searchWord3(translatedSentence);
                            translatedSentence = tc.searchWordIfSourceIsWord(translatedSentence);
                        }
                    }
                    return translatedSentence.trim();
                }

                private boolean suggestionHasValue() {
                    return lvSuggestion != null && !lvSuggestion.getAdapter().getItem(0).equals("");
                }

                private boolean isNotOnTheSuggestionList() {
                    return !SourceTextIsOnTheSuggestionList() ||
                            !tc.getIfSourceTextIsASentense(
                                    removeHTMLTagsFromString(srcText.getText().toString()));
                }

                private boolean sameResultWithSourceText() {
                    return removeHTMLTagsFromString(srcText.getText().toString()).equals(
                            removeHTMLTagsFromString(trgText.getText().toString()));
                }

                private boolean sourceTextIsEmpty() {
                    return srcText.length() == 0 || srcText.equals("") || srcText == null;
                }

                private boolean suggestionListHasNoVlue(String srcText) {
                    return (lvSuggestion.getAdapter().getCount() == 1 &&
                            lvSuggestion.getAdapter().getItem(0).toString().
                                    equals(removeHTMLTagsFromString(srcText)))
                            || (lvSuggestion.getAdapter().getCount() == 0
                            && (lvSuggestion.getAdapter().getItem(0).toString().
                            equals(removeHTMLTagsFromString(srcText))));
                }

                private boolean sourceTextIsAword(String srcText) {
                    return !tc.getIfSourceTextIsAword(removeHTMLTagsFromString(srcText))
                            && !tc.getIfSourceTextIsASentenceOneWord(removeHTMLTagsFromString(srcText))
                            && !tc.getIfSourceTextIsADictionaryWord(removeHTMLTagsFromString(srcText));
                }

                private boolean sourceTextIsASentence(String sourceString) {
                    return tc.getIfSourceTextIsASentense(sourceString);
                }

                /**
                 * Translation of word in background
                 */
                class TranslationFromTask extends AsyncTask<String, Void, String> {

                    @Override
                    protected String doInBackground(String... sourceString) {
                        return translateSourceSentence(sourceString[0]);
                    }

                    @Override
                    protected void onPostExecute(String translatedSentence) {
                        /** RESULT FROM TRANSLATION */
                        trgText.setText(removeHTMLTagsFromString(String.valueOf(translatedSentence)));
                    }

                    @Override
                    protected void onPreExecute() {
                    }

                    @Override
                    protected void onProgressUpdate(Void... values) {
                    }
                }
            }); /** End of UITHread Runnables*/

        } else if (view == srcToolbar && !editing) {
            trgCard.setVisibility(trgCard.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            //            updateSrcToolbar();
        } else if(view==trgToolbar) {
            srcCard.setVisibility(srcCard.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            //            updateTrgToolbar();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {

            case RESULT_OK:

                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    System.out.println("I'm in onActivityResult");
                } else {
                    Intent install = new Intent();
                    install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(install);
                }

                break;

            case RESULT_CANCELED:

                // ... Handle this situation
                break;
        }
    }

    /**
     * Interface Method which communicates to the Acitivty here from the {@link TranslateSuggestionListAdapter}
     *
     * @param position - the position
     * @param sentence - the text to pass back
     */
    @Override
    public void onHandleSelection(int position, SentenceWrapper sentence, int mode) {

        // Toast.makeText(this, "Selected item in list "+ position + " with text "+ sentence.getArabicSentence(), Toast.LENGTH_SHORT).show();
        srcText.setText(removeHTMLTagsFromString(String.valueOf(sentence.getArabicSentence())));
        // ... Start a new Activity here and pass the values
        switch (mode) {
            /*case 1 :
                _speaker.speak(sentence.getExample());
                break;
            case 2 :
                displayDialog(sentence.getVideoFileName());
                break;*/
        }
    }

    // TODO: What is being tested Here...
    private boolean SourceTextIsOnTheSuggestionList() {
        if (results == null || results.isEmpty())
            return false;

        int lvCount = results.size();
        String stringToFind = removeHTMLTagsFromString(srcText.getText().toString());
        for (int x = 0; x < lvCount; x++) { // Loop thru suggestion list
            if (removeHTMLTagsFromString(results.get(x).getArabicSentence()).equals(stringToFind)) {
                return true;
            }
        }
        return false;
        /*
            int lvCount = lvSuggestion.getAdapter().getCount();
            for (int x = 0; x < lvCount; x++) { // Loop thru suggestion list
                if (!removeHTMLTagsFromString(lvSuggestion.getAdapter().getItem(x).toString()).equals(removeHTMLTagsFromString(srcText.getText().toString())))
                /*switch (srcText.getText().toString().split(" ").length) { // if only one/two word
                    case 1:
                    case 2:
                        //if (removeHTMLTagsFromString(lvSuggestion.getAdapter().getItem(x).toString()).equals(removeHTMLTagsFromString(srcText.getText().toString())))
                        {
                            trgText.setText("");
                            Log.d("CLSS", "lvSuggestion has no suggestions");
                            x = lvCount; // To Stop the loop
                        }
                        break;
                }
            trgText.setText("");
            Log.d("CLSS", "lvSuggestion has no suggestions");
        }
        * */
    }

    private void setSuggestionVisibility(int visible) {
        //src_cardSuggestions.setVisibility(visible);
        src_cardSuggestions.setVisibility(View.GONE);
    }

    public String readFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
            android.content.ClipData data = clipboard.getPrimaryClip();
            if (data != null && description != null
                    && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                return String.valueOf(data.getItemAt(0).getText());
        }
        return null;
    }

    /**************
     * ListSuggestions METHODS
     ****************/
    private void setUpListViewSuggestion(String[] suggestions) {
        // Get ListView object from xml
        //lvSuggestion = (ListView) findViewById(R.id.list);

        // Defined Array values to show in ListView
        /*suggestions = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };*/

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.activity_translator_list_item, R.id.text1, suggestions);
        // Assign adapter to ListView
        lvSuggestion.setAdapter(adapter);
        // ListView Item Click Listener
        lvSuggestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;
                // ListView Clicked item value
                String itemValue = (String) lvSuggestion.getItemAtPosition(position);
                // Show Alert
                /*Toast.makeText(getApplicationContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();*/

                srcText.setText(itemValue.replace("[", "").replace("]", ""));

            }
        });
    }

    public void showAToast(String st) { //"Toast toast" is declared in the class
        try {
            toast.getView().isShown();     // true if visible
            toast.setText(st);
        } catch (Exception e) {         // invisible if exception
            toast = Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT);
        }
        toast.show();  //finally display it
    }

    /**************
     * SPEECH METHODS
     ****************/
    /*
    * For Speech to Text Result
    * */
    private void checkTTS() {
        //if (_speaker == null)

        _speaker = new Speaker(this);
        System.out.println("I'm in checkTTS");
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }



    private void speakEnglishTranslation() {
        String txt = trgText.getText().toString();
        if (txt != "" || txt != null) {
            _speaker.speak(txt);
        }
    }

    /**************SHAREPREFS METHODS****************/
    /** Called at TranslateViewActivity, AlphanetViewActivity */
    /**
     * Set the Shared Pref to Resume the EditTextBox Content
     */
    private void setSharedPref(String KEY, String value) {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                edit().putString(KEY, value).apply();
    }

    /**
     * Get the Shared Pref to Resume the EditTextBox Content
     * Default is BLANK
     */
    private String getSharedPref(String KEY) {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                getString(KEY, "");
    }

    /**
     * Store values
     * USER DEFINED - Store EditTextBox Content for Resuming the Editing of EditTextbox
     */
    private void storeValueSP() {
        setSharedPref(SHARED_PREF_SRC_TEXT, srcText.getText().toString());
        setSharedPref(SHARED_PREF_TRG_TEXT, trgText.getText().toString());
    }

    /**
     * Store values
     * USER DEFINED - Store EditTextBox Content for Resuming the Editing of EditTextbox
     */
    private void loadValueSP() {
        srcText.setText(getSharedPref(SHARED_PREF_SRC_TEXT));
        trgText.setText(getSharedPref(SHARED_PREF_TRG_TEXT));
    }

    /**
     * SETTING UP RECYCLERVIEW SUGGESTION SENTENCE LIST
     */
    private void setupResultsList() {
        mSearchResultsAdapter = new TranslateSuggestionListAdapter(this);
        //mSearchResultsAdapter.setContext(getApplicationContext());
        mSearchResultsList.setAdapter(mSearchResultsAdapter);
        mSearchResultsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void populateCardList(String searchSentence) {
        results = getArraySuggestion(searchSentence);

        mSearchResultsList.setHasFixedSize(true);
        mSearchResultsList.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultsList.setItemAnimator(new DefaultItemAnimator());
        mSearchResultsAdapter.swapData(results);
    }

    // Not Used
    private String[] getArraySuggestion_v1(String arabic_word) {
        TranslatorClass tc = new TranslatorClass();
        tc.setContext(getApplicationContext());
        // tc.getArrayOfArrabicSuggestion("مساء الخير");
        // return tc.getArabicSentence("جيد");
        return tc.getArabicSentence(arabic_word);
    }

    private List<SentenceWrapper> getArraySuggestion(String arabic_word) {
        TranslatorClass tc = new TranslatorClass();
        tc.setContext(getApplicationContext());
        // TODO: Add Loop here for the sentence parsing and formatting
        return tc.getArabicSentenceList(arabic_word);
        // TODO: Add ternary operator for the return list of suggestion
    }


    private class TTSTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            checkTTS();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
