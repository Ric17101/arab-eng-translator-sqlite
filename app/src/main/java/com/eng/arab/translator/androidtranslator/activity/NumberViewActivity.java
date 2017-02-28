package com.eng.arab.translator.androidtranslator.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.eng.arab.translator.androidtranslator.MainActivity;
import com.eng.arab.translator.androidtranslator.R;
import com.eng.arab.translator.androidtranslator.ShowDetailsAlphabet;
import com.eng.arab.translator.androidtranslator.adapter.AlphabetListAdapter;
import com.eng.arab.translator.androidtranslator.adapter.NumberListAdapter;
import com.eng.arab.translator.androidtranslator.model.DatabaseAccess;
import com.eng.arab.translator.androidtranslator.number.NumberDataHelper;
import com.eng.arab.translator.androidtranslator.number.NumberSuggestion;
import com.eng.arab.translator.androidtranslator.number.NumberWrapper;

import java.util.List;

public class NumberViewActivity extends AppCompatActivity implements NumberListAdapter.CallbackInterface, MediaPlayer.OnPreparedListener {
    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private static final int REQUEST_CODE = 1234;
    private static final String SHARED_PREF_SEARCH_TEXT = "SEARCH_TEXT";
    private final String TAG = "BlankFragment";
    private final int CHECK_CODE = 0x1;
    private final int SHORT_DURATION = 1000;
    private FloatingSearchView mSearchView;
    private RecyclerView mSearchResultsList;
    private NumberListAdapter mSearchResultsAdapter;
    private boolean mIsDarkSearchTheme = false;
    private String mLastQuery = "";
    //TTS object
    private Speaker _speaker;
    // Video object
    private VideoView mVideoView;
    private int position = 0;
    private MediaController mediaController;

    public NumberViewActivity() {
        // Required empty pubclic constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_number_search);

        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_number_view);
        mSearchResultsList = (RecyclerView) findViewById(R.id.search_results_number_list);

        setupFloatingSearch();
        setupResultsList();

        populateCardList();
        new TTSTask().execute("");
    }

    private void populateCardList() {
        List<NumberWrapper> results = getAllData(getApplicationContext());
//        mSearchResultsAdapter = new AlphabetListAdapter(results);
//        mSearchResultsList.setAdapter(mSearchResultsAdapter);

        mSearchResultsList.setHasFixedSize(true);
        mSearchResultsList.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultsList.setItemAnimator(new DefaultItemAnimator());
        mSearchResultsAdapter.swapData(results);
    }

    /*
        Setting data of sAlphabetWrappers onSearch
        CALLED at AlphabetViewActivity
    */
    public List<NumberWrapper> getAllData(Context context) {
        DatabaseAccess db;
        db = DatabaseAccess.getInstance(context.getApplicationContext());
        db.open();
        List<NumberWrapper> list = db.getAllDetailsOfNumbers();
        db.close();
        return list;
    }

    public void showDetails(View v) {
        startActivity(new Intent(NumberViewActivity.this, ShowDetailsAlphabet.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        _speaker.destroy();
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
                    NumberDataHelper.findSuggestions(getApplicationContext(), newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new NumberDataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<NumberSuggestion> results) {

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

                NumberSuggestion numberSuggestion = (NumberSuggestion) searchSuggestion;
                NumberDataHelper.findNumbers(getApplicationContext(), numberSuggestion.getWord(),
                        new NumberDataHelper.OnFindNumberListener() {

                            @Override
                            public void onResults(List<NumberWrapper> results) {
                                mSearchResultsAdapter.swapData(results);
                            }

                        });
                Log.d(TAG, "onSuggestionClicked()");

                mLastQuery = searchSuggestion.getWord();
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;

                NumberDataHelper.findNumbers(getApplicationContext(), query,
                        new NumberDataHelper.OnFindNumberListener() {

                            @Override
                            public void onResults(List<NumberWrapper> results) {
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
                mSearchView.swapSuggestions(NumberDataHelper.getHistory(getApplicationContext(), 5));

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

                    /*mIsDarkSearchTheme = true;

                    //demonstrate setting colors for items
                    mSearchView.setBackgroundColor(Color.parseColor("#787878"));
                    mSearchView.setViewTextColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setHintTextColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setActionMenuOverflowColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setMenuItemIconColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setClearBtnColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setDividerColor(Color.parseColor("#BEBEBE"));
                    mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));*/
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
                    //Toast.makeText(getApplicationContext().getApplicationContext(), item.getTitle(),
                    //        Toast.LENGTH_SHORT).show();
                }

            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {
                startActivity(new Intent(NumberViewActivity.this, MainActivity.class)
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
                NumberSuggestion numberSuggestion = (NumberSuggestion) item;

                String textColor = mIsDarkSearchTheme ? "#ffffff" : "#000000";
                String textLight = mIsDarkSearchTheme ? "#bfbfbf" : "#787878";

                if (numberSuggestion.getIsHistory()) {
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
                }

                textView.setTextColor(Color.parseColor(textColor));
                String text = numberSuggestion.getWord()
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
    /*
    * For Speech to Text Result
    * */
    private void checkTTS() {
        _speaker = new Speaker(this);
        System.out.println("I'm in checkTTS");
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {

            case RESULT_OK:

                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    System.out.println("I'm in onActivityResult");
                   /* _speaker = new Speaker(getApplicationContext());
                    ImageButton buttonSpeakArabicNumber = (ImageButton) findViewById(R.id.buttonSpeakKorean_alphabet);
                    buttonSpeakArabicNumber.setClickable(true);*/
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
     * Interface Method which communicates to the Acitivty here from the {@link AlphabetListAdapter}
     *
     * @param position - the position
     * @param alpha    - the text to pass back
     */
    @Override
    public void onHandleSelection(int position, NumberWrapper alpha, int mode) {

        //Toast.makeText(this, "Selected item in list "+ position + " with text "+ alpha, Toast.LENGTH_SHORT).show();

        // ... Start a new Activity here and pass the values
        /*Intent secondActivity = new Intent(MainActivity.this, DetailActivity.class);
        secondActivity.putExtra("Text",text);
        secondActivity.putExtra("Position", position);
        startActivityForResult(secondActivity, MY_REQUEST);*/
        switch (mode) {
            case 1:
                _speaker.speak(alpha.getExample());
                break;
            case 2:
                displayDialog(alpha.getVideoFileName());
                break;
        }

    }

    /*
    * Show video for the Alphabet using Dialog object, String vid is the file name without extension
    * from assets folder in the project
    * @param String vid
    * @return null
    * if (getResources().getIdentifier(vid, "raw", getPackageName()) == 0) {

        } else {
            Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.alphabet_video_view);
            dialog.setCancelable(true);

            VideoView mVideoView = (VideoView) dialog.findViewById(R.id.videoView);
            String path = "android.resource://" + getPackageName() + "/" + //R.raw.alif;
                    getResources().getIdentifier(vid, "raw", getPackageName());
            mVideoView.setOnPreparedListener(this);
            mVideoView.getHolder().setFixedSize(400, 400);
            dialog.show();
            mVideoView.setVideoURI(Uri.parse(path));
            //mVideoView.setVideoPath(path);
            mVideoView.requestFocus();
            mVideoView.start();
        }
    * */
    public void displayDialog(String vid) {
        vid = "number_" + vid;
        if (getResources().getIdentifier(vid, "raw", getPackageName()) == 0) {
            /* TEST if RAW file doesn't exist then do nothing*/
        } else {
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.number_video_view);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(true);

            mVideoView = (VideoView) dialog.findViewById(R.id.videoView);
            mVideoView.setZOrderMediaOverlay(true);
            String path = "android.resource://" + getPackageName() + "/" + //R.raw.alif;
                    getResources().getIdentifier(vid, "raw", getPackageName());

            FrameLayout fl = (FrameLayout) dialog.findViewById(R.id.VideoFrameLayout);
            ImageButton imageButtonClose = (ImageButton) fl.findViewById(R.id.imageButtonClose);
            imageButtonClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //dialog.dismiss();
                    if (v.getId() == R.id.imageButtonClose) {
                        dialog.dismiss();
                    }
                }
                // Perform button logic
            });

            // Set the media controller buttons
            if (mediaController == null) {
                mediaController = new MediaController(NumberViewActivity.this);

                // Set the videoView that acts as the anchor for the MediaController.
                mediaController.setAnchorView(mVideoView);

                // Set MediaController for VideoView
                mVideoView.setMediaController(mediaController);
            }

            mVideoView.setVideoURI(Uri.parse(path));
            mVideoView.requestFocus();

            // When the video file ready for playback.
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mVideoView.seekTo(position);
                    if (position == 0) {
                        mVideoView.start();
                    }
                    // When video Screen change size.
                    mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                            // Re-Set the videoView that acts as the anchor for the MediaController
                            mediaController.setAnchorView(mVideoView);
                        }
                    });
                }
            });
            dialog.show();
        }
    }

    public void setupResultsList() {
        mSearchResultsAdapter = new NumberListAdapter(this);
        mSearchResultsList.setAdapter(mSearchResultsAdapter);
        mSearchResultsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mVideoView.start();
        //mediaController.show(2000);
        mediaController.show();
        if (!mediaController.isShowing()) { // To always show the MediaContoll for VideiView
            mediaController.show();
        }
    }

    /* VIDEO */
    // Find ID corresponding to the name of the resource (in the directory raw).
    public int getRawResIdByName(String resName) {
        String pkgName = this.getPackageName();
        // Return 0 if not found.
        int resID = this.getResources().getIdentifier(resName, "raw", pkgName);
        Log.i("AndroidVideoView", "Res Name: " + resName + "==> Res ID = " + resID);
        return resID;
    }


    // When you change direction of phone, this method will be called.
    // It store the state of video (Current position)
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Store current position.
        if (mVideoView != null) {
            savedInstanceState.putInt("CurrentPosition", mVideoView.getCurrentPosition());
            mVideoView.pause();
        }
    }


    // After rotating the phone. This method is called.
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Get saved position.
        position = savedInstanceState.getInt("CurrentPosition");
        mVideoView.seekTo(position);
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
