package com.outplaysoftworks.sidedeckv2;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Billy on 5/13/2016.
 */
public class CalculatorModel {

    private Integer enteredValue;
    private String player1Name;
    private Random random = new Random(System.nanoTime());
    private int lastDiceRoll;
    private String player2Name;
    private ArrayList<Calculation> actions;
    private String enteredValueString;
    private String appendedString;
    private Integer player1Lp;
    private Integer player1LpPrevious;
    private Integer player2Lp;
    private Integer player2LpPrevious;
    public Integer defaultLp;
    private Integer currentTurn;

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    private CalculatorPresenter mCalculatorPresenter;
    private LogPresenter mLogPresenter;
    public CalculatorModel(CalculatorPresenter calculatorPresenter){
        mCalculatorPresenter = calculatorPresenter;
        enteredValue = 0;
        currentTurn = 1;
        initializeLp();
        actions = new ArrayList<Calculation>();
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
        if(mCalculatorPresenter.mCalculatorFragment != null){
            SharedPreferences sharedPreferences = MainActivity.sharedPreferences;
            return sharedPreferences.getString(AppConstants.KEY_PLAYER_ONE_DEF_NAME, "Player 1");
        }else {
            return "Player 1";
        }
    }
    private String getPlayerTwoNameFromPreferences(){
        if(mCalculatorPresenter.mCalculatorFragment != null){
            SharedPreferences sharedPreferences = MainActivity.sharedPreferences;
            return sharedPreferences.getString(AppConstants.KEY_PLAYER_TWO_DEF_NAME, "Player 2");
        }else {
            return "Player 2";
        }
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
            createCalculationAction(player1LpPrevious, player1Lp, 1, this);
            mCalculatorPresenter.onP1LpUpdated(player1LpPrevious, player1Lp);
            doEnteredValue();
        }
    }

    public void doP1Sub() {
        if(enteredValue != 0) {
            player1LpPrevious = player1Lp;
            player1Lp -= enteredValue;
            createCalculationAction(player1LpPrevious, player1Lp, 1, this);
            mCalculatorPresenter.onP1LpUpdated(player1LpPrevious, player1Lp);
            doEnteredValue();
        }
    }

    public void doP2Add() {
        if(enteredValue != 0) {
            player2LpPrevious = player2Lp;
            player2Lp += enteredValue;
            createCalculationAction(player2LpPrevious, player2Lp, 2, this);
            mCalculatorPresenter.onP2LpUpdated(player2LpPrevious, player2Lp);
            doEnteredValue();
        }
    }

    public void doP2Sub() {
        if(enteredValue != 0) {
            player2LpPrevious = player2Lp;
            player2Lp -= enteredValue;
            createCalculationAction(player2LpPrevious, player2Lp, 2, this);
            mCalculatorPresenter.onP2LpUpdated(player2LpPrevious, player2Lp);
            doEnteredValue();
        }
    }

    private void createCalculationAction(int previousLp, int newLp, int player, CalculatorModel model){
        Calculation calculationAction = new Calculation(previousLp, newLp, player, model);
        addCalculationActionToList(calculationAction);
    }

    private void addCalculationActionToList(Calculation action){
        actions.add(action);
        if(mLogPresenter ==  null){
            mLogPresenter = MainActivity.mLogFragment.mLogPresenter;
        }
        mLogPresenter.relayActionToLogFragment(action);
    }

    private void undoLastCalculation(){
        Calculation lastAction = actions.get(actions.size()-1);
        actions.remove(actions.size()-1);
    }

    public Integer getDefaultLp() {
        return defaultLp;
    }

    public void doTurnClick() {
        currentTurn++;
        mCalculatorPresenter.onTurnUpdated(currentTurn);
    }

    public void doTurnLongClick() {
        currentTurn--;
        mCalculatorPresenter.onTurnUpdated(currentTurn);
    }


    public void doDiceRoll() {
        lastDiceRoll = random.nextInt(6);
        mCalculatorPresenter.onDiceRollComplete(lastDiceRoll);
    }
}
