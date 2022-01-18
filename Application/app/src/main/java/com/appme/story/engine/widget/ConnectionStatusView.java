package com.appme.story.engine.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.appme.story.R;

public class ConnectionStatusView extends StatusView{

    public ConnectionStatusView(Context context) {
        super(context, R.layout.sv_layout_complete, R.layout.sv_layout_error, R.layout.sv_layout_loading);
    }

    public ConnectionStatusView(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.sv_layout_complete, R.layout.sv_layout_error, R.layout.sv_layout_loading);
    }

    public ConnectionStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, R.layout.sv_layout_complete, R.layout.sv_layout_error, R.layout.sv_layout_loading);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ConnectionStatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes, R.layout.sv_layout_complete, R.layout.sv_layout_error, R.layout.sv_layout_loading);
    }
}
