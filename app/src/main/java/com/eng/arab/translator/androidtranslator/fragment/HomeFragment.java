package com.eng.arab.translator.androidtranslator.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eng.arab.translator.androidtranslator.R;
import com.eng.arab.translator.androidtranslator.translate.TranslateViewActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private final  String TAG = this.getClass().getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private View setUpMainCardViewButton(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //setOnclickListeners(view);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);
        //snackBar(view);
        return view;
    }



    /*
        Onlcick Listeers for the Cardviews
    **/
    /*private void setOnclickListeners(View view) {
        LinearLayout cv_translate = (LinearLayout) view.findViewById(R.id.cv_translator);
            cv_translate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "TranslateViewActivity", Toast.LENGTH_SHORT).show();
                    translateFragment();
                }
            });
        LinearLayout cv_dictionary = (LinearLayout) view.findViewById(R.id.cv_dictionary);
            cv_dictionary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "Dictionary", Toast.LENGTH_SHORT).show();
                    dictionaryFragment();
                }
            });
        LinearLayout cv_alphabets = (LinearLayout) view.findViewById(R.id.cv_alphabet);
            cv_alphabets.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alphabetFragment();
                    Toast.makeText(getContext(), "Alphabet", Toast.LENGTH_SHORT).show();
                }
            });
        LinearLayout cv_months = (LinearLayout) view.findViewById(R.id.cv_month);
            cv_months.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    monthFragment();
                    Toast.makeText(getContext(), "Month", Toast.LENGTH_SHORT).show();
                }
            });
        LinearLayout cv_about_us = (LinearLayout) view.findViewById(R.id.cv_about_us);
            cv_about_us.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aboutFragment();
                    Toast.makeText(getContext(), "About Us", Toast.LENGTH_SHORT).show();
                }
            });
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, mParam1);
        Log.d(TAG, mParam2);
        return setUpMainCardViewButton(inflater, container);
        //return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /***************FRAGMENT CALL******************/
    private void translateFragment() {
        startActivity(new Intent(getContext(), TranslateViewActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void dictionaryFragment() {
        DictionaryListFragment listMethod = new DictionaryListFragment();
        FragmentManager manager = getFragmentManager();
//        manager.beginTransaction().replace(R.id.activity_translators,
//                listMethod,
//                listMethod.getTag()
//        ).commit();
    }

    private void alphabetFragment() {
        FragmentManager manager = getFragmentManager();
        AlphabetFragment alphabetFragment = new AlphabetFragment();
//        manager.beginTransaction().replace(R.id.activity_translators,
//                alphabetFragment,
//                alphabetFragment.getTag()
//        ).commit();
    }

    private void monthFragment() {
        FragmentManager manager = getFragmentManager();
        MonthFragment monthFragment = new MonthFragment();
//        manager.beginTransaction().replace(R.id.activity_translators,
//                monthFragment,
//                monthFragment.getTag()
//        ).commit();
    }

    private void homeFragment() {
        FragmentManager manager = getFragmentManager();
        HomeFragment homeFragment = HomeFragment.newInstance("some1","some2");
//        manager.beginTransaction().replace(R.id.activity_translators,
//                homeFragment,
//                homeFragment.getTag()
//        ).commit();
    }

    private void aboutFragment() {
        AboutFragment aboutFragment = AboutFragment.newInstance("some1","some2");
        FragmentManager manager = getFragmentManager();
        //AboutFragment aboutFragment = AboutFragment.newInstance(3);
//        manager.beginTransaction().replace(R.id.activity_translators,
//                aboutFragment,
//                aboutFragment.getTag()
//        ).commit();
    }

}
