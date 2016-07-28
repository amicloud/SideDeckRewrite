package com.outplaysoftworks.sidedeckv2;

import android.animation.ValueAnimator;
import android.graphics.Color;
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
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;


public class CalculatorFragment extends Fragment {
    private static final Integer diceRollAnimationDuration = 2000; //In milliseconds
    private static final Integer diceRollAnimationFrameCount = 5;
    //View objects
    public static View view;
    private static boolean isDiceButtonWaitingToReset = false;
    private static boolean canDiceButtonBeReset = true;
    //Sound stuff
    private static SoundPool soundPool;
    private static Integer lpCounterSoundId;
    private static Integer diceRollSoundId;
    private static Integer coinFlipSoundId;
    //Drawable stuff
    private static ArrayList<Drawable> diceDrawables = new ArrayList<>();
    private static Drawable diceRollBackgroundDrawable;
    final Handler diceResetHandler = new Handler();
    @BindView(R.id.enteredValue)
    TextView enteredValueView;
    @BindView(R.id.player1Lp)
    TextView player1Lp;
    @BindView(R.id.player2Lp)
    TextView player2Lp;
    @BindView(R.id.player1Name)
    EditText player1Name;
    @BindView(R.id.player2Name)
    EditText player2Name;
    @BindView(R.id.buttonDiceRoll)
    Button buttonDiceRoll;
    @BindView(R.id.buttonCoinFlip)
    Button coinFlipButton;
    @BindView(R.id.buttonTurn)
    Button buttonTurn;
    Integer lpSoundStreamId;
    private CalculatorPresenter mCalculatorPresenter;
    private ArrayList<Toast> lpToasts = new ArrayList<>();

    public CalculatorFragment() {
        makePresenter();
    }

    private static void animateTextView(int initialValue, int finalValue, final TextView textview, int duration) {
        if (initialValue != finalValue) { //will not do anything if both values are equal
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calculator, container, false);
        ButterKnife.bind(this, view);
        loadDrawables();
        setUpSounds();
        setPlayerNamesToDefault();
        reset(false);
        // Inflate the layout for this fragment
        return view;
    }

    @OnClick(R.id.buttonReset)
    void reset(boolean askForConfirmation) {
        if(askForConfirmation){
            //TODO: Put a dialog here
        }
        setLpToDefault();
        mCalculatorPresenter.onResetClicked();
    }

    private void setPlayerNamesToDefault() {
        player1Name.setText(mCalculatorPresenter.getPlayer1Name());
        player2Name.setText(mCalculatorPresenter.getPlayer2Name());
    }

    private void loadDrawables() {
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_1));
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_2));
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_3));
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_4));
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_5));
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_6));
        diceRollBackgroundDrawable = buttonDiceRoll.getBackground();
    }

    private void setUpSounds() {
        soundPool = new SoundPool(32, AudioManager.STREAM_MUSIC, 0);
        lpCounterSoundId = soundPool.load(getContext(), R.raw.lpcountersound, 1);
        coinFlipSoundId = soundPool.load(getContext(), R.raw.coinflipsound, 1);
        diceRollSoundId = soundPool.load(getContext(), R.raw.dicerollsound, 1);
    }

    @OnClick({R.id.button0, R.id.button00, R.id.button000, R.id.button1, R.id.button2, R.id.button3,
            R.id.button4, R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9})
    public void onClickNumbers(View view) {
        String tag = view.getTag().toString();
        mCalculatorPresenter.relayNumbers(tag);
    }

    private void makePresenter() {
        if (mCalculatorPresenter == null) {
            mCalculatorPresenter = new CalculatorPresenter(this);
        }
    }

    public void onEnteredValueUpdated(String enteredValue) {
        String previousEnteredValue = enteredValueView.getText().toString();
        if (enteredValue.equals("")) {
            enteredValueView.setText("");
            return;
        }
        if (enteredValue.equals("0")) {
            enteredValueView.setText("");
            return;
        }
        if (enteredValueView != null) {
            if (previousEnteredValue.equals("")) {
                previousEnteredValue = "0";
            }
            //animateTextView(Integer.parseInt(previousEnteredValue), Integer.parseInt(enteredValue),
            //        enteredValueView, AppConstants.ENTEREDVALUEVIEWANIMATIONDURATION);
            enteredValueView.setText(enteredValue);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.enteredValue)
    public void onClickEnteredValue() {
        mCalculatorPresenter.relayEnteredValue();
    }

    @OnClick(R.id.buttonP1Add)
    public void onClickP1Add() {
        mCalculatorPresenter.relayP1Add();
    }

    @OnClick(R.id.buttonP1Sub)
    public void onClickP1Sub() {
        mCalculatorPresenter.relayP1Sub();
    }

    @OnClick(R.id.buttonP2Add)
    public void onClickP2Add() {
        mCalculatorPresenter.relayP2Add();
    }

    @OnClick(R.id.buttonP2Sub)
    public void onClickP2Sub() {
        mCalculatorPresenter.relayP2Sub();
    }

    @OnClick(R.id.buttonDiceRoll)
    public void onClickDiceRoll() {
        playDiceSound();
        mCalculatorPresenter.relayDiceRoll();
    }

    @OnClick(R.id.buttonCoinFlip)
    public void onClickCoinFlip() {
        mCalculatorPresenter.relayCoinFlip();
        playCoinSound();
    }

    @OnClick(R.id.buttonTurn)
    public void onClickTurn() {
        mCalculatorPresenter.relayTurnClick();
    }

    @OnLongClick(R.id.buttonTurn)
    public boolean onLongClickTurn() {
        mCalculatorPresenter.relayTurnLongClick();
        return true;
    }

    public void onLpUpdated(Integer playerLpPrevious, Integer playerLp, Integer player, String toast,
                            boolean doNotShowToast) {
        playLpSound();
        if (!doNotShowToast) makeToast(toast);
        switch (player) {
            case 1:
                animateTextView(playerLpPrevious, playerLp, player1Lp, AppConstants.LPCHANGEANIMATIONDURATION);
                break;
            case 2:
                animateTextView(playerLpPrevious, playerLp, player2Lp, AppConstants.LPCHANGEANIMATIONDURATION);
                break;
        }
    }

    public void setLpToDefault() {
        player1Lp.setText(mCalculatorPresenter.getDefaultLp());
        player2Lp.setText(mCalculatorPresenter.getDefaultLp());
    }

    public void playLpSound() {
        if (lpSoundStreamId != null) {
            soundPool.stop(lpSoundStreamId);
        }
        lpSoundStreamId = soundPool.play(lpCounterSoundId, 1, 1, 1, 0, 1);
    }

    public void playDiceSound() {
        soundPool.play(diceRollSoundId, 1, 1, 1, 0, 1);
    }

    public void playCoinSound() {
        soundPool.play(coinFlipSoundId, 1, 1, 1, 0, 1);
    }

    public void onTurnUpdated(Integer turn) {
        buttonTurn.setText(view.getResources().getString(R.string.turn) + turn.toString());
    }

    //TODO: Make sure this scales correctly across multiple screen dpi settings
    public void onDiceRollComplete(final int lastDiceRoll) {
        buttonDiceRoll.setClickable(false);
        final Drawable originalBackgroundDrawable = diceRollBackgroundDrawable;
        RandomAnimationBuilder randomAnimationBuilder = new RandomAnimationBuilder(diceDrawables,
                diceRollAnimationDuration, diceRollAnimationFrameCount);
        AnimationDrawable animation = randomAnimationBuilder.makeAnimation();
        buttonDiceRoll.setBackground(animation);
        buttonDiceRoll.setText("");
        //animation.setEnterFadeDuration(randomAnimationBuilder.getFrameDuration()/2);
        //animation.setExitFadeDuration(randomAnimationBuilder.getFrameDuration()/2);
        animation.start();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonDiceRoll.setAlpha(1f);
                buttonDiceRoll.setBackgroundDrawable(diceDrawables.get(lastDiceRoll));
                buttonDiceRoll.setClickable(true);
            }
        }, diceRollAnimationDuration + (randomAnimationBuilder.getFrameDuration() * 2));
        resetDiceRollButtonAfterDelay(originalBackgroundDrawable);
    }

    private void resetDiceRollButtonAfterDelay(final Drawable originalBackground) {
        diceResetHandler.removeCallbacksAndMessages(null);
        diceResetHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonDiceRoll.setText(view.getResources().getString(R.string.diceRoll));
                buttonDiceRoll.setBackground(originalBackground);
            }
        }, diceRollAnimationDuration + 6000);
    }

    public void makeToast(String toastText) {
        if (lpToasts.size() > 0) {
            lpToasts.get(0).cancel();
            lpToasts.clear();
        }
        Toast toast = Toast.makeText(this.getContext(), toastText, Toast.LENGTH_LONG);
        lpToasts.add(toast);
        View view = toast.getView();
        view.setBackgroundColor(getResources().getColor(R.color.material_light_dark));
        TextView textView = ButterKnife.findById(view, android.R.id.message);
        textView.setBackgroundColor(view.getSolidColor());
        toast.show();
    }

    @OnTextChanged(R.id.player1Name)
    public void onPlayer1NameChanged() {
        String name = player1Name.getText().toString();
        mCalculatorPresenter.onPlayerNameChanged(name, 1);
    }

    @OnTextChanged(R.id.player2Name)
    public void onPlayer2NameChanged() {
        String name = player2Name.getText().toString();
        mCalculatorPresenter.onPlayerNameChanged(name, 2);
    }
}
