package com.mohamadamin.fastsearch.free.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mohamadamin.fastsearch.free.R;
import com.mohamadamin.fastsearch.free.modules.CustomFile;
import com.mohamadamin.fastsearch.free.utils.FileUtils;
import com.mohamadamin.fastsearch.free.utils.PicassoUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        View.OnClickListener {

    Context context;
    List<CustomFile> list;
    Picasso picasso;
    String filter;

    int lastPosition = -1;

    public VideoAdapter(Context context, List<CustomFile> list, String filter) {
        this.list = list;
        this.filter = filter;
        this.context = context;
        initializePicasso();
    }

    private void initializePicasso() {
        picasso = new Picasso.Builder(context)
                .addRequestHandler(new PicassoUtils.VideoRequestHandler(context))
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
        holder.setTitleText(getSpannedString(customFile.name, filter));
        holder.setDescriptionText(customFile.directory);
        holder.getParent().setTag(position);
        holder.getParent().setOnClickListener(this);
        picasso.load(PicassoUtils.SCHEME_VIDEO+":"+customFile.fullPath)
                .placeholder(R.drawable.file_video)
                .error(R.drawable.file_video)
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
        CustomFile customFile = list.get((Integer) view.getTag());
        Intent intent = new Intent(Intent.ACTION_VIEW);
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
