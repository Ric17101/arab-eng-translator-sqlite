package com.eng.arab.translator.androidtranslator.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.eng.arab.translator.androidtranslator.ShowDetailsDictionary;
import com.eng.arab.translator.androidtranslator.model.DatabaseAccess;
import com.eng.arab.translator.androidtranslator.model.TranslateModel;

import java.util.List;

// Setting Dialog Title

/**
 * A simple {@link Fragment} subclass.
 */
public class DictionaryListFragment extends Fragment implements SearchView.OnQueryTextListener {
    private DatabaseAccess db;
    private List<String> words;
    private ListView mListView;

    public DictionaryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        db.open();
        words = db.getArabicTranslations();
        db.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, words);

        View header = inflater.inflate(R.layout.listview_header_row_dict, null);
        View view = inflater.inflate(R.layout.listview_item_row, container, false);
        SearchView searchView = (SearchView) view.findViewById(R.id.searchViewWord);
        setUpSearchAlphabetOnclickListener(searchView);

        // Set the Text Column for Column Header of List
//        TextView tvWordHeader = (TextView) view.findViewById(R.id.txtHeader);
//        tvWordHeader.setText(R.string.word);

        mListView = (ListView) view.findViewById(R.id.listWord);
        mListView.setAdapter(adapter);

        // ListView Item Click Listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                int itemPosition = position;
                if (position != 0) { // TO nxot select the header
                    String itemValue = (String) mListView.getItemAtPosition(position);
                    showWordDetails(itemValue);
                }
            }
        });
        mListView.addHeaderView(header);
        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String letter) {
        if (searchAlphabetList(letter)){
            showWordDetails(letter);
            Toast.makeText(getContext(), letter + " exist!", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(getContext(), letter + " doesn't exist!", Toast.LENGTH_SHORT);
        }
        return false;
    }

    private void snackBar(View view){
        Snackbar.make(view, "Dictionary", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
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

    private void showWordDetails(String letter) {
        DatabaseAccess db = DatabaseAccess.getInstance(getContext());
        db.open();
        List<TranslateModel> word = db.getAllDetailsByWord(letter);
        /*StringBuilder sb = new StringBuilder();
        sb.append("Arabic: ");
        sb.append(word.get(0).getArabic());
        sb.append("\nEnglish: ");
        sb.append(word.get(0).getEnglish());
        sb.append("\nTranscription: ");
        sb.append(word.get(0).getPronunciation());
        sb.append("\nDefinition: ");
        sb.append(word.get(0).getDifinition());
        sb.append("\nType: ");
        sb.append(word.get(0).getType());
        showAlert(sb.toString());*/

        Intent intent = new Intent(getActivity() ,ShowDetailsDictionary.class);
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
        return words.contains(searchText);
    }

    private void showAlert(String val) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Translation Details")
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


}
//db = DatabaseAccess.getInstance(getActivity());
//        db.open();
//        List<TranslateModel> quotes = db.getAllTranslations();
//        db.close();
//        View header = inflater.inflate(R.layout.listview_header_row, null);
//        View view = inflater.inflate(R.layout.listview_item_row, container, false);
//
//        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.listView);
//        RecyclerAdapter adapter = new RecyclerAdapter(getActivity(), quotes);
//
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setAdapter(adapter);

//for (TranslateModel quote : quotes) {
//        Log.i("TAGs ", quote._arabic);
////            Log.i("ENG ", quote._english);
////            Log.i("STRUECTURE ", quote._structure);
////            Log.i("ID", " " + quote._id);
//        }

