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

    public void relayClear() {
        mCalculatorModel.doClear();
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

    public void onLpUpdated(Integer playerLpPrevious, Integer playerLp, int player, boolean isReset){
        if(mCalculatorFragment != null){
            String toast = createLpToastText(playerLpPrevious, playerLp, player);
            mCalculatorFragment.onLpUpdated(playerLpPrevious, playerLp, player, toast, isReset);
        }
    }

    private String createLpToastText(int playerLpPrevious, int playerLp, int player){
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

    public String getPlayer1Name(){
        return mCalculatorModel.getPlayer1Name();
    }

    public String getPlayer2Name(){
        return mCalculatorModel.getPlayer2Name();
    }

    public void onPlayerNameChanged(String name, int i) {
        mCalculatorModel.doPlayerNameChanged(name, i);
    }

    public void onResetClicked(){
        mCalculatorModel.doReset();
    }

    public Integer getEnteredValue(){
        return mCalculatorModel.getEnteredValue();
    }
}
