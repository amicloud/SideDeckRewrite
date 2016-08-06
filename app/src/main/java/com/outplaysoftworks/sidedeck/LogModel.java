package com.outplaysoftworks.sidedeck;

import java.util.ArrayList;

/** A model for all log related information.  Might be useless
 * Created by Billy on 5/30/2016.
 */
public class LogModel {

    private LogPresenter mLogPresenter;
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

    public void removeLastCalculation(){
        calculationList.remove(calculationList.size()-1);
    }
}
