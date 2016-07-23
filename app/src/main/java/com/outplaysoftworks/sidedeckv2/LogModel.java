package com.outplaysoftworks.sidedeckv2;

import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Billy on 5/30/2016.
 */
public class LogModel {

    private LogPresenter mLogPresenter;
    private ArrayList<LinearLayout> layouts;

    public LogModel(LogPresenter logPresenter) {
        mLogPresenter = logPresenter;
    }

    public void addToLayoutList(LinearLayout action){
        layouts.add(action);
    }

    public void recieveAction(Calculation action) {
    }

    private void createLayoutFromAction(Calculation action){
    }
}
