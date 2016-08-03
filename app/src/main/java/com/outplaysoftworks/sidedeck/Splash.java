package com.outplaysoftworks.sidedeck;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Splash extends AppCompatActivity {
    private static final int delay = 0; //Milliseconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Log.d("Splash", "Main activity should launch in " + delay + " milliseconds");
            //Intent intent = new Intent(this, MainActivity.class);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, delay);
    }
}
