package com.outplaysoftworks.sidedeckv2;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity{
    private SectionsPagerAdapter mSectionsPagerAdapter;
    CalculatorFragment mCalculatorFragment;
    ViewPager mViewPager;
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCalculatorFragment = new CalculatorFragment();
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
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
                    return new CalculatorFragment();
                case 1:
                    return new LogFragment();
            }
            return new LogFragment();
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

    void onClickNumbers(View view){
        mCalculatorFragment.onClickNumbers(view);
    }
    void onClickEnteredValue(View view){
        mCalculatorFragment.onClickEnteredValue(view);
    }

    void onClickP1Add(View view){
        mCalculatorFragment.onClickP1Add(view);
    }
    void onClickP1Sub(View view){
        mCalculatorFragment.onClickP1Sub(view);
    }
    void onClickP2Add(View view){
        mCalculatorFragment.onClickP2Add(view);
    }
    void onClickP2Sub(View view){
        mCalculatorFragment.onClickP2Sub(view);
    }
    void onClickDiceRoll(View view){
        mCalculatorFragment.onClickDiceRoll(view);
    }
    void onClickCoinFlip(View view){
        mCalculatorFragment.onClickCoinFlip(view);
    }

}
