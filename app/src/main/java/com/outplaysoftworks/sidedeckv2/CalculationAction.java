package com.outplaysoftworks.sidedeckv2;

import android.view.View;

/**
 * Created by Billy on 5/14/2016.
 */
public class CalculationAction {

    private int numberChange;
    private int player;
    private int previousNumber;
    private View view;
    public CalculationAction(int previousLp, int lPAfterChange, int player) {
        this.previousNumber = previousLp;
        this.numberChange = lPAfterChange;
        this.player = player;
        this.view = createViewForAction();
    }

    private View createViewForAction(){
        View completedView = null;
        return completedView;
    }
}
