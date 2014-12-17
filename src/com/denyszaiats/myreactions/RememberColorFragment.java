package com.denyszaiats.myreactions;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.denyszaiats.myreactions.drawer.DrawCircle;
import com.denyszaiats.myreactions.drawer.DrawLine;
import com.denyszaiats.myreactions.drawer.DrawRect;
import com.denyszaiats.myreactions.drawer.DrawView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class RememberColorFragment extends Fragment {

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
    private TextView textColorTimer;
    private TextView textColorScore;
    private TextView textLevel;
    private TextView textHighScore;
    private ImageView buttonRefresh;
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
    private LinkedList<Integer> listColor;
    private HashMap<Integer, String> mapCoord;
    private SharedPreferences.Editor editor;
    private CountDownTimer cT;

    public RememberColorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();

        View rootView = inflater.inflate(R.layout.fragment_remember_color, container, false);

        areaView = (RelativeLayout) rootView.findViewById(R.id.areaRemColor);
        areaViewAppear = (RelativeLayout) rootView.findViewById(R.id.areaRemColorInternal);
        areaColorAppear = (RelativeLayout) rootView.findViewById(R.id.areaRemColorAppear);
        buttonStart = (Button) rootView.findViewById(R.id.startButtonRemColor);
        nextLevelButton = (Button) rootView.findViewById(R.id.nextLevelButtonRemColor);
        tryAgainButton = (Button) rootView.findViewById(R.id.tryAgainButtonRemColor);
        readyButton = (Button) rootView.findViewById(R.id.readyButtonRemColor);
        scoreArea = (RelativeLayout) rootView.findViewById(R.id.resultsAreaRemColor);
        textColorTimer = (TextView) rootView.findViewById(R.id.textTimerRemColor);
        textColorScore = (TextView) rootView.findViewById(R.id.textScoreRemColor);
        textLevel = (TextView) rootView.findViewById(R.id.textLevelRemColor);
        buttonRefresh = (ImageView) rootView.findViewById(R.id.buttomRefreshRemColor);
        textHighScore = (TextView) rootView.findViewById(R.id.textRemColorHighscores);
        colorMap = new HashMap<Integer, Integer>();

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score = 0;
                level = 1;
                countShapes = 2;
                size = 100;
                runGame();
            }
        });

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                score = 0;
                level = 1;
                countShapes = 2;
                size = 100;
                drawCircleNominative.setVisibility(View.INVISIBLE);
                runGame();
            }
        });

        nextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawCircleNominative.setVisibility(View.INVISIBLE);
                runGame();
            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cT.cancel();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Confirmation");
                builder.setMessage("Do You really want to start new game?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        level = 1;
                        size = 105;
                        score = 0;
                        countShapes = 2;
                        runGame();
                    }

                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

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
                areaViewAppear.setAlpha(0.0f);

                listCreatedLines = new LinkedList<DrawLine>();
                for(Map.Entry<Integer, String> map: mapCoord.entrySet()){
                    if(map.getValue().toString().startsWith("0,")){
                        DrawLine lineView = new DrawLine(context);
                        lineView.setBackgroundColor(getResources().getColor(R.color.silver));
                        int point = Integer.parseInt(map.getValue().split(",")[1]);
                        lineView.setStartPoint(new Point(0, point));
                        lineView.setEndPoint(new Point(areaViewAppear.getWidth(), point));
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                areaViewAppear.getWidth(), 4
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
                        lineView.setEndPoint(new Point(point, areaViewAppear.getHeight()));
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                4, areaViewAppear.getHeight()
                        );
                        lineView.setLayoutParams(params);
                        lineView.setY(0);
                        lineView.setX(point);
                        listCreatedLines.add(lineView);
                    }
                }

                for(DrawView line: listCreatedLines){
                    areaViewAppear.addView(line);
                }
            }
        });

        highscore = prefs.getInt(Constants.REM_COLOR_HIGHSCORE, 0);
        textHighScore.setText("High score: " + String.valueOf(highscore));

        boolean isFinished = prefs.getBoolean(Constants.REM_COLOR_IS_FINISHED, true);
        if (!isFinished) {
            int tempScore = prefs.getInt(Constants.REM_COLOR_TEMP_SCORE, 0);
            int tempLevel = prefs.getInt(Constants.REM_COLOR_TEMP_LEVEL, 0);
            tryAgainButton.setVisibility(View.INVISIBLE);
            nextLevelButton.setVisibility(View.VISIBLE);
            buttonStart.setVisibility(View.INVISIBLE);
            scoreArea.setVisibility(View.VISIBLE);
            areaColorAppear.setVisibility(View.VISIBLE);
            textLevel.setVisibility(View.VISIBLE);
            textColorScore.setVisibility(View.VISIBLE);
            textColorTimer.setVisibility(View.VISIBLE);
            textHighScore.setVisibility(View.VISIBLE);
            textColorTimer.setText("Level done!");
            textLevel.setText("Level " + String.valueOf(tempLevel));
            textColorScore.setText("Score: " + String.valueOf(tempScore));
            level = tempLevel;
            score = tempScore;
        }

        return rootView;
    }

    private void runGame() {
        removeView();
        editor.putBoolean(Constants.REM_COLOR_IS_CLICKABLE, true);
        editor.commit();
        countShapes = level + countShapes;
        if ((size - level * 5) >= 50) {
            size = size - level * 5;
        } else {
            size = 50;
        }
        colorMap.put(1, Color.BLACK);
        colorMap.put(2, Color.BLUE);
        colorMap.put(3, Color.RED);
        colorMap.put(4, Color.GREEN);
        colorMap.put(5, Color.YELLOW);
        colorMap.put(6, Color.MAGENTA);
        colorMap.put(7, Color.CYAN);
        colorMap.put(8, Color.GRAY);
        textColorScore.setText("Score: " + String.valueOf(score));
        tryAgainButton.setVisibility(View.INVISIBLE);
        nextLevelButton.setVisibility(View.INVISIBLE);
        buttonStart.setVisibility(View.INVISIBLE);
        scoreArea.setVisibility(View.VISIBLE);
        areaColorAppear.setVisibility(View.VISIBLE);
        textLevel.setVisibility(View.VISIBLE);
        textHighScore.setVisibility(View.VISIBLE);
        buttonRefresh.setVisibility(View.VISIBLE);
        readyButton.setVisibility(View.VISIBLE);
        textLevel.setText("Level " + String.valueOf(level));
        initShapes();

        cT = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                int va = (int) ((millisUntilFinished % 60000) / 1000);
                textColorTimer.setText("00:" + String.format("%02d", va));
                time--;
            }

            public void onFinish() {
                //areaColorAppear.setVisibility(View.INVISIBLE);
                editor.putBoolean(Constants.REM_COLOR_IS_CLICKABLE, false);

                if (score >= 10 * level) {
                    nextLevelButton.setVisibility(View.VISIBLE);
                    level++;
                    countShapes = 2;
                    size = 100;
                    editor.putBoolean(Constants.REM_COLOR_IS_FINISHED, false);
                    textColorTimer.setText("Level done!");
                } else {
                    textColorTimer.setText("Finish!");
                    buttonRefresh.setVisibility(View.INVISIBLE);
                    tryAgainButton.setVisibility(View.VISIBLE);
                    highscore = prefs.getInt(Constants.REM_COLOR_HIGHSCORE, 0);
                    if (score > highscore) {
                        highscore = score;
                    }
                    textHighScore.setText("High score: " + String.valueOf(highscore));
                    editor.putInt(Constants.REM_COLOR_HIGHSCORE, highscore);
                    editor.putBoolean(Constants.REM_COLOR_IS_FINISHED, true);
                    level = 1;
                    countShapes = 2;
                    score = 0;
                    size = 100;
                }
                editor.putInt(Constants.REM_COLOR_TEMP_LEVEL, level);
                editor.putInt(Constants.REM_COLOR_TEMP_SCORE, score);
                editor.commit();

            }
        };
        cT.start();
    }

    private void drawShapes() {
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
                    rectSize, rectSize
            );
            params.setMargins(5, 5, 5, 5);
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
            drawRect.setBackgroundColor(colorMap.get(genIndexColorRect));
            drawRect.setX(rectX);
            drawRect.setY(rectY);
            listCreatedViews.add(drawRect);
            listColor.add(genIndexColorRect);
        }

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
        nominativeColor = listColor.get(generateRandomInteger(0, listColor.size() - 1, new Random()));

        drawCircleNominative = new DrawCircle(context);
        drawCircleNominative.setLayoutParams(new RelativeLayout.LayoutParams(pxFromDp(40), pxFromDp(40)));
        drawCircleNominative.setSideSize(pxFromDp(40));
        drawCircleNominative.setX(areaColorAppear.getWidth() / 2 - pxFromDp(25));
        drawCircleNominative.setY(0);
        drawCircleNominative.setBackgroundColor(colorMap.get(nominativeColor));
        areaColorAppear.addView(drawCircleNominative);
    }

    private void calculateResults(View v) {
        ColorDrawable color = (ColorDrawable) v.getBackground();
        if (color.getColor() == colorMap.get(nominativeColor)) {
            score += time;
            textColorScore.setText("Score: " + String.valueOf(score));
            nextLevelButton.setVisibility(View.VISIBLE);
        }
        else {
            tryAgainButton.setVisibility(View.VISIBLE);
            textColorScore.setText("Score: " + String.valueOf(score));
        }
        areaViewAppear.setAlpha(1.0f);
        System.gc();
    }

    private int pxFromDp(float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    private void initShapes() {
        removeView();
        drawShapes();
    }

    private void generateMapCoordinates() {
        //Random random = new Random();
        int generatedSize = pxFromDp(size);//generateRandomInteger(150, 200, random);
        rectSize = generatedSize;

        int k = 1;
        mapCoord = new HashMap<Integer, String>();
        for (int i = 0; i < areaViewAppear.getWidth(); i++) {
            for (int j = 0; j < areaViewAppear.getHeight(); j++) {
                if ((i % generatedSize == 0) && (j % generatedSize == 0)) {
                    if (((i + generatedSize) < areaViewAppear.getWidth()) && ((j + generatedSize) < areaViewAppear.getHeight())) {
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
