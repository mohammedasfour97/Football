package com.moh.asf.footballawy.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.moh.asf.footballawy.R;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

public class MainActivity extends AppCompatActivity {

    private LinearLayout player , team , competation ;
    private TextView splayer , steam , scompetation , header ;
    private Button signout , contact_us ;
    private Typeface tf ;
    private StartAppAd startAppAd ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "207904062", false);
        startAppAd = new StartAppAd(this);
        startAppAd.loadAd(new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                startAppAd.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {

            }
        });
        startAppAd.disableSplash();
        setContentView(R.layout.activity_main);

        player = findViewById(R.id.player);
        team = findViewById(R.id.team);
        competation = findViewById(R.id.competation);
        splayer = findViewById(R.id.splayer);
        steam = findViewById(R.id.steam);
        scompetation = findViewById(R.id.cycle);
        signout = findViewById(R.id.Logout);
        contact_us = findViewById(R.id.contact_us);
        header = findViewById(R.id.header);

        tf = Typeface.createFromAsset(getAssets(), "VIP Hakm Regular VIP Hakm.ttf");

        splayer.setTypeface(tf , Typeface.BOLD);
        steam.setTypeface(tf , Typeface.BOLD);
        scompetation.setTypeface(tf , Typeface.BOLD);
        signout.setTypeface(tf , Typeface.BOLD);
        contact_us.setTypeface(tf , Typeface.BOLD);

        team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this , Teams.class));
            }
        });

        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this , SearchPlayer.class));
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent  = new Intent(MainActivity.this , LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this , ContactUs.class));
            }
        });

        competation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this , Competation.class));
            }
        });
    }
}
