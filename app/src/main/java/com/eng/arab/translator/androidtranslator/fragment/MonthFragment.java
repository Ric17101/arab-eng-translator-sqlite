package com.eng.arab.translator.androidtranslator.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.eng.arab.translator.androidtranslator.R;
import com.eng.arab.translator.androidtranslator.ShowDetailsMonth;
import com.eng.arab.translator.androidtranslator.model.DatabaseAccess;

import java.util.List;
import java.util.Locale;
// Setting Dialog Title

/**
 * A simple {@link Fragment} subclass.
 */
public class MonthFragment extends Fragment implements SearchView.OnQueryTextListener, OnInitListener{
    private DatabaseAccess db;

    private List<String> months;
    private ListView mListView;

    //TTS object
    private TextToSpeech myTTS;
    //status check code
    private int MY_DATA_CHECK_CODE = 0;

    public MonthFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        tts_init();

        db = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        db.open();
        months = db.getMonts();
        db.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                months);
        View header = inflater.inflate(R.layout.listview_header_row_month, null);
        View view = inflater.inflate(R.layout.listview_item_row_month, container, false);
        SearchView searchView = (SearchView) view.findViewById(R.id.searchViewMonth);
        setUpSearchAlphabetOnclickListener(searchView);

        // Set the Text Column for Column Header of List
//        TextView tvWordHeader = (TextView) view.findViewById(R.id.txtHeader);
//        tvWordHeader.setText(R.string.alphabet);

        mListView = (ListView) view.findViewById(R.id.listMonth);
        mListView.setAdapter(adapter);

        // ListView Item Click Listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                //int itemPosition = position;
                if (position != 0) { // TO not select the header
                    String itemValue = (String) mListView.getItemAtPosition(position);
                    // Show Alert
                    showMonthlyDetails(itemValue);
                }
            }
        });
        mListView.addHeaderView(header);
        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String month) {
        if (searchAlphabetList(month)) {
            Toast.makeText(getContext(), month + " exist!", Toast.LENGTH_SHORT);
            showMonthlyDetails(month);
        } else {
            Toast.makeText(getContext(), month + " doesn't exist!", Toast.LENGTH_SHORT);
        }
        return false;
    }

    public boolean onQueryTextChange(String letter) {
        Log.d("TRANSLATOR", "onQueryTextSubmit: ");
        Toast.makeText(getContext(), letter + " OnQueryTextChange", Toast.LENGTH_SHORT);
        return true;
    }

    private void setUpSearchAlphabetOnclickListener(SearchView mSearchView) {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here");
    }

    private void showMonthlyDetails(String month) {
        /*
        DatabaseAccess db = DatabaseAccess.getInstance(getContext());
        db.open();
        List<MonthModel> alpha = db.getAllDetailsByMonth(month);
        StringBuilder sb = new StringBuilder();
        sb.append("Month: ");
        sb.append(alpha.get(0).getEnglishMonth());
        sb.append("\nMonth in Arabic: ");
        sb.append(alpha.get(0).getArabicMonth());
        sb.append("\nPronunciation: ");
        sb.append(alpha.get(0).getExample());
//        sb.append("\nID: ");
//        sb.append(alpha.get(0).getID());
        showAlert(sb.toString());
        */

        Intent intent = new Intent(getActivity() ,ShowDetailsMonth.class);
        intent.putExtra("MONTH", month);
        startActivity(intent);
    }

    /*
        Search from the List and return True if contains the parameters
        @param searchText
        @return  TRUE | FALSE
     */
    private Boolean searchAlphabetList(String searchText)
    {
        return months.contains(searchText);
    }

    private void showAlert(String val) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
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
                myTTS = new TextToSpeech(getContext(), this);
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
            Toast.makeText(getContext(), "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }
}

