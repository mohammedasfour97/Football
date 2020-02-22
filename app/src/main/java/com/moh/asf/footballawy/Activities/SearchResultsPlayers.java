package com.moh.asf.footballawy.Activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moh.asf.footballawy.Models.Team;
import com.moh.asf.footballawy.R;
import com.moh.asf.footballawy.Utils.CustomTypefaceSpan;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchResultsPlayers extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Team> itemsList;
    private SearchResultsPlayers.PlayerAdapter playerAdapter;
    private DatabaseReference databaseReference ;
    private String scity, place , search ;
    private final int CALL_REQUEST = 100;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView tvMsg;
    private Toolbar toolbar ;
    private Intent intent;
    private SpannableString hint;
    private RecyclerView.LayoutManager mLayoutManager ;
    private View parentLayout ;
    private Snackbar snackbar ;
    private Typeface tf ;
    private Spinner positions ;
    private StartAppAd startAppAd ;
    private String position , search_text ;
    private android.support.v7.widget.SearchView searchView ;

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
        setContentView(R.layout.serch_results_players);
        positions = findViewById(R.id.positions);
        parentLayout = findViewById(android.R.id.content);
        intent = getIntent();
        search = intent.getStringExtra("search");
        scity = intent.getStringExtra("gover");
        place = intent.getStringExtra("place");
        tvMsg = (TextView) findViewById(R.id.header);

        position = search_text = "" ;

        tf = Typeface.createFromAsset(getAssets(), "VIP Hakm Regular VIP Hakm.ttf");

        if (isNetworkConnected() == false){
            Toast.makeText(this, "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();
        }

        TypefaceSpan typefaceSpan = new CustomTypefaceSpan(tf);
        hint = new SpannableString(getResources().getString(R.string.search_player_hint));
        hint.setSpan(typefaceSpan, 0, hint.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        itemsList = new ArrayList<>();
        playerAdapter = new SearchResultsPlayers.PlayerAdapter(this , itemsList);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        itemsList.clear();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        fill_players();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = findViewById(R.id.searchview);
        searchView.onActionViewExpanded();
        searchView.setIconifiedByDefault(false);
        searchView.clearFocus();
        searchView.setFocusable(false);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(hint);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        searchView.requestFocus();
        Intent intent1 = getIntent();
        searchView.setQuery(place , false);
        playerAdapter.notifyDataSetChanged();

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });


        // listening to search query text change
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                playerAdapter.getFilter().filter(query);
                search_text = query ;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                playerAdapter.getFilter().filter(query);
                search_text = query ;
                return false;
            }
        });

        final EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTypeface(tf);
        searchEditText.setTextColor(getResources().getColor(R.color.tw__composer_black));

        MySpinnerAdapter dataAdapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, Arrays.asList(getResources().getStringArray(R.array.position)));
        dataAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        positions.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();

        positions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                searchEditText.setText("");
                if (adapterView.getItemAtPosition(i).toString().equals("كل المراكز")){
                    position = "" ;
                    playerAdapter.getFilter().filter("");
                }
                else {
                    playerAdapter.getFilter().filter(adapterView.getItemAtPosition(i).toString());
                    position = adapterView.getItemAtPosition(i).toString() ;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }

    private void show_snamckbar () {
        snackbar =  Snackbar.make(parentLayout , getResources().getString(R.string.no_results) , Snackbar.LENGTH_LONG) ;
        View sb = snackbar.getView();
        sb.setBackgroundColor(getResources().getColor(R.color.white));
        TextView tv = (TextView) sb.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.colorPrimary));
        tv.setTypeface(tf , Typeface.BOLD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackbar.show();
    }

    private void fill_players (){
        recyclerView.setAdapter(playerAdapter);
        if (place.equals("")) {
            databaseReference.child("user").orderByChild("governorate").equalTo(scity).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()){
                        show_snamckbar () ;
                    }
                    else {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.child("visual").getValue().equals("visual")) {
                                Team taem = new Team();
                                taem = ds.getValue(Team.class);
                                taem.key = ds.getKey();
                                itemsList.add(taem);
                                playerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else {
            databaseReference.child("user").orderByChild("governorate").equalTo(scity).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()){
                        show_snamckbar () ;
                    }
                    else {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.child("place").getValue().equals(place)) {
                                if (ds.child("visual").getValue().equals("visual")) {
                                    Team taem = new Team();
                                    taem = ds.getValue(Team.class);
                                    taem.key = ds.getKey();
                                    itemsList.add(taem);
                                    playerAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (itemsList.size() == 0) {
                show_snamckbar () ;
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private class PlayerAdapter extends RecyclerView.Adapter<SearchResultsPlayers.PlayerAdapter.MyViewHolder> implements Filterable{
        private Context context;
        private List<Team> departmentsList;
        private List<Team> ListFiltered;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            Context context;
            public TextView name , location  , number , position ;
            public LinearLayout call ;
            public RatingBar ratingBar ;

            public MyViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.name);
                location = (TextView) view.findViewById(R.id.location);
                number = (TextView) view.findViewById(R.id.number);
                call =  view.findViewById(R.id.call);
                position = view.findViewById(R.id.number_of_players);
                ratingBar = view.findViewById(R.id.ratingBar2);
                context = itemView.getContext();


            }
        }

        public PlayerAdapter(Context context, List<Team> departmentsList) {
            this.context = context;
            this.departmentsList = departmentsList;
            this.ListFiltered = departmentsList;
        }

        @Override
        public SearchResultsPlayers.PlayerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.player_item, parent, false);

            return new SearchResultsPlayers.PlayerAdapter.MyViewHolder(itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(final SearchResultsPlayers.PlayerAdapter.MyViewHolder holder, final int position) {
            final Team team = ListFiltered.get(position);

            holder.name.setText(team.getName());
            holder.location.setText(team.getPlace());
            holder.number.setText(team.getPhone1());
            holder.position.setText(team.getPosition());
            setRating(holder.ratingBar , team.getKey());
            Typeface tf = Typeface.createFromAsset(getAssets(), "VIP Hakm Regular VIP Hakm.ttf");
            holder.name.setTypeface(tf, Typeface.BOLD);
            holder.location.setTypeface(tf, Typeface.BOLD);
            holder.number.setTypeface(tf, Typeface.BOLD);
            holder.position.setTypeface(tf, Typeface.BOLD);

            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try
                    {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        {
                            if (ActivityCompat.checkSelfPermission(SearchResultsPlayers.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling

                                ActivityCompat.requestPermissions(SearchResultsPlayers.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);

                            }
                        }

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + team.getPhone1().toString().trim()));
                        startActivity(callIntent);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SearchResultsPlayers.this , PlayerDetails.class);
                    intent.putExtra("key" , team.getKey());
                    startActivity(intent);

                }
            });

        }

        @Override
        public int getItemCount() {
            return ListFiltered.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (position.isEmpty()) {
                        ListFiltered = departmentsList;
                    } else {
                        List<Team> filteredList = new ArrayList<>();
                        for (Team row : departmentsList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getPosition().toLowerCase().contains(position.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }

                        ListFiltered = filteredList;
                    }

                    if (search_text != ""){
                        List<Team> filteredList = new ArrayList<>();
                        for (Team row : ListFiltered) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getName().toLowerCase().contains(search_text.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }

                        ListFiltered = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = ListFiltered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    ListFiltered = (ArrayList<Team>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        private void setRating (final RatingBar ratingBar , String key) {
            final float[] sum = {0};
            final long[] number = {0};
            databaseReference.child("user").child(key).child("rating").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    number[0] = dataSnapshot.getChildrenCount();
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                         sum[0] += ds.getValue(Float.class);
                    }
                    ratingBar.setRating(sum[0] / number [0]);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }



}


