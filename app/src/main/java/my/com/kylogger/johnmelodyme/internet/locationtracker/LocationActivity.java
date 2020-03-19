package my.com.kylogger.johnmelodyme.internet.locationtracker;

/**
 * Copyright 2020 © John Melody Melissa
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @Author : John Melody Melissa
 * @Copyright: John Melody Melissa  © Copyright 2020
 * @INPIREDBYGF : Sin Dee <3
 * @Class: LocationActivity.class
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

import my.com.kylogger.johnmelodyme.internet.locationtracker.Service.LocationTrack;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LocationActivity extends AppCompatActivity {
    private static final String TAG = "GPSTRACKER";
    private static final int ALL_PERMISSIONS_RESULT = 0x65;
    public LocationTrack locationTrack;
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private Button StartTracking, StopTracking;
    private TextView LONG, LA;
    private ProgressDialog dialog;
    private Handler handler;

    public void DeclarationInit() {
        StartTracking = findViewById(R.id.btn_start);
//        LA = findViewById(R.id.LA);
        LONG = findViewById(R.id.Long);
        handler = new Handler();

        // REQ PERM
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Starting " + LocationActivity.class.getSimpleName());
        DeclarationInit();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (permissionsToRequest.size() > 0){
//                requestPermissions((String[])
//                        permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
//                        ALL_PERMISSIONS_RESULT);
//            }
//        }

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        StartTracking.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog = ProgressDialog.show(LocationActivity.this,
                                "",
                                "Getting GPS data \uD83D\uDEF0......",
                                true);
                        STARTLOCATION();
                    }
                },3);
            }
        });

        StartTracking.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onLongClick(View v) {
                StartTracking.setText("Start");
                LONG.setText(null);
                Log.d(TAG, "onLongClick: LONG.setText(null); " );
                return true;
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        LONG.setVisibility(View.INVISIBLE);
        StartTracking.animate();
//        StartTracking.setText("Stop");
//        String PROVIDER = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
//        if(PROVIDER != null){
//            StartTracking.setEnabled(true);
//        }else{
//            StartTracking.setEnabled(false);
//            Intent intent;
//            intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivityForResult(intent, ALL_PERMISSIONS_RESULT);
//            finish();
//        }
//        STARTLOCATION();
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(Object permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission((String) permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationTrack = new LocationTrack(LocationActivity.this);
                if (locationTrack.canGetLocation()) {
                    double longitude = locationTrack.getLongitude();
                    double latitude = locationTrack.getLatitude();
                    String LG = String.valueOf(longitude);
                    String La = String.valueOf(latitude);
                    Toast.makeText(getApplicationContext(), "Longitude:" + longitude +
                            "\nLatitude:" + latitude, Toast.LENGTH_SHORT).show();
                    LONG.setText(Double.toString(longitude));
                    LA.setText(Double.toString(latitude));
                } else {
                    locationTrack.showSettingsAlert();
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
        LONG.setText(null);
        StartTracking.setText("Start");
    }

    @SuppressLint("SetTextI18n")
    private void STARTLOCATION() {
        LONG.setVisibility(View.VISIBLE);
        StartTracking.setText("Stop");
        locationTrack = new LocationTrack(LocationActivity.this);
        dialog.dismiss();
        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();
            String LG = String.valueOf(longitude);
            String La = String.valueOf(latitude);
            //Toast.makeText(getApplicationContext(), "Longitude:" + longitude + "\nLatitude:" + latitude, Toast.LENGTH_SHORT).show();
            FancyToast.makeText(this,
                    "Longitude: " + longitude + "\n" + "Latitude: " + latitude,
                    FancyToast.LENGTH_LONG,
                    FancyToast.INFO,
                    true);
            LONG.setText("Longitude: " + longitude + "\n" + "Latitude: " + latitude);
            Log.d(TAG, "Longitude: " + longitude + " || " + "Latitude: " + latitude);
        } else {
            locationTrack.showSettingsAlert();
        }
    }
}