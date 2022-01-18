package com.appme.story.engine.app;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.graphics.PorterDuff;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.appme.story.R;
import com.appme.story.settings.PrefStore;

public class ActionBarActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private PrefStore mPreferences;

    public PrefStore getPreferences()
    {
        return mPreferences;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        mPreferences = new PrefStore(this);
        mPreferences.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        if (mPreferences.useFullScreen())
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s)
    {
        if (s.equals(getString(R.string.key_full_screen)))
        {
            if (mPreferences.useFullScreen())
            {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            else
            {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        mPreferences.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    public void setupToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final Drawable backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        if (backArrow != null)
        {
            backArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }

        if (toolbar != null)
        {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);
        }

    }

}


