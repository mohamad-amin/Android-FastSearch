package com.mohamadamin.fastsearch.free.adapters;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mohamadamin.fastsearch.free.R;
import com.mohamadamin.fastsearch.free.fragments.SearchFragment;

public class HomeAdapter extends FragmentStatePagerAdapter {

    private final String[] titles;
    SearchFragment[] searchFragments;
    String filter;

    public HomeAdapter(FragmentManager fragmentManager, String filter, Resources resources) {
        super(fragmentManager);
        this.filter = filter;
        this.titles = new String[] {
                resources.getString(R.string.files),
                resources.getString(R.string.applications),
                resources.getString(R.string.contacts),
                resources.getString(R.string.musics)
        };
        this.searchFragments = new SearchFragment[titles.length];
        for (int i=0; i<searchFragments.length; i++) {
            searchFragments[i] = SearchFragment.newInstance(i, filter);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Fragment getItem(int position) {
        return searchFragments[position];
    }

}
