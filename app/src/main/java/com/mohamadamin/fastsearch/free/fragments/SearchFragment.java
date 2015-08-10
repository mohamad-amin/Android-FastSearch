package com.mohamadamin.fastsearch.free.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import com.mohamadamin.fastsearch.free.activities.MainActivity;
import com.mohamadamin.fastsearch.free.adapters.ApplicationAdapter;
import com.mohamadamin.fastsearch.free.adapters.ContactAdapter;
import com.mohamadamin.fastsearch.free.adapters.FileAdapter;
import com.mohamadamin.fastsearch.free.databases.ApplicationsDB;
import com.mohamadamin.fastsearch.free.databases.DirectoriesDB;
import com.mohamadamin.fastsearch.free.databases.FilesDB;
import com.mohamadamin.fastsearch.free.modules.CustomFile;
import com.mohamadamin.fastsearch.free.utils.ContactUtils;
import com.mohamadamin.fastsearch.free.utils.Interfaces;
import com.mohamadamin.fastsearch.free.utils.SdkUtils;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import jp.wasabeef.recyclerview.animators.LandingAnimator;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class SearchFragment extends Fragment implements Interfaces.OnFilePressedListener {

    public static final String POSITION="position", FILTER="filter";
    int position;
    String filter;

    View mainLayout;
    CardView cardView;
    LinearLayout searchFirstLayout, nothingFoundLayout;

    ProgressDialog progressDialog;
    ActionMode actionMode;

    RecyclerView searchResultList;
    RecyclerView.Adapter baseAdapter;
    VerticalRecyclerViewFastScroller fastScroller;

    FilesDB filesDB;
    DirectoriesDB directoriesDB;
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
        filesDB = new FilesDB(getActivity());
        directoriesDB = new DirectoriesDB(getActivity());
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
        searchResultList.setItemAnimator(new LandingAnimator());
        searchResultList.getItemAnimator().setAddDuration(300);
        searchResultList.getItemAnimator().setRemoveDuration(300);
        fastScroller = (VerticalRecyclerViewFastScroller) mainLayout.findViewById(R.id.search_fast_scroller);

        searchResultList.setLayoutManager(new LinearLayoutManager(getActivity()));
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
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = ProgressDialog.show(
                            getActivity(),
                            null,
                            getString(R.string.please_wait)
                    );
                    progressDialog.setCancelable(false);
                }

                @Override
                protected Void doInBackground(Void... params) {
                    switch (position) {
                        case 0 : searchFiles(); break;
                        case 1 : searchApplications(); break;
                        case 2 : searchContacts(); break;
                        default: break;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    if (getActivity() == null) return;
                    else if (getActivity().isFinishing()) return;
                    dismissProgressDialog();
                    if (baseAdapter.getItemCount() > 0) {
                        cardView.setVisibility(View.VISIBLE);
                        nothingFoundLayout.setVisibility(View.GONE);
                        searchResultList.setAdapter(baseAdapter);
                    } else {
                        cardView.setVisibility(View.GONE);
                        nothingFoundLayout.setVisibility(View.VISIBLE);
                    }
                }

            };

            if (SdkUtils.isHoneycombOrHigher()) task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else task.execute();

        }

    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    private void searchFiles() {
        List<CustomFile> customFiles = directoriesDB.getFiles(filter);
        customFiles.addAll(filesDB.getFiles(filter));
        Collections.shuffle(customFiles, new Random(System.nanoTime()));
        baseAdapter = new FileAdapter(getActivity(), mainLayout, customFiles, filter, this);
    }

    private void searchApplications() {
        baseAdapter = new ApplicationAdapter(getActivity(), applicationsDB.filterApplications(filter), filter);
    }

    private void searchContacts() {
        baseAdapter = new ContactAdapter(getActivity(), ContactUtils.filterContacts(getActivity(), filter), filter);
    }

    @Override
    public void onFilePressed(final String fileName, final int position) {
        if (actionMode != null) actionMode.finish();
        actionMode = ((MainActivity)getActivity()).startSupportActionMode(new ActionMode.Callback() {

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
                ((FileAdapter)baseAdapter).selectItem(position);
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_mode_rename: {
                        ((FileAdapter)baseAdapter).clearSelections();
                        ((FileAdapter)baseAdapter).renameFile(position);
                        mode.finish();
                        return true;
                    }
                    case R.id.action_mode_delete: {
                        ((FileAdapter)baseAdapter).clearSelections();
                        ((FileAdapter)baseAdapter).removeFile(position);
                        mode.finish();
                        return true;
                    }
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                ((FileAdapter)baseAdapter).clearSelections();
            }

        });

    }

    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

}
