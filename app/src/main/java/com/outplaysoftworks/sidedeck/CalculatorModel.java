package com.outplaysoftworks.sidedeck;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Random;

/** Model for the calculator, hooks up to the calculator presenter and the log presenter
 * Created by Billy on 5/13/2016.
 */
class CalculatorModel {

    private Integer enteredValue;
    private String player1Name;
    private final Random random = new Random(System.nanoTime());
    private String player2Name;
    private final ArrayList<Calculation> calculations;
    private String enteredValueString;
    private Integer player1Lp;
    private Integer player1LpPrevious;
    private Integer player2Lp;
    private Integer player2LpPrevious;
    private Integer defaultLp;
    private Integer currentTurn;

    public Integer getEnteredValue(){
        return this.enteredValue;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer1Lp(int lp){
        this.player1Lp = lp;
        this.player1LpPrevious = lp;
        mCalculatorPresenter.onP1LpSet(lp);
    }

    public void setPlayer2Lp(int lp){
        this.player2Lp = lp;
        this.player2LpPrevious = lp;
        mCalculatorPresenter.onP2LpSet(lp);
    }

    private final CalculatorPresenter mCalculatorPresenter;

    public LogPresenter getmLogPresenter(){
        if(this.mLogPresenter == null){
            makeLogPresenter();
        }
        return this.mLogPresenter;
    }

    private LogPresenter mLogPresenter;
    public CalculatorModel(CalculatorPresenter calculatorPresenter){
        mCalculatorPresenter = calculatorPresenter;
        enteredValue = 0;
        currentTurn = 1;
        initializeLp();
        calculations = new ArrayList<>();
        /*player1Name = getPlayerOneNameFromPreferences();
        player2Name = getPlayerTwoNameFromPreferences();*/
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
        String defaultLpString = "";
        if(mCalculatorPresenter.mCalculatorFragment != null) {
            try {
                SharedPreferences sharedPreferences = MainActivity.sharedPreferences;
                defaultLpString = sharedPreferences.getString(AppConstants.KEY_DEFAULT_LP, "8000");
            } catch(NullPointerException e){
                e.printStackTrace();
            }
        } else {
            defaultLpString = "8000";
        }
        return Integer.parseInt(defaultLpString);
    }

    public void doNumbers(String tag) {
        enteredValueString = enteredValue.toString();
        String appendedString;
        if(tag.length() + enteredValueString.length() < 7) {
            appendedString = enteredValueString + tag;
        } else{
            appendedString = "999999";  //NON-NLS
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
            if(!getAllowNegativeLp() && tPlayer1Lp < 0){
                tPlayer1Lp = 0;
            }
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
            if(!getAllowNegativeLp() && tPlayer2Lp < 0){
                tPlayer2Lp = 0;
            }
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
        Calculation Calculation = new Calculation(previousLp, newLp, player, currentTurn, model);
        addCalculation(Calculation);
    }

    private void addCalculation(Calculation calculation){
        calculations.add(calculation);
        makeLogPresenter();
        mLogPresenter.sendCalculationToLogModel(calculation, currentTurn);
    }

    public void doUndo(){
        if(calculations.size() > 0) {
            undoLastCalculation();
        }
    }

    private void undoLastCalculation(){
        makeLogPresenter();
        Calculation lastCalculation = calculations.get(calculations.size()-1);
        lastCalculation.undoCalculation();
        calculations.remove(calculations.size()-1);
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
        }

    }

    public void doDiceRoll() {
        int lastDiceRoll = random.nextInt(6);
        mCalculatorPresenter.onDiceRollComplete(lastDiceRoll);
    }

    public void doReset(){
        makeLogPresenter();
        defaultLp = getDefaultLpFromPreferences();
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean getAllowNegativeLp(){
        return mCalculatorPresenter.getAllowedNegativeLp();
    }
}
