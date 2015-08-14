package com.mohamadamin.fastsearch.free.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mohamadamin.fastsearch.free.R;
import com.mohamadamin.fastsearch.free.adapters.HomeAdapter;
import com.mohamadamin.fastsearch.free.fragments.SearchFragment;
import com.mohamadamin.fastsearch.free.modules.SlidingTabLayout;

public class SearchActivity extends AppCompatActivity implements
        View.OnClickListener,
        TextView.OnEditorActionListener {

    ViewPager viewPager;
    SlidingTabLayout slidingTabLayout;
    HomeAdapter homeAdapter;

    Toolbar toolbar;
    ActionBar actionBar;
    EditText searchInput;
    ImageButton launchButton, searchButton;

    String filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (savedInstanceState != null) {
            this.filter = savedInstanceState.getString(SearchFragment.FILTER);
        }

        initializeViews();
        organizeViews();
        organizeToolbar();

    }
    
    private void initializeViews() {
        viewPager = (ViewPager) findViewById(R.id.search_pager);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.search_tabs);
    }

    private void organizeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        launchButton = (ImageButton) findViewById(R.id.toolbar_launch);
        searchButton = (ImageButton) findViewById(R.id.toolbar_search);
        searchInput = (EditText) findViewById(R.id.toolbar_input);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        launchButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        searchInput.setOnEditorActionListener(this);
    }

    private void applyFilter(String filter) {
        int currentPosition = viewPager.getCurrentItem();
        homeAdapter = new HomeAdapter(getSupportFragmentManager(), filter, getResources());
        viewPager.setAdapter(homeAdapter);
        viewPager.setCurrentItem(currentPosition, true);
    }

    private void organizeViews() {
        homeAdapter = new HomeAdapter(getSupportFragmentManager(), filter, getResources());
        viewPager.setAdapter(homeAdapter);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(android.R.color.white);
            }
        });
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            this.filter = searchInput.getText().toString();
            applyFilter(filter);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_launch : {
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
            }
            case R.id.toolbar_search : {
                this.filter = searchInput.getText().toString();
                applyFilter(filter);
                break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SearchFragment.FILTER, filter);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.filter = savedInstanceState.getString(SearchFragment.FILTER);
    }

}
