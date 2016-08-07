package com.outplaysoftworks.sidedeck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity{

    private static boolean IS_BETA_RELEASE = true;

    public static CalculatorFragment mCalculatorFragment;
    public static LogFragment mLogFragment;
    private ViewPager mViewPager;
    private static Context context;
    private static PopupMenu popupMenu;
    public static SharedPreferences sharedPreferences;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = database.getReference();
    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCalculatorFragment = new CalculatorFragment();
        mLogFragment = new LogFragment();
        context = this;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        IS_BETA_RELEASE = checkIfIsBeta();

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        assert mViewPager != null;
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //Setup the tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(mViewPager);
        trackAppOpens();
        checkFirebaseForUpdate();
    }

    private void trackAppOpens(){
        int timesOpened = sharedPreferences.getInt("TIMES_OPENED", 0); //NON-NLS
        timesOpened++;
        sharedPreferences.edit().putInt("TIMES_OPENED", timesOpened).apply();//NON-NLS
        boolean shouldAskUser = sharedPreferences.getBoolean("KEY_ASK_USER_TO_RATE", true);//NON-NLS
        if(timesOpened > 10 && shouldAskUser){
            askUserToRate();
        }
    }

    private void askUserToRate() {
        new AlertDialog.Builder(this, R.style.MyDialog)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.rateMe)
                .setMessage(R.string.rateMeMessage)
                .setPositiveButton(getString(R.string.ok), (dialog, which)->{
                    takeUserToPlayStore();
                    sharedPreferences.edit().putBoolean("KEY_ASK_USER_TO_RATE", false).apply();//NON-NLS
                })
                .setNegativeButton(getString(R.string.noThanks), (dialog, which)->{
                    sharedPreferences.edit().putBoolean("KEY_ASK_USER_TO_RATE", false).apply();//NON-NLS
                })
                .show();
    }

    private boolean checkIfIsBeta() {
        if(BuildConfig.VERSION_NAME.contains("b") || BuildConfig.VERSION_NAME.contains("B")){ //NON-NLS
            Log.d("BETA: ", "YOU ARE CURRENTLY USING A BETA RELEASE"); //NON-NLS
            return true;
        } else{
            return false;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            switch (position) {
                case 0:
                    return mCalculatorFragment;
                case 1:
                    return mLogFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getText(R.string.calculator);
                case 1:
                    return getResources().getText(R.string.duelLog);
            }
            return null;
        }
    }

    @SuppressWarnings("unused")
    public void showPopupMenu(View view){
        final Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        popupMenu = new PopupMenu(context, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.actions, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            switch(item.getItemId()){
                case R.id.menuItemUndo:
                    mCalculatorFragment.onUndoClicked();
                    break;
                case R.id.menuItemReset:
                    mCalculatorFragment.onResetClick();
                    break;
                case R.id.menuItemSettings:
                    startActivity(preferencesIntent);
                    break;
                case R.id.menuItemCalculator:
                    mCalculatorFragment.showCalculator();
                    break;
            }
            return false;
        });
    }

    private void checkFirebaseForUpdate(){
        Log.d("DB: ", "CHECKING FOR NEW VERSION"); //NON-NLS
        databaseReference.child("currentRelease").addListenerForSingleValueEvent(new ValueEventListener() { //NON-NLS
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Version version = dataSnapshot.getValue(Version.class);
                onGotNewVersion(version);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FireBase", " couldn't get version information"); //NON-NLS
            }
        });
        if(IS_BETA_RELEASE) {
            databaseReference.child("currentBeta").addListenerForSingleValueEvent(new ValueEventListener() { //NON-NLS
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Version version = dataSnapshot.getValue(Version.class);
                    onGotNewVersionBeta(version);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("FireBase", " couldn't get version information"); //NON-NLS
                }
            });
        }
    }

    private void onGotNewVersionBeta(Version newVersion) {
        Version localVersion = new Version(System.currentTimeMillis()/1000L,BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME);
        Log.d("Got version: ", "newest beta version is " + newVersion.getVersionCode()); //NON-NLS
        //Log.d("Got version: ", "current beta is " + newVersion.getVersionCode());
        int remoteVersionCode = newVersion.getVersionCode();
        String remoteVersionName = newVersion.getVersionName();
        Long remoteVersionAvailableAt = newVersion.getAvailableAt();
        Log.d("FB: ", "Remote avail: " + remoteVersionAvailableAt.toString() + ", local time: " + System.currentTimeMillis()/1000L); //NON-NLS
        if(localVersion.getVersionCode() < remoteVersionCode  &&
               System.currentTimeMillis()/1000L > remoteVersionAvailableAt){
            Bundle bundle = new Bundle();
            bundle.putString("got_new_version", remoteVersionName); //NON-NLS
            bundle.putString("old_version", BuildConfig.VERSION_NAME); //NON-NLS
            new AlertDialog.Builder(this, R.style.MyDialog)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.newVersionAvailable)
                    .setMessage(getString(R.string.newestVersionIs) + remoteVersionName + "\n" +
                            getString(R.string.youHaveVersion) + BuildConfig.VERSION_NAME + "\n" +
                            getString(R.string.goToPlayStore))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        takeUserToPlayStore();
                        bundle.putString("user_accepted", "yes");//NON-NLS
                        mFirebaseAnalytics.logEvent("beta_user_checked_update", bundle);//NON-NLS
                    })
                    .setNegativeButton(getString(R.string.noThanks), (dialog, which) -> {
                        bundle.putString("user_accepted", "no");//NON-NLS
                        mFirebaseAnalytics.logEvent("beta_user_checked_update", bundle);//NON-NLS
                    })
                    .show();
        }
    }

    private void onGotNewVersion(Version newVersion){
        Version localVersion = new Version(System.currentTimeMillis()/1000L, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME);
        Log.d("Got version: ", "newest release version: " + newVersion.getVersionCode()); //NON-NLS
        //Log.d("Got version: ", "current beta is " + newVersion.getVersionCode());
        int remoteVersionCode = newVersion.getVersionCode();
        String remoteVersionName = newVersion.getVersionName();
        Long remoteVersionAvailableAt = newVersion.getAvailableAt();
        if(localVersion.getVersionCode() < remoteVersionCode &&
                System.currentTimeMillis()/1000L > remoteVersionAvailableAt){
            Bundle bundle = new Bundle();
            bundle.putString("got_new_version", remoteVersionName);//NON-NLS
            bundle.putString("old_version", BuildConfig.VERSION_NAME);//NON-NLS
            new AlertDialog.Builder(this, R.style.MyDialog)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(getString(R.string.newVersionAvailable))
                    .setMessage(getString(R.string.newestVersionIs) + remoteVersionName + "\n" +
                            getString(R.string.youHaveVersion) + BuildConfig.VERSION_NAME + "\n" +
                            getString(R.string.goToPlayStore))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        takeUserToPlayStore();
                        bundle.putString("user_accepted", "yes");//NON-NLS
                        mFirebaseAnalytics.logEvent("user_checked_update", bundle);//NON-NLS
                    })
                    .setNegativeButton(getString(R.string.noThanks), (dialog, which) -> {
                        bundle.putString("user_accepted", "no");//NON-NLS
                        mFirebaseAnalytics.logEvent("user_checked_update", bundle);//NON-NLS
                    })
                    .show();
        }
    }

    private void takeUserToPlayStore() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName))); //NON-NLS
        } catch (android.content.ActivityNotFoundException ActivityNotFound) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName))); //NON-NLS
        }
    }

    @Override
    public void onBackPressed(){
        if(mCalculatorFragment.holderCalculator.getVisibility() == View.VISIBLE){
            mCalculatorFragment.holderCalculator.setVisibility(View.GONE);
        } else if( mCalculatorFragment.holderTimer.getVisibility() ==  View.VISIBLE){
            mCalculatorFragment.holderTimer.setVisibility(View.GONE);
        } else if(mCalculatorFragment.player1Name.isFocused()){
            mCalculatorFragment.player1Name.clearFocus();
        } else if(mCalculatorFragment.player2Name.isFocused()){
            mCalculatorFragment.player2Name.clearFocus();
        }
        else{
            super.onBackPressed();
        }

        //TODO: Check if the keyboard is showing and all that bull shit for the edittexts
        //http://toastdroid.com/2014/10/14/on-screen-keyboard-state-tracking-in-3-easy-steps/
    }
}

class Version{
    private int versionCode;
    private String versionName;
    private Long availableAt;
    public int getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public Long getAvailableAt(){
        return this.availableAt;
    }

    @SuppressWarnings("SameParameterValue")
    public Version(Long availableAt, int versionCode, String versionName){
        this.availableAt = availableAt;
        this.versionCode = versionCode;
        this.versionName = versionName;
    }

    public Version(){

    }
}