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
    private ArrayList<Calculation> calculations;
    private String enteredValueString;
    private String appendedString;
    private Integer player1Lp;
    private Integer player1LpPrevious;
    private Integer player2Lp;
    private Integer player2LpPrevious;
    public Integer defaultLp;
    private Integer currentTurn;

    public Integer getEnteredValue(){
        return this.enteredValue;
    }

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
        calculations = new ArrayList<Calculation>();
        player1Name = getPlayerOneNameFromPreferences();
        player2Name = getPlayerTwoNameFromPreferences();
    }

    private void makeLogPresenter(){
        mLogPresenter = MainActivity.mLogFragment.mLogPresenter;
    }

    private void initializeLp() {
        defaultLp = getDefaultLpFromPreferences();
        player1Lp = defaultLp;
        player2Lp = defaultLp;
        player1LpPrevious = player1Lp;
        player2LpPrevious = player2Lp;
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

    public void doClear() {
        enteredValue = 0;
        enteredValueString = "";
        mCalculatorPresenter.onEnteredValueUpdated(enteredValue.toString());
    }


    public void doP1Add() {
        if(enteredValue != 0) {
            int tPlayer1LpPrevious = player1Lp;
            int tPlayer1Lp = player1Lp + enteredValue;
            setPlayerLp(tPlayer1LpPrevious, tPlayer1Lp, 1, false);
            createCalculation(player1LpPrevious, player1Lp, 1, this);
            doClear();
        }
    }

    public void doP1Sub() {
        if(enteredValue != 0) {
            int tPlayer1LpPrevious = player1Lp;
            int tPlayer1Lp = player1Lp - enteredValue;
            setPlayerLp(tPlayer1LpPrevious, tPlayer1Lp, 1, false);
            createCalculation(player1LpPrevious, player1Lp, 1, this);
            doClear();
        }
    }

    public void doP2Add() {
        if(enteredValue != 0) {
            int tPlayer2LpPrevious = player2Lp;
            int tPlayer2Lp = player2Lp + enteredValue;
            setPlayerLp(tPlayer2LpPrevious, tPlayer2Lp, 2, false);
            createCalculation(player2LpPrevious, player2Lp, 2, this);
            doClear();
        }
    }

    public void doP2Sub() {
        if(enteredValue != 0) {
            int tPlayer2LpPrevious = player2Lp;
            int tPlayer2Lp = player2Lp - enteredValue;
            setPlayerLp(tPlayer2LpPrevious, tPlayer2Lp, 2, false);
            createCalculation(player2LpPrevious, player2Lp, 2, this);
            doClear();
        }
    }

    private void setPlayerLp(int playerLpPrevious, int playerLp, int player, boolean isReset){
        switch(player){
            case 1:
                player1LpPrevious = playerLpPrevious;
                player1Lp = playerLp;
                mCalculatorPresenter.onLpUpdated(playerLpPrevious, playerLp, player, isReset);
                break;
            case 2:
                player2LpPrevious = playerLpPrevious;
                player2Lp = playerLp;
                mCalculatorPresenter.onLpUpdated(playerLpPrevious, playerLp, player, isReset);
                break;
        }
    }

    private void createCalculation(int previousLp, int newLp, int player, CalculatorModel model){
        Calculation Calculation = new Calculation(previousLp, newLp, player, model);
        addCalculation(Calculation);
    }

    private void addCalculation(Calculation calculation){
        calculations.add(calculation);
        makeLogPresenter();
        mLogPresenter.sendCalculationToLogModel(calculation, currentTurn);
    }

    private void undoLastCalculation(){
    }

    public Integer getDefaultLp() {
        return defaultLp;
    }

    public void doTurnClick() {
        makeLogPresenter();
        currentTurn++;
        mCalculatorPresenter.onTurnUpdated(currentTurn);
        mLogPresenter.onTurnIncremented(currentTurn);

    }
    public void doTurnLongClick() {
        makeLogPresenter();
        if(currentTurn > 1) {
            currentTurn--;
            mCalculatorPresenter.onTurnUpdated(currentTurn);
            mLogPresenter.onTurnDecremented(currentTurn);
        }

    }

    public void doDiceRoll() {
        lastDiceRoll = random.nextInt(6);
        mCalculatorPresenter.onDiceRollComplete(lastDiceRoll);
    }

    public void doReset(){
        makeLogPresenter();
        setPlayerLp(0, defaultLp, 1, true);
        setPlayerLp(0, defaultLp, 2, true);
        player1LpPrevious = player1Lp;
        player2LpPrevious = player2Lp;
        calculations.clear();
        mLogPresenter.reset();
        enteredValue = 0;
        enteredValueString = "";
        currentTurn = 1;
        mCalculatorPresenter.onTurnUpdated(currentTurn);
    }

    public void doPlayerNameChanged(String name, int i) {
        switch(i){
            case 1:
                player1Name = name;
                break;
            case 2:
                player2Name = name;
                break;
        }
    }
}
