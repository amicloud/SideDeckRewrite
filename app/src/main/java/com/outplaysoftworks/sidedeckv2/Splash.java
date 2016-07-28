package com.outplaysoftworks.sidedeckv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splash extends AppCompatActivity {
    private static final int delay = 750; //Milliseconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Log.d("Main activity should launch in " + delay + " milliseconds");
            //Intent intent = new Intent(this, MainActivity.class);
            startActivity(new Intent(this, MainActivity.class);
            finish();
        }, delay);
    }
}
