package com.mohamadamin.fastsearch.free.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mohamadamin.fastsearch.free.R;
import com.mohamadamin.fastsearch.free.modules.CustomApplication;
import com.mohamadamin.fastsearch.free.utils.PicassoUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        View.OnClickListener {

    Context context;
    List<CustomApplication> list;
    Picasso picasso;
    String filter;

    int lastPosition = -1;

    public ApplicationAdapter(Context context, List<CustomApplication> list, String filter) {
        this.list = list;
        this.filter = filter;
        this.context = context;
        initializePicasso();
    }

    private void initializePicasso() {
        picasso = new Picasso.Builder(context)
                .addRequestHandler(new PicassoUtils.ApplicationRequestHandler(context))
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
        CustomApplication customApplication = list.get(position);
        holder.setTitleText(getSpannedString(customApplication.titleText, filter));
        holder.setDescriptionText(customApplication.packageName);
        holder.getParent().setTag(position);
        holder.getParent().setOnClickListener(this);
        String packageName = customApplication.packageName;
        picasso.load(PicassoUtils.SCHEME_APPLICATION+":/"+packageName)
                .placeholder(R.drawable.file_android)
                .error(R.drawable.file_android)
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
        try {
            context.startActivity(context.getPackageManager().
                    getLaunchIntentForPackage(list.get((Integer) view.getTag()).packageName));
        } catch (Exception e) {
            Toast.makeText(context, context.getResources().getString(R.string.error_opening_application)
                    , Toast.LENGTH_LONG).show();
        }
    }

}
