package com.outplaysoftworks.sidedeck;

import android.content.Context;

/**
 * Created by Billy on 6/15/2016.
 */
public class PreferenceHandler {
    private Context context;
    public PreferenceHandler(Context context){
        this.context = context;
    }
    private final static String KEY_playerOneDefaultNameSetting = "playerOneDefaultNameSetting";
    private final static String KEY_playerTwoDefaultNameSetting = "playerTwoDefaultNameSetting";
    private final static String KEY_soundOnOff= "soundOnOff";
    private final static String KEY_defaultLpSetting = "defaultLpSetting";
    private final static String KEY_amoledNightModeSetting = "amoledNightModeSetting";
    private final static String KEY_hasUserRatedAppYet = "hasUserRatedAppYet";
    private final static String KEY_launchWhenUserPressedRemind = "launchWhenUserPressedRemind";
}
