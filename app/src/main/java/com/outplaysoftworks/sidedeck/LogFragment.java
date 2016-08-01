package com.outplaysoftworks.sidedeck;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import me.grantland.widget.AutofitHelper;

public class LogFragment extends Fragment {

    private static final String TAG = "Log Fragment";
    public static View view;
    private static ArrayList<LinearLayout> layouts = new ArrayList<>();
    private static ArrayList<LinearLayout> sections = new ArrayList<>();
    public LogPresenter mLogPresenter;
    /*@BindView(R.id.layoutHolder)*/
    static LinearLayout layoutHolder;

    public LogFragment() {
        makePresenter();
    }

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
        playerName.setMaxLines(1);
        AutofitHelper.create(playerName);
        String lpDiff = calculation.getLpDifference().toString();
        if (lpDiff.contains("-")) {
            lpDiff = lpDiff.replace("-", "");
        }
        lpDifference.setText(lpDiff);
        AutofitHelper.create(lpDifference);
        String lpAft = calculation.getLpAfter().toString();
        if (lpAft.contains("-")) {
            lpAft = lpAft.replace("-", "");
        }
        lpAfter.setText(lpAft);
        AutofitHelper.create(lpAfter);
        if (calculation.isLpLoss()) {
            lpDifference.setTextColor(view.getResources().getColor(R.color.material_red));
        } else if (!calculation.isLpLoss()) {
            lpDifference.setTextColor(view.getResources().getColor(R.color.material_green));
        }
        if (sections.get(calculation.getTurn() - 1).getChildCount() > 1) {
            calculationLayout.addView(createHorizontalRule());
        }
        return calculationLayout;
    }

    private String getPlayerName(Integer playerNumber) {
        String name = "";
        if (playerNumber == 1) {
            name = MainActivity.mCalculatorFragment.getPlayer1Name();
        } else if (playerNumber == 2) {
            name = MainActivity.mCalculatorFragment.getPlayer2Name();
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

    private LinearLayout createSection(Integer turnNumber) {
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        LinearLayout section = (LinearLayout) layoutInflater.inflate(R.layout.section, layoutHolder, false);
        TextView sectionText = ButterKnife.findById(section, R.id.text);
        String text = view.getResources().getString(R.string.logTurn) + " " + turnNumber.toString();
        sectionText.setText(text);
        return section;
    }

    private void addSectionToLayout(LinearLayout section) {
        layoutHolder = ButterKnife.findById(view, R.id.layoutHolder);
        layoutHolder.addView(section, 0);
        sections.add(section);
    }

    private void addCalculationToSection(int sectionNumber, LinearLayout calculation) {
        sections.get(sectionNumber).addView(calculation, 1);
    }

    private RelativeLayout createHorizontalRule() {
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        RelativeLayout rule = (RelativeLayout) layoutInflater.inflate(R.layout.log_horizontal_rule,
                layoutHolder, false);
        return rule;
    }

    public void onTurnIncremented(Integer turn) {
        if (sections.size() < turn) {
            addSectionToLayout(createSection(turn));
        }
        /*if(sections.get(turn - 2) != null){
            addSectionToLayout(createSection(turn));
        }*/
    }

    public void onTurnDecremented(Integer turn) {
        if (sections.get(turn - 1) != null) {
            //sections.remove(turn - 1);
        }
    }

    public void reset() {
        //layoutHolder = ButterKnife.findById(view, R.id.layoutHolder);
        if(view != null) {
            layoutHolder = (LinearLayout) view.findViewById(R.id.layoutHolder);
            if (layoutHolder.getChildCount() > 0) {
                layoutHolder.removeAllViews();
                layouts.clear();
                sections.clear();
                addSectionToLayout(createSection(1));
            }
        }
    }

    public void onUndo(Calculation calculation) {
        sections.get(calculation.getTurn() - 1).removeViewAt(1);
    }
}
