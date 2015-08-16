package com.mohamadamin.fastsearch.free.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.dynamixsoftware.ErrorAgent;
import com.mohamadamin.fastsearch.free.R;
import com.mohamadamin.fastsearch.free.fragments.HomeFragment;
import com.mohamadamin.fastsearch.free.utils.BusinessUtils;
import com.mohamadamin.fastsearch.free.utils.NotificationUtils;
import com.mohamadamin.fastsearch.free.utils.PreferenceUtils;
import com.mohamadamin.fastsearch.free.utils.SdkUtils;

public class MainActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    View slidingPanel;
    ProgressDialog progressDialog;

    TextView notificationText, emailText, instagramText;
    TextView[] textViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ErrorAgent.register(this, 145L);

        checkNotifications();
        checkDatabases();
        initializeViews();
        handleClicks();
        if (savedInstanceState == null) launchHome();

    }

    private void checkNotifications() {
        if (PreferenceUtils.shouldShowSearchNotification(this)) NotificationUtils.showSearchNotification(this);
        else NotificationUtils.dismissSearchNotification(this);
    }

    private void initializeViews() {
        slidingPanel = findViewById(R.id.main_slider);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        notificationText = (TextView) findViewById(R.id.slider_notification);
        emailText = (TextView) findViewById(R.id.slider_email);
        instagramText = (TextView) findViewById(R.id.slider_instagram);
        textViews = new TextView[]{notificationText, emailText, instagramText};
    }

    private void handleClicks() {
        View.OnClickListener otherOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.slider_notification : changeNotification(); break;
                    case R.id.slider_email : BusinessUtils.sendEmailToDeveloper(MainActivity.this); break;
                    case R.id.slider_instagram : BusinessUtils.launchInstagramPage(MainActivity.this); break;
                }
                closeDrawerLayout();
            }
        };
        for (TextView textView : textViews) {
            textView.setOnClickListener(otherOnClickListener);
        }
        if (PreferenceUtils.shouldShowSearchNotification(this)) {
            notificationText.setText(getString(R.string.cancel_notification));
        } else notificationText.setText(getString(R.string.show_notification));
    }

    private void changeNotification() {
        PreferenceUtils.setShouldShowSearchNotification(
                this,
                !PreferenceUtils.shouldShowSearchNotification(this)
        );
        if (PreferenceUtils.shouldShowSearchNotification(this)) {
            notificationText.setText(getString(R.string.cancel_notification));
            NotificationUtils.showSearchNotification(this);
        } else {
            notificationText.setText(getString(R.string.show_notification));
            NotificationUtils.dismissSearchNotification(this);
        }
    }

    private void launchHome() {
        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, homeFragment).commit();
    }

    public void openDrawerLayout() {
        if (drawerLayout != null) drawerLayout.openDrawer(slidingPanel);
    }

    public void closeDrawerLayout() {
        if (drawerLayout != null) drawerLayout.closeDrawer(slidingPanel);
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void checkDatabases() {

        final boolean shouldFill = PreferenceUtils.shouldWriteDatabases(this);
        if (!shouldFill) return;

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                NotificationUtils.showSubmitDataNotification(MainActivity.this);
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle(getString(R.string.submit_data_title));
                progressDialog.setMessage(getString(R.string.submit_data_description));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setProgressPercentFormat(null);
                progressDialog.setProgressNumberFormat(null);
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                SdkUtils.addPackagesToDatabase(MainActivity.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                PreferenceUtils.setShouldWriteDatabases(MainActivity.this, false);
                NotificationUtils.dismissSubmitDataNotification(MainActivity.this);
                if (MainActivity.this.isFinishing()) return;
                dismissProgressDialog();
            }

        };

        if (SdkUtils.isHoneycombOrHigher()) task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else task.execute();

    }

    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

}