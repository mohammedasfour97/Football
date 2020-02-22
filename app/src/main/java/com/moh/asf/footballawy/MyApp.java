package com.moh.asf.footballawy;

import android.app.Application;

import com.moh.asf.footballawy.Utils.TypeFaceUtil;

public class MyApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        TypeFaceUtil.overrideFont(getApplicationContext(), "SERIF", "VIP Hakm Regular VIP Hakm.ttf");

    }
}