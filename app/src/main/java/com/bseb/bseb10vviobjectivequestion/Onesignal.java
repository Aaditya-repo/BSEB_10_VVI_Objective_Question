package com.bseb.bseb10vviobjectivequestion;

import android.app.Application;

import com.onesignal.OneSignal;

public class Onesignal extends Application {

    private static final String ONESIGNAL_APP_ID = "0f7e1b44-e444-4a9f-8f63-52d78185cf8c";


    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

    }
}
