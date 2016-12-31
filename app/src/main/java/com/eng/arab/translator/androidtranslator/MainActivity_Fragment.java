package com.eng.arab.translator.androidtranslator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.eng.arab.translator.androidtranslator.fragment.AboutFragment;
import com.eng.arab.translator.androidtranslator.fragment.AlphabetFragment;
import com.eng.arab.translator.androidtranslator.fragment.DictionaryListFragment;
import com.eng.arab.translator.androidtranslator.fragment.HelpFragment;
import com.eng.arab.translator.androidtranslator.fragment.HomeFragment;
import com.eng.arab.translator.androidtranslator.fragment.MonthFragment;
import com.eng.arab.translator.androidtranslator.model.DatabaseAccess;
import com.eng.arab.translator.androidtranslator.model.TranslateModel;
import com.eng.arab.translator.androidtranslator.translate.TranslateViewActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class MainActivity_Fragment extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String STATE_SRC_CARD_VISIBILITY = "SRC_CARD_VISIBILITY";
    private static final String STATE_TRG_CARD_VISIBILITY = "TRG_CARD_VISIBILITY";
    private static final String STATE_SRC_TEXT = "SRC_TEXT";
    private static final String STATE_TRG_TEXT = "TRG_TEXT";
    private static final int SETTINGS_ACTIVITY_ID = 1;
    private int keyboard_flag = 0;

    private boolean editing = false;

    private SharedPreferences preferences;
    private LinearLayout mainPanel;
    private Spinner actionBarSpinner;

    private LinearLayout srcContent;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);
//        Toolbar actionbar = (Toolbar) findViewById(R.id.toolbar);
//        actionbar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setUpNavDrawer();
        setUpTranslatorLayout();

        changeKeyboard();
        // This is for QUERYing DATA from database
        homeFragment();
        //setBackGround();
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setBackGround(){
        DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
        dl.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.background_red));
    }

    private void changeKeyboard() {
        String languageToLoad  = "ar"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private void setUpNavDrawer()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Home Page", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                homeFragment();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(mDrawerToggle);
        //drawer.setDrawerListener(toggle); // depricated replaced with AddDrwaer
        mDrawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpTranslatorLayout()
    {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);



//        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_actionbar);
//        setSupportActionBar(toolbar);
//        final Context context = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? this : getSupportActionBar().getThemedContext();
//        final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        actionBarSpinner = (Spinner)inflater.inflate(R.layout.actionbar_spinner, null);
////        actionBarSpinner.setOnItemSelectedListener(this);
//        toolbar.addView(actionBarSpinner);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.action_settings:
                final Intent intent = new Intent(MainActivity_Fragment.this,
                        SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_ACTIVITY_ID);
                return true;
            case R.id.action_about:
                aboutFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Navigation Drawer for the OPTION on the LEFT side DRAWER
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            homeFragment();
        } else if (id == R.id.nav_translate) {
            translateFragment();
        } else if (id == R.id.nav_dictionary) {
            dictionaryFragment();
        } else if (id == R.id.nav_alphabet) {
            alphabetFragment();
        } else if (id == R.id.nav_month) {
            monthFragment();
        } else if (id == R.id.nav_help) {
            helpFragment();
        }  else if (id == R.id.nav_about) {
            aboutFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /***************FRAGMENT CALL******************/
    private void translateFragment() {
        startActivity(new Intent(this, TranslateViewActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void dictionaryFragment() {
        DictionaryListFragment listMethod = new DictionaryListFragment();
        FragmentManager manager = getSupportFragmentManager();
//        manager.beginTransaction().replace(R.id.activity_translators,
//                listMethod,
//                listMethod.getTag()
//        ).commit();
    }

    private void alphabetFragment() {
        FragmentManager manager = getSupportFragmentManager();
        AlphabetFragment alphabetFragment = new AlphabetFragment();
//        manager.beginTransaction().replace(R.id.activity_translators,
//                alphabetFragment,
//                alphabetFragment.getTag()
//        ).commit();
    }

    private void monthFragment() {
        FragmentManager manager = getSupportFragmentManager();
        MonthFragment alphabetFragment = new MonthFragment();
//        manager.beginTransaction().replace(R.id.activity_translators,
//                alphabetFragment,
//                alphabetFragment.getTag()
//        ).commit();
    }

    private void helpFragment() {
        HelpFragment secondMethod = new HelpFragment();
        FragmentManager manager = getSupportFragmentManager();
//        manager.beginTransaction().replace(R.id.activity_translators,
//                secondMethod,
//                secondMethod.getTag()
//        ).commit();
    }

    private void homeFragment() {
        FragmentManager manager = getSupportFragmentManager();
        HomeFragment homeFragment = HomeFragment.newInstance("some1","some2");
//        manager.beginTransaction().replace(R.id.activity_translators,
//                homeFragment,
//                homeFragment.getTag()
//        ).commit();

    }

    private void aboutFragment() {
        AboutFragment aboutFragment = AboutFragment.newInstance("some1","some2");
        FragmentManager manager = getSupportFragmentManager();
        //AboutFragment aboutFragment = AboutFragment.newInstance(3);
//        manager.beginTransaction().replace(R.id.activity_translators,
//                aboutFragment,
//                aboutFragment.getTag()
//        ).commit();
        //NavBarItemSeletected(4);
    }

    private void NavBarItemSeletected(int index){
//        onNavigationItemSelected(navigationView.getMenu().getItem(index));
//        navigationView.getMenu().getItem(index).setChecked(true);
        //onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_about));
        //navigationView.setCheckedItem(R.id.nav_about);
        onNavigationItemSelected(navigationView.getMenu().getItem(index));
        navigationView.setCheckedItem(index);
    }

    /*********************************************************************
     *****************Translation of Senctence 7/30/2016******************
     *********************************************************************/
    private List<TranslateModel> DBAccess(String mode)
    {
        DatabaseAccess db = DatabaseAccess.getInstance(this);
        db.open();
        List<TranslateModel> qoutes;
        if (mode == "all")
            qoutes = db.getAllTranslations();
        else
            qoutes = null;
        db.close();
        return qoutes;
    }

    private String translateSentence(String srcSenctence) {
        List<TranslateModel> languages;
        StringBuffer bufferCol1 = new StringBuffer();
        StringBuffer bufferCol2 = new StringBuffer();
        try {
            languages = DBAccess("all");
            for (TranslateModel lang : languages) // Loop all the database equivalence
            {
                bufferCol1.append("Id: " + lang.getID() + "\n");
                bufferCol1.append("English: " + lang.getEnglish()
                        + "\n");
                bufferCol2.append("Id: " + lang.getID() + "\n");
                bufferCol2.append("Arabic: " + lang.getArabic()
                        + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            bufferCol1.append(e);
        }
        return bufferCol2.toString();
    }

    private int countSpace(String strToken)
    {
        int spaceCount = 0;
        int i = 0;
        while( i < strToken.length() ){
            if( strToken.charAt(i) == ' ' ) {
                spaceCount++;
            }
            i++;
        }
        return spaceCount;
    }

    public String searchWord(String srcSenctence) {
        DatabaseAccess db = DatabaseAccess.getInstance(this);
        db.open();
        String searchTextWord = srcSenctence;

        StringBuffer buffer = new StringBuffer();
        int index = 0;
        String strcat = " ";

        StringTokenizer stk = new StringTokenizer(searchTextWord, " ");
        int counter = new StringTokenizer(searchTextWord, " \n").countTokens(); // Number of entered words
        String[] arrayTextResult = new String[counter];
        String[] arrayCatAfter = new String[counter];
        System.out.println("counter: " + counter);
        String arrayWord = " ";
        // By word DECODING using space as an index
        int iCount = 0;
        int spaceCount = 0;
        TranslateModel languages1 = new TranslateModel();
        while (stk.hasMoreTokens()) // Assign Words into Array then use it to query on the DB
        {
            strcat = " ";
            arrayWord = stk.nextToken();
            if (arrayWord.contains("\n") || arrayWord.contains("<br/>")) {
                strcat = "<br/>";
            }
//            arrayWord = stk.nextToken("\n");
            try { // Getting data from the database
                System.out.println("index[" + index + "] = " + arrayWord);
                languages1 = db.getTranslationToArabic(arrayWord);
                if (isNumber(arrayWord)) { // To check if the text is a number
                    arrayTextResult[index] =  arabicNumberFormatter(arrayWord);
                }
                else {
                    if (index == 0) {
                        arrayTextResult[index] = (languages1.getArabic().substring(0, 1).toUpperCase()
                                + languages1.getArabic().substring(1).toLowerCase());
                    }
                    else if (languages1.getArabic() == null) { // this is like Exception error
                        arrayTextResult[index] = (arrayWord);
                    }
                    else{
                        arrayTextResult[index] = (languages1.getArabic());
                    }
                }
            } catch (Exception e) {
                if (isNumber(arrayWord)) { // To check if the text is a number
                    arrayTextResult[index] = (arabicNumberFormatter(arrayWord));
                }
                else { // This will change the color (RED) of the invalid word and enclosed it with []
                    arrayTextResult[index] = ("<font color='#EE0000'>[" + arrayWord + "]</font>");
                }
            } finally {
                if (arrayWord.contains("\n") || arrayWord.contains("<br/>") || arrayTextResult.equals('\n')) {
//                    strcat = "<br/>";
//                    strcat = " ";
                }
                else {
//                    strcat = "<br/>";
//                    strcat = "-";
                }
                arrayCatAfter[index] = strcat;
            }
//                index += spaceCount;
            index++;
            System.out.println("end: " + index);
        } // End of WHile loop -->

        db.close();
        for (int i = 0; i < index; i++) // Data Rendering
        {
            buffer.append(arrayTextResult[i] + arrayCatAfter[i]);
        }
////        System.out.println("BUFFER = " + buffer.toString());
////        textResultCol2.setText(Html.fromHtml(buffer.toString()));
////        textResultCol1.setText(searchTextWord);
        return String.valueOf(Html.fromHtml(buffer.toString()));
//        return "";
    } // End of searchWord() -->

    public String searchWord2(String srcSenctence) {
        DatabaseAccess db = DatabaseAccess.getInstance(this);
        db.open();
        String searchTextWord = srcSenctence;

        StringBuffer buffer = new StringBuffer();
        int index = 0;
        String strcat = " ";
        String[] stk = searchTextWord.split("(?<=\n)\\b");
        int spltCounter = stk.length;
        String[] arrayTextResult = new String[spltCounter];
        String[] arrayCatAfter = new String[spltCounter];

        TranslateModel languages1;
        new TranslateModel();
        for (String arrayWord: stk)
        {
            System.out.println("index[" + index + "] = " + arrayWord);
            strcat = arrayWord;

            try { // Getting data from the database
                System.out.println("indexss[" + index + "] = " + arrayWord);
                if (isNumber(arrayWord)) { // To check if the text is a number
                    arrayTextResult[index] = arabicNumberFormatter(arrayWord);
                }
                else {
                    languages1 = db.getTranslationToArabic(arrayWord);
                    if (index == 0) {
                        arrayTextResult[index] = languages1.getArabic().substring(0, 1).toUpperCase()
                                + languages1.getArabic().substring(1).toLowerCase();
                    }
                    else if (languages1.getArabic() == null) { // this is like Exception error
                        arrayTextResult[index] = arrayWord;
                    }
                    else{
                        arrayTextResult[index] = languages1.getArabic();
                    }
                }
            } catch (Exception e) {
                if (isNumber(arrayWord)) { // To check if the text is a number
                    arrayTextResult[index] = arabicNumberFormatter(arrayWord);
                }
                else { // This will change the color (RED) of the invalid word and enclosed it with []
                    arrayTextResult[index] = "<font color='#EE0000'>[" + arrayWord + "]</font>";
                }
            } finally {
                if (arrayWord.contains("\n") || arrayWord.contains(" ") || arrayTextResult.equals('\n')) {
                    strcat = "<br/>";
//                    strcat = "";
                }
                else {
                    strcat = " ";
//                    strcat = "\n";
                }
                arrayCatAfter[index] = strcat;
                index++;
            }
        }
        db.close();
        for (int i = 0; i < index; i++) // Data Rendering
        {
            buffer.append(arrayTextResult[i] + arrayCatAfter[i]);
        }
        System.out.print("ARRAY: " + String.valueOf(Html.fromHtml(buffer.toString())));
        return String.valueOf(Html.fromHtml(buffer.toString()));
//        return "";
    } // End of searchWord() -->

    public boolean isNumber(String num)
    {
        String text = num;
        boolean bool = false;
        try {
            int nums = Integer.parseInt(text);
            Log.i("", nums + " is a number");
            bool = true;
        } catch (NumberFormatException e) {
            Log.i("", text + " is not a number");
            bool = false;
        }
        return bool;
    }


    public String arabicNumberFormatter(String array) // Format Numeric number into ARABIC number Symbol
    {
        // String fulltext =
        // array.replaceAll("1","١").replaceAll("2","٢").replaceAll("3","٣");
        // arrayTextResult[index] = fulltext;
        // or
        int myNum = 0;
        NumberFormat nf = null;
        try {
            nf = NumberFormat.getInstance(new Locale("ar", "EG"));
            myNum = Integer.parseInt(array);
        } catch (NumberFormatException nfe) {
        }
        return nf.format(myNum);
    }

}
