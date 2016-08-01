package com.outplaysoftworks.sidedeck;

/**
 * Created by Billy on 6/13/2016.
 */
public class LogPresenter {

    private LogFragment mLogFragment;
    private LogModel mLogModel;

    public LogPresenter(LogFragment logFragment) {
        mLogFragment = logFragment;
        if(mLogModel == null){
            mLogModel = new LogModel(this);
        }
    }

    public void sendCalculationToLogModel(Calculation calculation, Integer turn){
        mLogModel.addCalculationToList(calculation, turn);
    }

    public void createCalculation(Calculation calculation, Integer turn) {
        mLogFragment.onCalculation(calculation, turn);
    }

    public void onTurnIncremented(Integer currentTurn) {
        mLogFragment.onTurnIncremented(currentTurn);
    }

    public void onTurnDecremented(Integer currentTurn) {
        mLogFragment.onTurnDecremented(currentTurn);
    }

    public void reset() {
        mLogFragment.reset();
    }

    public void onUndo(Calculation calculation){
        mLogModel.removeLastCalculation();
        mLogFragment.onUndo(calculation);
    }
}