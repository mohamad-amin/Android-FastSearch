package com.mohamadamin.fastsearch.free.modules;

import android.graphics.Bitmap;

public class CustomApplication {

    public String titleText, descriptionText, packageName, imageLink;
    public Bitmap iconBitmap;
    public int id;

    public CustomApplication(String titleText, String descriptionText, String packageName, String imageLink) {
        super();
        this.titleText = titleText;
        this.imageLink = imageLink;
        this.packageName = packageName;
        this.descriptionText = descriptionText;
    }

    public CustomApplication() {
        super();
    }

}
