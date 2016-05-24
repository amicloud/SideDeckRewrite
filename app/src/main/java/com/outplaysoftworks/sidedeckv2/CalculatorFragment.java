package com.outplaysoftworks.sidedeckv2;

import android.animation.ValueAnimator;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;


public class CalculatorFragment extends Fragment {
    private CalculatorPresenter mCalculatorPresenter;

    //Sound stuff
    private static SoundPool soundPool;
    private static Integer lpCounterSoundId;
    private static Integer diceRollSoundId;
    private static Integer coinFlipSoundId;
    //TODO: Create view objects and inject them please
    private static TextView enteredValueView;
    private static TextView player1Lp;
    private static TextView player2Lp;
    private static EditText player1Name;
    private static EditText player2Name;
    private static Button diceRollButton;
    private static Button coinFlipButton;

    public CalculatorFragment() {
        makePresenter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);
        /*mCalculatorPresenter = new CalculatorPresenter(this);*/
        assignViewsByIds(view);
        setUpSounds();
        setLpToDefault();
        // Inflate the layout for this fragment
        return view;
    }

    private void setUpSounds() {
        soundPool = new SoundPool(32, AudioManager.STREAM_MUSIC, 0);
        lpCounterSoundId = soundPool.load(getContext(), R.raw.lpcountersound, 1);
        coinFlipSoundId = soundPool.load(getContext(), R.raw.coinflipsound, 1);
        diceRollSoundId = soundPool.load(getContext(), R.raw.dicerollsound, 1);
    }

    private void assignViewsByIds(View view) {
        enteredValueView = (TextView)view.findViewById(R.id.enteredValue);
        player1Lp = (TextView)view.findViewById(R.id.player1Lp);
        player2Lp = (TextView)view.findViewById(R.id.player2Lp);
        player1Name = (EditText)view.findViewById(R.id.player1Name);
        player2Name= (EditText)view.findViewById(R.id.player2Name);
    }

    public void onClickNumbers(View view) {
        String tag = view.getTag().toString();
        mCalculatorPresenter.relayNumbers(tag);
    }

    private void makePresenter() {
        if(mCalculatorPresenter == null){
            mCalculatorPresenter = new CalculatorPresenter(this);
        }
    }


    public void onEnteredValueUpdated(String enteredValue) {
        String previousEnteredValue = enteredValueView.getText().toString();
        if(enteredValue.equals("")){
            enteredValueView.setText("");
            return;
        }
        if(enteredValue.equals("0")){
            enteredValueView.setText("");
            return;
        }
        if(enteredValueView != null) {
            if(previousEnteredValue.equals("")){
                previousEnteredValue = "0";
            }
            //animateTextView(Integer.parseInt(previousEnteredValue), Integer.parseInt(enteredValue),
            //        enteredValueView, AppConstants.ENTEREDVALUEVIEWANIMATIONDURATION);
            enteredValueView.setText(enteredValue);
        }
    }

    private static void animateTextView(int initialValue, int finalValue, final TextView textview, int duration) {
        if(initialValue != finalValue) { //will not do anything if both values are equal
            ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
            valueAnimator.setDuration(duration);

            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    textview.setText(valueAnimator.getAnimatedValue().toString());
                }
            });
            valueAnimator.start();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onClickEnteredValue(View view) {
        mCalculatorPresenter.relayEnteredValue();
    }

    public void onClickP1Add(View view) {
        mCalculatorPresenter.relayP1Add();
    }

    public void onClickP1Sub(View view) {
        mCalculatorPresenter.relayP1Sub();
    }

    public void onClickP2Add(View view) {
        mCalculatorPresenter.relayP2Add();
    }

    public void onClickP2Sub(View view) {
        mCalculatorPresenter.relayP2Sub();
    }

    public void onClickDiceRoll(View view) {
        mCalculatorPresenter.relayDiceRoll();
    }

    public void onClickCoinFlip(View view) {
        mCalculatorPresenter.relayCoinFlip();
    }


    public void onP1LpUpdated(Integer player1LpPrevious, Integer player1LpDo) {
        animateTextView(player1LpPrevious, player1LpDo, player1Lp, AppConstants.LPCHANGEANIMATIONDURATION);
    }

    public void onP2LpUpdated(Integer player2LpPrevious, Integer player2LpDo) {
        animateTextView(player2LpPrevious, player2LpDo, player2Lp, AppConstants.LPCHANGEANIMATIONDURATION);
    }

    public void setLpToDefault() {
        player1Lp.setText(mCalculatorPresenter.getDefaultLp());
        player2Lp.setText(mCalculatorPresenter.getDefaultLp());
    }

    public void playLpSound(){
        soundPool.play(lpCounterSoundId, 1, 1, 1, 0, 1);
    }

    public void playDiceSound(){
        soundPool.play(diceRollSoundId, 1, 1, 1, 0, 1);
    }

    public void playCoinSound(){
        soundPool.play(coinFlipSoundId, 1, 1, 1, 0, 1);
    }
}
