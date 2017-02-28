package com.eng.arab.translator.androidtranslator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eng.arab.translator.androidtranslator.activity.DictionaryViewActivity;
import com.eng.arab.translator.androidtranslator.model.DatabaseAccess;
import com.eng.arab.translator.androidtranslator.model.TranslateModel;

import java.util.List;
import java.util.Locale;


public class ShowDetailsDictionary extends AppCompatActivity implements OnClickListener, TextToSpeech.OnInitListener {
    private static final String STATE_SRC_CARD_VISIBILITY = "SRC_CARD_VISIBILITY";
    private static final String STATE_SRC_TEXT = "SRC_TEXT";
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
//    private LinearLayout srcContent;
private Toolbar srcToolbar;
    private TextView trg_text_details;
    private TextView textViewKorean;
    private TextView textViewPronunciation;
    private TextView textViewWordType;
    private TextView textViewDefinitions;
    private TextView textViewEnglish;
    private ImageButton buttonSpeakKorean;
    //TTS object
    private TextToSpeech myTTS;
    //status check code
    private int MY_DATA_CHECK_CODE = 0;
    private String TextToSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_dictionary);

        // Get the Intent Extras
        Intent extras = getIntent();
        if (extras != null) {
            String month = extras.getStringExtra("MONTH"); //The key argument here must match that used in the other activity
            showMonthlyDetails(month);
        }

        Init_ToolBarReturnButton();
        InitMainPanel();
        new TTSTask().execute("");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        returnPage();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        myTTS.stop();
    }

    private void returnPage()
    {
        startActivity(new Intent(this, DictionaryViewActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void InitMainPanel() {
        mainPanel = (LinearLayout)findViewById(R.id.activity_translators_dictionary);
        srcCard = (CardView)findViewById(R.id.src_card_dictionary);
        srcToolbar = (Toolbar)findViewById(R.id.src_toolbar_dictionary);
        srcToolbar.inflateMenu(R.menu.src_card_dictionary);
        srcToolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_copy_dictionary:
                                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboard.setText(textViewKorean.getText());
                                showAToast("Arabic word copied");
                                break;
                            /*case R.id.action_clear_dictionary:
                                Toast.makeText(ShowDetailsDictionary.this, "CLEAR",Toast.LENGTH_SHORT).show();
                                break;*/
                        }
                        return true;
                    }
                });
        srcToolbar.setOnClickListener(this);
        srcToolbar.setTitle("ARABIC"); /**UPPER Text to translate**/
        //srcContent = (LinearLayout)findViewById(R.id.src_content_details);

        mainPanel.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {

            }
        });

        final Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction()) && "text/plain".equals(intent.getType())) {
            trg_text_details.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
        }
        //snackBar();
    }

    private void showMonthlyDetails(String arabic_word) {
        // Setting and Getting the Details from the Database
        DatabaseAccess db = DatabaseAccess.getInstance(getApplicationContext());
        db.open();
        List<TranslateModel> alpha = db.getAllDetailsByWord(arabic_word);
        TextToSpeak = alpha.get(0).getEnglish();

        textViewKorean = (TextView) findViewById(R.id.textViewKorean_dictionary);
        textViewPronunciation = (TextView) findViewById(R.id.textViewPronunciation_dictionary);
        textViewWordType = (TextView) findViewById(R.id.textViewWordType_dictionary);
        textViewDefinitions = (TextView) findViewById(R.id.textViewDefinitions_dictionary);

        LinearLayout src_content_details_definitions =
                (LinearLayout) findViewById(R.id.src_content_dictionary_definitions);
        textViewEnglish = (TextView) findViewById(R.id.textViewEnglish_dictionary);

        // Setting Details for the MONTH
        textViewKorean.setText(arabic_word.trim());
        textViewPronunciation.setText("\\" + alpha.get(0).getPronunciation().trim() + "\\");
        textViewWordType.setText(alpha.get(0).getType().toLowerCase());
        textViewDefinitions.setText(": " + alpha.get(0).getDefinition());
        textViewEnglish.setText(": " + alpha.get(0).getEnglish());

        // Button to Speak
        buttonSpeakKorean = (ImageButton) findViewById(R.id.buttonSpeakKorean_dictionary);
        buttonSpeakKorean.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                speakWords(TextToSpeak);
            }
        });
        /*buttonPlayVideo = (ImageButton) findViewById(R.id.buttonPlayVideo_dictionary);
        buttonPlayVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "TEST: "+ TextToSpeak, Toast.LENGTH_LONG).show();
            }
        });*/

    }

    private void tts_init() {
        //check for TTS data
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
    }

    private void speakWords(String speech) {
        //speak straight away
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }

    //act on result of TTS data check
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(getApplicationContext(), this);
            }
            else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    //setup TTS
    public void onInit(int initStatus) {

        //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {

            if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            showAToast("Sorry! Text To Speech failed...");
        }
    }

    private void snackBar(View view){
        Snackbar.make(view, "Dictionary", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void Init_ToolBarReturnButton() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar_dictionary); // Adding Home Button on Toolbar
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
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
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    private void hideSoftKeyboard() {
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(trg_text_details.getApplicationWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() { super.onPause(); }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
//        if (view == return_button) {
//
//        }
    }

    public void showAToast(String st) { //"Toast toast" is declared in the class
        try{ toast.getView().isShown();     // true if visible
            toast.setText(st);
        } catch (Exception e) {         // invisible if exception
            toast = Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT);
        }
        toast.show();  //finally display it
    }

    private class TTSTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            tts_init();
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
