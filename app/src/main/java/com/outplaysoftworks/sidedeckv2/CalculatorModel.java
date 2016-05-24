package com.outplaysoftworks.sidedeckv2;

import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by Billy on 5/13/2016.
 */
public class CalculatorModel {

    private Integer enteredValue;
    private String player1Name;
    private String player2Name;
    private ArrayList<CalculationAction> actions;
    private CalculatorPresenter mCalculatorPresenter;
    private String enteredValueString;
    private String appendedString;
    private Integer player1Lp;
    private Integer player1LpPrevious;
    private Integer player2Lp;
    private Integer player2LpPrevious;
    public Integer defaultLp;

    public CalculatorModel(CalculatorPresenter calculatorPresenter){
        mCalculatorPresenter = calculatorPresenter;
        enteredValue = 0;
        initializeLp();
        actions = new ArrayList<CalculationAction>();
        player1Name = getPlayerOneNameFromPreferences();
        player2Name = getPlayerTwoNameFromPreferences();
    }

    private void initializeLp() {
        defaultLp = getDefaultLpFromPreferences();
        player1Lp = defaultLp;
        player2Lp = defaultLp;
        player1LpPrevious = player1Lp;
        player2LpPrevious = player2Lp;
        //mCalculatorPresenter.setLpToDefault(defaultLp);
        /*mCalculatorPresenter.onP1LpUpdated(player1LpPrevious, player1Lp);
        mCalculatorPresenter.onP2LpUpdated(player2LpPrevious, player2Lp);*/
    }

    private Integer getDefaultLpFromPreferences() {
        String defaultLpString;
        if(mCalculatorPresenter.mCalculatorFragment != null) {
            SharedPreferences sharedPreferences = MainActivity.sharedPreferences;
            defaultLpString = sharedPreferences.getString(AppConstants.KEY_DEFAULT_LP, "8000");
        } else {
            defaultLpString = "8000";
        }
        return Integer.parseInt(defaultLpString);
    }

    private String getPlayerOneNameFromPreferences(){
        return null;
    }
    private String getPlayerTwoNameFromPreferences(){
        return null;
    }

    public void doNumbers(String tag) {
        enteredValueString = enteredValue.toString();
        appendedString = "";
        if(tag.length() + enteredValueString.length() < 7) {
            appendedString = enteredValueString + tag;
        } else{
            appendedString = "999999";
        }
        enteredValue = Integer.parseInt(appendedString);
        mCalculatorPresenter.onEnteredValueUpdated(enteredValue.toString());
    }

    public void doEnteredValue() {
        enteredValue = 0;
        enteredValueString = "";
        mCalculatorPresenter.onEnteredValueUpdated(enteredValue.toString());
    }


    public void doP1Add() {
        if(enteredValue != 0) {
            player1LpPrevious = player1Lp;
            player1Lp += enteredValue;
            createCalculationAction(player1LpPrevious, player1Lp, 1);
            mCalculatorPresenter.onP1LpUpdated(player1LpPrevious, player1Lp);
            doEnteredValue();
        }
    }

    public void doP1Sub() {
        if(enteredValue != 0) {
            player1LpPrevious = player1Lp;
            player1Lp -= enteredValue;
            createCalculationAction(player1LpPrevious, player1Lp, 1);
            mCalculatorPresenter.onP1LpUpdated(player1LpPrevious, player1Lp);
            doEnteredValue();
        }
    }

    public void doP2Add() {
        if(enteredValue != 0) {
            player2LpPrevious = player2Lp;
            player2Lp += enteredValue;
            createCalculationAction(player2LpPrevious, player2Lp, 2);
            mCalculatorPresenter.onP2LpUpdated(player2LpPrevious, player2Lp);
            doEnteredValue();
        }
    }

    public void doP2Sub() {
        if(enteredValue != 0) {
            player2LpPrevious = player2Lp;
            player2Lp -= enteredValue;
            createCalculationAction(player2LpPrevious, player2Lp, 2);
            mCalculatorPresenter.onP2LpUpdated(player2LpPrevious, player2Lp);
            doEnteredValue();
        }
    }

    private void createCalculationAction(int previousLp, int newLp, int player){
        CalculationAction calculationAction = new CalculationAction(previousLp, newLp, player);
        actions.add(calculationAction);
    }

    private void undoLastCalculation(){
        CalculationAction lastAction = actions.get(actions.size()-1);

        actions.remove(actions.size()-1);
    }

    public Integer getDefaultLp() {
        return defaultLp;
    }
}
