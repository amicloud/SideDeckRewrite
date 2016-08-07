package com.outplaysoftworks.sidedeck;

/** This class is used to create an object to hold information about a calculation that the user
 * performed.  The object is capable of undoing itself, but only with a linear undo system
 * Created by Billy on 5/14/2016.
 */
class Calculation {
    //TODO: See if making these final actually broke anything
    private final Integer lpAfter;
    private final Integer player;
    private final Integer lpPrevious;
    private final CalculatorModel mCalculatorModel;
    private final Integer lpDifference;
    private final boolean isLpLoss;
    private final int turn;
    public Calculation(int previousLp, int lpAfter, int player, int turn, CalculatorModel model) {
        this.lpPrevious = previousLp;
        this.lpAfter = lpAfter;
        this.player = player;
        this.turn = turn;
        isLpLoss = lpAfter < previousLp;
        this.lpDifference = lpAfter - previousLp;
        this.mCalculatorModel = model;
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
