package com.denyszaiats.myreactions;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.denyszaiats.myreactions.ChartView.LabelAdapter;

public class ValueLabelAdapter extends LabelAdapter {
    public enum LabelOrientation {
        HORIZONTAL, VERTICAL
    }

    private Context mContext;
    private LabelOrientation mOrientation;

    public ValueLabelAdapter(Context context, LabelOrientation orientation) {
        mContext = context;
        mOrientation = orientation;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView labelTextView;
        if (convertView == null) {
            convertView = new TextView(mContext);
        }

        labelTextView = (TextView) convertView;

        int gravity = Gravity.CENTER;
        if (mOrientation == LabelOrientation.VERTICAL) {
            if (position == 0) {
                gravity = Gravity.BOTTOM | Gravity.RIGHT;
            } else if (position == getCount() - 1) {
                gravity = Gravity.TOP | Gravity.RIGHT;
            } else {
                gravity = Gravity.CENTER | Gravity.RIGHT;
            }
        } else if (mOrientation == LabelOrientation.HORIZONTAL) {
            if (position == 0) {
                gravity = Gravity.CENTER | Gravity.LEFT;
            } else if (position == getCount() - 1) {
                gravity = Gravity.CENTER | Gravity.RIGHT;
            }
        }
        Helper helper = new Helper();
        int type = helper.getTypeDisplay(mContext);
        int labelTextSize;
        if(type == 1){
            labelTextSize = 6;
        }
        else if(type == 2){
            labelTextSize = 8;
        }
        else if(type == 3){
            labelTextSize = 10;
        }
        else if(type == 4){
            labelTextSize = 12;
        }
        else {
            labelTextSize = 8;
        }
        labelTextView.setTextSize(labelTextSize);
        labelTextView.setGravity(gravity);
        labelTextView.setPadding(8, 0, 8, 0);
        String value = String.format("%.0f", getItem(position));
        labelTextView.setText(value);

        return convertView;
    }
}
