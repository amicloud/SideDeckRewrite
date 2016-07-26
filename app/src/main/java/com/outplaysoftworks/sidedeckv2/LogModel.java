package com.outplaysoftworks.sidedeckv2;

import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Billy on 5/30/2016.
 */
public class LogModel {

    private LogPresenter mLogPresenter;
    private ArrayList<LinearLayout> layouts;
    private List<Calculation> calculationList;
    public LogModel(LogPresenter logPresenter) {
        mLogPresenter = logPresenter;
    }

    public void addCalculationToList(Calculation calculation){
        calculationList.add(calculation);
    }

    private void onCalculationAddedToList(){
        mLogPresenter.createCalculation();
    }
}
