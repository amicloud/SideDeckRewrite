package com.outplaysoftworks.sidedeck;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import me.grantland.widget.AutofitHelper;

public class LogFragment extends Fragment {

    private View view;
    private static final ArrayList<LinearLayout> sections = new ArrayList<>();
    public LogPresenter mLogPresenter;
    private Drawable arrowUp;
    private Drawable arrowDown;
    private LinearLayout layoutHolder;

    public LogFragment() {
        makePresenter();
    }

    private void makePresenter() {
        if (mLogPresenter == null) {
            mLogPresenter = new LogPresenter(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_log, container, false);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this, view);
        loadDrawables();
        setupLog();
        return view;
    }

    private void loadDrawables() {
        arrowUp = getActivity().getApplication().getApplicationContext().getResources().getDrawable(R.drawable.arrow_up);
        arrowDown = getActivity().getApplication().getApplicationContext().getResources().getDrawable(R.drawable.arrow_down);
    }

    private void setupLog() {
        addSectionToLayout(createSection(1));
    }

    public void onCalculation(Calculation calculation, Integer turn) {
        LinearLayout layout = makeLayoutForCalculation(calculation);
        addCalculationToSection(turn - 1, layout);
    }

    @SuppressWarnings("deprecation")
    private LinearLayout makeLayoutForCalculation(Calculation calculation) {
        loadDrawables();
        LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
        LinearLayout calculationLayout = (LinearLayout) layoutInflater.inflate(R.layout.calculation, layoutHolder, false);
        TextView playerName = ButterKnife.findById(calculationLayout, R.id.playerName);
        TextView lpDifference = ButterKnife.findById(calculationLayout, R.id.lpDifference);
        TextView lpAfter = ButterKnife.findById(calculationLayout, R.id.lpAfter);
        //ImageView arrowHolder = ButterKnife.findById(calculationLayout, R.id.holderArrow);
        ImageView arrowHolder = (ImageView)calculationLayout.findViewById(R.id.holderArrow);
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
            arrowHolder.setImageDrawable(arrowDown);
        } else if (!calculation.isLpLoss()) {
            lpDifference.setTextColor(view.getResources().getColor(R.color.material_green));
            arrowHolder.setImageDrawable(arrowUp);
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
        return (RelativeLayout) layoutInflater.inflate(R.layout.log_horizontal_rule,
                layoutHolder, false);
    }

    public void onTurnIncremented(Integer turn) {
        if (sections.size() < turn) {
            addSectionToLayout(createSection(turn));
        }
    }

    public void reset() {
        if(view != null) {
            layoutHolder = (LinearLayout) view.findViewById(R.id.layoutHolder);
            if (layoutHolder.getChildCount() > 0) {
                layoutHolder.removeAllViews();
                sections.clear();
                addSectionToLayout(createSection(1));
            }
        }
    }

    public void onUndo(Calculation calculation) {
        sections.get(calculation.getTurn() - 1).removeViewAt(1);
    }
}
