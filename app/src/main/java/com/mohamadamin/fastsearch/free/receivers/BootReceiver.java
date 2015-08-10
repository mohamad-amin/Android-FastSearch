package com.mohamadamin.fastsearch.free.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mohamadamin.fastsearch.free.utils.RunUtils;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        RunUtils.startServicesAndListeners(context);
    }

}
