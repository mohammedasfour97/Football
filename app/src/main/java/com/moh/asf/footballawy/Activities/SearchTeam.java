package com.moh.asf.footballawy.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moh.asf.footballawy.R;
import com.moh.asf.footballawy.Utils.CustomTypefaceSpan;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.util.Arrays;

public class SearchTeam extends AppCompatActivity {

    private TextView header, search_text  ;
    private EditText place;
    private Spinner  cities;
    private ConstraintLayout search;
    private SpannableString spplace ;
    private Typeface tf ;
    private String scity ;
    private DatabaseReference databaseReference ;
    private RadioGroup radio_places ;
    private RadioButton choose_all , choose_specific ;
    private String detect , complete ;
    private Intent intent ;
    private TextView myteam , thome , tcall ;
    private LinearLayout my_team , home , call ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "207904062", false);
        setContentView(R.layout.search_team);

        header = findViewById(R.id.header);
        place = findViewById(R.id.place);
        cities = findViewById(R.id.cities);
        search = findViewById(R.id.search);
        search_text = findViewById(R.id.searchtext);
        radio_places = findViewById(R.id.radio_places);
        choose_all = findViewById(R.id.choose_all);
        choose_specific = findViewById(R.id.choose_specefic);
        my_team = findViewById(R.id.myteam);
        home = findViewById(R.id.home);
        call = findViewById(R.id.contact_us);
        myteam = findViewById(R.id.my_team);
        thome = findViewById(R.id.thome);
        tcall = findViewById(R.id.tcall);

        intent = getIntent();
        complete = intent.getStringExtra("complete");

        if (complete.equals("complete")){
            header.setText(getResources().getString(R.string.search_uncompleted));
        }
        else {
            header.setText(getResources().getString(R.string.uncompletable_team));
        }

        place.setVisibility(View.GONE);
        detect = "all" ;

        tf = Typeface.createFromAsset(getAssets(), "VIP Hakm Regular VIP Hakm.ttf");

        search_text.setTypeface(tf , Typeface.BOLD);
        header.setTypeface(tf, Typeface.BOLD);
        choose_specific.setTypeface(tf, Typeface.BOLD);
        choose_all.setTypeface(tf, Typeface.BOLD);
        myteam.setTypeface(tf, Typeface.BOLD);
        tcall.setTypeface(tf , Typeface.BOLD);
        thome.setTypeface(tf , Typeface.BOLD);

        TypefaceSpan typefaceSpan = new CustomTypefaceSpan(tf);
        spplace = new SpannableString(getResources().getString(R.string.mante2a_name));
        spplace.setSpan(typefaceSpan, 0, spplace.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        place.setHint(spplace);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        MySpinnerAdapter dataAdapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, Arrays.asList(getResources().getStringArray(R.array.Egypt)));
        dataAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        cities.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();

        radio_places.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i==R.id.choose_all){
                    place.setVisibility(View.GONE);
                    detect = "all" ;
                }

                else if (i==R.id.choose_specefic){
                    place.setVisibility(View.VISIBLE);
                    detect = "specific" ;
                }
            }
        });

        cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                scity = adapterView.getItemAtPosition(i).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String getplace = place.getText().toString();

                if (TextUtils.isEmpty(getplace)) {
                    if (detect.equals("specific")) {
                        place.setError(getResources().getString(R.string.error_place));
                    }
                    else {
                        Intent intent = new Intent(SearchTeam.this , SearchResultsTeams.class);
                        intent.putExtra("gover" , scity);
                        intent.putExtra("place" , "");
                        SearchTeam.this.finish();
                    }
                }

                Intent intent = new Intent(SearchTeam.this , SearchResultsTeams.class);
                intent.putExtra("gover" , scity);
                intent.putExtra("place" , getplace);
                intent.putExtra("search" , complete);
                startActivity(intent);

            }
        });

        my_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchTeam.this , MakeTeam.class);
                intent.putExtra("edit" , "yes");
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchTeam.this , MainActivity.class);
                startActivity(intent);
                finish();
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
