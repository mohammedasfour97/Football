package com.moh.asf.footballawy.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class PlayerDetails extends AppCompatActivity {

    private TextView  mobile1 , smobile1 , mobile2 , smobile2 , position , sposition , available_days , savailable_days ;
    private Typeface tf ;
    private DatabaseReference databaseReference , myref ;
    private FirebaseUser user ;
    private Button thanks ;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView tvMsg;
    private Toolbar toolbar ;
    private RatingBar ratingBar ;
    private StartAppAd startAppAd ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "207904062", false);
        startAppAd = new StartAppAd(this);
        startAppAd.disableSplash();
        setContentView(R.layout.player_details);
        mobile1 = findViewById(R.id.mobile);
        smobile1 = findViewById(R.id.smobile);
        mobile2 = findViewById(R.id.mobile2);
        smobile2 = findViewById(R.id.smobile2);
        position = findViewById(R.id.position);
        sposition = findViewById(R.id.sposition);
        available_days = findViewById(R.id.available_days);
        savailable_days = findViewById(R.id.savailable_days);
        ratingBar = findViewById(R.id.ratingBar2);

        user = FirebaseAuth.getInstance().getCurrentUser();

        tf = Typeface.createFromAsset(getAssets(), "VIP Hakm Regular VIP Hakm.ttf");


        mobile1.setTypeface(tf , Typeface.BOLD);
        smobile1.setTypeface(tf, Typeface.BOLD);
        mobile2.setTypeface(tf, Typeface.BOLD);
        smobile2.setTypeface(tf, Typeface.BOLD);
        position.setTypeface(tf, Typeface.BOLD);
        sposition.setTypeface(tf, Typeface.BOLD);
        available_days.setTypeface(tf, Typeface.BOLD);
        savailable_days.setTypeface(tf, Typeface.BOLD);

        final Intent intent = getIntent();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        myref = databaseReference.child("user").child(intent.getStringExtra("key"));
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mobile1.setText(dataSnapshot.child("phone").getValue().toString());
                mobile2.setText(dataSnapshot.child("phone2").getValue().toString());
                mobile2.setText(dataSnapshot.child("phone2").getValue().toString());
                position.setText(dataSnapshot.child("position").getValue().toString());
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

        getRating();

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (user.getUid().equals(intent.getStringExtra("key"))) {
                    Toast.makeText(PlayerDetails.this, getResources().getString(R.string.self_rating), Toast.LENGTH_SHORT).show();
                    ratingBar.setRating(0);
                }
                else {
                    myref.child("rating").child(user.getUid()).setValue(v);
                }
            }
        });


    }

    private void getRating(){
        myref.child("rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user.getUid())){
                    ratingBar.setRating(dataSnapshot.child(user.getUid()).getValue(Float.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == android.R.id.home) {
            toggle();

        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
