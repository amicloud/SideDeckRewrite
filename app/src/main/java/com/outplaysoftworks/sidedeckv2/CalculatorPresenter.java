package com.outplaysoftworks.sidedeckv2;

/**
 * Created by Billy on 5/13/2016.
 */
public class CalculatorPresenter {

    CalculatorModel mCalculatorModel;
    CalculatorFragment mCalculatorFragment;
    public CalculatorPresenter(CalculatorFragment calculatorFragment){
        mCalculatorFragment = calculatorFragment;
        if(mCalculatorModel == null){
            mCalculatorModel = new CalculatorModel(this);
        }
    }

    public void relayNumbers(String tag) {
        mCalculatorModel.doNumbers(tag);
    }

    public void onEnteredValueUpdated(String enteredValue){
        mCalculatorFragment.onEnteredValueUpdated(enteredValue);
    }

    public void relayEnteredValue() {
        mCalculatorModel.doEnteredValue();
    }

    public void relayP1Add() {
        mCalculatorModel.doP1Add();
    }
    public void relayP1Sub() {
        mCalculatorModel.doP1Sub();
    }
    public void relayP2Add() {
        mCalculatorModel.doP2Add();
    }
    public void relayP2Sub() {
        mCalculatorModel.doP2Sub();
    }

    public void relayDiceRoll() {
    }

    public void relayCoinFlip() {
    }

    public void onP1LpUpdated(Integer player1LpPrevious, Integer player1Lp){
        if(mCalculatorFragment != null) {
            mCalculatorFragment.onP1LpUpdated(player1LpPrevious, player1Lp);
            mCalculatorFragment.playLpSound();
        }

    }
    public void onP2LpUpdated(Integer player2LpPrevious, Integer player2Lp){
        if(mCalculatorFragment != null) {
            mCalculatorFragment.onP2LpUpdated(player2LpPrevious, player2Lp);
            mCalculatorFragment.playLpSound();
        }
    }

    public String getDefaultLp() {
        return mCalculatorModel.getDefaultLp().toString();
    }
}
