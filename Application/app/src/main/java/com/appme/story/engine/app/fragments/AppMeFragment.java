package com.appme.story.engine.app.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.appme.story.R;
import com.appme.story.engine.app.commons.BaseToolbarFragment;

public class AppMeFragment extends BaseToolbarFragment  {
    
    
    public static AppMeFragment newInstance(){
        AppMeFragment fragments = new AppMeFragment();
        
        return fragments;
    }
    
    private AppCompatActivity mActivity;
    private Context mContext;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appme_application, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }
	
    private void initView(View view) {
        mContext = getActivity();
        mActivity = (AppCompatActivity)getActivity();
        setTitle("Home");
	}
}
