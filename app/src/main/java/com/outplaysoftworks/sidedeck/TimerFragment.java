package com.outplaysoftworks.sidedeck;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimerFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if(currentTimeInSeconds == 0){
                timerFinished();
                return;
            }
            decrementTime();
        }
    };

    private void decrementTime() {
        currentTimeInSeconds--;
        picker.setValue(currentTimeInSeconds);

    }

    private void timerFinished() {
        timerRunning = false;
        timer.cancel();
    }

    private View view;
    @BindView(R.id.picker)
    HoloCircleSeekBar picker;
    @BindView(R.id.buttonStopTimer)
    Button stopTimer;
    @BindView(R.id.buttonStartTimer)
    Button startTimer;
    @BindView(R.id.textTime)
    TextView textTime;

    static Integer[] time = {0,0};

    private Integer currentTimeInSeconds;
    private boolean timerRunning;
    public void startTimer(){
        initializeTimerTask();
    }

    public void stopTimer(){
        timerRunning = false;
        timer.cancel();
    }

    public void initializeTimerTask(){
        timerRunning = true;
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }
    private void getTimeFromMilliseconds(){
        time[0] = currentTimeInSeconds / 60;
        time[1] = currentTimeInSeconds % 60;
        String theTime = time[0] + ":" + time[1];
        textTime.setText(theTime);
    }
    public void nothing(){
        picker.setOnSeekBarChangeListener(new HoloCircleSeekBar.OnCircleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(HoloCircleSeekBar holoCircleSeekBar, int i, boolean b) {
                currentTimeInSeconds = holoCircleSeekBar.getValue();
            }

            @Override
            public void onStartTrackingTouch(HoloCircleSeekBar holoCircleSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(HoloCircleSeekBar holoCircleSeekBar) {
                currentTimeInSeconds = holoCircleSeekBar.getValue();
                startTimer();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_timer, container, false);
        ButterKnife.bind(this, view);
        nothing();
        return view;
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
