package com.outplaysoftworks.sidedeckv2;

import android.content.Context;

/**
 * Created by Billy on 6/15/2016.
 */
public class PreferenceHandler {
    private Context context;
    public PreferenceHandler(Context context){
        this.context = context;
    }

    private final static String KEYplayerOneDefaultNameSetting = "KEYplayerOneDefaultNameSetting";
    private final static String KEYplayerTwoDefaultNameSetting = "KEYplayerTwoDefaultNameSetting";
    private final static String KEYsoundOnOff= "KEYsoundOnOff";
    private final static String KEYdefaultLpSetting = "KEYdefaultLpSetting";
    private final static String KEYamoledNightModeSetting = "KEYamoledNightModeSetting";
    private final static String KEYhasUserRatedAppYet = "KEYhasUserRatedAppYet";
    private final static String KEYlaunchWhenUserPressedRemind = "KEYlaunchWhenUserPressedRemind";
}
