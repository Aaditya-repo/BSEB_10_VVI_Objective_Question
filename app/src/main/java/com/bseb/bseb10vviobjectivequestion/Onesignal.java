package com.bseb.bseb10vviobjectivequestion;

import android.app.Application;

import com.onesignal.OneSignal;

public class Onesignal extends Application {

    private static final String ONESIGNAL_APP_ID = "8d271c6d-4bd3-46d6-8435-d870afbfdccd";


    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

    }
}
