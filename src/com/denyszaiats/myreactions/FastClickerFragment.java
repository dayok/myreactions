package com.denyszaiats.myreactions;

import java.util.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.*;
import com.denyszaiats.myreactions.ChartView.ChartView;
import com.denyszaiats.myreactions.ChartView.LinearSeries;

public class FastClickerFragment extends Fragment {

	/*
     * Add functionality for each fingers and both hands
	 */

    private TextView chronometer;
    private TextView resultsView;
    private TextView textTitleFastClicker;
    private TextView labelClicks;
    private TextView labelSeconds;
    private Button startTimer;
    private Button tryAgain;
    private Button handButton;
    private Button fingerButton;
    private ImageView tapButton;
    private ImageView leftHand;
    private ImageView rightHand;
    private ImageView fingerThumb;
    private ImageView fingerIndex;
    private ImageView fingerMiddle;
    private ImageView fingerRing;
    private ImageView fingerPinky;
    private RelativeLayout scrollView;
    private RelativeLayout mainArea;
    private Context context;
    private LinkedList<String> results;
    private SharedPreferences prefs;
    private Editor editor;
    private int summaryClicks;
    private int maxTempClicks;
    private String hand = Constants.RIGHT_HAND;
    private String finger = Constants.INDEX_FINGER;
    private LinearSeries series;
    private String prefix;

    public FastClickerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_fast_clicker,
                container, false);
        context = container.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefix = prefs.getString(Constants.LANG_PREFIX, "_en");

        editor = prefs.edit();
        boolean isChecked = prefs.getBoolean(Constants.FAST_CLICKER_FRAGMENT + "_CHECKED", false);
        editor.putString(Constants.FRAGMENT_NAME, Constants.FAST_CLICKER_FRAGMENT);
        editor.commit();

        if(!isChecked) {
            Intent i = new Intent(context,
                    GuideModalActivity.class);
            startActivity(i);
        }

        summaryClicks = Integer.valueOf(prefs.getString(Constants.SUMMARY_CLICKS, "0"));
        chronometer = (TextView) rootView
                .findViewById(R.id.chronometerFasterClicker);
        resultsView = (TextView) rootView
                .findViewById(R.id.resultsFasterClicker);

        // Base
        startTimer = (Button) rootView.findViewById(R.id.startButton);{
            startTimer.setText(Helper.setStringFromResources(context, "but_start" + prefix));
        }
        tryAgain = (Button) rootView.findViewById(R.id.tryAgainButton);{
            tryAgain.setText(Helper.setStringFromResources(context, "but_try_again" + prefix));
        }
        handButton = (Button) rootView.findViewById(R.id.handButton);{
            handButton.setText(Helper.setStringFromResources(context, "choose" + prefix) + " " + Helper.setStringFromResources(context, "hand" + prefix));
        }
        fingerButton = (Button) rootView.findViewById(R.id.fingerButton);{
            fingerButton.setText(Helper.setStringFromResources(context, "choose" + prefix) + " " + Helper.setStringFromResources(context, "finger" + prefix));
        }
        tapButton = (ImageView) rootView.findViewById(R.id.imageTapButton);
        scrollView = (RelativeLayout) rootView.findViewById(R.id.scrollViewResults);
        mainArea = (RelativeLayout) rootView.findViewById(R.id.areaMainFastClicker);
        textTitleFastClicker = (TextView) rootView.findViewById(R.id.textTitleFastClicker);{
            textTitleFastClicker.setText(Helper.setStringFromResources(context, "title_fast_clicker_fragment" + prefix));
        }
        labelClicks = (TextView) rootView.findViewById(R.id.fastLabelClicks);{
            labelClicks.setText(Helper.setStringFromResources(context, "graph_lbl_clicks" + prefix));
        }
        labelSeconds = (TextView) rootView.findViewById(R.id.fastLabelSeconds);{
            labelSeconds.setText(Helper.setStringFromResources(context, "graph_lbl_seconds" + prefix));
        }
        // Hands
        leftHand = (ImageView) rootView.findViewById(R.id.imageHandLeft);
        rightHand = (ImageView) rootView.findViewById(R.id.imageRightHand);
        // Fingers
        fingerThumb = (ImageView) rootView.findViewById(R.id.imageThumbFinger);
        fingerIndex = (ImageView) rootView.findViewById(R.id.imageIndexFinger);
        fingerMiddle = (ImageView) rootView.findViewById(R.id.imageMiddleFinger);
        fingerRing = (ImageView) rootView.findViewById(R.id.imageRingFinger);
        fingerPinky = (ImageView) rootView.findViewById(R.id.imagePinkyFinger);
        final ChartView chartViewLatestResults = (ChartView) rootView.findViewById(R.id.chartViewLatestResults);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        final int height = displaymetrics.heightPixels;
        int size;
        if (height < width){
            size = height;
        }
        else {
            size = width;
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(size - pxFromDp(30), size - pxFromDp(30));
        layoutParams.setMargins(pxFromDp(10), pxFromDp(0), pxFromDp(10), pxFromDp(10));
        layoutParams.addRule(RelativeLayout.BELOW, R.id.chronometerFasterClicker);
        tapButton.setLayoutParams(layoutParams);
        results = new LinkedList<String>();
        startTimer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(fingerButton.getText().toString().contains(Helper.setStringFromResources(context, "choose" + prefix)) || handButton.getText().toString().contains(Helper.setStringFromResources(context, "choose" + prefix))){
                    TextView msg = new TextView(getActivity());
                    msg.setText(Helper.setStringFromResources(context, "dialog_msg_fast_clicker" + prefix));
                    msg.setPadding(20, 10, 20, 10);
                    msg.setGravity(Gravity.CENTER);
                    msg.setTextSize(20);
                    new AlertDialog.Builder(getActivity())
                            .setView(msg)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
               else if (leftHand.getVisibility() == View.INVISIBLE && fingerThumb.getVisibility() == View.INVISIBLE) {
                    v.setVisibility(View.INVISIBLE);
                    doAnimation(tapButton);
                    chronometer.setVisibility(View.VISIBLE);
                    CountDownTimer cT = new CountDownTimer(10010, 1000) {

                        public void onTick(long millisUntilFinished) {
                            int va = (int) ((millisUntilFinished % 60000) / 1000);
                            chronometer.setText("00:" + String.format("%02d", va));
                        }

                        public void onFinish() {
                            ScaleAnimation animation = new ScaleAnimation(0.0f,
                                    1.0f, 0.0f, 1.0f);
                            animation.setDuration(1000);
                            chronometer.setText("Finish");
                            chronometer.startAnimation(animation);
                            tapButton.setVisibility(View.INVISIBLE);
                            resultsView.setText(String.valueOf(results.size()));
                            resultsView.setVisibility(View.VISIBLE);
                            tryAgain.setVisibility(View.VISIBLE);
                            summaryClicks += results.size();
                            editor.putString(Constants.SUMMARY_CLICKS, String.valueOf(summaryClicks));
                            editor.putString(String.format("%s-%s-%s", hand, finger, String.valueOf(System.currentTimeMillis() / 1000)), results.toString());
                            editor.commit();

                            scrollView.setVisibility(View.VISIBLE);

                            series = new LinearSeries();

                            int seconds = setSeriesForLatestResults();
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height/4);
                            params.setMargins(pxFromDp(5), pxFromDp(8), pxFromDp(5), pxFromDp(10));
                            params.addRule(RelativeLayout.BELOW, R.id.resultsFasterClicker);
                            chartViewLatestResults.setLayoutParams(params);
                            ChartBuilder.buildChart(series, chartViewLatestResults, 3, seconds - 2, new ValueLabelAdapter(context, ValueLabelAdapter.LabelOrientation.VERTICAL), new ValueLabelAdapter(context, ValueLabelAdapter.LabelOrientation.HORIZONTAL));



                        }
                    };
                    cT.start();

                }
            }
        });

        tapButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Date date = new Date();
                long date = System.currentTimeMillis() / 1000;
                results.add(String.valueOf(date));
            }
        });

        tryAgain.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startTimer.setVisibility(View.VISIBLE);
                resultsView.setVisibility(View.INVISIBLE);
                chronometer.setVisibility(View.INVISIBLE);
                results.clear();
                scrollView.setVisibility(View.INVISIBLE);
            }
        });

        handButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (startTimer.getVisibility() == View.VISIBLE && (fingerThumb.getVisibility() != View.VISIBLE)) {
                    leftHand.setVisibility(View.VISIBLE);
                    rightHand.setVisibility(View.VISIBLE);
                    doAnimation(leftHand);
                    doAnimation(rightHand);
                }
                else {
                    doAnimation(tryAgain);
                }
            }
        });

        leftHand.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hand = Constants.LEFT_HAND;
                leftHand.setVisibility(View.INVISIBLE);
                rightHand.setVisibility(View.INVISIBLE);
                handButton.setText(Helper.setStringFromResources(context, "left_hand" + prefix) + " " + Helper.setStringFromResources(context, "hand" + prefix).replace("ку","ка"));
            }
        });

        rightHand.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                hand = Constants.RIGHT_HAND;
                leftHand.setVisibility(View.INVISIBLE);
                rightHand.setVisibility(View.INVISIBLE);
                handButton.setText(Helper.setStringFromResources(context, "right_hand" + prefix) + " " + Helper.setStringFromResources(context, "hand" + prefix).replace("ку","ка"));
            }
        });

        fingerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (startTimer.getVisibility() == View.VISIBLE && (leftHand.getVisibility() != View.VISIBLE)) {
                    setFingersVisibility(View.VISIBLE);
                    doAnimation(fingerThumb);
                    doAnimation(fingerIndex);
                    doAnimation(fingerMiddle);
                    doAnimation(fingerRing);
                    doAnimation(fingerPinky);
                }
                else {
                    doAnimation(tryAgain);
                }
            }
        });

        fingerThumb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finger = Constants.THUMB_FINGER;
                setFingersVisibility(View.INVISIBLE);
                fingerButton.setText(Helper.setStringFromResources(context, "thumb_finger" + prefix) + " " + Helper.setStringFromResources(context, "finger" + prefix));
            }
        });

        fingerIndex.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finger = Constants.INDEX_FINGER;
                setFingersVisibility(View.INVISIBLE);
                fingerButton.setText(Helper.setStringFromResources(context, "index_finger" + prefix) + " " + Helper.setStringFromResources(context, "finger" + prefix));
            }
        });

        fingerMiddle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finger = Constants.MIDDLE_FINGER;
                setFingersVisibility(View.INVISIBLE);
                fingerButton.setText(Helper.setStringFromResources(context, "middle_finger" + prefix) + " " + Helper.setStringFromResources(context, "finger" + prefix));
            }
        });

        fingerRing.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finger = Constants.RING_FINGER;
                setFingersVisibility(View.INVISIBLE);
                fingerButton.setText(Helper.setStringFromResources(context, "ring_finger" + prefix) + " " + Helper.setStringFromResources(context, "finger" + prefix));
            }
        });

        fingerPinky.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finger = Constants.PINKY_FINGER;
                setFingersVisibility(View.INVISIBLE);
                fingerButton.setText(Helper.setStringFromResources(context, "pinky_finger" + prefix) + " " + Helper.setStringFromResources(context, "finger" + prefix));
            }
        });

        return rootView;
    }

    private int setSeriesForLatestResults() {
        String filter = hand + "-" + finger;
        Map<String, ?> allEntries = prefs.getAll();
        HashSet<String> set;
        LinkedList<String> list = new LinkedList<String>();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        int k = 0;

        for (String s : results.toString().split(",")) {
            list.add(s.replace("]", "").replace("[", "").trim());
        }
        Collections.sort(list);
        set = new HashSet<String>(list);
        LinkedList<String> newSet = new LinkedList<String>(set);
        Collections.sort(newSet);
        for (int n = 0; n < newSet.size(); n++) {
            int i = 0;
            String s = newSet.get(n);
            for (int m = 0; m < list.size(); m++) {
                if (list.get(m).toString().equals(s)) {
                    i++;
                }
            }
            map.put(s, i);
        }
        int j = 0;

        SortedSet<String> keys = new TreeSet<String>(map.keySet());
        for (String key : keys) {
            j++;
            series.addPoint(new LinearSeries.LinearPoint(j, map.get(key)));
            k++;
        }

        if (k == 0) {
            series.addPoint(new LinearSeries.LinearPoint(0, 0));
        }
        return map.size();
    }

    private void doAnimation(View image) {
        image.setVisibility(View.VISIBLE);
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        image.setAnimation(animation);
    }

    private void setFingersVisibility(int visibility) {
        fingerThumb.setVisibility(visibility);
        fingerIndex.setVisibility(visibility);
        fingerMiddle.setVisibility(visibility);
        fingerRing.setVisibility(visibility);
        fingerPinky.setVisibility(visibility);
    }

    private void sleep(float sec) {
        try {
            Thread.sleep((long) (sec * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int pxFromDp(float dp)
    {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}
