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

import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eng.arab.translator.androidtranslator.R;
import com.eng.arab.translator.androidtranslator.activity.Speaker;

public class TranslateViewActivity extends AppCompatActivity implements OnClickListener
{
    private static final String STATE_SRC_CARD_VISIBILITY = "SRC_CARD_VISIBILITY";
    private static final String STATE_TRG_CARD_VISIBILITY = "TRG_CARD_VISIBILITY";
    private static final String STATE_SRC_TEXT = "SRC_TEXT";
    private static final String STATE_TRG_TEXT = "TRG_TEXT";
    private static final String SHARED_PREF_SRC_TEXT = "SRC_TEXT";
    private static final String SHARED_PREF_TRG_TEXT = "TRG_TEXT";
    private static final int SETTINGS_ACTIVITY_ID = 1;
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

    //TTS object
    private static Speaker _speaker;
    private final int CHECK_CODE = 0x1;
    private final int SHORT_DURATION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_translator);

        Init_ToolBarReturnButton();

        //preferences = PreferenceManager.getDefaultSharedPreferences(this);

        initTranslatorPanel();
        checkTTS();
        loadValueSP();
    }

    private void initTranslatorPanel() {
        mainPanel = (LinearLayout)findViewById(R.id.activity_translator_translate);

        srcCard = (CardView)findViewById(R.id.src_card);
        srcToolbar = (Toolbar)findViewById(R.id.src_toolbar);
        srcToolbar.inflateMenu(R.menu.src_card);
        srcToolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
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
        srcContent = (LinearLayout)findViewById(R.id.src_translate_content);

        translateButton = (Button)findViewById(R.id.main_translate_button);
        translateButton.setOnClickListener(this);

        trgCard = (CardView)findViewById(R.id.translate_trg_card);
        trgToolbar = (Toolbar)findViewById(R.id.translate_trg_toolbar);
        trgToolbar.inflateMenu(R.menu.trg_card);
        trgToolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){

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
        trgContent = (LinearLayout)findViewById(R.id.trg_content);

        srcText = (EditText)findViewById(R.id.src_text);

        trgTextScroll = (ScrollView)findViewById(R.id.trg_text_scroll);
        trgText = (TextView)findViewById(R.id.trg_text);

        mainPanel.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            private int trgCardPreEditingVisibility;
            @Override public void onGlobalLayout() {
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

    private void snackBar(View view){
        Snackbar.make(view, "Dictionary", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void Init_ToolBarReturnButton() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar_translate); // Adding Home Button on Toolbar
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
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
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(srcText.getApplicationWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() { super.onPause(); }

    @Override
    protected void onStop() {
        super.onStop();
        _speaker.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    @Override
    public void onClick(View view) {
        if (view == translateButton) {
            trgText.setTextColor(Color.BLACK);// Set Default Color
            trgText.setText(srcText.getText());
            hideSoftKeyboard();

            if (!srcText.getText().toString().equals("")) { // TEST Blank
                TranslatorClass tc = new TranslatorClass();
                tc.setContext(getApplicationContext());
                String translatedSentence =
                        tc.searchWord3(new TranslatorClass().
                                arabicNumberFormatter2(srcText.getText().toString()));

                trgText.setText(String.valueOf(Html.fromHtml(translatedSentence)));
            }
        } else if (view == srcToolbar && !editing) {
            trgCard.setVisibility(trgCard.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
//            updateSrcToolbar();
        } else if (view == trgToolbar) {
            srcCard.setVisibility(srcCard.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
//            updateTrgToolbar();
        }
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

    /**
     * <strong>public void showAToast (String st)</strong></br>
     * this little method displays a toast on the screen.</br>
     * it checks if a toast is currently visible</br>
     * if so </br>
     * ... it "sets" the new text</br>
     * else</br>
     * ... it "makes" the new text</br>
     * and "shows" either or
     * @param st the string to be toasted
     */
    Toast toast = null;
    public void showAToast(String st) { //"Toast toast" is declared in the class
        try{ toast.getView().isShown();     // true if visible
            toast.setText(st);
        } catch (Exception e) {         // invisible if exception
            toast = Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT);
        }
        toast.show();  //finally display it
    }

    /**************SPEECH METHODS****************/
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
    /** Set the Shared Pref to Resume the EditTextBox Content */
    private void setSharedPref(String KEY, String value){
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                edit().putString(KEY, value).apply();
    }

    /** Get the Shared Pref to Resume the EditTextBox Content
     *   Default is BLANK */
    private String getSharedPref(String KEY){
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                getString(KEY, "");
    }

    /** Store values
     * USER DEFINED - Store EditTextBox Content for Resuming the Editing of EditTextbox*/
    private void storeValueSP() {
        setSharedPref(SHARED_PREF_SRC_TEXT, srcText.getText().toString());
        setSharedPref(SHARED_PREF_TRG_TEXT, trgText.getText().toString());
    }

    /** Store values
     * USER DEFINED - Store EditTextBox Content for Resuming the Editing of EditTextbox*/
    private void loadValueSP() {
        srcText.setText(getSharedPref(SHARED_PREF_SRC_TEXT));
        trgText.setText(getSharedPref(SHARED_PREF_TRG_TEXT));
    }

}
