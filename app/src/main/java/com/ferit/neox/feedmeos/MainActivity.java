package com.ferit.neox.feedmeos;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity {

    ListView lvList;
    Button btnAdd;
    ImageView ivLoading;
    DatabaseReference root;
    EditText etSearch;
    private static final int LOCATION_REQUEST = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        check();
        root = FirebaseDatabase.getInstance().getReference().getRoot().child("Basic");
        setUpUI();
    }

    private void check() {

        boolean gpsStatus;
        NetworkInfo networkStatus;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkStatus = connectivityManager.getActiveNetworkInfo();

        while (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Log.e("TAG", "gps " + gpsStatus + "net " + networkStatus);

        if (!gpsStatus) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Enable GPS")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(1);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        if (networkStatus == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Connect to wifi")
                    .setCancelable(false)
                    .setPositiveButton("Connect to WIFI", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(1);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }


    private void setUpUI() {
        this.lvList = findViewById(R.id.lvList);
        this.btnAdd = findViewById(R.id.btnAdd);
        this.etSearch = findViewById(R.id.etSearch);
        this.ivLoading = findViewById(R.id.ivLoading);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Glide.with(this).load(R.drawable.loadingscreen).into(ivLoading);

        final BasicViewAdapter basicViewAdapter = new BasicViewAdapter(showList());
        final BasicViewAdapter searchViewAdapter = new BasicViewAdapter(showList());
        this.lvList.setAdapter(searchViewAdapter);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                basicViewAdapter.clear();

                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()){
                    DataSnapshot ds = dataSnapshot.child(((DataSnapshot)i.next()).getKey());
                    Iterator j = ds.getChildren().iterator();
                    while(j.hasNext())
                    {
                        String image = (String) ((DataSnapshot)j.next()).getValue();
                        String name = (String) ((DataSnapshot)j.next()).getValue();
                        basicViewAdapter.add(new BasicItem(name, image));
                    }
                    ivLoading.setVisibility(View.GONE);
                }
                searchViewAdapter.clear();
                for (int j = 0; j < basicViewAdapter.getCount(); j++){
                    searchViewAdapter.add((BasicItem) basicViewAdapter.getItem(j));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), AddActivity.class);
                startActivity(intent);
            }
        });

        this.lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LocationTask locationTask = new LocationTask(getApplicationContext());
                locationTask.start();

                Intent intent = new Intent();
                intent.putExtra("Name", basicViewAdapter.getName(i));
                intent.setClass(getApplicationContext(), AdvancedActivity.class);
                startActivity(intent);
            }
        });

        this.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchViewAdapter.clear();

                for(int j = 0; j < basicViewAdapter.getCount(); j++){
                    if (basicViewAdapter.getName(j).toLowerCase().startsWith(etSearch.getText().toString().toLowerCase())){
                        searchViewAdapter.add((BasicItem) basicViewAdapter.getItem(j));

                    }
                }
                searchViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private ArrayList<BasicItem> showList() {

        ArrayList<BasicItem> list= new ArrayList<BasicItem>();
        return list;
    }
}