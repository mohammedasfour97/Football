package com.moh.asf.footballawy.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moh.asf.footballawy.R;
import com.startapp.android.publish.adsCommon.StartAppSDK;

public class Teams extends AppCompatActivity {

    private Button completed , uncompleted ;
    private Typeface tf ;
    private Intent intent ;
    private TextView myteam , thome , tcall ;
    private LinearLayout my_team , home , call ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "207904062", false);

        setContentView(R.layout.team);

        completed = findViewById(R.id.search_completed);
        uncompleted = findViewById(R.id.search_uncompleted);
        myteam = findViewById(R.id.my_team);
        my_team = findViewById(R.id.myteam);
        thome = findViewById(R.id.thome);
        tcall = findViewById(R.id.tcall);
        home = findViewById(R.id.home);
        call = findViewById(R.id.contact_us);

        tf = Typeface.createFromAsset(getAssets(), "VIP Hakm Regular VIP Hakm.ttf");

        completed.setTypeface(tf);
        uncompleted.setTypeface(tf);
        myteam.setTypeface(tf, Typeface.BOLD);
        tcall.setTypeface(tf , Typeface.BOLD);
        thome.setTypeface(tf , Typeface.BOLD);

        intent = new Intent(Teams.this , SearchTeam.class);

        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("complete" , "complete");
                startActivity(intent);
            }
        });

        uncompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("complete" , "uncomplete");
                startActivity(intent);
            }
        });

        my_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Teams.this , MakeTeam.class);
                intent.putExtra("edit" , "yes");
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Teams.this , MainActivity.class);
                startActivity(intent);
                Teams.this.finish();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
