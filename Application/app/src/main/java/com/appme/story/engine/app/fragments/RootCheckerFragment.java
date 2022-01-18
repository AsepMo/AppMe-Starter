package com.appme.story.engine.app.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Button;

import java.util.ArrayList;

import com.appme.story.R;
import com.appme.story.engine.app.tasks.CheckRootTask;
import com.appme.story.engine.widget.TextViewFont;

public class RootCheckerFragment extends Fragment implements CheckRootTask.OnCheckRootFinishedListener {
    
    public static RootCheckerFragment newInstance(){
        RootCheckerFragment fragments = new RootCheckerFragment();
        
        return fragments;
    }
    
    private AppCompatActivity mActivity;
    private Context mContext;
    private static final String GITHUB_LINK = "https://github.com/scottyab/rootbeer";

    private AlertDialog infoDialog;
    private ProgressBar beerView;
    private TextViewFont isRootedText;
    private ArrayList<ImageView> checkRootImageViewList;
    private TextView isRootedTextDisclaimer;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_root_checker, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initView(view);
    }
    
    private void initView(View view) {
        mContext = getActivity();
        mActivity = (AppCompatActivity)getActivity();
        
        beerView = (ProgressBar) view.findViewById(R.id.loadingRootCheckBeerView);
        isRootedText = (TextViewFont) view.findViewById(R.id.content_main_is_rooted_text);
        isRootedTextDisclaimer = (TextView) view.findViewById(R.id.content_mainisRootedTextDisclaimer);

        ImageView rootCheck1ImageView = (ImageView) view.findViewById(R.id.content_main_root_check_image_1);
        ImageView rootCheck2ImageView = (ImageView) view.findViewById(R.id.content_main_root_check_image_2);
        ImageView rootCheck3ImageView = (ImageView) view.findViewById(R.id.content_main_root_check_image_3);
        ImageView rootCheck4ImageView = (ImageView) view.findViewById(R.id.content_main_root_check_image_4);
        ImageView rootCheck5ImageView = (ImageView) view.findViewById(R.id.content_main_root_check_image_5);
        ImageView rootCheck6ImageView = (ImageView) view.findViewById(R.id.content_main_root_check_image_6);
        ImageView rootCheck7ImageView = (ImageView) view.findViewById(R.id.content_main_root_check_image_7);
        ImageView rootCheck8ImageView = (ImageView) view.findViewById(R.id.content_main_root_check_image_8);
        ImageView rootCheck9ImageView = (ImageView) view.findViewById(R.id.content_main_root_check_image_9);
        ImageView rootCheck10ImageView = (ImageView) view.findViewById(R.id.content_main_root_check_image_10);
        ImageView rootCheck11ImageView = (ImageView) view.findViewById(R.id.content_main_root_check_image_11);
        checkRootImageViewList = new ArrayList<>();
        checkRootImageViewList.add(rootCheck1ImageView);
        checkRootImageViewList.add(rootCheck2ImageView);
        checkRootImageViewList.add(rootCheck3ImageView);
        checkRootImageViewList.add(rootCheck4ImageView);
        checkRootImageViewList.add(rootCheck5ImageView);
        checkRootImageViewList.add(rootCheck6ImageView);
        checkRootImageViewList.add(rootCheck7ImageView);
        checkRootImageViewList.add(rootCheck8ImageView);
        checkRootImageViewList.add(rootCheck9ImageView);
        checkRootImageViewList.add(rootCheck10ImageView);
        checkRootImageViewList.add(rootCheck11ImageView);
    }

    @Override
    public void onResume() {
        super.onResume();
        resetRootCheckImages();
        CheckRootTask checkRootTask = new CheckRootTask(mActivity, this, beerView,checkRootImageViewList);                                                     
        checkRootTask.execute(true);
    }


    private void resetRootCheckImages() {
        isRootedText.setVisibility(View.GONE);
        for (ImageView imageView : checkRootImageViewList) {
            imageView.setImageDrawable(null);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_application, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_github) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(GITHUB_LINK));
            startActivity(i);
            return true;
        } else if (id == R.id.action_info) {
            showInfoDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfoDialog() {
        if (infoDialog != null && infoDialog.isShowing()) {
            //do nothing if already showing
        } else {
            infoDialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.app_name)
                .setMessage(R.string.appme_info_details)
                .setCancelable(true)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("More info", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                                 Uri.parse(GITHUB_LINK)));
                    }
                })
                .create();
            infoDialog.show();
        }
    }

    @Override
    public void onCheckRootFinished(boolean isRooted) {
        isRootedText.setText(isRooted ? "ROOTED*" : "NOT ROOTED");
        isRootedTextDisclaimer.setVisibility(isRooted ? View.VISIBLE : View.GONE);
        isRootedText.setTextColor(isRooted ? getResources().getColor(R.color.root_fail) : getResources().getColor(R.color.root_pass));
        isRootedText.setVisibility(View.VISIBLE);
    }
}
