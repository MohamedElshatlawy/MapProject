package com.example.mohamed.mapproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ListActivity extends AppCompatActivity implements OnMapReadyCallback{
    Adapter adapter;

    ArrayList<DamageModel> data;
    ArrayList<DamageModel>filterData;
    ArrayList<String>spinnerCompany;


    //  boolean checkDeleted=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ListView lv=findViewById(R.id.lv);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        spinnerCompany=new ArrayList<>();
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


        data=new ArrayList<>();
        filterData=new ArrayList<>();

        adapter=new Adapter(this,filterData);
        lv.setAdapter(adapter);

        readDatabase();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               // android.widget.Adapter adapter = parent.getAdapter();

               // = ListActivity.this.adapter.getItem(position);
            //    checkDeleted=false;
                showDialog(adapter,position);


            }
        });

    }

    AlertDialog ad,adFilter;
    DamageModel mSelectedModel;
    SupportMapFragment mapFragment;
    private void showDialog(final Adapter adapter, int position) {

        final AlertDialog.Builder dialog=new AlertDialog.Builder(this);

        //dialog.setTitle("تسجيل العطل");
        //dialog.setMessage("Please use email to sign in");

        LayoutInflater inflater=LayoutInflater.from(this);
        final View dialog_layout=inflater.inflate(R.layout.list_item_dialog,null);

        dialog.setView(dialog_layout);
        dialog.setCancelable(false);
        mSelectedModel =  adapter.getItem(position);

        //Fragment fragment= dialog_layout.findViewById(R.id.map2);
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);


            mapFragment.getMapAsync(this);



        TextView tv1=dialog_layout.findViewById(R.id.list_dialg_tv1);
        TextView tv2=dialog_layout.findViewById(R.id.list_dialg_tv2);
        TextView tv3=dialog_layout.findViewById(R.id.list_dialg_tv3);
        TextView tv4=dialog_layout.findViewById(R.id.list_dialg_tv4);
        tv1.setText(tv1.getText()+mSelectedModel.getLocName());

        tv2.setText(tv2.getText()+mSelectedModel.getDamage());
        tv3.setText(tv3.getText()+mSelectedModel.getDate());
        tv4.setText(tv4.getText()+mSelectedModel.getCompany());


        Button delete=dialog_layout.findViewById(R.id.list_dialg_doneBtn);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkDelete();


            }
        });

        Button cancel=dialog_layout.findViewById(R.id.list_dialg_cancelBtn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(mapFragment!=null){

                getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
               }
                ad.dismiss();
            }
        });

        ad = dialog.show();
    }

    private void checkDelete() {
        final AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("تحذير");
        dialog.setMessage("هل انت متأكد من حذف العطل !!");
        dialog.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            deleteItem(mSelectedModel);
            dialog.dismiss();
            }
        });

        dialog.setNegativeButton("لا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

    dialog.show();
    }

    private void deleteItem(final DamageModel model) {
        DatabaseReference database=FirebaseDatabase.getInstance().getReference("data");
        Task<Void> voidTask = database.child(model.getId()).removeValue();

        voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
             //   checkDeleted=true;
                Toast.makeText(ListActivity.this,"تم الحذف",Toast.LENGTH_LONG).show();
                data.remove(model);
                filterData.remove(model);
                ListActivity.this.adapter.notifyDataSetChanged();
                ad.dismiss();
                if(mapFragment!=null){

                    getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ListActivity.this,"لم يتم الحذف حاول مرة اخرى",Toast.LENGTH_LONG).show();

            }
        });
    }


    private void readDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("data");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    System.out.println("there is data...");
                    for(DataSnapshot d:dataSnapshot.getChildren()){

                        DamageModel damageModel=d.getValue(DamageModel.class);

                        damageModel.setId(d.getKey());
                        //AllData.add(damageModel);
                        data.add(damageModel);
                        filterData.add(damageModel);
                        System.out.println(damageModel.getLocName()+":"+damageModel.getDamage());

                    }
                    //System.out.println("ArraySSize:"+data.size());
                    adapter.notifyDataSetChanged();
                    System.out.println("size of data:"+data.size());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ListActivity.this,"Error read data",Toast.LENGTH_LONG).show();

            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng latLng=new LatLng(mSelectedModel.getLat(),mSelectedModel.getLng());
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(mSelectedModel.getLocName());
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);



        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.filter:
                showFilterDialog();
                return true;
            case R.id.viewAll:
                viewAllData();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void viewAllData() {

        filterData.clear();
        for(int i=0;i<data.size();i++){
            filterData.add(data.get(i));
        }

        adapter.notifyDataSetChanged();
    }

    private int yearPicker,dayPicker,monthPicker;
    private String spinnerSelected;
    int radioChecked;

    private void showFilterDialog() {

        final AlertDialog.Builder dialog=new AlertDialog.Builder(this);

        //dialog.setTitle("تسجيل العطل");
        //dialog.setMessage("Please use email to sign in");

        LayoutInflater inflater=LayoutInflater.from(this);
        final View dialog_layout=inflater.inflate(R.layout.filter_menu,null);

        dialog.setView(dialog_layout);
        final Spinner spinner=dialog_layout.findViewById(R.id.spinner_filter);
        final DatePicker picker=dialog_layout.findViewById(R.id.date_picker);


        RadioGroup radioGroup=dialog_layout.findViewById(R.id.rd_group);
        radioChecked=radioGroup.getCheckedRadioButtonId();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioChecked=checkedId;
                if(checkedId==R.id.rd_company){
                    picker.setVisibility(View.GONE);
                    spinner.setVisibility(View.VISIBLE);

                }else{
                    spinner.setVisibility(View.GONE);
                    picker.setVisibility(View.VISIBLE);
                }
            }
        });

        final ArrayAdapter<String> adapter=new ArrayAdapter<>(ListActivity.this,android.R.layout.simple_spinner_dropdown_item,spinnerCompany);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerSelected = parent.getItemAtPosition(position).toString();

               // Toast.makeText(MapsActivity.this,spinnerSelected,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerSelected=parent.getItemAtPosition(0).toString();
            }
        });


        Calendar calendar = Calendar.getInstance();
        yearPicker=picker.getYear();
        monthPicker=picker.getMonth()+1;
        dayPicker=picker.getDayOfMonth();
        picker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
        //        Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
            yearPicker=year;
            monthPicker=month+1;
            dayPicker=dayOfMonth;


            }
        });

        dialog.setPositiveButton("تم", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                filterData.clear();
          // Toast.makeText(ListActivity.this,dayPicker+":"+monthPicker+":"+yearPicker,Toast.LENGTH_LONG).show();
            //data.clear();
            if(radioChecked==R.id.rd_company){
                System.out.println("fail");
                System.out.println("spinner:"+spinnerSelected);
                for(int i=0;i<data.size();i++){
                    if(data.get(i).getCompany().equals(spinnerSelected)){
                        System.out.println("done");
                        filterData.add(data.get(i));
                    }
                }

               // ListActivity.this.adapter=new Adapter(ListActivity.this,data);

            }else{
                DecimalFormat formatter = new DecimalFormat("00");
                String datePickerSelected = formatter.format(dayPicker)+"/"+formatter.format(monthPicker)
                        +"/"+yearPicker;
                System.out.println("datePick:"+datePickerSelected);
                for(int i=0;i<data.size();i++){
                    String[] split = data.get(i).getDate().split(" ");
                    if(split[0].equals(datePickerSelected)){
                        System.out.println("true");
                        filterData.add(data.get(i));
                    }else{
                        System.out.println("false");
                    }
                }

            }
                adFilter.dismiss();

                System.out.println("after:"+filterData.size());
                System.out.println("afterData:"+data.size());
               // ListActivity.this.adapter=new Adapter(ListActivity.this,filterData);
                ListActivity.this.adapter.notifyDataSetChanged();
              //  filterData.clear();
            }
        });
        adFilter=dialog.show();

    }
}
