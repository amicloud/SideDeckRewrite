package com.outplaysoftworks.sidedeckv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LogFragment extends Fragment {

    public static View view;
    private static ArrayList<LinearLayout> layouts = new ArrayList<>();
    private static ArrayList<LinearLayout> sections = new ArrayList<>();
    @BindView(R.id.layoutHolder)
    LinearLayout layoutHolder;

    public LogFragment() {
        makePresenter();
    }

    public LogPresenter mLogPresenter;
    private void makePresenter() {
        if (mLogPresenter == null) {
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
        ButterKnife.setDebug(true);
        ButterKnife.bind(this, view);
        setupLog();
        return view;
    }

    private void setupLog() {
        addSectionToLayout(createSection(1));
    }

    public void onCalculation(Calculation calculation, Integer turn) {
        LinearLayout layout = makeLayoutForCalculation(calculation);
        addCalculationToSection(turn - 1, layout);
    }

    private LinearLayout makeLayoutForCalculation(Calculation calculation) {
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        LinearLayout calculationLayout = (LinearLayout) layoutInflater.inflate(R.layout.calculation, layoutHolder, false);
        TextView playerName = ButterKnife.findById(calculationLayout, R.id.playerName);
        TextView lpDifference = ButterKnife.findById(calculationLayout, R.id.lpDifference);
        TextView lpAfter = ButterKnife.findById(calculationLayout, R.id.lpAfter);
        playerName.setText(getPlayerName(calculation.getPlayer()));
        String lpDiff = calculation.getLpDifference().toString();
        if (lpDiff.contains("-")) {
            lpDiff = lpDiff.replace("-", "");
        }
        lpDifference.setText(lpDiff);
        String lpAft = calculation.getLpAfter().toString();
        if (lpAft.contains("-")) {
            lpAft = lpAft.replace("-", "");
        }
        lpAfter.setText(lpAft);
        if (calculation.isLpLoss()) {
            lpDifference.setTextColor(view.getResources().getColor(R.color.material_red));
        } else if (!calculation.isLpLoss()) {
            lpDifference.setTextColor(view.getResources().getColor(R.color.material_green));
        }
        return calculationLayout;
    }

    private String getPlayerName(Integer playerNumber) {
        SharedPreferences sharedPreferences = MainActivity.sharedPreferences;
        String name;
        if (playerNumber == 1) {
            name = sharedPreferences.getString(AppConstants.KEY_PLAYER_ONE_DEF_NAME, "Player 1");
        } else if (playerNumber == 2) {
            name = sharedPreferences.getString(AppConstants.KEY_PLAYER_TWO_DEF_NAME, "Player 2");
        } else {
            return "Something went wrong";
        }
        return name;
    }

    private void addLayoutToList(LinearLayout layout) {
        layouts.add(layout);
        addLayoutToHolder(layout);
    }

    private void addLayoutToHolder(LinearLayout layout) {
        layoutHolder = ButterKnife.findById(view, R.id.layoutHolder);
        layoutHolder.addView(layout, 0);
    }

    private LinearLayout createSection(Integer turnNumber){
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        LinearLayout section = (LinearLayout) layoutInflater.inflate(R.layout.section, layoutHolder, false);
        TextView sectionText = ButterKnife.findById(section, R.id.text);
        String text = view.getResources().getString(R.string.logTurn) + " " + turnNumber.toString();
        sectionText.setText(text);
        return section;
    }

    private void addSectionToLayout(LinearLayout section){
        layoutHolder = ButterKnife.findById(view, R.id.layoutHolder);
        layoutHolder.addView(section, 0);
        sections.add(section);
    }

    private void addCalculationToSection(int sectionNumber, LinearLayout calculation){
        sections.get(sectionNumber).addView(calculation, 1);
        if(sections.get(sectionNumber).getChildCount() > 2){
            sections.get(sectionNumber).addView(createHorizontalRule(), 2);
        }
    }

    private RelativeLayout createHorizontalRule(){
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        RelativeLayout rule = (RelativeLayout) layoutInflater.inflate(R.layout.log_horizontal_rule,
                layoutHolder, false);
        return rule;
    }

    private void removeSection(){
        layoutHolder = ButterKnife.findById(view, R.id.layoutHolder);
        sections.remove(sections.size()-1);
        layoutHolder.removeViewAt(0);
    }

    private void removeCalculationFromSection(Integer section){
        sections.get(section).removeViewAt(0);
    }

    public void onTurnIncremented(Integer turn){
        if(sections.size() < turn){
            addSectionToLayout(createSection(turn));
        }
        /*if(sections.get(turn - 2) != null){
            addSectionToLayout(createSection(turn));
        }*/
    }

    public void onTurnDecremented(Integer turn){
        if(sections.get(turn - 1) != null){
            //sections.remove(turn - 1);
        }
    }

    public void reset() {
        layoutHolder = ButterKnife.findById(view, R.id.layoutHolder);
        if(layoutHolder.getChildCount() > 0) {
            layoutHolder.removeAllViews();
            layouts.clear();
            sections.clear();
            addSectionToLayout(createSection(1));
        }
    }
 /*   private void removeLayoutFromHolder(int index){
        layoutHolder.removeViewAt(index);
    }*/
}
