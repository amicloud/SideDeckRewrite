package com.outplaysoftworks.sidedeckv2;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class LogFragment extends Fragment {

    public LogPresenter mLogPresenter;
    private static View view;
    private static LayoutInflater mInflater;
    private static ArrayList<LinearLayout> layouts = new ArrayList<>();
    private static LinearLayout viewHolder;
    private static Context context;

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
        assignViewByIds();
        context = view.getContext();
        return view;
    }

    private void assignViewByIds() {
        viewHolder = (LinearLayout)view.findViewById(R.id.viewHolder);
    }

    public static void onAction(Calculation action){
        LinearLayout layout = makeLayoutForAction(action);
        addLayoutToList(layout);
    }

    private static LinearLayout makeLayoutForAction(Calculation action){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        LinearLayout actionLayout = (LinearLayout)layoutInflater.inflate(R.layout.calculation_action, viewHolder, false);
        TextView playerName = (TextView)actionLayout.findViewById(R.id.playerName);
        TextView lpDifference = (TextView)actionLayout.findViewById(R.id.lpDifference);
        TextView lpAfter = (TextView)actionLayout.findViewById(R.id.lpAfter);
        playerName.setText(getPlayerName(action.getPlayer()));
        String lpDiff = action.getLpDifference().toString();
        if(lpDiff.contains("-")){
            lpDiff = lpDiff.replace("-", "");
        }
        lpDifference.setText(lpDiff);
        String lpAft = action.getLpAfter().toString();
        if(lpAft.contains("-")){
            lpAft = lpAft.replace("-", "");
        }
        lpAfter.setText(lpAft);
        if(action.isLpLoss()){
            lpDifference.setTextColor(Color.RED);
        }else if(!action.isLpLoss()){
            lpDifference.setTextColor(Color.GREEN);
        }
        return actionLayout;
    }
    private static String getPlayerName(Integer playerNumber){
        return "Player"; //TODO: IMPLEMENT THE THING
    }
    private static void addLayoutToList(LinearLayout layout){
        layouts.add(layout);
        addLayoutToHolder(layout);
    }

    private static void addLayoutToHolder(LinearLayout layout){
        viewHolder.addView(layout, 0);
    }

    private void removeLayoutFromHolder(){

    }
}
