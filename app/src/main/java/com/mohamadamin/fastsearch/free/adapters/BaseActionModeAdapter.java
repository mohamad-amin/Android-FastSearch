package com.mohamadamin.fastsearch.free.adapters;


import android.support.v7.widget.RecyclerView;

public abstract class BaseActionModeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public abstract void clearSelections();
    public abstract void removeFile(int position);
    public abstract void renameFile(int position);
    public abstract void selectItem(int position);

}
