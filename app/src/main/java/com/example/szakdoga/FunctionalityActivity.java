package com.example.szakdoga;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.renderscript.RenderScript;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class FunctionalityActivity extends AppCompatActivity {

    private static final String LOG_TAG = FunctionalityActivity.class.getName();
    private FirebaseUser user;
    private static final int SECRET_KEY = 99;


    ImageView qrIcon;
    Button textButton;
    public static TextView qrtext;
    public static TextView scantext;
    private String m_Text = "";
    protected static String barcode = "";
    private String barcode2;
    private Date currentTime;
    private Date today;
    private String date;
    private String date2;
    private Double latitude = 0.0;
    public String hosszusag = "elso";
    private Double longitude = 0.0;
    private String cordinate = "";
    private Integer wrongCoord = 0;
    private ArrayList<Items> mItemsData;
    Geocoder geocoder;
    List<Address> addresses;
    private String street="";
    public static String city="";
    private String alert_Text = "";

    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest locationRequest;
    int PERMISSION_ID = 44;


    private FirebaseFirestore mFirestore;
    private DocumentReference docRef;
    private CollectionReference mItems;

    public String getBarcode() {
        return barcode;
    }

    public String getM_Text() {
        return m_Text;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }


    public void setM_Text(String m_Text) {
        this.m_Text = m_Text;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functionality);

        qrIcon = (ImageView) findViewById(R.id.scanIcon);
        textButton = (Button) findViewById(R.id.textbtn);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        qrIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), qrscanner.class));
            }
        });

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeInputText();
            }
        });


        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Azonosított felhasználó!");
        } else {
            Log.d(LOG_TAG, "Nem azonosított felhasználó!");
            finish();
        }

    }


    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {
            Log.i(LOG_TAG, "Itt0");

            // check if location is enabled
            if (isLocationEnabled()) {
                Log.i(LOG_TAG, "Itt1");
                // getting last
                // location from
                // FusedLocationClient
                // object

                mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.i(LOG_TAG, "Koordináták beolvasva: " + location.getLatitude() + ", " + location.getLongitude());
                            setLatitude(location.getLatitude());
                            setLongitude(location.getLongitude());
                            Log.i(LOG_TAG, "Koordináták beolvasva: " + location.getLatitude() + ", " + location.getLongitude());

                            if (getLatitude() != 0.0){
                                try {
                                    upload();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.i(LOG_TAG, "Nulla latitude");
                            }
                        }else {
                            requestNewLocationData();
                            Log.i(LOG_TAG, "Else ág");
                        }
                    }
                });


            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                Log.i(LOG_TAG, "Itt5");
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
            Log.i(LOG_TAG, "Itt6");
        }

    }


    @SuppressLint("MissingPermission")
    protected void requestNewLocationData() {


        // Initializing LocationRequest
        // object with appropriate methods

        LocationRequest mLocationRequest = LocationRequest.create()
                .setInterval(30 * 1000)
                .setFastestInterval(5 * 1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        // setting LocationRequest
        // on FusedLocationClient


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        Log.i(LOG_TAG, "A vége?");
    }


    protected LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.i(LOG_TAG, "Vagy itt?");
            Location mLastLocation = locationResult.getLastLocation();
            setLatitude(mLastLocation.getLatitude());
            setLongitude(mLastLocation.getLongitude());
            Log.i(LOG_TAG, "Koordináták beolvasva: " + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
            //upload();
        }
    };

    // method to check for permissions
    protected boolean checkPermissions() {
        Log.i(LOG_TAG, "Itt9");
        //return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;


        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    protected void requestPermissions() {
        Log.i(LOG_TAG, "Itt10");
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    protected boolean isLocationEnabled() {
        Log.i(LOG_TAG, "Itt11");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(LOG_TAG, "Itt12");

        if (requestCode == PERMISSION_ID) {
            Log.i(LOG_TAG, "Itt13");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
                Log.i(LOG_TAG, "Itt14");
            }else{
                Log.i(LOG_TAG, "Itt15");
            }
        }else{
            Log.i(LOG_TAG, "Itt16");
        }
    }



    public void upload() throws IOException {
        mItemsData = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        today = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -6);
        Date modifiedDate = cal.getTime();
        Log.i(LOG_TAG, "Ma: " + today);
        Log.i(LOG_TAG, "5 napja: " + modifiedDate);


        cordinate = getLatitude().toString() + "," + getLongitude().toString();
        Log.i(LOG_TAG, "cordinate: " + cordinate);

        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(getLatitude(), getLongitude(), 1);
        Log.i(LOG_TAG, "Addresses: " + addresses);


        city = addresses.get(0).getLocality();
        street = addresses.get(0).getThoroughfare() + " " + addresses.get(0).getSubThoroughfare();

        Log.i(LOG_TAG, "barcode: " + barcode);
        Log.i(LOG_TAG, "price: " + m_Text);
        Log.i(LOG_TAG, "hely: " + city + ", " + street);
        Log.i(LOG_TAG, "idő: " + today);


        mFirestore.collection("Items")
                .whereEqualTo("barcode", barcode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i(LOG_TAG, "Kollekció lekérdezés lefutott");
                            //Megegyező helyről, megegyező árral vagy törölni, vagy frissíteni az adatot
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getDate("date").after(modifiedDate)) {
                                    Log.i(LOG_TAG, document.get("barcode") + " barcode nem lett törölve");
                                } else {
                                    document.getReference().delete();
                                    Log.i(LOG_TAG, document.get("barcode") + " barcode törölve");
                                }
                            }


                            uploadItem();
                        } else {
                            Log.i(LOG_TAG, "Nem sikerült a lekérdezés");
                        }
                    }
                });
    }

    public void uploadItem() {
        mItems.add(new Items(barcode,
                city,
                street,
                today,
                m_Text,
                false));
        Log.i(LOG_TAG, "feltöltve");
        Intent intent = new Intent(this, ItemList.class);
        startActivity(intent);
    }

    public void barcodeInputText() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle("Kérlek írd be a vonalkódot!");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setBarcode(input.getText().toString());
                Log.i(LOG_TAG, "Vonalkód gépeléssel megadva: " + input.getText().toString());
                priceInputText();
            }
        });
        builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                FunctionalityActivity.scantext.setText("Sikertelen ár megadás!");
            }
        });
        builder.show();
    }

    public void priceInputText() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle("Kérlek add meg a termék árát!");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert_Text = input.getText().toString();
                Log.i(LOG_TAG, "Az ár: " + alert_Text);
                if (alert_Text.equals("") || alert_Text.equals("0") || alert_Text.isEmpty() || alert_Text == null || alert_Text.startsWith("0")) {
                    Log.i(LOG_TAG, "Termék ára nem lett beolvasva: " + alert_Text);
                    dialog.cancel();
                } else {
                    setM_Text(input.getText().toString());
                    Log.i(LOG_TAG, "Termék ára beolvasva: " + alert_Text);
                    getLastLocation();

                }
            }
        });
        builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                FunctionalityActivity.scantext.setText("Sikertelen ár megadás!");
            }
        });
        builder.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.function_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                Log.i(LOG_TAG, "menu: back");
                break;
            case R.id.profilF:
                startProfilPage();
                Log.i(LOG_TAG, "menu: settings");
                break;
            case R.id.infoF:
                startInfoPage();
                Log.i(LOG_TAG, "menu: info");
                break;
            case R.id.log_out_buttonF:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                Log.i(LOG_TAG, "menu: log outF");
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void startProfilPage() {
        Intent intent = new Intent(this, ProfilActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }
    private void startInfoPage() {
        Intent intent = new Intent(this, Info.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        Log.i(LOG_TAG, "onDestroy");
    }


}