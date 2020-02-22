package com.moh.asf.footballawy.Activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.moh.asf.footballawy.R;
import com.startapp.android.publish.adsCommon.StartAppSDK;

public class Competation extends AppCompatActivity {

    private TextView header , body ;
    private Typeface tf ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "207904062", false);
        setContentView(R.layout.competation_text);

        header = findViewById(R.id.header);
        body = findViewById(R.id.body);

        tf = Typeface.createFromAsset(getAssets(), "VIP Hakm Regular VIP Hakm.ttf");

        header.setTypeface(tf , Typeface.BOLD);
        body.setTypeface(tf);
    }
}

