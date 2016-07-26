package com.outplaysoftworks.sidedeckv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LogFragment extends Fragment {

    public LogPresenter mLogPresenter;
    private static View view;
    private static LayoutInflater mInflater;
    private static ArrayList<LinearLayout> layouts = new ArrayList<>();
    private static Context context;
    @BindView(R.id.viewHolder)
    private static LinearLayout viewHolder;

    public LogFragment(){
        makePresenter();
    }

    private void makePresenter() {
        if(mLogPresenter == null){
            mLogPresenter = new LogPresenter(this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_log, container, false);
        ButterKnife.bind(this, view);
        assignViewByIds();
        context = view.getContext();
        return view;
    }

    private void assignViewByIds() {
        viewHolder = (LinearLayout)view.findViewById(R.id.viewHolder);
    }

    public void onCalculation(Calculation calculation){
        LinearLayout layout = makeLayoutForCalculation(calculation);
        addLayoutToList(layout);
    }

    private LinearLayout makeLayoutForCalculation(Calculation calculation){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        LinearLayout calculationLayout = (LinearLayout)layoutInflater.inflate(R.layout.calculation, viewHolder, false);
        TextView playerName = (TextView)calculationLayout.findViewById(R.id.playerName);
        TextView lpDifference = (TextView)calculationLayout.findViewById(R.id.lpDifference);
        TextView lpAfter = (TextView)calculationLayout.findViewById(R.id.lpAfter);
        playerName.setText(getPlayerName(calculation.getPlayer()));
        String lpDiff = calculation.getLpDifference().toString();
        if(lpDiff.contains("-")){
            lpDiff = lpDiff.replace("-", "");
        }
        lpDifference.setText(lpDiff);
        String lpAft = calculation.getLpAfter().toString();
        if(lpAft.contains("-")){
            lpAft = lpAft.replace("-", "");
        }
        lpAfter.setText(lpAft);
        if(calculation.isLpLoss()){
            lpDifference.setTextColor(Color.RED);
        }else if(!calculation.isLpLoss()){
            lpDifference.setTextColor(Color.GREEN);
        }
        return calculationLayout;
    }
    private String getPlayerName(Integer playerNumber){
        SharedPreferences sharedPreferences = MainActivity.sharedPreferences;
        String name;
        if(playerNumber == 1){
            name = sharedPreferences.getString(AppConstants.KEY_PLAYER_ONE_DEF_NAME, "Player 1");
        } else if(playerNumber == 2){
            name = sharedPreferences.getString(AppConstants.KEY_PLAYER_TWO_DEF_NAME, "Player 2");
        } else{
            return "Something went wrong";
        }
        return name;
    }

    private static void addLayoutToList(LinearLayout layout){
        layouts.add(layout);
        addLayoutToHolder(layout);
    }

    private static void addLayoutToHolder(LinearLayout layout){
        viewHolder.addView(layout, 0);
    }

    private void removeLayoutFromHolder(int index){
        viewHolder.removeViewAt(index);
    }
}
