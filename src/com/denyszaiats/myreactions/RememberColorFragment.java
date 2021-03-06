package com.denyszaiats.myreactions;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.*;
import com.denyszaiats.myreactions.drawer.DrawCircle;
import com.denyszaiats.myreactions.drawer.DrawLine;
import com.denyszaiats.myreactions.drawer.DrawRect;
import com.denyszaiats.myreactions.drawer.DrawView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.zip.CheckedOutputStream;

public class RememberColorFragment extends Fragment {

    private Helper helper;
    private Context context;
    private Button buttonStart;
    private Button tryAgainButton;
    private Button nextLevelButton;
    private Button readyButton;
    private DrawCircle drawCircleNominative;
    private RelativeLayout areaView;
    private RelativeLayout areaViewAppear;
    private RelativeLayout areaColorAppear;
    private RelativeLayout scoreArea;
    private RelativeLayout lifeArea;
    private TextView textColorTimer;
    private TextView textColorScore;
    private TextView textLevel;
    private TextView textHighScore;
    private TextView textLife;
    private TextView textTitleRemColor;
    private ImageView buttonRefresh;
    private ImageView buttonHelp;
    private LinkedList<Integer> usedCoordinates;
    private LinkedList<DrawRect> listCreatedViews;
    private LinkedList<DrawLine> listCreatedLines;
    private int rectSize;
    private SharedPreferences prefs;
    private float rectX;
    private float rectY;
    private HashMap<Integer, Integer> colorMap;
    private Integer nominativeColor;
    private int score;
    private int highscore;
    private int genIndexColorRect;
    private int level = 1;
    private int time = 10;
    private int countShapes = 2;
    private int size = 105;
    private int life = 3;
    private int failClicks = 10;
    private LinkedList<Integer> listColor;
    private HashMap<Integer, String> mapCoord;
    private SharedPreferences.Editor editor;
    private CountDownTimer cT;
    private int maxX;
    private int maxY;
    private int highlevel;
    private String prefix;

    public RememberColorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();

        helper = new Helper();
        prefix = prefs.getString(Constants.LANG_PREFIX, "_en");
        boolean isChecked = prefs.getBoolean(Constants.REMEMBER_COLOR_FRAGMENT + "_CHECKED", false);
        editor.putString(Constants.FRAGMENT_NAME, Constants.REMEMBER_COLOR_FRAGMENT);
        editor.commit();
        if(!isChecked) {
            Intent i = new Intent(context,
                    GuideModalActivity.class);
            startActivity(i);
        }

        View rootView = inflater.inflate(R.layout.fragment_remember_color, container, false);

        areaView = (RelativeLayout) rootView.findViewById(R.id.areaRemColor);
        areaViewAppear = (RelativeLayout) rootView.findViewById(R.id.areaRemColorInternal);
        areaColorAppear = (RelativeLayout) rootView.findViewById(R.id.areaRemColorAppear);
        lifeArea = (RelativeLayout) rootView.findViewById(R.id.areaLifeRemColor);
        buttonStart = (Button) rootView.findViewById(R.id.startButtonRemColor);{
            buttonStart.setText(Helper.setStringFromResources(context, "but_start" + prefix));
        }
        nextLevelButton = (Button) rootView.findViewById(R.id.nextLevelButtonRemColor);{
            nextLevelButton.setText(Helper.setStringFromResources(context, "but_next_level" + prefix));
        }
        tryAgainButton = (Button) rootView.findViewById(R.id.tryAgainButtonRemColor);{
            tryAgainButton.setText(Helper.setStringFromResources(context, "but_try_again" + prefix));
        }
        readyButton = (Button) rootView.findViewById(R.id.readyButtonRemColor);{
            readyButton.setText(Helper.setStringFromResources(context, "but_ready" + prefix));
        }
        scoreArea = (RelativeLayout) rootView.findViewById(R.id.resultsAreaRemColor);
        textColorTimer = (TextView) rootView.findViewById(R.id.textTimerRemColor);
        textColorScore = (TextView) rootView.findViewById(R.id.textScoreRemColor);
        textLevel = (TextView) rootView.findViewById(R.id.textLevelRemColor);
        textLife = (TextView) rootView.findViewById(R.id.textLifeRemColor);
        buttonRefresh = (ImageView) rootView.findViewById(R.id.buttomRefreshRemColor);
        buttonHelp = (ImageView) rootView.findViewById(R.id.buttomHelpRemColor);
        textHighScore = (TextView) rootView.findViewById(R.id.textRemColorHighscores);
        textTitleRemColor = (TextView) rootView.findViewById(R.id.textTitleRemColor);{
            textTitleRemColor.setText(Helper.setStringFromResources(context, "title_rem_color_fragment" + prefix));
        }
        colorMap = new HashMap<Integer, Integer>();
        size = helper.getShapeStartSize(context);

        areaViewAppear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nextLevelButton.getVisibility() == View.INVISIBLE && tryAgainButton.getVisibility() == View.INVISIBLE && readyButton.getVisibility() == View.INVISIBLE) {
                    failClicks--;
                    if (failClicks == 0) {
                        lockShapes();
                        for (DrawView view : listCreatedViews) {
                            view.setAlpha(1.0f);
                        }
                        if(life>0) {
                            repeatLeve();
                        }
                        else {
                            tryAgainButton.setVisibility(View.VISIBLE);
                            tryAgainButton.setTextColor(Color.YELLOW);
                            textColorScore.setText(Helper.setStringFromResources(context, "score" + prefix) + String.valueOf(score));
                            editor.putBoolean(Constants.REM_COLOR_IS_FINISHED, true);
                            textColorTimer.setText(Helper.setStringFromResources(context, "game_over" + prefix));
                            if(score>highscore) {
                                highscore = score;
                                editor.putInt(Constants.REM_COLOR_HIGHSCORE, highscore);
                                textHighScore.setText(Helper.setStringFromResources(context, "high_score_home" + prefix) + String.valueOf(score));
                            }
                            if(level>highlevel) {
                                highlevel = level;
                                editor.putInt(Constants.REM_COLOR_HIGHLEVEL, level);
                            }
                            editor.commit();
                        }

                    } else if (failClicks == 3) {
                        TextView msg = new TextView(getActivity());
                        msg.setText(String.format(Helper.setStringFromResources(context, "clicks_attepmpts" + prefix), String.valueOf(failClicks)));
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
                }
            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runGame();
            }
        });

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score = 0;
                level = 1;
                countShapes = 2;
                time = 10;
                life = 3;
                size = helper.getShapeStartSize(context);
                drawCircleNominative.setVisibility(View.INVISIBLE);
                runGame();
            }
        });

        nextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawCircleNominative != null) {
                    drawCircleNominative.setVisibility(View.INVISIBLE);
                }
                time = 10;
                runGame();
            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView msg = new TextView(getActivity());
                msg.setText(Helper.setStringFromResources(context, "dialog_start_new_game_msg" + prefix));
                msg.setPadding(20, 10, 20, 10);
                msg.setGravity(Gravity.CENTER);
                msg.setTextSize(20);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle(Helper.setStringFromResources(context, "dialog_title_delete_rslt" + prefix));
                builder.setView(msg);

                builder.setPositiveButton(Helper.setStringFromResources(context, "dialog_yes" + prefix), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        if(cT != null) {
                            cT.cancel();
                        }
                        editor.putBoolean(Constants.REM_COLOR_IS_FINISHED, true);
                        level = 1;
                        size = helper.getShapeStartSize(context);
                        score = 0;
                        time = 10;
                        countShapes = 2;
                        life = 3;
                        runGame();
                    }

                });

                builder.setNegativeButton(Helper.setStringFromResources(context, "dialog_no" + prefix), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cT.cancel();
                readyButton.setVisibility(View.INVISIBLE);
                drawNominativeColor();
                for(DrawView view: listCreatedViews) {
                    view.setAlpha(0.0f);
                }
                unlockShapes();
            }
        });

        buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((readyButton.getVisibility() != View.VISIBLE) && (nextLevelButton.getVisibility() != View.VISIBLE)) {
                    if (listCreatedViews != null) {
                        if (life >= 2) {
                            life -= 2;
                            if(life < 2){
                                textLife.setTextColor(Color.RED);
                            }
                            //life--;
                            //score -= 25;

                            lockShapes();
                            textColorScore.setText(Helper.setStringFromResources(context, "score" + prefix) + String.valueOf(score));
                            textLife.setText(String.valueOf(life));
                            cT = new CountDownTimer(1000, 100) {

                                public void onTick(long millisUntilFinished) {
                                    for (DrawView view : listCreatedViews) {
                                        view.setAlpha(0.6f);
                                    }
                                }

                                public void onFinish() {
                                    for (DrawView view : listCreatedViews) {
                                        view.setAlpha(0.0f);
                                    }
                                    unlockShapes();
                                }
                            };
                            cT.start();

                        }
                        else {
                            textLife.setTextColor(Color.RED);
                        }
                    } else {
                        Toast.makeText(context, Helper.setStringFromResources(context, "continue_level" + prefix), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        highscore = prefs.getInt(Constants.REM_COLOR_HIGHSCORE, 0);
        highlevel = prefs.getInt(Constants.REM_COLOR_HIGHLEVEL, 0);
        textHighScore.setText(Helper.setStringFromResources(context, "high_score_home" + prefix) + String.valueOf(highscore));

        boolean isFinished = prefs.getBoolean(Constants.REM_COLOR_IS_FINISHED, true);
        if (!isFinished) {
            int tempScore = prefs.getInt(Constants.REM_COLOR_TEMP_SCORE, 0);
            int tempLevel = prefs.getInt(Constants.REM_COLOR_TEMP_LEVEL, 0);
            int tempLife  = prefs.getInt(Constants.REM_COLOR_TEMP_LIFE, 0);
            tryAgainButton.setVisibility(View.INVISIBLE);
            nextLevelButton.setVisibility(View.VISIBLE);
            buttonStart.setVisibility(View.INVISIBLE);
            scoreArea.setVisibility(View.VISIBLE);
            areaColorAppear.setVisibility(View.VISIBLE);
            textLevel.setVisibility(View.VISIBLE);
            textColorScore.setVisibility(View.VISIBLE);
            textColorTimer.setVisibility(View.VISIBLE);
            textHighScore.setVisibility(View.VISIBLE);
            buttonRefresh.setVisibility(View.VISIBLE);
            buttonHelp.setVisibility(View.VISIBLE);
            lifeArea.setVisibility(View.VISIBLE);
            textColorTimer.setText("00:10");
            textLevel.setText(Helper.setStringFromResources(context, "level" + prefix) + String.valueOf(tempLevel));
            textColorScore.setText(Helper.setStringFromResources(context, "score" + prefix) + String.valueOf(tempScore));
            textLife.setText(String.valueOf(tempLife));
            if(tempLife<2){
                textLife.setTextColor(Color.RED);
            }
            else {
                textLife.setTextColor(Color.WHITE);
            }
            level = tempLevel;
            score = tempScore;
            life = tempLife;
        }


        return rootView;
    }

    private void runGame() {
        failClicks = 10;
        countShapes = 2;
        removeView();
        removeGrid();
        editor.putBoolean(Constants.REM_COLOR_IS_CLICKABLE, true);
        editor.commit();
        countShapes = level + countShapes;
        boolean isSuccessPrevResults = prefs.getBoolean(Constants.IS_SUCCESS_RESULT_REM_COLOR, true);
        if(isSuccessPrevResults) {
            size = helper.getShapeStartSize(context);
            size = helper.getShapeSize(level, size, context);
        }
        colorMap.put(1, R.color.black);
        colorMap.put(2, R.color.dark_blue);
        colorMap.put(3, R.color.hot_red);
        colorMap.put(4, R.color.lime);
        colorMap.put(5, R.color.yellow);
        colorMap.put(6, R.color.pink);
        colorMap.put(7, R.color.cyan);
        colorMap.put(8, R.color.gray);
        colorMap.put(9, R.color.orange);
        colorMap.put(10, R.color.indigo);
        colorMap.put(11, R.color.dark_green);
        colorMap.put(12, R.color.brown);
        textColorScore.setText(Helper.setStringFromResources(context, "score" + prefix) + String.valueOf(score));
        tryAgainButton.setVisibility(View.INVISIBLE);
        nextLevelButton.setVisibility(View.INVISIBLE);
        buttonStart.setVisibility(View.INVISIBLE);
        scoreArea.setVisibility(View.VISIBLE);
        areaColorAppear.setVisibility(View.VISIBLE);
        textLevel.setVisibility(View.VISIBLE);
        lifeArea.setVisibility(View.VISIBLE);
        textHighScore.setVisibility(View.VISIBLE);
        buttonRefresh.setVisibility(View.VISIBLE);
        buttonHelp.setVisibility(View.VISIBLE);
        readyButton.setVisibility(View.VISIBLE);
        textLevel.setText(Helper.setStringFromResources(context, "level" + prefix) + String.valueOf(level));
        textLife.setText(String.valueOf(life));
        if(life<2){
            textLife.setTextColor(Color.RED);
        }
        else {
            textLife.setTextColor(Color.WHITE);
        }
        initShapes();
        lockShapes();
        cT = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                int va = (int) ((millisUntilFinished % 60000) / 1000);
                textColorTimer.setText("00:" + String.format("%02d", va));
                time--;
            }

            public void onFinish() {
                readyButton.setVisibility(View.INVISIBLE);
                drawNominativeColor();
                for(DrawView view: listCreatedViews) {
                    view.setAlpha(0.0f);
                }
                unlockShapes();
                textColorTimer.setText("00:00");

            }
        };
        cT.start();
    }

    private void lockShapes(){
        editor.putBoolean(Constants.REM_COLOR_IS_CLICKABLE, false);
        editor.commit();
    }

    private void unlockShapes(){
        editor.putBoolean(Constants.REM_COLOR_IS_CLICKABLE, true);
        editor.commit();
    }

    private void drawGrid(){
        listCreatedLines = new LinkedList<DrawLine>();
        for(Map.Entry<Integer, String> map: mapCoord.entrySet()){
            if(map.getValue().toString().startsWith("0,")){
                DrawLine lineView = new DrawLine(context);
                lineView.setBackgroundColor(getResources().getColor(R.color.silver));
                int point = Integer.parseInt(map.getValue().split(",")[1]);
                lineView.setStartPoint(new Point(0, point));
                lineView.setEndPoint(new Point(areaView.getWidth(), point));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        areaView.getWidth(), 3
                );
                lineView.setLayoutParams(params);
                lineView.setX(0);
                lineView.setY(point);
                listCreatedLines.add(lineView);
            }
            if(map.getValue().toString().endsWith(",0")){
                DrawLine lineView = new DrawLine(context);
                lineView.setBackgroundColor(getResources().getColor(R.color.silver));
                int point = Integer.parseInt(map.getValue().split(",")[0]);
                lineView.setStartPoint(new Point(point, 0));
                lineView.setEndPoint(new Point(point, areaView.getHeight()));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        3, areaView.getHeight()
                );
                lineView.setLayoutParams(params);
                lineView.setY(0);
                lineView.setX(point);
                listCreatedLines.add(lineView);
            }
        }

        DrawLine lineViewX = new DrawLine(context);
        lineViewX.setBackgroundColor(getResources().getColor(R.color.silver));
        lineViewX.setStartPoint(new Point(maxX, 0));
        lineViewX.setEndPoint(new Point(maxX, areaView.getHeight()));
        RelativeLayout.LayoutParams paramsX = new RelativeLayout.LayoutParams(
                3, areaView.getHeight()
        );
        lineViewX.setLayoutParams(paramsX);
        lineViewX.setY(0);
        lineViewX.setX(maxX - 2);
        listCreatedLines.add(lineViewX);

        DrawLine lineViewY = new DrawLine(context);
        lineViewY.setBackgroundColor(getResources().getColor(R.color.silver));
        lineViewY.setStartPoint(new Point(maxY, 0));
        lineViewY.setEndPoint(new Point(maxY, areaView.getWidth()));
        RelativeLayout.LayoutParams paramsY = new RelativeLayout.LayoutParams(
                areaView.getWidth(), 3
        );
        lineViewY.setLayoutParams(paramsY);
        lineViewY.setY(maxY - 2);
        lineViewY.setX(0);
        listCreatedLines.add(lineViewY);


        for(DrawView line: listCreatedLines){
            areaViewAppear.addView(line);
        }
    }

    private void removeGrid(){
        if (listCreatedLines != null) {
            for (DrawView line : listCreatedLines) {
                if(line != null)
                    areaViewAppear.removeView(line);
            }
        }
        areaView.setPadding(0,0,0,0);
    }

    private void drawShapes() {
        if(drawCircleNominative!=null) {
            areaColorAppear.removeView(drawCircleNominative);
        }
        listCreatedViews = new LinkedList<DrawRect>();
        listColor = new LinkedList<Integer>();
        usedCoordinates = new LinkedList<Integer>();
        generateMapCoordinates();

        if (countShapes >= mapCoord.size()) {
            countShapes = mapCoord.size();
        }
        for (int i = 0; i < countShapes; i++) {
            generateShapesParameters();
            DrawRect drawRect = new DrawRect(context);
            //drawRect.setLayoutParams(new ViewGroup.LayoutParams(rectSize, rectSize), );
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    rectSize - pxFromDp(1.5f), rectSize - pxFromDp(1.5f)
            );
            params.setMargins(pxFromDp(1.25f), pxFromDp(1.25f), pxFromDp(1.5f), pxFromDp(1.5f));
            drawRect.setLayoutParams(params);
            drawRect.setSideSize(rectSize);
            Random randColorRect = new Random();
            if (listColor.size() < colorMap.size()) {
                do {
                    genIndexColorRect = generateRandomInteger(1, colorMap.size(), randColorRect);
                }
                while (listColor.contains(genIndexColorRect));
            } else {
                genIndexColorRect = generateRandomInteger(1, colorMap.size(), randColorRect);
            }
            drawRect.setBackgroundColor(getResources().getColor(colorMap.get(genIndexColorRect)));
            drawRect.setX(rectX);
            drawRect.setY(rectY);
            listCreatedViews.add(drawRect);
            listColor.add(genIndexColorRect);
        }
        removeGrid();
        setPaddings();
        drawGrid();

        for (DrawRect drawRect : listCreatedViews) {
            do {
                areaViewAppear.addView(drawRect);
            }
            while (areaViewAppear.indexOfChild(drawRect) == -1);
            drawRect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isClickable = prefs.getBoolean(Constants.REM_COLOR_IS_CLICKABLE, true);
                    if (isClickable)
                        calculateResults(v);
                }
            });
        }

        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(200);
        areaViewAppear.startAnimation(animation);

    }

    private void drawNominativeColor(){
        try {
            nominativeColor = listColor.get(generateRandomInteger(0, listColor.size() - 1, new Random()));

            drawCircleNominative = new DrawCircle(context);
            drawCircleNominative.setLayoutParams(new RelativeLayout.LayoutParams(pxFromDp(40), pxFromDp(40)));
            drawCircleNominative.setSideSize(pxFromDp(40));
            drawCircleNominative.setX(areaColorAppear.getWidth() / 2 - pxFromDp(25));
            drawCircleNominative.setY(0);
            drawCircleNominative.setBackgroundColor(getResources().getColor(colorMap.get(nominativeColor)));
            areaColorAppear.addView(drawCircleNominative);
        }
        catch (IllegalStateException ex){
            Log.d("[Exception]", ex.toString());
        }
    }

    public void setPaddings(){
        int paddingX = (areaView.getWidth() - maxX)/2;
        int paddingY = (areaView.getHeight()- maxY)/2;

        areaView.setPadding(paddingX, paddingY, paddingX, paddingY);
    }

    private void calculateResults(View v) {
        ColorDrawable color = (ColorDrawable) v.getBackground();
        if (color.getColor() == getResources().getColor(colorMap.get(nominativeColor))) {
            if(time == 0) {
                score += 10;
            }
            else{
                score += 10 * time;
            }
            level++;
            life++;
            textLevel.setText(Helper.setStringFromResources(context, "level" + prefix) + String.valueOf(level));
            textColorScore.setText(Helper.setStringFromResources(context, "score" + prefix) + String.valueOf(score));
            nextLevelButton.setText(Helper.setStringFromResources(context, "but_next_level" + prefix));
            nextLevelButton.setTextColor(Color.WHITE);
            nextLevelButton.setVisibility(View.VISIBLE);
            editor.putBoolean(Constants.REM_COLOR_IS_FINISHED, false);
            editor.putInt(Constants.REM_COLOR_TEMP_LEVEL, level);
            editor.putInt(Constants.REM_COLOR_TEMP_SCORE, score);
            editor.putInt(Constants.REM_COLOR_TEMP_LIFE, life);
            editor.putBoolean(Constants.IS_SUCCESS_RESULT_REM_COLOR, true);
        }
        else {
            if(life > 0){
                repeatLeve();
            }
            else {
                tryAgainButton.setVisibility(View.VISIBLE);
                tryAgainButton.setTextColor(Color.YELLOW);
                textColorScore.setText(Helper.setStringFromResources(context, "score" + prefix) + String.valueOf(score));
                editor.putBoolean(Constants.REM_COLOR_IS_FINISHED, true);
                textColorTimer.setText(Helper.setStringFromResources(context, "game_over" + prefix));
            }
        }
        if(score>highscore) {
            highscore = score;
            editor.putInt(Constants.REM_COLOR_HIGHSCORE, highscore);
            textHighScore.setText(Helper.setStringFromResources(context, "high_score_home" + prefix) + String.valueOf(score));
        }
        if(level>highlevel) {
            highlevel = level;
            editor.putInt(Constants.REM_COLOR_HIGHLEVEL, level);
        }
        lockShapes();
        for(DrawView view: listCreatedViews) {
            view.setAlpha(1.0f);
        }
        cT.cancel();
        System.gc();
    }

    private void repeatLeve(){
        life--;
        textLevel.setText(Helper.setStringFromResources(context, "level" + prefix) + String.valueOf(level));
        textColorScore.setText(Helper.setStringFromResources(context, "score" + prefix) + String.valueOf(score));
        textLife.setText(String.valueOf(life));
        if(life<2){
            textLife.setTextColor(Color.RED);
        }
        else {
            textLife.setTextColor(Color.WHITE);
        }
        nextLevelButton.setText(Helper.setStringFromResources(context, "but_repeat_level" + prefix));
        nextLevelButton.setTextColor(Color.RED);
        nextLevelButton.setVisibility(View.VISIBLE);
        editor.putBoolean(Constants.REM_COLOR_IS_FINISHED, false);
        editor.putInt(Constants.REM_COLOR_TEMP_LEVEL, level);
        editor.putInt(Constants.REM_COLOR_TEMP_SCORE, score);
        editor.putInt(Constants.REM_COLOR_TEMP_LIFE, life);
        editor.putBoolean(Constants.IS_SUCCESS_RESULT_REM_COLOR, false);
    }

    private int pxFromDp(float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    private void initShapes() {
        removeView();
        removeGrid();
        drawShapes();
    }

    private void generateMapCoordinates() {
        //Random random = new Random();
        int generatedSize = pxFromDp(size);//generateRandomInteger(150, 200, random);
        rectSize = generatedSize;
        maxX = 0;
        maxY = 0;
        int k = 1;
        mapCoord = new HashMap<Integer, String>();
        for (int i = 0; i < areaView.getWidth(); i++) {
            for (int j = 0; j < areaView.getHeight(); j++) {
                if ((i % generatedSize == 0) && (j % generatedSize == 0)) {
                    if (((i + generatedSize) < areaView.getWidth()) && ((j + generatedSize) < areaView.getHeight())) {
                        if((i+generatedSize) > maxX) {
                            maxX = (i+generatedSize);
                        }
                        if((j+generatedSize) > maxY) {
                            maxY = (j+generatedSize);
                        }
                        mapCoord.put(k, i + "," + j);
                        k++;
                    }
                }
            }
        }

    }

    private void generateShapesParameters() {
        Random randomNumberRect = new Random();
        int randomGeneratedNumberRect;
        do {
            randomGeneratedNumberRect = generateRandomInteger(1, mapCoord.size(), randomNumberRect);
        }
        while (usedCoordinates.contains(randomGeneratedNumberRect));

        rectX = Float.parseFloat(mapCoord.get(randomGeneratedNumberRect).split(",")[0]);
        rectY = Float.parseFloat(mapCoord.get(randomGeneratedNumberRect).split(",")[1]);

        usedCoordinates.add(randomGeneratedNumberRect);
    }


    private int generateRandomInteger(int aStart, int aEnd, Random aRandom) {
        if (aStart > aEnd) {
            int tempEnd = aEnd;
            int tempStart = aStart;

            aEnd = tempStart;
            aStart = tempEnd;
        }
        long range = (long) aEnd - (long) aStart + 1;
        long fraction = (long) (range * aRandom.nextDouble());
        return (int) (fraction + aStart);
    }

    private void removeView() {
        if (listCreatedViews != null) {
            for (DrawView view : listCreatedViews) {
                if (view != null) {
                    areaViewAppear.removeView(view);
                }
            }
        }
    }
}
