package com.lit.litmoments.Settings;

import android.content.Context;
import android.graphics.Typeface;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyPreferenceCategory extends PreferenceCategory {

    public MyPreferenceCategory(Context context) {
        super(context);
    }

    public MyPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPreferenceCategory(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected View onCreateView(ViewGroup parent) {
        // It's just a TextView!
        TextView categoryTitle =  (TextView)super.onCreateView(parent);
       // categoryTitle.setTextColor(parent.getResources().getColor(R.color.bluecolorPrimary));
        categoryTitle.setTextSize(20);
        categoryTitle.setTypeface( Typeface.DEFAULT, Typeface.BOLD);
        return categoryTitle;
    }




}
