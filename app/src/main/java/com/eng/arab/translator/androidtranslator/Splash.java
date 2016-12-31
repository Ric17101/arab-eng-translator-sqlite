package com.eng.arab.translator.androidtranslator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Splash extends Activity {

	private static final String VERSION = BuildConfig.VERSION_NAME;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		// FullScreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);

		// Set Build Version Below
		TextView textViewVersion = (TextView) findViewById(R.id.textViewVersionSplash);
		textViewVersion.setText("V" + VERSION);
		runSplash();
	}

	private void runSplash()
	{
		Thread timer = new Thread() {
			public void run() {
				try {
					sleep((long) 1430.14);//1430.14
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {

					Intent i = new Intent(Splash.this, MainActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(i);
					overridePendingTransition(R.anim.abc_popup_enter,
							R.anim.abc_popup_exit);

				}
			}
		};
		timer.start();
	}
	// To stop the BACK button returning from the Login (e.g. splash class)
	@Override
	protected void onPause() {
		super.onPause();  
		finish();
		// setContentView(R.layout.loggin_in_loading); //Loading XML
		// (loggin_in_loading.xml)
	}

}
