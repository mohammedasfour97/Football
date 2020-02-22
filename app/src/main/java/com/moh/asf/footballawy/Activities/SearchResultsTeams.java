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
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.SearchView;
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
import java.util.List;

public class SearchResultsTeams extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Team> itemsList;
    private SearchResultsTeams.CompletedTeamAdapter completedTeamAdapter;
    private SearchResultsTeams.UncompletedTeamAdapter uncompletedTeamAdapter;
    private DatabaseReference databaseReference;
    private String scity, place, search;
    private final int CALL_REQUEST = 100;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView tvMsg;
    private SpannableString hint;
    private Toolbar toolbar;
    private Intent intent;
    private RecyclerView.LayoutManager mLayoutManager;
    private View parentLayout;
    private Snackbar snackbar;
    private Typeface tf;
    private StartAppAd startAppAd;
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
        setContentView(R.layout.recycler);
        parentLayout = findViewById(android.R.id.content);
        intent = getIntent();
        search = intent.getStringExtra("search");
        scity = intent.getStringExtra("gover");
        place = intent.getStringExtra("place");
        tvMsg = (TextView) findViewById(R.id.header);

        tf = Typeface.createFromAsset(getAssets(), "VIP Hakm Regular VIP Hakm.ttf");

        if (isNetworkConnected() == false) {
            Toast.makeText(this, "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        itemsList = new ArrayList<>();
        completedTeamAdapter = new CompletedTeamAdapter(this, itemsList);
        uncompletedTeamAdapter = new UncompletedTeamAdapter(this, itemsList);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        itemsList.clear();

        TypefaceSpan typefaceSpan = new CustomTypefaceSpan(tf);
        hint = new SpannableString(getResources().getString(R.string.search_team_hint));
        hint.setSpan(typefaceSpan, 0, hint.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (search.equals("complete")) {
            fill_completed();

        } else if (search.equals("uncomplete")) {
            fill_uncompleted();
        }

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
        completedTeamAdapter.notifyDataSetChanged();
        uncompletedTeamAdapter.notifyDataSetChanged();

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
                completedTeamAdapter.getFilter().filter(query);
                uncompletedTeamAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                completedTeamAdapter.getFilter().filter(query);
                uncompletedTeamAdapter.getFilter().filter(query);
                return false;
            }
        });
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTypeface(tf);
        searchEditText.setTextColor(getResources().getColor(R.color.tw__composer_black));

    }

    private void show_snamckbar() {
        snackbar = Snackbar.make(parentLayout, getResources().getString(R.string.no_results), Snackbar.LENGTH_LONG);
        View sb = snackbar.getView();
        sb.setBackgroundColor(getResources().getColor(R.color.white));
        TextView tv = (TextView) sb.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(getResources().getColor(R.color.colorPrimary));
        tv.setTypeface(tf, Typeface.BOLD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackbar.show();
    }

    /////////////////////////   Fill methods ///////////////////////////////////


    private void fill_completed() {
        recyclerView.setAdapter(completedTeamAdapter);
        if (place.equals("")) {
            databaseReference.child("teams").orderByChild("governorate").equalTo(scity).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        show_snamckbar();
                    } else {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.child("complete").getValue().equals("complete")) {
                                if (ds.child("visual").getValue().equals("visual")) {
                                    Team taem = new Team();
                                    taem = ds.getValue(Team.class);
                                    taem.key = ds.getKey();
                                    itemsList.add(taem);
                                    completedTeamAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            databaseReference.child("teams").orderByChild("governorate").equalTo(scity).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("place").getValue().equals(place)) {
                            if (ds.child("complete").getValue().equals("complete")) {
                                if (ds.child("visual").getValue().equals("visual")) {
                                    Team taem = new Team();
                                    taem = ds.getValue(Team.class);
                                    taem.key = ds.getKey();
                                    itemsList.add(taem);
                                    completedTeamAdapter.notifyDataSetChanged();
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
                show_snamckbar();
            }
        }
    }

    private void fill_uncompleted() {
        recyclerView.setAdapter(uncompletedTeamAdapter);
        if (place.equals("")) {
            databaseReference.child("teams").orderByChild("governorate").equalTo(scity).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        show_snamckbar();
                    } else {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.child("complete").getValue().equals("uncomplete")) {
                                if (ds.child("visual").getValue().equals("visual")) {
                                    Team taem = new Team();
                                    taem = ds.getValue(Team.class);
                                    taem.key = ds.getKey();
                                    itemsList.add(taem);
                                    uncompletedTeamAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            databaseReference.child("teams").orderByChild("governorate").equalTo(scity).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("place").getValue().equals(place)) {
                            if (ds.child("complete").getValue().equals("uncomplete")) {
                                if (ds.child("visual").getValue().equals("visual")) {
                                    Team taem = new Team();
                                    taem = ds.getValue(Team.class);
                                    taem.key = ds.getKey();
                                    itemsList.add(taem);
                                    uncompletedTeamAdapter.notifyDataSetChanged();
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
                show_snamckbar();
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    ////////////////////////////// Adapters //////////////////////////////////////////////////////

    private class UncompletedTeamAdapter extends RecyclerView.Adapter<UncompletedTeamAdapter.MyViewHolder> implements Filterable {
        private Context context;
        private List<Team> departmentsList;
        private List<Team> ListFiltered;


        public class MyViewHolder extends RecyclerView.ViewHolder {
            Context context;
            public TextView name, location, number, number_of_players;
            public LinearLayout call;

            public MyViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.name);
                location = (TextView) view.findViewById(R.id.location);
                number = (TextView) view.findViewById(R.id.number);
                call = view.findViewById(R.id.call);
                number_of_players = view.findViewById(R.id.number_of_players);
                context = itemView.getContext();


            }
        }

        public UncompletedTeamAdapter(Context context, List<Team> departmentsList) {
            this.context = context;
            this.departmentsList = departmentsList;
            this.ListFiltered = departmentsList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.uncompleted_team_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final Team team = ListFiltered.get(position);

            holder.name.setText(team.getName());
            holder.location.setText(team.getPlace());
            holder.number.setText(team.getPhone1());
            holder.number_of_players.setText(team.getNumber());
            Typeface tf = Typeface.createFromAsset(getAssets(), "VIP Hakm Regular VIP Hakm.ttf");
            holder.name.setTypeface(tf, Typeface.BOLD);
            holder.location.setTypeface(tf, Typeface.BOLD);
            holder.number.setTypeface(tf, Typeface.BOLD);
            holder.number_of_players.setTypeface(tf, Typeface.BOLD);

            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(SearchResultsTeams.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling

                                ActivityCompat.requestPermissions(SearchResultsTeams.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);

                            }
                        }

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + team.getPhone1().toString().trim()));
                        startActivity(callIntent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SearchResultsTeams.this, TeamDetails.class);
                    intent.putExtra("key", team.getKey());
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
                    if (charString.isEmpty()) {
                        ListFiltered = departmentsList;
                    } else {
                        List<Team> filteredList = new ArrayList<>();
                        for (Team row : departmentsList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getPlace().toLowerCase().contains(charString.toLowerCase())) {
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

    }

    private class CompletedTeamAdapter extends RecyclerView.Adapter<CompletedTeamAdapter.MyViewHolder> implements Filterable {
        private Context context;
        private List<Team> departmentsList;
        private List<Team> ListFiltered;


        public class MyViewHolder extends RecyclerView.ViewHolder {
            Context context;
            public TextView name, location, number, position;
            public LinearLayout call;

            public MyViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.name);
                location = (TextView) view.findViewById(R.id.location);
                number = (TextView) view.findViewById(R.id.number);
                call = view.findViewById(R.id.call);
                position = view.findViewById(R.id.number_of_players);
                context = itemView.getContext();


            }
        }

        public CompletedTeamAdapter(Context context, List<Team> departmentsList) {
            this.context = context;
            this.departmentsList = departmentsList;
            this.ListFiltered = departmentsList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.completed_team_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final Team team = ListFiltered.get(position);

            holder.name.setText(team.getName());
            holder.location.setText(team.getPlace());
            holder.number.setText(team.getPhone1());
            Typeface tf = Typeface.createFromAsset(getAssets(), "VIP Hakm Regular VIP Hakm.ttf");
            holder.name.setTypeface(tf, Typeface.BOLD);
            holder.location.setTypeface(tf, Typeface.BOLD);
            holder.number.setTypeface(tf, Typeface.BOLD);

            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(SearchResultsTeams.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling

                                ActivityCompat.requestPermissions(SearchResultsTeams.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);

                            }
                        }

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + team.getPhone1().toString().trim()));
                        startActivity(callIntent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SearchResultsTeams.this, TeamDetails.class);
                    intent.putExtra("key", team.getKey());
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
                    if (charString.isEmpty()) {
                        ListFiltered = departmentsList;
                    } else {
                        List<Team> filteredList = new ArrayList<>();
                        for (Team row : departmentsList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
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


    }
}


