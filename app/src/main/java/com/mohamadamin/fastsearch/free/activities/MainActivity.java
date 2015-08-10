package com.mohamadamin.fastsearch.free.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;

import com.dynamixsoftware.ErrorAgent;
import com.mohamadamin.fastsearch.free.R;
import com.mohamadamin.fastsearch.free.databases.ApplicationsDB;
import com.mohamadamin.fastsearch.free.databases.DirectoriesDB;
import com.mohamadamin.fastsearch.free.databases.FilesDB;
import com.mohamadamin.fastsearch.free.fragments.HomeFragment;
import com.mohamadamin.fastsearch.free.utils.BusinessUtils;
import com.mohamadamin.fastsearch.free.utils.FileUtils;
import com.mohamadamin.fastsearch.free.utils.NotificationUtils;
import com.mohamadamin.fastsearch.free.utils.RunUtils;
import com.mohamadamin.fastsearch.free.utils.SdkUtils;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    View slidingPanel;
    ProgressDialog progressDialog;

    TextView refreshText, emailText, instagramText;
    TextView[] textViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ErrorAgent.register(this, 145L);
        RunUtils.startServicesAndListeners(this);

        checkDatabases();
        initializeViews();
        handleClicks();
        changeSizes();
        if (savedInstanceState == null) launchHome();

    }

    private void initializeViews() {
        slidingPanel = findViewById(R.id.main_slider);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        refreshText = (TextView) findViewById(R.id.slider_refresh);
        emailText = (TextView) findViewById(R.id.slider_email);
        instagramText = (TextView) findViewById(R.id.slider_instagram);
        textViews = new TextView[]{refreshText, emailText, instagramText};
    }

    private void handleClicks() {
        View.OnClickListener otherOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.slider_refresh : showRefreshDatabasesDialog(); break;
                    case R.id.slider_email : BusinessUtils.sendEmailToDeveloper(MainActivity.this); break;
                    case R.id.slider_instagram : BusinessUtils.launchInstagramPage(MainActivity.this); break;
                }
                closeDrawerLayout();
            }
        };
        for (TextView textView : textViews) {
            textView.setOnClickListener(otherOnClickListener);
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

    private void changeSizes() {
        ViewTreeObserver viewTreeObserver = slidingPanel.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                SdkUtils.removeLayoutListener(slidingPanel, this);
                int width = slidingPanel.getMeasuredWidth();
                for (TextView textView : textViews) {
                    ViewGroup.LayoutParams params = textView.getLayoutParams();
                    params.width = width;
                    textView.setLayoutParams(params);
                }
            }
        });
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void showRefreshDatabasesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.refresh_databases))
                .setMessage(getString(R.string.refresh_databases_description))
                .setPositiveButton(getString(R.string.refresh), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ApplicationsDB applicationsDB = new ApplicationsDB(MainActivity.this);
                        DirectoriesDB directoriesDB = new DirectoriesDB(MainActivity.this);
                        FilesDB filesDB = new FilesDB(MainActivity.this);
                        applicationsDB.deleteRecords();
                        directoriesDB.deleteRecords();
                        filesDB.deleteRecords();
                        checkDatabases();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void checkDatabases() {

        final FilesDB filesDB = new FilesDB(this);
        final ApplicationsDB applicationsDB = new ApplicationsDB(this);
        final boolean fillFiles = filesDB.getCount()<1, fillApplications = applicationsDB.getCount()<1;

        if (!fillApplications && !fillFiles) return;

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
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (fillFiles) {
                    FileUtils fileUtils = new FileUtils(MainActivity.this);
                    fileUtils.openDatabases();
                    List<File> storage = FileUtils.getStorageFiles();
                    for (File file : storage) {
                        if (file != null && file.exists()) fileUtils.addFilesToDatabase(file);
                    }
                    fileUtils.closeDatabases();
                }
                if (fillApplications) SdkUtils.addPackagesToDatabase(MainActivity.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
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