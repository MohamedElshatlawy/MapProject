package com.example.mohamed.mapproject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;

import android.location.Location;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;

    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    ArrayList<String> spinnerCompany;
    FirebaseAuth firebaseAuth;
    SupportMapFragment mapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment= (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(MapsActivity.this);

        firebaseAuth=FirebaseAuth.getInstance();

        //if(firebaseAuth.getCurrentUser()==null){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            View view=LayoutInflater.from(this).inflate(R.layout.dialog_auth,null,false);
            final EditText e1=view.findViewById(R.id.ed_auth_name);
            final EditText e2=view.findViewById(R.id.ed_auth_pass);
            Button doneAuth=view.findViewById(R.id.doneAuth);
            Button outAuth=view.findViewById(R.id.outAuth);

            builder.setView(view);
            builder.setCancelable(false);


            /*if(firebaseAuth.getCurrentUser()==null){
                Toast.makeText(this,"NOLogIn",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,"Yes",Toast.LENGTH_LONG).show();

            }*/

            outAuth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adAuth.dismiss();
                    finish();
                }
            });


            doneAuth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!e1.getText().toString().isEmpty() && !e2.getText().toString().isEmpty()){
                        firebaseAuth.signInWithEmailAndPassword(e1.getText().toString(),e2.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        adAuth.dismiss();
                                        mapFragment.getMapAsync(MapsActivity.this);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MapsActivity.this,e.getMessage(),Toast.LENGTH_LONG)
                                                .show();

                                    }
                                });
                    }
                }
            });





            adAuth=builder.show();



        spinnerCompany = new ArrayList<>();
        spinnerCompany.add("شبين الكوم");
        spinnerCompany.add("سرس الليان");
        spinnerCompany.add("منوف");
        spinnerCompany.add("تلا");
        spinnerCompany.add("اشمون");
        spinnerCompany.add("الشهداء");
        spinnerCompany.add("بركة السبع");
        spinnerCompany.add("الباجور");
        spinnerCompany.add("مركز السادات");
        spinnerCompany.add("قويسنا");


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
            Toast.makeText(this, "On Map...", Toast.LENGTH_LONG).show();
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    double latX,lngX;

    @Override
    public void onLocationChanged(Location location) {
        // Toast.makeText(this,"onlistener",Toast.LENGTH_LONG).show();
        if((latX!=location.getLatitude() && lngX!=location.getLongitude())||
                (latX!=location.getLatitude() && lngX==location.getLongitude())
                ||(latX==location.getLatitude() && lngX!=location.getLongitude())){
            latX=location.getLatitude();
            lngX=location.getLongitude();

            mLastLocation = location;
            System.out.println(this.latX+"\t"+location.getLatitude());
            System.out.println(lngX+"\t"+location.getLongitude());

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            //Place current location marker

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            //markerOptions.draggable(true);
            // markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mCurrLocationMarker = mMap.addMarker(markerOptions);


            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));


        }else{
            System.out.println(this.latX+"\t"+location.getLatitude());
            System.out.println(lngX+"\t"+location.getLongitude());

        }


        }
        //stop location updates
       /*if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,  this);
        }
        */




    @Override
    public void onConnected(@Nullable Bundle bundle) {


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);
        }
        Toast.makeText(this,"On connected...",Toast.LENGTH_LONG).show();

        readDatabase();


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void registerBtn(View view) {
        showDialog();

    }
    AlertDialog ad,adAuth ;
    String spinnerSelected;
    private void showDialog() {

        final AlertDialog.Builder dialog=new AlertDialog.Builder(this);

        //dialog.setTitle("تسجيل العطل");
        //dialog.setMessage("Please use email to sign in");

        LayoutInflater inflater=LayoutInflater.from(this);

        final View dialog_layout=inflater.inflate(R.layout.dialog,null);


        dialog.setView(dialog_layout);

        Spinner spinner=dialog_layout.findViewById(R.id.spinner);

        ArrayAdapter<String> adapter=new ArrayAdapter<>(MapsActivity.this,android.R.layout.simple_spinner_dropdown_item,spinnerCompany);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerSelected = parent.getItemAtPosition(position).toString();

                Toast.makeText(MapsActivity.this,spinnerSelected,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        Button done = dialog_layout.findViewById(R.id.doneBtn);
        Button cancel=dialog_layout.findViewById(R.id.canBtn);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText e1=dialog_layout.findViewById(R.id.d_e1);
                EditText e2=dialog_layout.findViewById(R.id.d_e2);




                if(!e1.getText().toString().isEmpty() && !e2.getText().toString().isEmpty()){
                    Toast.makeText(MapsActivity.this, e1.getText().toString()+"\n"+
                            e2.getText().toString(), Toast.LENGTH_SHORT).show();
                    SaveDatabase(e1.getText().toString(),e2.getText().toString(),spinnerSelected);
                    ad.dismiss();

                }
                else{
                    Toast.makeText(MapsActivity.this,"Please fill all fields",Toast.LENGTH_LONG).show();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.setCancelable(true);
                ad.dismiss();
            }
        });
         ad = dialog.show();
    }

    private void SaveDatabase(String e1,String e2,String spinnerSelected) {

        final ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setMessage("جاري الحفظ.....");
        progressBar.show();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("data");
        String id= myRef.push().getKey();
        DatabaseReference primRef = myRef.child(id);


        DamageModel e=new DamageModel(e1,e2,mLastLocation.getLatitude(),mLastLocation.getLongitude(),getDate(),
                spinnerSelected);
        Task<Void> voidTask = primRef.setValue(e);
        voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                progressBar.dismiss();
                Toast.makeText(MapsActivity.this,"تم الحفظ...",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.dismiss();
                Toast.makeText(MapsActivity.this,"لم يتم الحفظ...",Toast.LENGTH_LONG).show();
            }
        });


    }

    private String getDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        Date date = new Date();
        String d = formatter.format(date);

        return d;
    }



    private void readDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("data");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    System.out.println("there is data...");
                    for(DataSnapshot d:dataSnapshot.getChildren()){
                        DamageModel damageModel=d.getValue(DamageModel.class);
                        LatLng latLng = new LatLng(damageModel.getLat(), damageModel.getLng());
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(damageModel.getLocName());
                        //markerOptions.snippet(damageModel.getDamage());

                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                         mMap.addMarker(markerOptions).setTag(d.getKey());

                        System.out.println("Latlng:"+latLng.latitude+""+latLng.longitude);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void goListAc(View view) {
        Intent i=new Intent(this,ListActivity.class);

        startActivity(i);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        mMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
        markerOptions.title("Current Position");
        //markerOptions.draggable(true);
        // markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);


        readDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

     FirebaseAuth.getInstance().signOut();
    }
}
