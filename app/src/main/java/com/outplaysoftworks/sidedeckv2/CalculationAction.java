package com.outplaysoftworks.sidedeckv2;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Billy on 5/14/2016.
 */
public class CalculationAction {

    private Integer numberChange;
    private Integer player;
    private Integer previousNumber;
    private View view;
    private View viewForContext;
    private CalculatorModel mCalculatorModel;
    private boolean isLpLoss;
    public CalculationAction(int previousLp, int lPAfterChange, int player, CalculatorModel model) {
        this.previousNumber = previousLp;
        this.numberChange = lPAfterChange;
        this.player = player;

        this.mCalculatorModel = model;
    }
}
