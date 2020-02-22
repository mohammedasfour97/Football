package com.moh.asf.footballawy.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moh.asf.footballawy.R;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

public class TeamDetails extends AppCompatActivity {

    private TextView mobile1 , smobile1 , mobile2 , smobile2 , position , sposition , available_days , savailable_days ;
    private Typeface tf ;
    private DatabaseReference databaseReference ;
    private Button thanks ;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView tvMsg;
    private Toolbar toolbar ;
    private StartAppAd startAppAd ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "207904062", false);
        startAppAd = new StartAppAd(this);
        startAppAd.disableSplash();
        setContentView(R.layout.team_details);
        mobile1 = findViewById(R.id.mobile);
        smobile1 = findViewById(R.id.smobile);
        mobile2 = findViewById(R.id.mobile2);
        smobile2 = findViewById(R.id.smobile2);
        position = findViewById(R.id.position);
        sposition = findViewById(R.id.sposition);
        available_days = findViewById(R.id.available_days);
        savailable_days = findViewById(R.id.savailable_days);

        tf = Typeface.createFromAsset(getAssets(), "VIP Hakm Regular VIP Hakm.ttf");

        mobile1.setTypeface(tf , Typeface.BOLD);
        smobile1.setTypeface(tf, Typeface.BOLD);
        mobile2.setTypeface(tf, Typeface.BOLD);
        smobile2.setTypeface(tf, Typeface.BOLD);
        position.setTypeface(tf, Typeface.BOLD);
        sposition.setTypeface(tf, Typeface.BOLD);
        available_days.setTypeface(tf, Typeface.BOLD);
        savailable_days.setTypeface(tf, Typeface.BOLD);

        Intent intent = getIntent();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("teams").child(intent.getStringExtra("key")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mobile1.setText(dataSnapshot.child("phone").getValue().toString());
                mobile2.setText(dataSnapshot.child("phone2").getValue().toString());
                mobile2.setText(dataSnapshot.child("phone2").getValue().toString());
                if (dataSnapshot.child("complete").getValue().toString().equals("complete")){
                    position.setText(getResources().getString(R.string.completed_team));
                }
                else {
                    position.setText(dataSnapshot.child("number").getValue().toString());
                }
                for (int a = 1 ; a <= 7 ; a++){
                    if (dataSnapshot.child("day" + a).exists()){
                        if (a != 1) {
                            available_days.setText(available_days.getText() + " - " + dataSnapshot.child("day" + a).getValue().toString());
                        }
                        else {
                            available_days.setText(dataSnapshot.child("day" + a).getValue().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
