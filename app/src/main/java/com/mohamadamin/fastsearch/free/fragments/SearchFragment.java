package com.mohamadamin.fastsearch.free.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mohamadamin.fastsearch.free.R;
import com.mohamadamin.fastsearch.free.adapters.ApplicationAdapter;
import com.mohamadamin.fastsearch.free.adapters.BaseActionModeAdapter;
import com.mohamadamin.fastsearch.free.adapters.ContactAdapter;
import com.mohamadamin.fastsearch.free.adapters.FileAdapter;
import com.mohamadamin.fastsearch.free.adapters.ImageAdapter;
import com.mohamadamin.fastsearch.free.adapters.MusicAdapter;
import com.mohamadamin.fastsearch.free.adapters.VideoAdapter;
import com.mohamadamin.fastsearch.free.databases.ApplicationsDB;
import com.mohamadamin.fastsearch.free.utils.Interfaces;

import jp.wasabeef.recyclerview.animators.LandingAnimator;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class SearchFragment extends Fragment implements Interfaces.OnFilePressedListener {

    public static final String POSITION="position", FILTER="filter";
    public final int APPLICATIONS = 0, CONTACTS = 1, FILES = 2, MUSICS = 3, PHOTOS = 4, VIDEOS = 5;
    int position;
    String filter;

    View mainLayout;
    CardView cardView;
    LinearLayout searchFirstLayout, nothingFoundLayout;

    ActionMode actionMode;

    RecyclerView searchResultList;
    RecyclerView.Adapter baseAdapter;
    VerticalRecyclerViewFastScroller fastScroller;

    ApplicationsDB applicationsDB;

    public static SearchFragment newInstance(int position, String filter) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION, position);
        bundle.putString(FILTER, filter);
        searchFragment.setArguments(bundle);
        return searchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(POSITION);
        filter = getArguments().getString(FILTER);
        applicationsDB = new ApplicationsDB(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainLayout = inflater.inflate(R.layout.fragment_search, container, false);
        if (isAdded()) showContent();
        return mainLayout;
    }

    private void showContent() {
        initializeViews();
        organizeViews();
    }

    private void initializeViews() {

        cardView = (CardView) mainLayout.findViewById(R.id.search_card);
        nothingFoundLayout = (LinearLayout) mainLayout.findViewById(R.id.search_nothing_found);
        searchFirstLayout = (LinearLayout) mainLayout.findViewById(R.id.search_search_first);

        searchResultList = (RecyclerView) mainLayout.findViewById(R.id.search_list);
        searchResultList.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchResultList.setItemAnimator(new LandingAnimator());
        searchResultList.getItemAnimator().setAddDuration(300);
        searchResultList.getItemAnimator().setRemoveDuration(300);

        fastScroller = (VerticalRecyclerViewFastScroller) mainLayout.findViewById(R.id.search_fast_scroller);
        fastScroller.setRecyclerView(searchResultList);
        searchResultList.addOnScrollListener(fastScroller.getOnScrollListener());

    }

    private void organizeViews() {

        if (TextUtils.isEmpty(filter)) {
            cardView.setVisibility(View.GONE);
            nothingFoundLayout.setVisibility(View.GONE);
            searchFirstLayout.setVisibility(View.VISIBLE);
        } else {

            searchFirstLayout.setVisibility(View.GONE);

            switch (position) {
                case APPLICATIONS : searchApplications(); break;
                case CONTACTS : searchContacts(); break;
                case FILES : searchFiles(); break;
                case MUSICS : searchMusics(); break;
                case PHOTOS : searchPhotos(); break;
                case VIDEOS : searchVideos(); break;
                default: break;
            }

            closeDatabases();

            if (baseAdapter != null) {
                cardView.setVisibility(View.VISIBLE);
                nothingFoundLayout.setVisibility(View.GONE);
                searchResultList.setAdapter(baseAdapter);
            } else showNothingFoundLayout();

        }

    }

    public void showNothingFoundLayout() {
        cardView.setVisibility(View.GONE);
        nothingFoundLayout.setVisibility(View.VISIBLE);
    }

    private void closeDatabases() {
        applicationsDB.close();
    }

    private void searchFiles() {
        Activity context = getActivity();
        if (context == null) return;
        baseAdapter = new FileAdapter(this, context, mainLayout, filter, this);
    }

    private void searchApplications() {
        Activity context = getActivity();
        if (context == null) return;
        baseAdapter = new ApplicationAdapter(this, context, filter);
    }

    private void searchContacts() {
        Activity context = getActivity();
        if (context == null) return;
        baseAdapter = new ContactAdapter(this, context, filter);
    }

    private void searchMusics() {
        Activity context = getActivity();
        if (context == null) return;
        baseAdapter = new MusicAdapter(this, context, mainLayout, filter, this);
    }

    private void searchPhotos() {
        Activity context = getActivity();
        if (context == null) return;
        baseAdapter = new ImageAdapter(this, context, mainLayout, filter, this);
    }

    private void searchVideos() {
        Activity context = getActivity();
        if (context == null) return;
        baseAdapter = new VideoAdapter(this, context, mainLayout, filter, this);
    }

    @Override
    public void onFilePressed(final String fileName, final int position) {
        final BaseActionModeAdapter adapter = (BaseActionModeAdapter) baseAdapter;
        if (actionMode != null) actionMode.finish();
        actionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.file_action_mode, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(final ActionMode mode, Menu menu) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mode.setTitle(fileName);
                    }
                }, 150);
                adapter.selectItem(position);
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_mode_rename: {
                        adapter.clearSelections();
                        adapter.renameFile(position);
                        mode.finish();
                        return true;
                    }
                    case R.id.action_mode_delete: {
                        adapter.clearSelections();
                        adapter.removeFile(position);
                        mode.finish();
                        return true;
                    }
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapter.clearSelections();
            }

        });

    }

}
