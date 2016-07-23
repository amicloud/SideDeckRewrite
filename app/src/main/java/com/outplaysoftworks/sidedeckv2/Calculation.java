package com.outplaysoftworks.sidedeckv2;

import android.view.View;

/**
 * Created by Billy on 5/14/2016.
 */
public class Calculation {

    public Integer getLpPrevious() {
        return lpPrevious;
    }

    public Integer getLpAfter() {
        return lpAfter;
    }

    public Integer getLpDifference() {
        return lpDifference;
    }

    public boolean isLpLoss() {
        return isLpLoss;
    }

    public Integer getPlayer() {
        return player;
    }

    private Integer lpAfter;
    private Integer player;
    private Integer lpPrevious;
    private View view;
    private View viewForContext;
    private CalculatorModel mCalculatorModel;
    private Integer lpDifference;
    private boolean isLpLoss;
    public Calculation(int previousLp, int lpAfter, int player, CalculatorModel model) {
        this.lpPrevious = previousLp;
        this.lpAfter = lpAfter;
        this.player = player;
        if(lpAfter < previousLp){
            isLpLoss = true;
        }else{
            isLpLoss = false;
        }
        this.lpDifference = lpAfter - previousLp;
        this.mCalculatorModel = model;
    }
}
