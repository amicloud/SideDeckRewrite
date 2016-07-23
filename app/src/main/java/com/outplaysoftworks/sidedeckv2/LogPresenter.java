package com.outplaysoftworks.sidedeckv2;

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

    public void relayAddToActions(CalculationAction action){
        mLogModel.recieveAction(action);
    }

    public void relayActionToLogFragment(CalculationAction action){
        LogFragment.onAction(action);
    }
}
