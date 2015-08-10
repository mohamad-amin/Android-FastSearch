package com.mohamadamin.fastsearch.free.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DisplayUtils {

    public static int convertDpToPx(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return  (int) (dp*scale + 0.5f);
    }

    public static FrameLayout wrapViewInFrameLayout(View view, int marginLeftDp,
                                        int marginRightDp, int marginTopDp, int marginBottomDp) {

        FrameLayout container = new FrameLayout(view.getContext());
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.
                MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.leftMargin = convertDpToPx(view.getContext(), marginLeftDp);
        params.rightMargin = convertDpToPx(view.getContext(), marginRightDp);
        params.topMargin = convertDpToPx(view.getContext(), marginTopDp);
        params.bottomMargin = convertDpToPx(view.getContext(), marginBottomDp);

        view.setLayoutParams(params);
        container.addView(view);

        return container;

    }

}
