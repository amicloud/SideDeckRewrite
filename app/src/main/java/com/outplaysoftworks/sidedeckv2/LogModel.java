package com.outplaysoftworks.sidedeckv2;

import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Billy on 5/30/2016.
 */
public class LogModel {

    private LogPresenter mLogPresenter;
    private ArrayList<LinearLayout> layouts;
    private ArrayList<Calculation> calculationList = new ArrayList<>();
    public LogModel(LogPresenter logPresenter) {
        mLogPresenter = logPresenter;
    }

    public void addCalculationToList(Calculation calculation, Integer turn){
        calculationList.add(calculation);
        onCalculationAddedToList(calculation, turn);
    }

    private void onCalculationAddedToList(Calculation calculation, Integer turn){
        mLogPresenter.createCalculation(calculation, turn);
    }
}
