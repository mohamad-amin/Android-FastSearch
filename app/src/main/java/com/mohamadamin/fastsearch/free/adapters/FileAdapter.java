package com.mohamadamin.fastsearch.free.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
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
import com.mohamadamin.fastsearch.free.modules.CustomFile;
import com.mohamadamin.fastsearch.free.utils.DisplayUtils;
import com.mohamadamin.fastsearch.free.utils.FileUtils;
import com.mohamadamin.fastsearch.free.utils.Interfaces;
import com.mohamadamin.fastsearch.free.utils.PicassoUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        View.OnClickListener,
        View.OnLongClickListener {

    Context context;
    List<CustomFile> list;
    Picasso picasso;
    String filter;
    View parent;
    Interfaces.OnFilePressedListener onFilePressedListener;

    boolean removeUndone;
    SparseBooleanArray selectedItems;
    int lastPosition = -1, seletectedItem = -1;

    public FileAdapter(Context context, View parent, List<CustomFile> list,
                       String filter, Interfaces.OnFilePressedListener onFilePressedListener) {
        this.list = list;
        this.parent = parent;
        this.filter = filter;
        this.context = context;
        this.onFilePressedListener = onFilePressedListener;
        this.selectedItems = new SparseBooleanArray();
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
        holder.getParent().setSelected(selectedItems.get(position, false));

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
            intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(customFile.fullPath);
            intent.setDataAndType(uri, "resource/folder");
            try {
                context.startActivity(intent);
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

    public void add(CustomFile customFile, int position) {
        list.add(position, customFile);
        notifyItemInserted(position);
    }

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

    public void selectItem(int position) {
        selectedItems.clear();
        this.seletectedItem = position;
        selectedItems.put(position, true);
        notifyItemChanged(position);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyItemChanged(seletectedItem);
        seletectedItem = -1;
    }

    @Override
    public boolean onLongClick(View view) {
        if (onFilePressedListener != null) {
            int position = (Integer) view.getTag();
            onFilePressedListener.onFilePressed((list.get(position)).name, position);
            return true;
        } else return false;
    }

}
