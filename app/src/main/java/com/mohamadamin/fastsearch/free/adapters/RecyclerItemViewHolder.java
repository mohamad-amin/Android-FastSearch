package com.mohamadamin.fastsearch.free.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mohamadamin.fastsearch.free.R;

public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {

    private final View parent;
    private final TextView titleText, descriptionText;
    private final ImageView imageView;

    public RecyclerItemViewHolder(final View parent, ImageView imageView,
                                    TextView titleText, TextView descriptionText) {
        super(parent);
        this.parent = parent;
        this.imageView = imageView;
        this.titleText = titleText;
        this.descriptionText = descriptionText;
    }

    public static RecyclerItemViewHolder newInstance(View parent) {
        TextView titleText = (TextView) parent.findViewById(R.id.search_item_title);
        TextView descriptionText = (TextView) parent.findViewById(R.id.search_item_description);
        ImageView imageView = (ImageView) parent.findViewById(R.id.search_item_image);
        return new RecyclerItemViewHolder(parent, imageView, titleText, descriptionText);
    }

    public void setTitleText(Spannable text) {
        this.titleText.setText(text);
    }

    public void setDescriptionText(String text) {
        this.descriptionText.setText(text);
    }

    public void setDescriptionText(Spannable text) {
        this.descriptionText.setText(text);
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    public View getParent() {
        return this.parent;
    }

}
