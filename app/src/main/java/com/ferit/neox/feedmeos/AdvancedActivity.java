package com.ferit.neox.feedmeos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class AdvancedActivity extends AppCompatActivity {

    TextView tvDescription, tvTitle, tvAdress, tvCity;
    Button btnTake;
    RatingBar rbRating;
    ImageView ivAdvIMG;
    ImageButton ibGrade;
    DatabaseReference root, rootGrade;
    String name, chosenAdress;
    ArrayList<String> adressList = new ArrayList<>(), nameList = new ArrayList<>();
    Location location;
    FusedLocationProviderClient client;

    private static final int LOCATION_REQUEST = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced);
        Location();
        name = getIntent().getExtras().get("Name").toString();
        root = FirebaseDatabase.getInstance().getReference().getRoot().child("Advanced");
        rootGrade = FirebaseDatabase.getInstance().getReference().getRoot().child("Grades");
        setUpUI();
    }


    private void Location() {

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
        client = LocationServices.getFusedLocationProviderClient(this);

        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location mLocation) {
                while (mLocation == null);
                location = mLocation;
            }
        });
        LocationRequest locRequest = new LocationRequest();
        client.requestLocationUpdates(locRequest, mLocationCallback, Looper.myLooper());
    }

    private void setUpUI() {
        this.tvDescription = findViewById(R.id.tvDescription);
        this.tvTitle = findViewById(R.id.tvTitle);
        this.tvAdress = findViewById(R.id.tvAdress);
        this.tvCity = findViewById(R.id.tvCity);
        this.btnTake = findViewById(R.id.btnTake);
        this.ivAdvIMG = findViewById(R.id.ivAdvIMG);
        this.rbRating = findViewById(R.id.rbRating);
        this.ibGrade = findViewById(R.id.ibGrade);

        rbRating.setStepSize(0.5f);
        rbRating.setRating(0f);

        final SharedPreferences preferences =  getSharedPreferences("gradeFlag", 0);

        rbRating.setStepSize(0.5f);
        rbRating.setRating(0f);
        rbRating.setIsIndicator(false);

        rbRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (preferences.getInt(name, 0) == 0){
                    ibGrade.setVisibility(View.VISIBLE);
                    rbRating.setStepSize(1f);
                } else {
                    ibGrade.setVisibility(View.GONE);
                    rbRating.setIsIndicator(true);
                }
            }
        });

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()) {
                    DataSnapshot ds = dataSnapshot.child(((DataSnapshot)i.next()).getKey());
                    Iterator j = ds.getChildren().iterator();
                    while (j.hasNext()){
                        String nameHelp = ((DataSnapshot)j.next()).getValue().toString();
                        if ( nameHelp.equals((name))){
                            tvTitle.setText(nameHelp);
                            tvAdress.setText((String) ((DataSnapshot)j.next()).getValue());
                            String city = (String) ((DataSnapshot)j.next()).getValue();
                            tvDescription.setText((String) ((DataSnapshot)j.next()).getValue());
                            byte[] imgBytes = Base64.decode(((String) ((DataSnapshot)j.next()).getValue()).getBytes(), Base64.DEFAULT);
                            tvCity.setText(((String) ((DataSnapshot)j.next()).getValue()).concat("  ").concat(city));
                            Bitmap img = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                            ivAdvIMG.setImageBitmap(img);
                            chosenAdress = tvAdress.getText().toString();
                            chosenAdress = chosenAdress.concat(" " + tvCity.getText().toString());
                        } else {
                            String adress = ((DataSnapshot) j.next()).getValue().toString();
                            nameList.add(nameHelp);
                            adress = adress.concat(" ");
                            adress = adress.concat(((DataSnapshot) j.next()).getValue().toString());
                            adress = adress.concat(" ");
                            j.next();
                            j.next();
                            adress = adress.concat(((DataSnapshot) j.next()).getValue().toString());
                            adressList.add(adress);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rootGrade.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int cnt = 0;
                float grade = 0;
                String nameHelp;
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()) {
                    DataSnapshot ds = dataSnapshot.child(((DataSnapshot) i.next()).getKey());
                    nameHelp = ds.getKey();
                    if (nameHelp.equals(name)) {
                        Iterator j = ds.getChildren().iterator();
                        while (j.hasNext()) {
                            cnt++;
                            grade += Float.valueOf(((DataSnapshot) j.next()).getValue().toString());
                        }
                    }
                }
                rbRating.setStepSize(0.5f);
                rbRating.setRating(grade / cnt);
                ibGrade.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (location != null) {
                    Intent intent = new Intent();
                    final Bundle adressBundle = new Bundle();
                    adressBundle.putSerializable("adressList", adressList);
                    final Bundle nameBundle = new Bundle();
                    nameBundle.putSerializable("nameList", nameList);
                    intent.putExtra("adressList", adressBundle);
                    intent.putExtra("chosenAdress", chosenAdress);
                    intent.putExtra("nameList", nameBundle);
                    intent.putExtra("chosenName", name);
                    intent.putExtra("Location", location);
                    intent.setClass(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);
                }
            }
        });
        ibGrade.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                preferences.edit().putInt(name, 1).apply();
                String tempKey = rootGrade.push().getKey();
                Map<String, Object> mapGrade = new HashMap<>();
                mapGrade.put(tempKey, rbRating.getRating());
                rootGrade.child(name).updateChildren(mapGrade);
                Toast.makeText(getApplicationContext(), "Uspješno ste ocijenili restoran", Toast.LENGTH_LONG).show();

            }
        });
    }
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
            }
        }
    };
}