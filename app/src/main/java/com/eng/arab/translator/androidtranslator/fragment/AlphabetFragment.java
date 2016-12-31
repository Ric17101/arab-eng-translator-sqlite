package com.eng.arab.translator.androidtranslator.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.eng.arab.translator.androidtranslator.ShowDetailsAlphabet;
import com.eng.arab.translator.androidtranslator.model.DatabaseAccess;

import java.util.List;

// Setting Dialog Title

/**
 * A simple {@link Fragment} subclass.
 */
public class AlphabetFragment extends AppCompatActivity implements
        View.OnClickListener,
        SearchView.OnQueryTextListener{
    private DatabaseAccess db;

    private List<String> alphabets;
    private ListView mListView;

    public AlphabetFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.listview_item_row_alphabet);
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup container = (ViewGroup) findViewById(android.R.id.content);

//        LinearLayout item = (LinearLayout)findViewById(R.id.item);
//        View child = getLayoutInflater().inflate(R.layout.child, null);
//        item.addView(child);

        db = DatabaseAccess.getInstance(getApplicationContext().getApplicationContext());
        db.open();
//        alphabets = db.getAlphabets();
        db.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                alphabets);
        View header = inflater.inflate(R.layout.listview_header_row, null);
        View view = inflater.inflate(R.layout.listview_item_row_alphabet, container, false);
        SearchView searchView = (SearchView) view.findViewById(R.id.searchViewAlphabet);
        setUpSearchAlphabetOnclickListener(searchView);

        // Set the Text Column for Column Header of List
//        TextView tvWordHeader = (TextView) view.findViewById(R.id.txtHeader);
//        tvWordHeader.setText(R.string.alphabet);

        mListView = (ListView) view.findViewById(R.id.listAlphabet);
        mListView.setAdapter(adapter);

        // ListView Item Click Listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                int itemPosition = position;
                if (position != 0) { // TO not select the header
                    String itemValue = (String) mListView.getItemAtPosition(position);
                    // Show Alert
                    showAlphabetyDetails(itemValue);
                }
            }
        });
        mListView.addHeaderView(header);
        setContentView(mListView);
    }

    @Override
    public boolean onQueryTextSubmit(String letter) {
        if (searchAlphabetList(letter)){

            showAlphabetyDetails(letter);
            Toast.makeText(getApplicationContext(), letter + " exist!", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(getApplicationContext(), letter + " doesn't exist!", Toast.LENGTH_SHORT);
        }
        return false;
    }

    public boolean onQueryTextChange(String letter) {
        Log.d("TRANSLATOR", "onQueryTextSubmit: ");
        Toast.makeText(getApplicationContext(), letter + " OnQueryTextChange", Toast.LENGTH_SHORT);
        return true;
    }

    private void setUpSearchAlphabetOnclickListener(SearchView mSearchView) {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here");

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String letter) {
//                if (searchAlphabetList(letter)){
//                    Log.d("TRANSLATOR", "onQueryTextSubmit: ");
//                    showAlphabetyDetails(letter);
//                    Toast.makeText(getContext(), letter + " doesn't exist!", Toast.LENGTH_SHORT);
//                } else {
//                    Toast.makeText(getContext(), letter + " doesn't exist!", Toast.LENGTH_SHORT);
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String letter) {
//                Toast.makeText(getContext(), letter + " doesn't exist!", Toast.LENGTH_SHORT);
//                return false;
//            }
//

//        });
    }

    private void showAlphabetyDetails(String letter) {
        /*DatabaseAccess db = DatabaseAccess.getInstance(getContext());
        db.open();
        List<AlphabetModel> alpha = db.getAllDetailsByLetter(letter);
        StringBuilder sb = new StringBuilder();
        sb.append("Transcription: ");
        sb.append(alpha.get(0).getLetter());
        sb.append("\nAlphabet in Arabic: ");
        sb.append(alpha.get(0).getPronunciation());
        sb.append("\nExample: ");
        sb.append(alpha.get(0).getExample());
//        sb.append("\nID: ");
//        sb.append(alpha.get(0).getID());
        showAlert(sb.toString());*/

        Intent intent = new Intent(getApplicationContext() ,ShowDetailsAlphabet.class);
        intent.putExtra("MONTH", letter);
        startActivity(intent);
    }
    /*
        Search from the List and return True if contains the parameters
        @param searchText
        @return  TRUE | FALSE
     */
    private Boolean searchAlphabetList(String searchText)
    {
        return alphabets.contains(searchText);
    }

    private void showAlert(String val) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
        alertDialog.setTitle("Alphabet Details")
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
    }

    @Override
    public void onClick(View v) {

    }
}

