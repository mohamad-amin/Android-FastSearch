package com.mohamadamin.fastsearch.free.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mohamadamin.fastsearch.free.R;
import com.mohamadamin.fastsearch.free.fragments.SearchFragment;
import com.mohamadamin.fastsearch.free.modules.CustomFile;
import com.mohamadamin.fastsearch.free.utils.DisplayUtils;
import com.mohamadamin.fastsearch.free.utils.FileUtils;
import com.mohamadamin.fastsearch.free.utils.Interfaces;
import com.mohamadamin.fastsearch.free.utils.PicassoUtils;
import com.mohamadamin.fastsearch.free.utils.SdkUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends BaseActionModeAdapter implements
        View.OnClickListener,
        View.OnLongClickListener {

    Activity context;
    List<CustomFile> list;
    Picasso picasso;
    String filter;
    View parent;
    SearchFragment searchFragment;
    Interfaces.OnFilePressedListener onFilePressedListener;

    boolean removeUndone;
    SparseBooleanArray selectedItems;
    int lastPosition = -1, selectedItem = -1;

    public MusicAdapter(SearchFragment searchFragment, Activity context, View parent, String filter,
                        Interfaces.OnFilePressedListener onFilePressedListener) {
        this.searchFragment = searchFragment;
        this.filter = filter;
        this.context = context;
        this.parent = parent;
        this.onFilePressedListener = onFilePressedListener;
        this.selectedItems = new SparseBooleanArray();
        if (SdkUtils.isHoneycombOrHigher()) new MusicLoaderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else new MusicLoaderTask().execute();
        initializePicasso();
    }

    private void initializePicasso() {
        picasso = new Picasso.Builder(context)
                .addRequestHandler(new PicassoUtils.AudioRequestHandler(context))
                .build();
    }

    @Override
    public int getItemCount() {
        return (list == null) ? 0 : list.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
        return RecyclerItemViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;
        CustomFile customFile = list.get(position);
        holder.setTitleText(getSpannedString(customFile.title, filter));
        holder.setDescriptionText(getSpannedString(customFile.artist + " -> " + customFile.album, filter));
        holder.getParent().setTag(position);
        holder.getParent().setOnClickListener(this);
        holder.getParent().setOnLongClickListener(this);
        holder.getParent().setActivated(selectedItems.get(position, false));
        picasso.load(PicassoUtils.SCHEME_AUDIO+":"+customFile.audioPath)
                .placeholder(R.drawable.file_music)
                .error(R.drawable.file_music)
                .into(holder.getImageView());
    }

    @Override
    public void onViewAttachedToWindow(final RecyclerView.ViewHolder viewHolder) {

        super.onViewAttachedToWindow(viewHolder);
        final RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;

        holder.getParent().setVisibility(View.INVISIBLE);
        if (holder.getAdapterPosition() > lastPosition) {
            holder.getParent().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.getParent().setVisibility(View.VISIBLE);
                    ObjectAnimator alpha = ObjectAnimator.ofFloat(holder.getParent(), "alpha", 0f, 1f);
                    AnimatorSet animSet = new AnimatorSet();
                    animSet.play(alpha);
                    animSet.setDuration(400);
                    animSet.start();

                }
            }, 100);

            lastPosition = holder.getAdapterPosition();
        } else holder.getParent().setVisibility(View.VISIBLE);

    }

    private Spannable getSpannedString(String parent, String filter) {
        String sample = parent.toLowerCase();
        filter = filter.toLowerCase();
        List<Integer> integers = new ArrayList<>();
        Spannable spannable = new SpannableString(parent);
        int filterLength = filter.length();
        int index = sample.indexOf(filter);
        while (index >= 0) {
            integers.add(index);
            index = sample.indexOf(filter, index+1);
        }
        for (int i : integers) {
            spannable.setSpan(new ForegroundColorSpan(context.getResources()
                    .getColor(R.color.colorPrimary)), i, i+filterLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    @Override
    public void onClick(View view) {
        int position = (Integer) view.getTag();
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(list.get(position).audioPath);
        intent.setDataAndType(Uri.fromFile(file), "audio/*");
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, context.getResources().
                    getString(R.string.no_app_to_perform_action), Toast.LENGTH_LONG).show();
        }
    }

    public void remove(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void add(CustomFile customFile) {
        list.add(customFile);
        notifyItemInserted(getItemCount());
    }

    public void add(CustomFile customFile, int position) {
        list.add(position, customFile);
        notifyItemInserted(position);
    }

    @Override
    public void removeFile(final int position) {

        removeUndone = false;
        int duration = Snackbar.LENGTH_LONG;
        final CustomFile customFile = list.get(position);

        remove(position);

        Snackbar snackbar = Snackbar.make(parent, R.string.deleted, duration)
                .setAction(R.string.undo_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeUndone = true;
                        add(customFile, position);
                    }
                })
                .setActionTextColor(context.getResources().getColor(R.color.colorPrimary));
        snackbar.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!removeUndone) FileUtils.deleteFile(list.get(position));
            }
        }, duration);

    }

    @Override
    public void renameFile(final int position) {

        final CustomFile oldFile = list.get(position);

        final AppCompatEditText editText = new AppCompatEditText(context);
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        editText.setText(oldFile.title);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.rename))
                .setView(DisplayUtils.wrapViewInFrameLayout(editText, 20, 20, 0, 0))
                .setPositiveButton(context.getResources().getString(R.string.submit), null)
                .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    Toast.makeText(context, context.getString(R.string.enter_new_name), Toast.LENGTH_LONG).show();
                } else {
                    FileUtils.renameFile(oldFile, editText.getText().toString());
                    oldFile.title = editText.getText().toString();
                    list.set(position, oldFile);
                    alertDialog.dismiss();
                    notifyItemChanged(position);
                }
            }
        });

    }

    @Override
    public void selectItem(int position) {
        selectedItems.clear();
        this.selectedItem = position;
        selectedItems.put(position, true);
        notifyItemChanged(position);
    }

    @Override
    public void clearSelections() {
        selectedItems.clear();
        notifyItemChanged(selectedItem);
        selectedItem = -1;
    }

    @Override
    public boolean onLongClick(View view) {
        if (onFilePressedListener != null) {
            int position = (Integer) view.getTag();
            onFilePressedListener.onFilePressed((list.get(position)).name, position);
            return true;
        } else return false;
    }

    private class MusicLoaderTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            filterMusics();
            return null;
        }
    }

    private void filterMusics() {

        list = new ArrayList<>();

        String[] projection = new String[] {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DATA
        };
        String where = MediaStore.Audio.Media.TITLE + " LIKE ? OR " +
                MediaStore.Audio.Media.ALBUM + " LIKE ? OR " +
                MediaStore.Audio.Media.ARTIST + " LIKE ?";
        String[] params = new String[] {
                "%" + filter + "%",
                "%" + filter + "%",
                "%" + filter + "%"
        };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                where,
                params,
                MediaStore.Audio.Media.TITLE
        );

        if (cursor.getCount() == 0) {
            if (searchFragment!=null && context!=null) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchFragment.showNothingFoundLayout();
                    }
                });
            }
            return;
        }

        while (cursor.moveToNext()) {
            final CustomFile customFile = new CustomFile();
            customFile.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            customFile.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            customFile.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            customFile.audioPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            if (context != null) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        add(customFile);
                    }
                });
            }
        }

        cursor.close();

    }

}
