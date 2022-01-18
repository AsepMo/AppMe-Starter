package com.appme.story.engine.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.widget.TextView;
import android.util.AttributeSet;
import android.util.Log;

import com.appme.story.R;

public class TextViewFont extends TextView {

    private static final String TAG = "TextViewFont";

    public TextViewFont(Context context) {
        super(context);
    }

    public TextViewFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public TextViewFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
        String customFont = a.getString(R.styleable.TextViewPlus_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface typeface = null;
        try {
            typeface = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Unable to load typeface: "+e.getMessage());
            return false;
        }

        setTypeface(typeface);
        return true;
    }

}
