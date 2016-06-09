package com.outplaysoftworks.sidedeckv2;

import android.animation.ValueAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import java.util.logging.LogRecord;

import butterknife.ButterKnife;


public class CalculatorFragment extends Fragment {
    private CalculatorPresenter mCalculatorPresenter;
    public static View view;

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
    private static Button turnButton;
    private static ArrayList<Drawable> diceDrawables = new ArrayList<>();

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
        view = inflater.inflate(R.layout.fragment_calculator, container, false);
        assignViewsByIds(view);
        assignClickListeners();
        loadDrawables();
        setUpSounds();
        setLpToDefault();
        // Inflate the layout for this fragment
        return view;
    }

    private void loadDrawables() {
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_1));
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_2));
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_3));
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_4));
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_5));
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_6));
    }

    private void assignClickListeners() {
        turnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTurn();
            }
        });
        turnButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongClickTurn();
                return true;
            }
        });
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
        player2Name = (EditText)view.findViewById(R.id.player2Name);
        turnButton = (Button)view.findViewById(R.id.turnButton);
        diceRollButton = (Button)view.findViewById(R.id.diceRollButton);
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

    public void onClickTurn() {
        mCalculatorPresenter.relayTurnClick();
    }

    public void onLongClickTurn() {
        mCalculatorPresenter.relayTurnLongClick();
    }

    public void onTurnUpdated(Integer turn){
        turnButton.setText(view.getResources().getString(R.string.turn) + turn.toString());
    }

    public void onDiceRollComplete(final int lastDiceRoll) {
        diceRollButton.setClickable(false);
        final Drawable originalBackgroundDrawable = diceRollButton.getBackground();
        RandomAnimationBuilder randomAnimationBuilder = new RandomAnimationBuilder(diceDrawables, 2000, 10);
        AnimationDrawable animation = randomAnimationBuilder.makeAnimation();
        diceRollButton.setBackground(animation);
        diceRollButton.setText("");
        animation.start();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                diceRollButton.setBackgroundDrawable(diceDrawables.get(lastDiceRoll));
                diceRollButton.setClickable(true);
                resetDiceRollButtonAfterDelay(originalBackgroundDrawable);
            }
        }, 2050);
    }

    private void resetDiceRollButtonAfterDelay(final Drawable originalBackground){
        final Handler handler =  new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                diceRollButton.setText(view.getResources().getString(R.string.diceRoll));
                diceRollButton.setBackground(originalBackground);
            }
        }, 5000);
    }
}
