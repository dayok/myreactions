package com.denyszaiats.myreactions;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class GuideModalActivity extends Activity{

    private CheckBox dontShowAgain;
    private TextView textGuide;
    private SharedPreferences prefs;
    private Button closeGuide;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modal_guide);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        dontShowAgain = (CheckBox) findViewById(R.id.dontShowAgain);
        textGuide = (TextView) findViewById(R.id.textGuide);
        closeGuide = (Button) findViewById(R.id.buttonGuideOk);
        String guideText = "";

        final String parentFragmentName = prefs.getString(Constants.FRAGMENT_NAME, "");

        if(parentFragmentName.equals(Constants.USER_FRAGMENT)){
            guideText = "   This is main window where user could monitor all results and activity of this application.\n" +
                    "   User is able to choose Finger and Hand to look at results of clicking.\n" +
                    "   It's possible to monitor best, worth results and clicking results during long period of time.\n" +
                    "   It will help user to understand  - are results better or worth with each game.\n" +
                    "   Also it's possible to see high score results for another game-test of this application and publish results on Facebook";
        }
        else if(parentFragmentName.equals(Constants.FAST_CLICKER_FRAGMENT)){
            guideText = "   Fast clicker game. You need to so many clicks as You can per 10 seconds.\n" +
                    "   For this You need to choose Finger and Hand and click START.\n" +
                    "   You will see the chart of Your clicks during Your last session. You will be able to see how much clicks per each seconds You did.";
        }
        else if(parentFragmentName.equals(Constants.CHOOSE_COLOR_FRAGMENT)){
            guideText = "   In this game You need to recognize and click on rectangle with appropriate color as soon as You possible. Faster You will do it - more score You will have.\n" +
                    "   With each level amount of rectangles will be increased and time of appearing will be decreased.\n" +
                    "   So, You need to earn a lot of scores in the beginning levels.\n" +
                    "   You need to collect not less than 10 scores per 1 level.\n" +
                    "   Good luck!";
        }
        else if(parentFragmentName.equals(Constants.REMEMBER_COLOR_FRAGMENT)){
            guideText = "In this game You have 10 seconds to remember colors and their places on the screen. After this 10 seconds rectangles will be disappeared" +
                    "and You need to click on correct place where this shape was located. If You click on correct place - You will earn 10 points +1 life.\n" +
                    "   You can click button 'Ready' and not to wait 10 seconds. Then You will earn 10 x points from left seconds and +1 life.\n" +
                    "   If You will click on incorrect place - Your life will be decreased -1.\n" +
                    "   When life will be left - game is finished.\n" +
                    "   Each levels amount of rectangles will be increased.\n" +
                    "   Good luck!";

        }

        closeGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dontShowAgain.isChecked()) {
                    editor.putBoolean(parentFragmentName + "_CHECKED", true);
                    editor.commit();
                }
               onBackPressed();
            }
        });

        textGuide.setText(guideText);
    }
}