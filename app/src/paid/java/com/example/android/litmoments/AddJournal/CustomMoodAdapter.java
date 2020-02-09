package com.example.android.litmoments.AddJournal;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajts.androidmads.fontutils.FontUtils;
import com.lit.litmoments.R;


public class CustomMoodAdapter extends BaseAdapter {

    Context context;
    int images[];
    String[] fruit;
    LayoutInflater inflter;

    public CustomMoodAdapter(Context applicationContext, int[] flags, String[] fruit) {
        this.context = applicationContext;
        this.images = flags;
        this.fruit = fruit;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_custom_layout, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.textView);
        icon.setImageResource(images[i]);
        names.setText(fruit[i]);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String selectedFont = sharedPreferences.getString(context.getResources().getString(R.string.key_uiThemeFont), "6");
        Boolean isDarkTheme = sharedPreferences.getBoolean(context.getResources().getString(R.string.key_darkTheme), false);

        if(isDarkTheme){
            names.setTextColor(context.getResources().getColor(R.color.cardviewtitle));
        }
        if( selectedFont != null &&selectedFont.equals("0")){
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.parisienneregular);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);

        }else if(selectedFont.equals("1")){
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.patrick_hand_sc);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);


        } else if( selectedFont.equals("2")) {
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.sofadi_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);


        } else if(selectedFont.equals("3")){
            FontUtils fontUtils = new FontUtils();
            Typeface myCustomFont = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
            fontUtils.applyFontToView(names,myCustomFont);


        }  else if( selectedFont.equals("4")) {
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.concert_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);

        }
        else if( selectedFont.equals("5")) {
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.oleo_script);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);

        }
        else if( selectedFont.equals("6")) {
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.pt_sans_narrow);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names, myCustomFont);

        }  else if( selectedFont.equals("7")) {
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.roboto_condensed_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);
        }  else if( selectedFont.equals("8")) {
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.shadows_into_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);
        } else if( selectedFont.equals("9")) {
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.slabo_13px);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.spinner_custom_layout, null);
        TextView names = (TextView) view.findViewById(R.id.textView);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        icon.setImageResource(images[position]);
        names.setText(fruit[position]);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String selectedFont = sharedPreferences.getString(context.getResources().getString(R.string.key_uiThemeFont), "6");

        if( selectedFont != null &&selectedFont.equals("0")){
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.parisienneregular);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);
        }else if(selectedFont.equals("1")){
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.patrick_hand_sc);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);


        } else if( selectedFont.equals("2")) {
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.sofadi_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);

        } else if(selectedFont.equals("3")){
            FontUtils fontUtils = new FontUtils();
            Typeface myCustomFont = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
            fontUtils.applyFontToView(names,myCustomFont);

        }  else if( selectedFont.equals("4")) {
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.concert_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);

        }
        else if( selectedFont.equals("5")) {
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.oleo_script);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);

        }
        else if( selectedFont.equals("6")) {
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.pt_sans_narrow);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names, myCustomFont);

        }  else if( selectedFont.equals("7")) {
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.roboto_condensed_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);
        }  else if( selectedFont.equals("8")) {
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.shadows_into_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);
        } else if( selectedFont.equals("9")) {
            Typeface myCustomFont = ResourcesCompat.getFont(context, R.font.slabo_13px);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(names,myCustomFont);
        }

        return view;
    }
}
