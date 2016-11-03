package com.outplaysoftworks.sidedeck;

import android.content.Intent;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Splash extends AppCompatActivity {
    private static final int delay = 0; //Milliseconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            MainActivity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }catch(Exception e){
            Log.e("Shared pref error: \n", e.getStackTrace().toString());
        }
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Log.d("Splash", "Main activity should launch in " + delay + " milliseconds"); //NON-NLS
            //Intent intent = new Intent(this, MainActivity.class);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, delay);
    }
}
