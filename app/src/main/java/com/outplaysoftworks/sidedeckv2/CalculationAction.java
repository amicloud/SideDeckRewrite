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
        this.viewForContext = CalculatorFragment.view;
        this.view = createViewForAction(view);
        this.mCalculatorModel = model;
    }

    private View createViewForAction(View view){
        /*System.out.println("AHHHHHHHHH DEBUGS");
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout temp = new LinearLayout(CalculatorFragment.view.getContext());
        View mainView = inflater.inflate(R.layout.calculation_action, temp, false);
        TextView playerName = (TextView)mainView.findViewById(R.id.playerName);
        TextView lpDifference = (TextView)mainView.findViewById(R.id.lpDifference);
        TextView lpAfter = (TextView)mainView.findViewById(R.id.lpAfter);
        lpAfter.setText(numberChange.toString());
        Integer difference = numberChange - previousNumber;
        difference = Math.abs(difference);
        if(numberChange > previousNumber) {
            this.isLpLoss = false;
        }else{
            this.isLpLoss = true;
        }
        lpDifference.setText(difference);
        if(this.isLpLoss){
            lpDifference.setTextColor(Color.RED);
        } else{
            lpDifference.setTextColor(Color.GREEN);
        }
        if(this.player == 1){
            playerName.setText(mCalculatorModel.getPlayer1Name());
        }else if(this.player == 2){
            playerName.setText(mCalculatorModel.getPlayer2Name());
        }
        return mainView;*/
        return null;
    }
}
