package com.munsellapp.munsellcolorrecognitionapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.lang.Math;
/* This activity allows the user to enter details about the image they have taken. Munsell Value of the image will be
passed from ImageActivity and location will appear automatically. User has the option to save the information to the data.txt text
file, which will promp the DataForm class.*/

public class SubmitForm extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    ImageButton save, email;
    EditText idNumber, notes;
    TextView munsell, munsellValueText, updatedText, expectedMunsellValueText, distanceValueText, rgbDistanceValueText;
    String munsellChip, expectedChip, expectedHue, expectedValue, expectedChroma;
    String foundMunsellHue, foundMunsellValue, foundMunsellChroma;
    double distance, calibrateMunsellDistance;
    TextView location;
    String rgbDistance, calibratergbDistance;
    private GoogleApiClient googleApiClient;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0;
    String calibrateHue, calibrateValue, calibrateChroma, calibrationChip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_form);
        save = (ImageButton) findViewById(R.id.sfSaveButton);
        save.setOnClickListener(this);
        Bundle getBundle= getIntent().getExtras();
        munsellChip=getBundle.getString("MunsellChip");
        //uncomment when testing
   /*     expectedChip=getBundle.getString("expectedMunsellChip");
        expectedHue=getBundle.getString("expectedHue");
        expectedValue=getBundle.getString("expectedValue");
        expectedChroma=getBundle.getString("expectedChroma");
        foundMunsellHue=getBundle.getString("foundMunsellHue");
        foundMunsellValue=getBundle.getString("foundMunsellValue");
        foundMunsellChroma=getBundle.getString("foundMunsellChroma");*/
        calibrateHue=getBundle.getString("calibrateHue");
        calibrateValue=getBundle.getString("calibrateValue");
        calibrateChroma=getBundle.getString("calibrateChroma");
        //rgbDistance=getBundle.getString("rgbDistance");
       // calibratergbDistance=getBundle.getString("calibratergbDistance");
        //distance=getMunsellDistance(expectedHue,expectedValue,expectedChroma,foundMunsellHue,foundMunsellValue,foundMunsellChroma);
       // calibrateMunsellDistance=getMunsellDistance(expectedHue,expectedValue,expectedChroma,calibrateHue,calibrateValue,calibrateChroma);
        calibrationChip=calibrateHue+" "+calibrateValue+"/"+calibrateChroma;
        //rgbDistanceValueText=(TextView) findViewById(R.id.textView19);
        System.out.println(rgbDistance);
        //Can be uncommented when testing
        // rgbDistanceValueText.setText(rgbDistance);
        munsellValueText = (TextView) findViewById(R.id.sfMunsellChip);
        munsellValueText.setText(munsellChip);
        //expectedMunsellValueText= (TextView) findViewById(R.id.textView16);
        //Can be uncommented when testing
        //expectedMunsellValueText.setText(expectedChip);
        //distanceValueText=(TextView) findViewById(R.id.textView18);
        //Can be uncommented when testing
        //distanceValueText.setText(Double.toString(distance));
        location = (TextView) findViewById(R.id.textView6);
        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
    }

    private double getMunsellDistance(String expectedHue, String expectedValue, String expectedChroma, String foundMunsellHue, String foundMunsellValue, String foundMunsellChroma) {
        double distance;
        double expectedHueAngle=findAngle(expectedHue);
        System.out.println(expectedHueAngle);
        double foundMunsellHueAngle=findAngle(foundMunsellHue);
        System.out.println(foundMunsellHueAngle);
        double x1 = Math.sin(expectedHueAngle)*Double.parseDouble(expectedChroma);
        double y1= Math.cos(expectedHueAngle)*Double.parseDouble(expectedChroma);
        double z1=Double.parseDouble(expectedValue);
        double x2 = Math.sin(foundMunsellHueAngle)*Double.parseDouble(foundMunsellChroma);
        double y2= Math.cos(foundMunsellHueAngle)*Double.parseDouble(foundMunsellChroma);
        double z2=Double.parseDouble(foundMunsellValue);
        distance=Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) + (z2-z1)*(z2-z1));
        System.out.println(x1+" "+y1+" "+z1+" "+x2+" "+y2+" "+z2);
        return distance;
    }

    private double findAngle(String hue) {
        double angle= 0.0;
        switch (hue) {
            case "N":
                angle = 0.0;
                break;
            case "5.0R":
                angle = 0.0;
                break;
            case "7.5R":
                angle = 9.0;
                break;
            case "10.0R":
                angle = 18.0;
                break;
            case "2.5YR":
                angle = 27.0;
                break;
            case "5.0YR":
                angle = 36.0;
                break;
            case "7.5YR":
                angle = 45.0;
                break;
            case "10.0YR":
                angle = 54.0;
                break;
            case "2.5Y":
                angle = 63.0;
                break;
            case "5.0Y":
                angle = 72.0;
                break;
            case "7.5Y":
                angle = 81.0;
                break;
            case "10.0Y":
                angle = 90.0;
                break;
            case "2.5GY":
                angle = 99.0;
                break;
            case "5.0GY":
                angle = 108.0;
                break;
            case "7.5GY":
                angle = 117.0;
                break;
            case "10.0GY":
                angle = 126.0;
                break;
            case "2.5G":
                angle = 135.0;
                break;
            case "5.0G":
                angle = 144.0;
                break;
            case "7.5G":
                angle = 153.0;
                break;
            case "10.0G":
                angle = 162.0;
                break;
            case "2.5BG":
                angle = 171.0;
                break;
            case "5.0BG":
                angle = 180.0;
                break;
            case "7.5BG":
                angle = 189.0;
                break;
            case "10.0BG":
                angle = 198.0;
                break;
            case "2.5B":
                angle = 207.0;
                break;
            case "5.0B":
                angle = 216.0;
                break;
            case "7.5B":
                angle = 225.0;
                break;
            case "10.0B":
                angle = 234.0;
                break;
            case "2.5PB":
                angle = 243.0;
                break;
            case "5.0PB":
                angle = 252.0;
                break;
            case "7.5PB":
                angle = 261.0;
                break;
            case "10.0PB":
                angle = 270.0;
                break;
            case "2.5P":
                angle = 279.0;
                break;
            case "5.0P":
                angle = 288.0;
                break;
            case "7.5P":
                angle = 297.0;
                break;
            case "10.0P":
                angle = 306.0;
                break;
            case "2.5RP":
                angle = 315.0;
                break;
            case "5.0RP":
                angle = 324.0;
                break;
            case "7.5RP":
                angle = 333.0;
                break;
            case "10.0RP":
                angle = 342.0;
                break;
            case "2.5R":
                angle= 351.0;
                break;
            default:
                break;
        }
        return angle * 0.0174533;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "All good?!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void saveInInternalFolder(String aStringToSave, String aFileName){
        FileOutputStream fos=null;
        aStringToSave=idNumber+" , "+munsellChip+" , "+ notes;
        try{
            fos=openFileOutput(aFileName, this.MODE_PRIVATE);
            fos.write(aStringToSave.getBytes());
            fos.close();
            Toast.makeText(this, "file saved", Toast.LENGTH_LONG).show();

            FileInputStream fis = this.openFileInput(aFileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

        }catch (IOException e){
            Toast.makeText(this, "There is a problem saving to the internal file", Toast.LENGTH_LONG).show();

        }
    }
    @Override
    public void onClick(View v) {
        idNumber=(EditText) findViewById(R.id.sfIdEdit);
        notes=(EditText) findViewById(R.id.sfNotesEdit);
        updatedText=(TextView) findViewById(R.id.sfInfoStorage);
        Intent intent=new Intent(this, DataForm.class);
        Bundle bundle=new Bundle();
        bundle.putString("idNumber", idNumber.getText().toString());
        bundle.putString("munsellChip", munsellValueText.getText().toString());
        bundle.putString("notes", notes.getText().toString());
        bundle.putString("location",location.getText().toString());
        bundle.putString("expectedChip",expectedChip);
        bundle.putString("expectedHue",expectedHue);
        bundle.putString("expectedValue",expectedValue);
        bundle.putString("expectedChroma",expectedChroma);
        bundle.putString("distance",Double.toString(distance));
        bundle.putString("rgbDistance", rgbDistance);
        bundle.putString("calibratergbDistance",calibratergbDistance);
        bundle.putString("calibrateMunsellDistance",Double.toString(calibrateMunsellDistance));
        bundle.putString("calibrationChip", calibrationChip);


//        if(updatedText.equals("")){
        intent.putExtras(bundle);
        startActivity(intent);
//        }else{
//            bundle.putString("dataList", updatedText.getText().toString());
//            intent.putExtras(bundle);
        startActivity(intent);
    }


    protected String cityName;

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }
    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            double lat = lastLocation.getLatitude(), lon = lastLocation.getLongitude();
            String units = "imperial";
            System.out.println(lat);
            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(lat, lon, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }}
        location.setText(cityName);
    }
    @Override
    public void onConnectionSuspended(int cause) {
        googleApiClient.connect();
    }
    protected String getCityName(){
        return cityName;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
