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
        mCalculatorModel.doDiceRoll();
    }

    public void relayCoinFlip() {
    }

    public void onP1LpUpdated(Integer player1LpPrevious, Integer player1Lp){
        if(mCalculatorFragment != null) {
            mCalculatorFragment.onP1LpUpdated(player1LpPrevious, player1Lp);
            mCalculatorFragment.playLpSound();
            mCalculatorFragment.makeToast(createLpToastText(1, player1LpPrevious, player1Lp));
        }

    }
    public void onP2LpUpdated(Integer player2LpPrevious, Integer player2Lp){
        if(mCalculatorFragment != null) {
            mCalculatorFragment.onP2LpUpdated(player2LpPrevious, player2Lp);
            mCalculatorFragment.playLpSound();
            mCalculatorFragment.makeToast(createLpToastText(2,player2LpPrevious, player2Lp));
        }
    }

    private String createLpToastText(int player, int playerLpPrevious, int playerLp){
        String text, subOrAdd;
        String playerName = "";
        Integer change = playerLpPrevious - playerLp;
        if(change < 0){
            subOrAdd = mCalculatorFragment.getString(R.string.lpAddedTo);
        }else{
            subOrAdd = mCalculatorFragment.getString(R.string.lpSubtracedFrom);
        }
        if(player == 1){
            playerName = mCalculatorModel.getPlayer1Name();
        }else if(player == 2){
            playerName = mCalculatorModel.getPlayer2Name();
        }
        Integer changeAbs = Math.abs(change);
        text = changeAbs.toString() + subOrAdd + " " + playerName;
        return text;
    }
    public String getDefaultLp() {
        return mCalculatorModel.getDefaultLp().toString();
    }

    public void relayTurnClick(){
        mCalculatorModel.doTurnClick();
    }

    public void relayTurnLongClick(){
        mCalculatorModel.doTurnLongClick();
    }

    public void onTurnUpdated(Integer turn){
        mCalculatorFragment.onTurnUpdated(turn);
    }

    public void onDiceRollComplete(int lastDiceRoll) {
        mCalculatorFragment.onDiceRollComplete(lastDiceRoll);
    }
}
