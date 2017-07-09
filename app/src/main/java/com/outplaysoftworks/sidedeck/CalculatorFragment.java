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
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import me.grantland.widget.AutofitHelper;


@SuppressWarnings("WeakerAccess")
public class CalculatorFragment extends Fragment {

    //TODO: Add a sound for when lp hits zero

    //This code is disgusting...

    private final Integer diceRollAnimationDuration = 2000; //In milliseconds
    //View objects
    public static View view;
    //Sound stuff
    private SoundPool soundPool;
    private Integer lpCounterSoundId;
    private Integer lpCounterSound2Id;
    private Integer diceRollSoundId;
    private Integer coinFlipSoundId;
    private Integer timerWarning1SoundId;
    private Integer timerWarning2SoundId;
    private Integer timerWarning3SoundId;
    private int timerBeepSoundId;
    //Drawable stuff
    private final ArrayList<Drawable> diceDrawables = new ArrayList<>();
    private Drawable diceRollBackgroundDrawable;
    private Drawable calcArrow;
    private final Handler diceResetHandler = new Handler();
    private final Handler coinHandler = new Handler();
    @SuppressWarnings("unused")
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
    @BindView(R.id.calculatorWork)
    TextView calculatorWork;
    @BindView(R.id.calculatorResults)
    TextView calculatorResults;
    @BindView(R.id.holderCalculator)
    LinearLayout holderCalculator;
    @BindView(R.id.buttonReset)
    Button buttonReset;
    @BindView(R.id.buttonShowCalc)
    Button buttonShowCalc;
    @BindView(R.id.buttonClear)
    Button buttonClear;
    @BindView(R.id.buttonUndo)
    Button buttonUndo;
    @BindView(R.id.calcEquals)
    Button buttonCalcEquals;

    Integer lpSoundStreamId;
    public CalculatorPresenter mCalculatorPresenter;
    @SuppressWarnings("CanBeFinal")
    private final ArrayList<Toast> lpToasts = new ArrayList<>();

    //Calculator stuff
    private String calcWork = "";
    final Evaluator evaluator = new Evaluator();

    //Timer stuff

    //Firebase stuff
    FirebaseAnalytics mFirebaseAnalytics;
    private Long timesTimerOpened = 0L;
    private Long timesCalculatorOpened = 0L;
    private Long timesUndoPressed = 0L;
    private Long timesResetPressed = 0L;
    private Long timesCoinPressed = 0L;
    private Long timesDicePressed = 0L;
    //Text-to-speach
    TextToSpeech tts;
    boolean ttsIsInitialized = false;

    public CalculatorFragment() {
        makePresenter();
    }

    private static void animateTextView(int initialValue, int finalValue, final TextView textview, boolean isZero) {
        if (initialValue != finalValue) { //will not do anything if both values are equal
            ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
            if(isZero){
                valueAnimator.setDuration(AppConstants.LPCHANGEANIMATIONDURATIONLONG);
            } else{
                valueAnimator.setDuration(AppConstants.LPCHANGEANIMATIONDURATION);
            }
            valueAnimator.addUpdateListener(valueAnimator1 -> textview.setText(valueAnimator1.getAnimatedValue().toString()));
            valueAnimator.start();
        }
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
        //initializeTextToSpeechEngine(0);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this.getContext());
        currentTimeInSeconds = getDefaultTimerTime();
        // Inflate the layout for this fragment
        return view;
    }

    private void debugVoice() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {;
            for(Voice v : tts.getVoices()){
                System.out.println(v.getName());
                System.out.println(v.getLocale());
                System.out.println(v.getQuality());
                System.out.println("\n");
            }
        }
    }

    private void initializeTextToSpeechEngine(int timesTried) {
        final int finalTimesTried = timesTried + 1;
        tts = new TextToSpeech(getContext(), i -> {
            if(i == 0){
                ttsIsInitialized = true;
                //debugVoice();
            } else if(i == -1){
                ttsIsInitialized = false;
                if(finalTimesTried < 100) {
                    initializeTextToSpeechEngine(finalTimesTried);
                } else{
                    try {
                        throw new Throwable("Text to speech init failed");
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        });
    }

    private void createAutoFitTextHelpers(){
        AutofitHelper.create(enteredValueView);
        AutofitHelper.create(player1Name);
        AutofitHelper.create(player2Name);
        AutofitHelper.create(player1Lp);
        AutofitHelper.create(player2Lp);
        AutofitHelper.create(buttonClear);
        AutofitHelper.create(buttonReset);
        AutofitHelper.create(buttonUndo);
        AutofitHelper.create(buttonShowCalc);
        AutofitHelper.create(buttonTimer);
        AutofitHelper.create(buttonDiceRoll);
        AutofitHelper.create(coinFlipButton);
        AutofitHelper.create(buttonTurn);
    }

    @OnClick(R.id.buttonReset)
    void onResetClick() {
        new AlertDialog.Builder(this.getContext(), R.style.MyDialog)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(this.getContext().getResources().getString(R.string.reset) + "?")
            .setMessage(this.getContext().getResources().getString(R.string.AreYouSure))
            .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                reset();
                timesResetPressed++;
            })
            .setNegativeButton(getString(R.string.notReally), (dialog, which) ->{
                //Do nothing
            })
            .show();
    }

    private void reset(){
        setLpToDefault();
        mCalculatorPresenter.onResetClicked();
    }

    private void setPlayerNamesToDefault() {
        player1Name.setText(mCalculatorPresenter.getPlayer1Name());
        player2Name.setText(mCalculatorPresenter.getPlayer2Name());
    }

    @SuppressWarnings("deprecation")
    private void loadDrawables() {
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_1));
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_2));
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_3));
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_4));
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_5));
        diceDrawables.add(view.getResources().getDrawable(R.drawable.dice_6));
        diceRollBackgroundDrawable = buttonDiceRoll.getBackground();
        calcArrow = view.getResources().getDrawable(R.drawable.calc_arrow);
    }

    private void setUpSounds() {
        //noinspection deprecation
        soundPool = new SoundPool(32, AudioManager.STREAM_MUSIC, 0);
        lpCounterSoundId = soundPool.load(getContext(), R.raw.lpcountersound, 1);
        lpCounterSound2Id = soundPool.load(getContext(), R.raw.lpcountersound2, 1);
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
        if (enteredValue.equals("") || enteredValue.equals("0")) {
            enteredValueView.setText("");
            return;
        }
        enteredValueView.setText(enteredValue);
    }

    public SharedPreferences getPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplication().getApplicationContext());
    }

    @OnClick({R.id.enteredValue, R.id.buttonClear})
    public void onClickClear() {
        mCalculatorPresenter.relayClear();
    }

    private void showSafeValueDialog(int player, boolean add){
        new AlertDialog.Builder(this.getContext(), R.style.MyDialog)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.possibleAccidentalEntry)//TODO: Pick better words and localize
                .setMessage(getString(R.string.theEnteredNumber) + mCalculatorPresenter.getEnteredValue()
                    + getString(R.string.seemsTooLarge))
                .setPositiveButton(R.string.Continue, (dialog, which) ->{
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
                .setNegativeButton(R.string.cancel, null)
                .show();
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
        timesDicePressed++;
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
        timesCoinPressed++;
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
                        Log.d("CF: ", "coin handler crashed"); //NON-NLS
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
                Log.d("CF: ", "Coin handler 2 crashed"); //NON-NLS
            }
        }, 8000);
    }

    private Coin makeCoin(){
        return new Coin(Math.random(), getContext());
    }

    @OnClick(R.id.buttonTurn)
    public void onClickTurn() {
        mCalculatorPresenter.relayTurnClick();
    }

    @SuppressWarnings("SameReturnValue")
    @OnLongClick(R.id.buttonTurn)
    public boolean onLongClickTurn() {
        mCalculatorPresenter.relayTurnLongClick();
        return true;
    }

    @OnClick(R.id.buttonTimer)
    public void onClickTimerShow(){

        getTimeFromSeconds(false);
        holderTimer.setVisibility(View.VISIBLE);
        timesTimerOpened++;
    }

    @OnClick({R.id.buttonTimerClose, R.id.spacerTimerBottom, R.id.spacerTimerTop})
    public void onClickTimerHide(){
        holderTimer.setVisibility(View.GONE);
    }

    public void onLpUpdated(Integer playerLpPrevious, Integer playerLp, Integer player, String toast,
                            boolean doNotShowToast) {
        boolean isZero = playerLp <= 0;
        playLpSound(isZero);
        if (!doNotShowToast) makeToast(toast);
        switch (player) {
            case 1:
                animateTextView(playerLpPrevious, playerLp, player1Lp,isZero);
                break;
            case 2:
                animateTextView(playerLpPrevious, playerLp, player2Lp,isZero);
                break;
        }
    }

    public void setLpToDefault() {
        player1Lp.setText(mCalculatorPresenter.getDefaultLp());
        player2Lp.setText(mCalculatorPresenter.getDefaultLp());
    }

    public void playLpSound(Boolean isZero) {
        if (lpSoundStreamId != null) {
            soundPool.stop(lpSoundStreamId);
        }
        if(getIsSoundEnabled()) {
            if(isZero){
                lpSoundStreamId = soundPool.play(lpCounterSound2Id, 1, 1, 1, 0, 1);
            }
            else {
                lpSoundStreamId = soundPool.play(lpCounterSoundId, 1, 1, 1, 0, 1);
            }
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
        buttonTurn.setText(String.format("%s%s", view.getResources().getString(R.string.turn), turn.toString())); //NON-NLS
    }

    //TODO: Make sure this scales correctly across multiple screen dpi settings
    public void onDiceRollComplete(final int lastDiceRoll) {
        buttonDiceRoll.setClickable(false);
        final Drawable originalBackgroundDrawable = diceRollBackgroundDrawable;
        Integer diceRollAnimationFrameCount = 12;
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
                //noinspection deprecation
                buttonDiceRoll.setBackgroundDrawable(diceDrawables.get(lastDiceRoll));
                buttonDiceRoll.setClickable(true);
            } catch (NullPointerException e){
                Log.d("CF: ", "Dice handler crashed"); //NON-NLS
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
                Log.d("CF: ", "Dice reset handler crashed"); //NON-NLS
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
        //noinspection deprecation
        view.setBackgroundColor(getResources().getColor(R.color.material_light_dark));
        TextView textView = ButterKnife.findById(view, android.R.id.message);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundColor(view.getSolidColor());
        toast.show();
    }

    @OnClick(R.id.buttonUndo)
    public void onUndoClicked(){
        mCalculatorPresenter.onUndoClicked();
        timesUndoPressed++;
    }

    public void setPlayer1Lp(Integer lp){
        player1Lp.setText(lp.toString());
    }

    public void setPlayer2Lp(Integer lp){
        player2Lp.setText(lp.toString());
    }

    public String getPlayer1Name(){
        return getPreferences().getString(getString(R.string.KEYplayerOneDefaultNameSetting),
                getString(R.string.playerOne));
    }

    public String getPlayer2Name(){
        return getPreferences().getString(getString(R.string.KEYplayerTwoDefaultNameSetting),
                getString(R.string.playerTwo));
    }

    public boolean getPreferenceAllowNegativeLp(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return prefs.getBoolean(getContext().getString(R.string.KEYallowNegativeLp), false);
    }

    final Handler timerHandler = new Handler();
    final Runnable timerTask = new Runnable(){
        @Override
        public void run() {
            try {
                timerRunning = true;
                if (currentTimeInSeconds == 0) {
                    timerFinished();
                    timerBeep();
                    holderTimer.setVisibility(View.VISIBLE);
                    return;
                }
                if (currentTimeInSeconds == 301) {
                    timerAlert();
                }
                decrementTimeOnUiThread();
                timerHandler.postDelayed(this, 999);
            } catch (NullPointerException e){
                Log.d("CF: ", "timerTask continued after activity was destroyed"); //NON-NLS
            }
        }
    };

    private void timerBeep() {
        if(getIsSoundEnabled()){
            soundPool.play(timerBeepSoundId, 1, 1, 1, 0, 1);
        }
    }

    private void timerAlert() {
        //noinspection deprecation
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
        getTimeFromSeconds(true);
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
    private Integer currentTimeInSeconds;// = getDefaultTimerTime();
    @OnClick(R.id.buttonStartTimer)
    public void startTimer(){
        if(!timerRunning) {
            initializeTimerTask();
        }else //noinspection ConstantConditions
            if(timerRunning){
            stopTimer();
        }
    }

    public void stopTimer(){
        timerRunning = false;
        timerHandler.removeCallbacksAndMessages(null);
        buttonStartTimer.setText(R.string.start);
    }

    @OnClick(R.id.buttonResetTimer)
    public void resetTimer(){
        stopTimer();
        picker.setValue(getDefaultTimerTime());
        currentTimeInSeconds = getDefaultTimerTime();
        getTimeFromSeconds(false);
        buttonTimer.setText(R.string.timer);
    }

    public void initializeTimerTask(){
        timerHandler.removeCallbacksAndMessages(null);
        timerHandler.postDelayed(timerTask, 999);
        buttonStartTimer.setText(R.string.stop);

    }
    private void getTimeFromSeconds(Boolean setButtonTimerText){
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
        if(setButtonTimerText) {
            buttonTimer.setText(theTime);
        }
    }
    
    public Integer getDefaultTimerTime(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String timeInMinutes = sharedPreferences.getString(getString(R.string.KEYdefaultTimer), "40");
        return Integer.parseInt(timeInMinutes)*60; //Converts to seconds
    }
    
    public void setPickerListener(){
        picker.setValue(getDefaultTimerTime());
        picker.setOnSeekBarChangeListener(new HoloCircleSeekBar.OnCircleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(HoloCircleSeekBar holoCircleSeekBar, int i, boolean b) {
                currentTimeInSeconds = holoCircleSeekBar.getValue();
                getTimeFromSeconds(true);
            }

            @Override
            public void onStartTrackingTouch(HoloCircleSeekBar holoCircleSeekBar) {
                currentTimeInSeconds = holoCircleSeekBar.getValue();
                getTimeFromSeconds(false);
            }

            @Override
            public void onStopTrackingTouch(HoloCircleSeekBar holoCircleSeekBar) {
                currentTimeInSeconds = holoCircleSeekBar.getValue();
                getTimeFromSeconds(false);
            }
        });
    }

    @Override
    public void onDestroy(){
        logAnalytics();
        coinHandler.removeCallbacksAndMessages(null);
        diceResetHandler.removeCallbacksAndMessages(null);
        /*p1NameHandler.removeCallbacksAndMessages(null);
        p2NameHandler.removeCallbacksAndMessages(null);*/
        timerHandler.removeCallbacksAndMessages(null);
        //tts.shutdown();
        super.onDestroy();
    }

    public void logAnalytics(){
        Bundle params_calc = new Bundle();
        params_calc.putLong(FirebaseAnalytics.Param.VALUE, timesCalculatorOpened);
        Bundle params_timer = new Bundle();
        params_timer.putLong(FirebaseAnalytics.Param.VALUE, timesTimerOpened);
        Bundle params_undo = new Bundle();
        params_undo.putLong(FirebaseAnalytics.Param.VALUE, timesUndoPressed);
        Bundle params_reset = new Bundle();
        params_reset.putLong(FirebaseAnalytics.Param.VALUE, timesResetPressed);
        Bundle params_coin = new Bundle();
        params_coin.putLong(FirebaseAnalytics.Param.VALUE, timesCoinPressed);
        Bundle params_dice = new Bundle();
        params_dice.putLong(FirebaseAnalytics.Param.VALUE, timesDicePressed);
        mFirebaseAnalytics.logEvent("calculator_opened", params_calc);//NON-NLS
        mFirebaseAnalytics.logEvent("timer_opened", params_timer);//NON-NLS
        mFirebaseAnalytics.logEvent("undo_pressed", params_undo);//NON-NLS
        mFirebaseAnalytics.logEvent("reset_pressed", params_reset);//NON-NLS
        mFirebaseAnalytics.logEvent("coin_pressed", params_coin);//NON-NLS
        mFirebaseAnalytics.logEvent("dice_pressed", params_dice);//NON-NLS
        Log.d("Analytics ", "times calc opened: " + timesCalculatorOpened + "; times " + //NON-NLS
                "timer opened: " + timesTimerOpened); //NON-NLS
    }

    @OnClick(R.id.buttonShowCalc)
    public void showCalculator(){
        holderCalculator.setVisibility(View.VISIBLE);
        timesCalculatorOpened++;
    }

    @OnClick({R.id.spacerCalculatorTop, R.id.spacerCalculatorBottom})
    public void hideCalculator(){
        holderCalculator.setVisibility(View.GONE);
    }
    @OnClick({R.id.calc0, R.id.calc1, R.id.calc2, R.id.calc3, R.id.calc4, R.id.calc5, R.id.calc6,
            R.id.calc7, R.id.calc8, R.id.calc9, R.id.calcAdd, R.id.calcMinus, R.id.calcMultiply,
            R.id.calcDivide, R.id.calcDecimal, R.id.calcLeftParen, R.id.calcRightParen})
    public void onClickCalculatorNumbersAndOperators(View view){
        shouldSendToCalc = false;
        buttonCalcEquals.setText("="); //NON-NLS
        buttonCalcEquals.setBackgroundDrawable(null);
        buttonCalcEquals.setBackgroundColor(getResources().getColor(R.color.material_accent));
        String tag = view.getTag().toString();
        calcWork += tag;
        calculatorWork.setText(calcWork);
    }
    boolean shouldSendToCalc = false;
    @OnClick(R.id.calcEquals)
    public void onClickCalculatorEquals(){
        if(!shouldSendToCalc) {
            String results;
            try {
                results = evaluator.evaluate(calcCorrectParens(calcWork));
                calculatorResults.setText(results);
            } catch (EvaluationException e) {
                Log.e("JEVAL: ", calcWork); //NON-NLS
                calculatorResults.setText(R.string.error);
                e.printStackTrace();
                if (calcCheckIsParenMismatch(calcWork)) {
                    calculatorResults.setText(R.string.parenMismatch);
                }
            }
            shouldSendToCalc = true;
            buttonCalcEquals.setBackgroundColor(getResources().getColor(R.color.material_accent));
            buttonCalcEquals.setBackgroundDrawable(calcArrow);
            buttonCalcEquals.setText(""); //NON-NLS
            hideCalculator();
            //TODO: SCALE FOR SIZE
        } else {
            if(calculatorResults.getText().equals("") || calculatorResults.getText().equals(
                    getResources().getString(R.string.error)) || calculatorResults.getText().equals(
                            getResources().getString(R.string.parenMismatch))){
                buttonCalcEquals.setBackgroundDrawable(null);
                buttonCalcEquals.setBackgroundColor(getResources().getColor(R.color.material_accent));
                buttonCalcEquals.setText("="); //NON-NLS
                return;
            }
            Double tempVal = (double) Math.round(Double.parseDouble(calculatorResults.getText().toString()));
            Integer tempInt = tempVal.intValue();
            mCalculatorPresenter.relayClear();
            mCalculatorPresenter.relayNumbers(tempInt.toString());
            shouldSendToCalc = false;
            buttonCalcEquals.setBackgroundDrawable(null);
            buttonCalcEquals.setBackgroundColor(getResources().getColor(R.color.material_accent));
            buttonCalcEquals.setText("="); //NON-NLS
        }
    }

    private boolean calcCheckIsParenMismatch(String expression) {
        int openCount = 0;
        int closeCount = 0;
        for (char c : expression.toCharArray()){
            if(c == '(') {
                openCount++;
            } else if(c == ')'){
                closeCount++;
            }
        }
        return closeCount != openCount;
    }

    @OnClick(R.id.calcBackspace)
    public void onClickCalcBackspace(){
        if(calcWork.length() > 0) {
            calcWork = calcWork.substring(0, calcWork.length() - 1);
            calculatorWork.setText(calcWork);
        }
    }

    @OnClick(R.id.calcClear)
    public void onClickCalcClear(){
        if(calcWork.length() == 0){
            calculatorResults.setText("");
        }
        calculatorWork.setText("");
        calcWork = "";
    }

    public String calcCorrectParens(String expression){
        if(expression.length() == 0){
            return "";
        }
        String[] temp = expression.split("");
        ArrayList<String> chars = new ArrayList<>();
        Collections.addAll(chars, temp);
        for(int i = 1; i < chars.size() - 1; i++) {
            if(chars.get(i).equals("(") && (chars.get(i-1).matches("^[0-9]") || chars.get(i-1).equals(")"))){
                chars.add(i, "*");
                i++; //Prevents infinite loop
            }
        }
        for(int i = 0; i < chars.size() - 1; i++) {
            if(chars.get(i).equals(")") && (chars.get(i+1).matches("^[0-9]") || chars.get(i+1).equals("("))){
                chars.add(i+1, "*");
                i++; //Prevents infinite loop
            }
        }
        String corrected = "";
        for(String s : chars){
            corrected += s;
        }
        System.out.println(corrected);
        return corrected;
    }

}
