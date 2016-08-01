package com.outplaysoftworks.sidedeck;

import android.view.View;

/**
 * Created by Billy on 5/14/2016.
 */
public class Calculation {

    private Integer lpAfter;
    private Integer player;
    private Integer lpPrevious;
    private View view;
    private View viewForContext;
    private CalculatorModel mCalculatorModel;
    private Integer lpDifference;
    private boolean isLpLoss;
    private int turn;
    public Calculation(int previousLp, int lpAfter, int player, int turn, CalculatorModel model) {
        this.lpPrevious = previousLp;
        this.lpAfter = lpAfter;
        this.player = player;
        this.turn = turn;
        if (lpAfter < previousLp) {
            isLpLoss = true;
        } else {
            isLpLoss = false;
        }
        this.lpDifference = lpAfter - previousLp;
        this.mCalculatorModel = model;
    }

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

    public void undoCalculation() {
        switch (player) {
            case 1:
                mCalculatorModel.setPlayer1Lp(lpPrevious);
                break;
            case 2:
                mCalculatorModel.setPlayer2Lp(lpPrevious);
                break;
        }
        mCalculatorModel.getmLogPresenter().onUndo(this);
    }

    public int getTurn() {
        return this.turn;
    }
}
