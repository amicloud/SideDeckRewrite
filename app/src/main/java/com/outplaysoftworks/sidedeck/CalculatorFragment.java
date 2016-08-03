package com.outplaysoftworks.sidedeck;

import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;
import me.grantland.widget.AutofitHelper;


public class CalculatorFragment extends Fragment {
    private final Integer diceRollAnimationDuration = 2000; //In milliseconds
    private final Integer diceRollAnimationFrameCount = 12;
    //View objects
    public static View view;
    //Sound stuff
    private SoundPool soundPool;
    private Integer lpCounterSoundId;
    private Integer diceRollSoundId;
    private Integer coinFlipSoundId;
    private Integer timerWarning1SoundId;
    private Integer timerWarning2SoundId;
    private Integer timerWarning3SoundId;
    private int timerBeepSoundId;
    //Drawable stuff
    private ArrayList<Drawable> diceDrawables = new ArrayList<>();
    private Drawable diceRollBackgroundDrawable;
    final Handler diceResetHandler = new Handler();
    final Handler coinHandler = new Handler();
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
    @BindView(R.id.holderTimer)
    LinearLayout holderTimer;
    @BindView(R.id.buttonTimer)
    Button buttonTimer;


    Integer lpSoundStreamId;
    private CalculatorPresenter mCalculatorPresenter;
    private ArrayList<Toast> lpToasts = new ArrayList<>();
    private int timerBeep;

    public CalculatorFragment() {
        makePresenter();
    }

    private static void animateTextView(int initialValue, int finalValue, final TextView textview, int duration) {
        if (initialValue != finalValue) { //will not do anything if both values are equal
            ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
            valueAnimator.setDuration(duration);

            valueAnimator.addUpdateListener(valueAnimator1 -> textview.setText(valueAnimator1.getAnimatedValue().toString()));
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
        reset();
        createAutoFitTextHelpers();
        setPickerListener();
        // Inflate the layout for this fragment
        return view;
    }

    private void createAutoFitTextHelpers(){
        AutofitHelper.create(enteredValueView);
        AutofitHelper.create(player1Name);
        AutofitHelper.create(player2Name);
        AutofitHelper.create(player1Lp);
        AutofitHelper.create(player2Lp);
    }

    @OnClick(R.id.buttonReset)
    void onResetClick() {
        AlertDialog alertDialog = new AlertDialog.Builder(this.getContext(), R.style.MyDialog)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(this.getContext().getResources().getString(R.string.reset) + "?")
            .setMessage(this.getContext().getResources().getString(R.string.AreYouSure))
            .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                reset();
            })
            .setNegativeButton(getString(R.string.notReally), (dialog, which) ->{
                //Do nothing
            })
            .show();
        TextView message = ButterKnife.findById(alertDialog, android.R.id.message);
        /*if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            message.setTextColor(getResources().getColor(R.color.material_black));
        } else {
            message.setTextColor(getResources().getColor(R.color.material_white));
        }*/
    }

    private void reset(){
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
        timerWarning1SoundId = soundPool.load(getContext(), R.raw.timer_warning1, 1);
        timerWarning2SoundId = soundPool.load(getContext(), R.raw.timer_warning2, 1);
        timerWarning3SoundId = soundPool.load(getContext(), R.raw.timer_warning3, 1);
        timerBeepSoundId = soundPool.load(getContext(), R.raw.timer_beep, 1);

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

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(this.getContext());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick({R.id.enteredValue, R.id.buttonClear})
    public void onClickClear() {
        mCalculatorPresenter.relayClear();
    }

    private void showSafeValueDialog(int player, boolean add){
        AlertDialog alertDialog = new AlertDialog.Builder(this.getContext(), R.style.MyDialog)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Possible accidental entry")//TODO: Pick better words and localize
                .setMessage("The entered number " + mCalculatorPresenter.getEnteredValue()
                    + " seems too large.  Are you sure that this is correct?")
                .setPositiveButton("Continue", (dialog, which) ->{
                    switch(player){
                        case 1:
                            if(add){
                                mCalculatorPresenter.relayP1Add();
                            } else {
                                mCalculatorPresenter.relayP1Sub();
                            }
                            break;
                        case 2:
                            if(add){
                                mCalculatorPresenter.relayP2Add();
                            } else {
                                mCalculatorPresenter.relayP2Sub();
                            }
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

        TextView message = ButterKnife.findById(alertDialog, android.R.id.message);
        /*if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            message.setTextColor(getResources().getColor(R.color.material_black));
        } else {
            message.setTextColor(getResources().getColor(R.color.material_white));
        }*/
    }



    @OnClick(R.id.buttonP1Add)
    public void onClickP1Add() {
        if(getPreferences().getBoolean(getString(R.string.KEYcheckSafeEntry), true)
                && mCalculatorPresenter.getEnteredValue() > 100000){
            showSafeValueDialog(1, true);
            return;
        }
        mCalculatorPresenter.relayP1Add();
    }

    @OnClick(R.id.buttonP1Sub)
    public void onClickP1Sub() {
        if(getPreferences().getBoolean(getString(R.string.KEYcheckSafeEntry), true)
                && mCalculatorPresenter.getEnteredValue() > 100000){
            showSafeValueDialog(1, false);
            return;
        }
        mCalculatorPresenter.relayP1Sub();
    }

    @OnClick(R.id.buttonP2Add)
    public void onClickP2Add() {
        if(getPreferences().getBoolean(getString(R.string.KEYcheckSafeEntry), true)
                && mCalculatorPresenter.getEnteredValue() > 10000){
            showSafeValueDialog(2, true);
            return;
        }
        mCalculatorPresenter.relayP2Add();
    }

    @OnClick(R.id.buttonP2Sub)
    public void onClickP2Sub() {
        if(getPreferences().getBoolean(getString(R.string.KEYcheckSafeEntry), true)
                && mCalculatorPresenter.getEnteredValue() > 10000){
            showSafeValueDialog(2, false);
            return;
        }
        mCalculatorPresenter.relayP2Sub();
    }

    @OnClick(R.id.buttonDiceRoll)
    public void onClickDiceRoll() {
        playDiceSound();
        mCalculatorPresenter.relayDiceRoll();
    }
    private int currentFrame;
    private int getCurrentFrame(){
        return currentFrame;
    }
    private void setCurrentFrame(int cf){ currentFrame = cf; }
    @OnClick(R.id.buttonCoinFlip)
    public void onClickCoinFlip() {
        mCalculatorPresenter.relayCoinFlip();
        playCoinSound();
            currentFrame = 0;
            if(getIsSoundEnabled()){
                soundPool.play(coinFlipSoundId, 1, 1, 1, 0, 1);
            }
            coinFlipButton.setClickable(false);
            final int frames = 10;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (getCurrentFrame() < frames) {
                            Coin coin = makeCoin();
                            coinFlipButton.setText(coin.getFace().toString());
                            setCurrentFrame(getCurrentFrame() + 1);
                            handler.postDelayed(this, 200);
                        }
                        if (frames == getCurrentFrame()) {
                            resetCoinFlipAfterDelay();
                            coinFlipButton.setClickable(true);
                        }
                    }catch (NullPointerException e){
                        Log.d("CF: ", "coin handler crashed");
                    }
                }
            },400);
    }
    private void resetCoinFlipAfterDelay(){
        coinHandler.removeCallbacksAndMessages(null);
        coinHandler.postDelayed(() -> {
            try {
                coinFlipButton.setText(getContext().getString(R.string.coinFlip));
            } catch (NullPointerException e){
                Log.d("CF: ", "Coin handler 2 crashed");
            }
        }, 8000);
    }

    private Coin makeCoin(){
        Double toss = Math.random();
        Coin coin = new Coin(toss, getContext());
        return coin;
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

    @OnClick(R.id.buttonTimer)
    public void onClickTimerShow(){
        getTimeFromSeconds();
        holderTimer.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.buttonTimerClose, R.id.spacerTimerBottom, R.id.spacerTimerTop})
    public void onClickTimerHide(){
        holderTimer.setVisibility(View.GONE);
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
        if(getIsSoundEnabled()) {
            lpSoundStreamId = soundPool.play(lpCounterSoundId, 1, 1, 1, 0, 1);
        }
    }

    public boolean getIsSoundEnabled(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return prefs.getBoolean(getString(R.string.KEYsoundOnOff), true);
    }
    public void playDiceSound() {
        if (getIsSoundEnabled()) {
            soundPool.play(diceRollSoundId, 1, 1, 1, 0, 1);
        }
    }

    public void playCoinSound() {
        if(getIsSoundEnabled()) {
            soundPool.play(coinFlipSoundId, 1, 1, 1, 0, 1);
        }
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
        AnimationDrawable animation = randomAnimationBuilder.makeAnimation(false);
        buttonDiceRoll.setBackground(animation);
        buttonDiceRoll.setText("");
        animation.start();
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                buttonDiceRoll.setAlpha(1f);
                buttonDiceRoll.setBackgroundDrawable(diceDrawables.get(lastDiceRoll));
                buttonDiceRoll.setClickable(true);
            } catch (NullPointerException e){
                Log.d("CF: ", "Dice handler crashed");
            }
        }, diceRollAnimationDuration + (randomAnimationBuilder.getFrameDuration() * 2));
        resetDiceRollButtonAfterDelay(originalBackgroundDrawable);
    }

    private void resetDiceRollButtonAfterDelay(final Drawable originalBackground) {
        diceResetHandler.removeCallbacksAndMessages(null);
        diceResetHandler.postDelayed(() -> {
            try {
                buttonDiceRoll.setText(view.getResources().getString(R.string.diceRoll));
                buttonDiceRoll.setBackground(originalBackground);
            } catch (NullPointerException e){
                Log.d("CF: ", "Dice reset handler crashed");
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
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundColor(view.getSolidColor());
        toast.show();
    }
    int debounceTime = 3000;
    Handler p1NameHandler = new Handler();
    @OnTextChanged(R.id.player1Name)
    public void onPlayer1NameChanged() {
        p1NameHandler.removeCallbacksAndMessages(null);
        p1NameHandler.postDelayed(()-> player1Name.clearFocus(), debounceTime);
        String name = player1Name.getText().toString();
        mCalculatorPresenter.onPlayerNameChanged(name, 1);
    }
    Handler p2NameHandler = new Handler();
    @OnTextChanged(R.id.player2Name)
    public void onPlayer2NameChanged() {
        p2NameHandler.removeCallbacksAndMessages(null);
        p2NameHandler.postDelayed(()-> player2Name.clearFocus(), debounceTime);
        String name = player2Name.getText().toString();
        mCalculatorPresenter.onPlayerNameChanged(name, 2);
    }


    @OnClick(R.id.buttonUndo)
    public void onUndoClicked(){
        mCalculatorPresenter.onUndoClicked();
    }

    public void setPlayer1Lp(Integer lp){
        player1Lp.setText(lp.toString());
    }

    public void setPlayer2Lp(Integer lp){
        player2Lp.setText(lp.toString());
    }

    public String getPlayer1Name(){
        return mCalculatorPresenter.getPlayer1Name();
    }

    public String getPlayer2Name(){
        return mCalculatorPresenter.getPlayer2Name();
    }

    public boolean getPreferenceAllowNegativeLp(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return prefs.getBoolean(getContext().getString(R.string.KEYallowNegativeLp), false);
    }

    Handler timerHandler = new Handler();
    Runnable timerTask = new Runnable(){
        @Override
        public void run() {
            try {
                timerRunning = true;
                if (currentTimeInSeconds == 0) {
                    timerFinished();
                    timerBeep();
                    return;
                }
                if (currentTimeInSeconds == 301) {
                    timerAlert();
                }
                decrementTimeOnUiThread();
                timerHandler.postDelayed(this, 999);
            } catch (NullPointerException e){
                Log.d("CF: ", "timerTask continued after activity was destroyed");
            }
        }
    };

    private void timerBeep() {
        if(getIsSoundEnabled()){
            soundPool.play(timerBeepSoundId, 1, 1, 1, 0, 1);
        }
    }

    private void timerAlert() {
        if(getIsSoundEnabled()){
            Random random = new Random();
            int rand = random.nextInt(2);
            switch (rand){
                case 0:
                    soundPool.play(timerWarning1SoundId, 1, 1, 1, 0, 1);
                    break;
                case 1:
                    soundPool.play(timerWarning2SoundId, 1, 1, 1, 0, 1);
                    break;
                case 2:
                    soundPool.play(timerWarning3SoundId, 1, 1, 1, 0, 1);
                    break;
            }
        }
    }

    private void decrementTimeOnUiThread(){
        this.getActivity().runOnUiThread(this::decrementTime);
    }
    private void decrementTime() {
        currentTimeInSeconds--;
        picker.setValue(currentTimeInSeconds);
        getTimeFromSeconds();
        //Log.d("TIMER", "decrementTime: " + currentTimeInSeconds);

    }

    private void timerFinished() {
        timerRunning = false;
    }

    @BindView(R.id.picker)
    HoloCircleSeekBar picker;
    @BindView(R.id.buttonResetTimer)
    Button buttonResetTimer;
    @BindView(R.id.buttonStartTimer)
    Button buttonStartTimer;
    @BindView(R.id.textTime)
    TextView textTime;


    private boolean timerRunning = false;
    private Integer defaultTimeInSeconds = 2400;
    private Integer currentTimeInSeconds = defaultTimeInSeconds;
    @OnClick(R.id.buttonStartTimer)
    public void startTimer(){
        if(!timerRunning) {
            initializeTimerTask();
        }else if(timerRunning){
            stopTimer();
        }
    }

    public void stopTimer(){
        timerRunning = false;
        timerHandler.removeCallbacksAndMessages(null);
        buttonStartTimer.setText("START");
    }

    @OnClick(R.id.buttonResetTimer)
    public void resetTimer(){
        stopTimer();
        picker.setValue(defaultTimeInSeconds);
        currentTimeInSeconds = defaultTimeInSeconds;
        getTimeFromSeconds();
        buttonTimer.setText("Timer");
    }

    public void initializeTimerTask(){
        timerHandler.removeCallbacksAndMessages(null);
        timerHandler.postDelayed(timerTask, 999);
        buttonStartTimer.setText("STOP");

    }
    private void getTimeFromSeconds(){
        Integer time1 = currentTimeInSeconds / 60;
        Integer time2 = currentTimeInSeconds % 60;
        String timeMinutes = time1.toString();
        if(timeMinutes.length() < 2){
            timeMinutes = "0" + timeMinutes;
        }
        String timeSeconds = time2.toString();
        if(timeSeconds.length() < 2){
            timeSeconds = "0" + timeSeconds;
        }
        String theTime = timeMinutes + ":" + timeSeconds;
        textTime.setText(theTime);
        buttonTimer.setText(theTime);

    }
    public void setPickerListener(){
        picker.setValue(defaultTimeInSeconds);
        picker.setOnSeekBarChangeListener(new HoloCircleSeekBar.OnCircleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(HoloCircleSeekBar holoCircleSeekBar, int i, boolean b) {
                currentTimeInSeconds = holoCircleSeekBar.getValue();
                getTimeFromSeconds();
            }

            @Override
            public void onStartTrackingTouch(HoloCircleSeekBar holoCircleSeekBar) {
                currentTimeInSeconds = holoCircleSeekBar.getValue();
                getTimeFromSeconds();
            }

            @Override
            public void onStopTrackingTouch(HoloCircleSeekBar holoCircleSeekBar) {
                currentTimeInSeconds = holoCircleSeekBar.getValue();
                getTimeFromSeconds();
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        coinHandler.removeCallbacksAndMessages(null);
        diceResetHandler.removeCallbacksAndMessages(null);
        p1NameHandler.removeCallbacksAndMessages(null);
        p2NameHandler.removeCallbacksAndMessages(null);
        timerHandler.removeCallbacksAndMessages(null);
    }
}
