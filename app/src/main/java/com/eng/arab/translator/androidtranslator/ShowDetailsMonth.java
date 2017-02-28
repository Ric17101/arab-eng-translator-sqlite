package com.eng.arab.translator.androidtranslator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eng.arab.translator.androidtranslator.model.DatabaseAccess;
import com.eng.arab.translator.androidtranslator.model.MonthModel;

import java.util.List;
import java.util.Locale;


public class ShowDetailsMonth extends AppCompatActivity implements OnClickListener, TextToSpeech.OnInitListener {
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
    private TextView textViewPronunciation;
    private TextView textViewWordType;
    private TextView textViewDefinitions;
    private TextView textViewEnglish;
    private Button buttonSpeakKorean;

    //TTS object
    private TextToSpeech myTTS;
    //status check code
    private int MY_DATA_CHECK_CODE = 0;

    private String TextToSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_month);

        // Get the Intent Extras
        Intent extras = getIntent();
        if (extras != null) {
            String month = extras.getStringExtra("MONTH"); //The key argument here must match that used in the other activity

            showMonthlyDetails(month);
            //Toast.makeText(getApplicationContext(), "There is a param..." + month, Toast.LENGTH_SHORT).show();
        }

        Init_ToolBarReturnButton();
        InitMainPanel();
        tts_init();
    }


    private void InitMainPanel() {
        mainPanel = (LinearLayout)findViewById(R.id.activity_translators_details);

        srcCard = (CardView)findViewById(R.id.src_card_details);
        srcToolbar = (Toolbar)findViewById(R.id.src_toolbar_details);
        srcToolbar.inflateMenu(R.menu.src_card_months);
        srcToolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_share_details:
                                if (keyboard_flag == 0) {
                                    hideSoftKeyboard();
                                    keyboard_flag = 1;
                                }
                                else keyboard_flag = 0;
                                Toast.makeText(ShowDetailsMonth.this,"Share",Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.action_clear_details:
                                Toast.makeText(ShowDetailsMonth.this,"CLEAR",Toast.LENGTH_SHORT).show();
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

    private void showMonthlyDetails(String month) {
        DatabaseAccess db = DatabaseAccess.getInstance(getApplicationContext());
        db.open();
        List<MonthModel> alpha = db.getAllDetailsByMonth(month);
        TextToSpeak = alpha.get(0).getEnglishMonth();

        textViewKorean = (TextView) findViewById(R.id.textViewKorean);
        textViewPronunciation = (TextView) findViewById(R.id.textViewPronunciation);
        textViewWordType = (TextView) findViewById(R.id.textViewWordType);
        textViewDefinitions = (TextView) findViewById(R.id.textViewDefinitions);
        LinearLayout src_content_details_definitions =
                (LinearLayout) findViewById(R.id.src_content_details_definitions);
        textViewEnglish = (TextView) findViewById(R.id.textViewEnglish);

        // Setting Details for the MONTH
        textViewKorean.setText(month);
        textViewPronunciation.setText("\\" + alpha.get(0).getExample() + "\\");
        textViewWordType.setText("noun");
        // textViewDefinitions.setText("TESTs");
        src_content_details_definitions.setVisibility(View.GONE);
        textViewEnglish.setText(": " + alpha.get(0).getEnglishMonth());

        // Button to Speak
        buttonSpeakKorean = (Button) findViewById(R.id.buttonSpeakKorean);
        buttonSpeakKorean.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                speakWords(TextToSpeak);
            }
        });
    }


    private void showAlert(String val) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
        alertDialog.setTitle("Month Details")
                .setMessage(val)
                .setCancelable(false)
                //.setPositiveButton("OK",  null)
                .setNegativeButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setCancelable(false);
        AlertDialog alert = alertDialog.create();
        // Showing Alert Message
        alertDialog.show();

        speakWords(val);
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
        Snackbar.make(view, "Dictionary", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void Init_ToolBarReturnButton() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar_details); // Adding Home Button on Toolbar
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
        //savedInstanceState.putInt(STATE_SRC_CARD_VISIBILITY, srcCard.getVisibility());
        //savedInstanceState.putString(STATE_SRC_TEXT, Html.toHtml(trg_text_details.getEditableText()));
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
//        final int srcCardVisibility = savedInstanceState.getInt(STATE_SRC_CARD_VISIBILITY);
//        trg_text_details.setText(Html.fromHtml(savedInstanceState.getString(STATE_SRC_TEXT)));
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        /*if (view == srcToolbar && !editing) {
//            trgCard.setVisibility(trgCard.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        } else //if (view == trgToolbar) {
            srcCard.setVisibility(srcCard.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
//      }*/

//        if (view == return_button) {
//
//        }
    }

}
