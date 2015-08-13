package com.mohamadamin.fastsearch.free.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mohamadamin.fastsearch.free.R;
import com.mohamadamin.fastsearch.free.activities.MainActivity;
import com.mohamadamin.fastsearch.free.adapters.HomeAdapter;
import com.mohamadamin.fastsearch.free.modules.SlidingTabLayout;
import com.mohamadamin.fastsearch.free.utils.BusinessUtils;

public class HomeFragment extends Fragment implements
        View.OnClickListener,
        TextView.OnEditorActionListener {

    View mainLayout;
    ViewPager viewPager;
    SlidingTabLayout slidingTabLayout;
    HomeAdapter homeAdapter;

    Toolbar toolbar;
    ActionBar actionBar;
    EditText searchInput;
    ImageButton menuButton, actionButton, searchButton;

    String filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            filter = savedInstanceState.getString(SearchFragment.FILTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainLayout = inflater.inflate(R.layout.fragment_home, container, false);
        if (isAdded()) showContent();
        return mainLayout;
    }

    private void showContent() {
        initializeViews();
        organizeViews();
        organizeToolbar();
    }

    private void initializeViews() {
        viewPager = (ViewPager) mainLayout.findViewById(R.id.home_pager);
        slidingTabLayout = (SlidingTabLayout) mainLayout.findViewById(R.id.home_tabs);
    }

    private void organizeToolbar() {
        toolbar = (Toolbar) mainLayout.findViewById(R.id.home_toolbar);
        actionButton = (ImageButton) mainLayout.findViewById(R.id.toolbar_action);
        menuButton = (ImageButton) mainLayout.findViewById(R.id.toolbar_overflow);
        searchButton = (ImageButton) mainLayout.findViewById(R.id.toolbar_search);
        searchInput = (EditText) mainLayout.findViewById(R.id.toolbar_input);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        actionButton.setOnClickListener(this);
        menuButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        searchInput.setOnEditorActionListener(this);
    }

    private void organizeViews() {
        homeAdapter = new HomeAdapter(getChildFragmentManager(), filter, getResources());
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


    private void applyFilter(String filter) {
        int currentPosition = viewPager.getCurrentItem();
        homeAdapter = new HomeAdapter(getChildFragmentManager(), filter, getResources());
        viewPager.setAdapter(homeAdapter);
        viewPager.setCurrentItem(currentPosition, true);
    }

    private boolean onOptionsItemSelected(int menuId) {
        switch (menuId) {
            case R.id.action_email : {
                BusinessUtils.sendEmailToDeveloper(getActivity());
                return true;
            }
            default: return false;
        }
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
            case R.id.toolbar_action : {
                if (((MainActivity)getActivity()).drawerLayout.isDrawerOpen(GravityCompat.START)) ((MainActivity)getActivity()).closeDrawerLayout();
                else ((MainActivity)getActivity()).openDrawerLayout();
                break;
            }
            case R.id.toolbar_overflow : {
                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onOptionsItemSelected(item.getItemId());
                    }
                });
                popupMenu.show();
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

}
