package com.outplaysoftworks.sidedeck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity{
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static CalculatorFragment mCalculatorFragment;
    public static LogFragment mLogFragment;
    ViewPager mViewPager;
    static Context context;
    static PopupMenu popupMenu;
    public static SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCalculatorFragment = new CalculatorFragment();
        mLogFragment = new LogFragment();
        context = this;
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //Setup the tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
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

    public void showPopupMenu(View view){
        final View v = view;
        final Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        popupMenu = new PopupMenu(context, v);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.actions, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() ==  R.id.menuItemUndo){
                    mCalculatorFragment.onUndoClicked();
                }else if(item.getItemId() == R.id.menuItemReset){
                    mCalculatorFragment.onResetClick();
                }else if(item.getItemId() == R.id.menuItemSettings){
                    startActivity(preferencesIntent);
                }else if(item.getItemId() == R.id.menuItemShowQuickCalc){

                }
                return false;
            }
        });

    }

}