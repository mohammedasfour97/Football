package com.moh.asf.footballawy.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moh.asf.footballawy.R;
import com.moh.asf.footballawy.Utils.CustomTypefaceSpan;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MakePlayer extends AppCompatActivity {

    private EditText name , place  , phone , phone2 ;
    private Spinner governorates , position ;
    private RadioGroup  radio_visual ;
    private RadioButton visual , invisual ;
    private Button save , cancel ;
    private TextView build , available_days , description ;
    private Typeface tf ;
    private SpannableString sname , splace , sphone , sphone2 ;
    private String gover , detect , getname , getplace , getphone , getphone2 , sposition ;
    private CheckBox saturday , sunday , monday , tuesday , wednesday , thursday , friday , all_week ;
    private List<String> weeklist ;
    private List<CheckBox> checkBoxList ;
    private DatabaseReference databaseReference , myref ;
    private FirebaseUser user ;
    private Intent intent ;
    private MySpinnerAdapter dataAdapter  , dataAdapter2 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(this, "207904062", false);
        setContentView(R.layout.make_player);

        name = findViewById(R.id.name);
        place = findViewById(R.id.place);
        phone = findViewById(R.id.phone);
        phone2 = findViewById(R.id.phone2);
        governorates = findViewById(R.id.governorates);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        saturday = findViewById(R.id.saturday);
        sunday = findViewById(R.id.sunday);
        monday = findViewById(R.id.monday);
        tuesday = findViewById(R.id.tuesday);
        wednesday = findViewById(R.id.wednesday);
        thursday = findViewById(R.id.thursday);
        friday = findViewById(R.id.friday);
        all_week = findViewById(R.id.all_week);
        build = findViewById(R.id.build);
        available_days = findViewById(R.id.available_days);
        radio_visual = findViewById(R.id.radio_visual);
        visual = findViewById(R.id.visual);
        description = findViewById(R.id.description);
        position = findViewById(R.id.position);
        invisual = findViewById(R.id.invisual);

        tf = Typeface.createFromAsset(getAssets(), "VIP Hakm Regular VIP Hakm.ttf");

        name.setTypeface(tf, Typeface.BOLD);
        place.setTypeface(tf, Typeface.BOLD);
        phone2.setTypeface(tf, Typeface.BOLD);
        phone.setTypeface(tf, Typeface.BOLD);
        save.setTypeface(tf, Typeface.BOLD);
        cancel.setTypeface(tf, Typeface.BOLD);
        available_days.setTypeface(tf , Typeface.BOLD);
        saturday.setTypeface(tf);
        sunday.setTypeface(tf);
        monday.setTypeface(tf);
        tuesday.setTypeface(tf);
        wednesday.setTypeface(tf);
        thursday.setTypeface(tf);
        friday.setTypeface(tf);
        all_week.setTypeface(tf);
        visual.setTypeface(tf);
        invisual.setTypeface(tf);
        description.setTypeface(tf, Typeface.BOLD);
        build.setTypeface(tf , Typeface.BOLD);

        weeklist = new ArrayList<>();
        weeklist.clear();

        checkBoxList = new ArrayList<>();
        checkBoxList.clear();
        checkBoxList.add(saturday);
        checkBoxList.add(sunday);
        checkBoxList.add(monday);
        checkBoxList.add(tuesday);
        checkBoxList.add(wednesday);
        checkBoxList.add(thursday);
        checkBoxList.add(friday);
        checkBoxList.add(all_week);

        for (CheckBox checkBox : checkBoxList){
            checkBox.setTypeface(tf);
            if (checkBox.getId() != R.id.all_week){
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b == true) {
                            all_week.setChecked(false);
                        }
                    }
                });
            }
        }

        intent = getIntent();

        databaseReference = FirebaseDatabase.getInstance().getReference() ;
        user = FirebaseAuth.getInstance().getCurrentUser();

        TypefaceSpan typefaceSpan = new CustomTypefaceSpan(tf);
        sname = new SpannableString(getResources().getString(R.string.hint_name));
        sname.setSpan(typefaceSpan, 0, sname.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        name.setHint(sname);

        splace = new SpannableString(getResources().getString(R.string.mante2a_name));
        splace.setSpan(typefaceSpan, 0, splace.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        place.setHint(splace);

        sphone = new SpannableString(getResources().getString(R.string.phone1));
        sphone.setSpan(typefaceSpan, 0, sphone.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        phone.setHint(sphone);

        sphone2 = new SpannableString(getResources().getString(R.string.phone2));
        sphone2.setSpan(typefaceSpan, 0, sphone2.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        phone2.setHint(sphone2);

        dataAdapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, Arrays.asList(getResources().getStringArray(R.array.Egypt)));
        dataAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        governorates.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();

        governorates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gover = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dataAdapter2 = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, Arrays.asList(getResources().getStringArray(R.array.position)));
        dataAdapter2.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        position.setAdapter(dataAdapter2);
        dataAdapter2.notifyDataSetChanged();

        position.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sposition = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (intent.getStringExtra("edit").equals("yes")){
            setEdit ();
        }
        else {
            myref = databaseReference.child("user").child(user.getUid());
            detect = "visual" ;
        }

        radio_visual.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.visual){
                    detect = "visual" ;
                }
                else  {
                    detect = "invisual" ;
                }
            }

        });

        all_week.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    for (CheckBox checkBox : checkBoxList){
                        checkBox.setChecked(false);
                    }
                    all_week.setChecked(true);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getname = name.getText().toString();
                getplace = place.getText().toString();
                getphone = phone.getText().toString();
                getphone2 = phone2.getText().toString();
                if (TextUtils.isEmpty(getname)) {
                    name.setError(getResources().getString(R.string.error_name));
                }
                else if (TextUtils.isEmpty(getplace)) {
                    place.setError(getResources().getString(R.string.error_place));
                }
                else if (TextUtils.isEmpty(getphone)) {
                    phone.setError(getResources().getString(R.string.error_phone1));
                }
                else {
                    if (intent.getStringExtra("edit").equals("yes")){
                        publish_player();
                        MakePlayer.this.finish();
                    }
                    else {
                        publish_player();
                        Intent intent = new Intent(MakePlayer.this, MakeTeam.class);
                        intent.putExtra("edit", "no");
                        startActivity(intent);
                        MakePlayer.this.finish();
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intent.getStringExtra("edit").equals("yes")){
                    MakePlayer.this.finish();
                }
                else {
                    Intent intent = new Intent(MakePlayer.this, MakeTeam.class);
                    intent.putExtra("edit", "no");
                    startActivity(intent);
                    MakePlayer.this.finish();
                }
            }
        });

    }

    private void check_checkbox(){
        if (saturday.isChecked()){
            weeklist.add(getResources().getString(R.string.saturday));
        }
        else if (saturday.isChecked()==false){
            if (weeklist.contains(getResources().getString(R.string.saturday))){
                weeklist.remove(getResources().getString(R.string.saturday));
            }
        }
        if (sunday.isChecked()){
            weeklist.add(getResources().getString(R.string.sunday));
        }
        else if (sunday.isChecked()==false){
            if (weeklist.contains(getResources().getString(R.string.sunday))){
                weeklist.remove(getResources().getString(R.string.sunday));
            }
        }
        if (monday.isChecked()){
            weeklist.add(getResources().getString(R.string.monday));
        }
        else if (monday.isChecked()==false){
            if (weeklist.contains(getResources().getString(R.string.monday))){
                weeklist.remove(getResources().getString(R.string.monday));
            }
        }
        if (tuesday.isChecked()){
            weeklist.add(getResources().getString(R.string.tuesday));
        }
        else if (tuesday.isChecked()==false){
            if (weeklist.contains(getResources().getString(R.string.tuesday))){
                weeklist.remove(getResources().getString(R.string.tuesday));
            }
        }
        if (wednesday.isChecked()){
            weeklist.add(getResources().getString(R.string.wednesday));
        }
        else if (wednesday.isChecked()==false){
            if (weeklist.contains(getResources().getString(R.string.wednesday))){
                weeklist.remove(getResources().getString(R.string.wednesday));
            }
        }
        if (thursday.isChecked()){
            weeklist.add(getResources().getString(R.string.thursday));
        }
        else if (thursday.isChecked()==false){
            if (weeklist.contains(getResources().getString(R.string.thursday))){
                weeklist.remove(getResources().getString(R.string.thursday));
            }
        }
        if (friday.isChecked()){
            weeklist.add(getResources().getString(R.string.friday));
        }
        else if (friday.isChecked()==false){
            if (weeklist.contains(getResources().getString(R.string.friday))){
                weeklist.remove(getResources().getString(R.string.friday));
            }
        }
        if (all_week.isChecked()){
            weeklist.add(getResources().getString(R.string.all_week));
        }
        else if (all_week.isChecked()==false){
            if (weeklist.contains(getResources().getString(R.string.all_week))){
                weeklist.remove(getResources().getString(R.string.all_week));
            }
        }
    }

    private void publish_player () {
        myref.child("name").setValue(getname);
        myref.child("place").setValue(getplace);
        myref.child("phone").setValue(getphone);
        myref.child("phone2").setValue(getphone2);
        myref.child("governorate").setValue(gover);
        myref.child("position").setValue(sposition);
        myref.child("visual").setValue(detect);
        myref.child("email").setValue(user.getEmail());
        check_checkbox();
        int count = 1 ;
        for (String day : weeklist){
            myref.child("day" + count).setValue(day);
            count++ ;
        }
    }

   /* private void clear_checks(){
        int count = 1 ;
        for (String color : color_items){
            myref.child("color"+count).removeValue();
            count++;
        }
        color_items.clear();
    }*/

    private void setEdit () {
        myref = databaseReference.child("user").child(user.getUid());
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    name.setText(dataSnapshot.child("name").getValue().toString());
                    detect = dataSnapshot.child("visual").getValue().toString();
                    if (detect.equals("visual")) {
                        visual.setChecked(true);
                    } else {
                        invisual.setChecked(true);
                    }
                    place.setText(dataSnapshot.child("place").getValue().toString());
                    phone.setText(dataSnapshot.child("phone").getValue().toString());
                    phone2.setText(dataSnapshot.child("phone2").getValue().toString());
                    governorates.setSelection(dataAdapter.getPosition(dataSnapshot.child("governorate").getValue().toString()));
                    position.setSelection(dataAdapter2.getPosition(dataSnapshot.child("position").getValue().toString()));
                    String[] days = new String[7];
                    for (int a = 1; a <= 7; a++) {
                        if (dataSnapshot.child("day" + a).exists()) {
                            days[a] = dataSnapshot.child("day" + a).getValue().toString();
                            switch (days[a]) {
                                case "السبت": {
                                    saturday.setChecked(true);
                                    break;
                                }
                                case "الاحد": {
                                    sunday.setChecked(true);
                                    break;
                                }
                                case "الاثنين": {
                                    monday.setChecked(true);
                                    break;
                                }
                                case "الثلاثاء": {
                                    tuesday.setChecked(true);
                                    break;
                                }
                                case "الأربعاء": {
                                    wednesday.setChecked(true);
                                    break;
                                }
                                case "الخميس": {
                                    thursday.setChecked(true);
                                    break;
                                }
                                case "الجمعة": {
                                    friday.setChecked(true);
                                    break;
                                }
                                case "طوال الأسبوع": {
                                    all_week.setChecked(true);
                                    break;
                                }

                            }
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


