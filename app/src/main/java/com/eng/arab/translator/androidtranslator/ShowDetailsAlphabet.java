package com.eng.arab.translator.androidtranslator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
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

import com.eng.arab.translator.androidtranslator.alphabet.AlphabetModel;
import com.eng.arab.translator.androidtranslator.model.DatabaseAccess;

import java.util.List;
import java.util.Locale;


public class ShowDetailsAlphabet extends AppCompatActivity implements OnClickListener, TextToSpeech.OnInitListener {
    private static final String STATE_SRC_CARD_VISIBILITY = "SRC_CARD_VISIBILITY";
    private static final String STATE_SRC_TEXT = "SRC_TEXT";
    private int keyboard_flag = 0;

    private boolean editing = false;

    private SharedPreferences preferences;
    private LinearLayout mainPanel;
    private Spinner actionBarSpinner;

    private CardView srcCard;
    private Toolbar srcToolbar;
//    private LinearLayout srcContent;

    private TextView trg_text_details;
    private TextView textViewKorean;
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
        setContentView(R.layout.activity_show_alphabet);

        // Get the Intent Extras
        Intent extras = getIntent();
        if (extras != null) {
            String month = extras.getStringExtra("MONTH"); //The key argument here must match that used in the other activity

            showMonthlyDetails(month);
        }

        Init_ToolBarReturnButton();
        InitMainPanel();
        tts_init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void InitMainPanel() {
        mainPanel = (LinearLayout)findViewById(R.id.activity_translators_alphabet);

        srcCard = (CardView)findViewById(R.id.src_card_alphabet);
        srcToolbar = (Toolbar)findViewById(R.id.src_toolbar_alphabet);
        srcToolbar.inflateMenu(R.menu.src_card_alphabet);
        srcToolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_share_alphabet:
                                Toast.makeText(ShowDetailsAlphabet.this, "Share",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.action_clear_alphabet:
                                Toast.makeText(ShowDetailsAlphabet.this, "CLEAR",Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });
        srcToolbar.setOnClickListener(this);
        srcToolbar.setTitle("ARABIC"); /**UPPER Text to translate*/
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
        DatabaseAccess db = DatabaseAccess.getInstance(getApplicationContext());
        db.open();
        List<AlphabetModel> alpha = db.getAllDetailsByLetter(arabic_word);
        TextToSpeak = alpha.get(0).getPronunciation();

        textViewKorean = (TextView) findViewById(R.id.textViewKorean_alphabet);
        textViewWordType = (TextView) findViewById(R.id.textViewWordType_alphabet);
        textViewDefinitions = (TextView) findViewById(R.id.textViewDefinitions_alphabet);

        // Shown only to all excepton alphabet
        LinearLayout src_content_details_definitions =
                (LinearLayout) findViewById(R.id.src_content_alphabet_definitions);
        //textViewEnglish = (TextView) findViewById(R.id.textViewEnglish_alphabet);

        // Setting Details for the MONTH
        textViewKorean.setText(arabic_word);
        textViewWordType.setText("letter");
        textViewDefinitions.setText(": " + alpha.get(0).getExample());
        //textViewEnglish.setText(": " + alpha.get(0).getExample());

        // Button to Speak
        buttonSpeakKorean = (ImageButton) findViewById(R.id.buttonSpeakKorean_alphabet);
        buttonSpeakKorean.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                speakWords(TextToSpeak);
            }
        });
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
            Toast.makeText(getApplicationContext(), "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    private void snackBar(View view){
        Snackbar.make(view, "Alphabet", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void Init_ToolBarReturnButton() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar_alphabet); // Adding Home Button on Toolbar
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
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

}
