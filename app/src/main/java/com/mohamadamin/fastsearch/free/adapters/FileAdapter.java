package com.mohamadamin.fastsearch.free.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
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

public class FileAdapter extends BaseActionModeAdapter implements
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

    public FileAdapter(SearchFragment searchFragment, Activity context, View parent, String filter,
                       Interfaces.OnFilePressedListener onFilePressedListener) {
        this.searchFragment = searchFragment;
        this.parent = parent;
        this.filter = filter;
        this.context = context;
        this.onFilePressedListener = onFilePressedListener;
        this.selectedItems = new SparseBooleanArray();
        if (SdkUtils.isHoneycombOrHigher()) new FileLoaderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else new FileLoaderTask().execute();
        initializePicasso();
    }

    private void initializePicasso() {
        picasso = new Picasso.Builder(context)
                .addRequestHandler(new PicassoUtils.ApkRequestHandler(context))
                .addRequestHandler(new PicassoUtils.FileRequestHandler(context))
                .addRequestHandler(new PicassoUtils.VideoRequestHandler(context))
                .addRequestHandler(new PicassoUtils.AudioRequestHandler(context))
                .build();
    }

    @Override
    public int getItemCount() {
        return (list == null) ? 0 : list.size();
    }

    @Override
     public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false);
        return RecyclerItemViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;

        CustomFile customFile = list.get(position);
        File file = new File(customFile.fullPath);

        holder.setTitleText(getSpannedString(customFile.name, filter));
        holder.setDescriptionText(customFile.directory);
        holder.getParent().setTag(position);
        holder.getParent().setOnClickListener(this);
        holder.getParent().setOnLongClickListener(this);
        holder.getParent().setActivated(selectedItems.get(position, false));

        String extension = FileUtils.getExtensionFromFilePath(customFile.fullPath);
        String mimeType = FileUtils.getMimeTypeFromFilePath(customFile.fullPath).split("/")[0];

        if (extension.equalsIgnoreCase("apk")) {
            picasso.load(PicassoUtils.SCHEME_APK+":"+customFile.fullPath)
                    .placeholder(R.drawable.file_android)
                    .error(R.drawable.file_android)
                    .into(holder.getImageView());
        } else if (mimeType.equalsIgnoreCase("audio")) {
            picasso.load(PicassoUtils.SCHEME_AUDIO+":"+customFile.fullPath)
                    .placeholder(R.drawable.file_music)
                    .error(R.drawable.file_music)
                    .into(holder.getImageView());
        } else if (mimeType.equalsIgnoreCase("video")) {
            picasso.load(PicassoUtils.SCHEME_VIDEO+":"+customFile.fullPath)
                    .placeholder(R.drawable.file_video)
                    .error(R.drawable.file_video)
                    .into(holder.getImageView());
        } else if (mimeType.equalsIgnoreCase("image")) {
            picasso.load(file)
                    .placeholder(R.drawable.file_image)
                    .error(R.drawable.file_image)
                    .into(holder.getImageView());
        } else if (file.exists() && file.isDirectory()) {
            picasso.load(R.drawable.file_folder).into(holder.getImageView());
        } else picasso.load(PicassoUtils.SCHEME_FILE+":"+customFile.fullPath)
                .placeholder(R.drawable.file_unknown)
                .error(R.drawable.file_unknown)
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
        Intent intent;
        CustomFile customFile = list.get((Integer) view.getTag());
        File file = new File(customFile.fullPath);
        if (file.exists() && file.isDirectory()) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "text/csv");
            try {
                context.startActivity(Intent.createChooser(intent, context.getString(R.string.open_folder)));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, context.getString(R.string.no_app_to_perform_action), Toast.LENGTH_LONG).show();
            }
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(customFile.fullPath)),
                    FileUtils.getMimeTypeFromFilePath(customFile.fullPath));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, context.getString(R.string.no_app_to_perform_action), Toast.LENGTH_LONG).show();
            }
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
        editText.setText(oldFile.name);

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
                    oldFile.name = editText.getText().toString();
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

    private class FileLoaderTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            filterFiles();
            return null;
        }
    }

    private void filterFiles() {

        list = new ArrayList<>();
        String path;

        String nonMediaCondition = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
        String where = nonMediaCondition + " AND " + MediaStore.Files.FileColumns.TITLE + " LIKE ?";
        String[] params = new String[] {"%"+filter+"%"};

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                new String[]{MediaStore.Files.FileColumns.DATA},
                where,
                params,
                null
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
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            if (path != null) {
                final CustomFile customFile = new CustomFile();
                customFile.fullPath = path;
                customFile.name = FileUtils.getFileName(path);
                customFile.directory = FileUtils.getFileParent(path);
                if (context != null) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            add(customFile);
                        }
                    });
                }
            }
        }

        cursor.close();

    }

}
