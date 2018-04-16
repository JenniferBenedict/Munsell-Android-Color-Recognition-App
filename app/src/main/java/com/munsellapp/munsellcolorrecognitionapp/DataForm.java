package com.munsellapp.munsellcolorrecognitionapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
//This activity appends to a data.txt file each time a new submission is made from submitform activity, 
//from here the user has the option to email the data.txt file.
public class DataForm extends AppCompatActivity implements View.OnClickListener {
    TextView dataListText, savedData;
    String idNumber, munsellChip, notes, dataString, dataListString, dataListStringBundle;
    ImageButton home, email;
    SharedPreferences savedValues;
    String savedDataString, restoreData;
    String savedDataPref;
    String fileLocation, location, expectedChip;
    String distance ,rgbDistance, calibratergbDistance,calibrateMunsellDistance, calibrationChip;
    ImageButton takeAnother;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_form);
        dataListText = (TextView) findViewById(R.id.dataList);
        home = (ImageButton) findViewById(R.id.dfHome);
        home.setOnClickListener(this);
        email = (ImageButton) findViewById(R.id.dfEmail);
        email.setOnClickListener(this);

//
        takeAnother=(ImageButton) findViewById(R.id.imageButton);
        takeAnother.setOnClickListener(this);
        Bundle getBundle = getIntent().getExtras();
        idNumber = getBundle.getString("idNumber");
        munsellChip = getBundle.getString("munsellChip");
        notes = getBundle.getString("notes");
        location= getBundle.getString("location");
        expectedChip= getBundle.getString("expectedChip");
        calibrationChip=getBundle.getString("calibrationChip");
        distance= getBundle.getString("distance");
        rgbDistance=getBundle.getString("rgbDistance");
        calibratergbDistance=getBundle.getString("calibratergbDistance");
        calibrateMunsellDistance=getBundle.getString("calibrateMunsellDistance");
        SharedPreferences sp = getSharedPreferences("key", 0);
        savedDataPref = sp.getString("savedDataPref", "");
        //uncomment for final this is just for testing
        dataListText.setText("\""+idNumber+ "\"" + " , " +"\"" + calibrationChip + "\"" + " , " + "\"" + munsellChip + "\"" + " , " + "\""+location+ "\""+ " , "+"\"" + notes + "\"" + "\n" + savedDataPref);
        //dataListText.setText( calibrationChip+ " , "+expectedChip  + " , " +munsellChip+ " , "+distance+ " , "+rgbDistance+" , "+calibrateMunsellDistance+ " , "+calibratergbDistance+"\n"+ savedDataPref);

    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        // Save UI state changes to the savedInstanceState.
//        // This bundle will be passed to onCreate if the process is
//        // killed and restarted.
//
//        savedInstanceState.putString("dataList", dataListText.getText().toString());
//        // etc.
//    }
//
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        // Restore UI state from the savedInstanceState.
//        // This bundle has also been passed to onCreate.
//        restoreData=savedInstanceState.getString("dataList");
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dfHome:
                dataListText = (TextView) findViewById(R.id.dataList);
                Intent intent = new Intent(this, MainActivity.class);

                SharedPreferences sp = getSharedPreferences("key", 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("savedDataPref", dataListText.getText().toString());
                editor.commit();
                startActivity(intent);

                break;
            case R.id.imageButton:
                dataListText = (TextView) findViewById(R.id.dataList);
                Intent Newintent = new Intent(this, ImageActivity.class);

                SharedPreferences Newsp = getSharedPreferences("key", 0);
                SharedPreferences.Editor NewEditor = Newsp.edit();
                NewEditor.putString("savedDataPref", dataListText.getText().toString());
                NewEditor.commit();
                startActivity(Newintent);

                break;
            case R.id.dfEmail:
                new AlertDialog.Builder(DataForm.this)
                        .setTitle("Warning!")
                        .setMessage("Data list will be deleted upon emailing results. Are you sure " +
                                "you want to proceed?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                writeFile();
                                Intent emailIntent = new Intent((Intent.ACTION_SEND));
                                emailIntent.setType("text/plain");
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Munsell Data List");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fileLocation));
                                startActivity(emailIntent);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("AlertDialog", "Negative");
                            }
                        })
                        .show();
                break;

        }
    }

    public void writeFile() {
        String content = dataListText.getText().toString();
        //This string is used when testing
       // String heading = "\"" + "Calibration Chip" + "\"" + "," + "\""+"\"" + "Expected Munsell" + "\"" + "," + "\"" + "Found Munsell" + "\"" + ',' + "\"" + "Distance in Munsell between found and expected" + "\""+ "," + "\"" + "Distance in RGB  between found and expected" + "\""+ "," + "\"" +"Distance in Munsell between calibration chip and expected"+"\""+ "," + "\"" +"Distance in rgb between calibration chip and expected"+"\"";
        //This string is what the user should use for transferring data
        String heading = "\"" + "ID"  + "\"" + "," + "\"" +"Calibration Chip" + "\"" + "," + "\"" + "Munsell Chip" + "\"" + "," + "\"" + "Location" + "\"" + ',' + "\"" + "Notes" +"\"";
        String fullContent = heading + "\n" + content;
        File file;
        FileOutputStream outputStream;
        try {
            file = new File(Environment.getExternalStorageDirectory(), "DataList.txt");
            outputStream = new FileOutputStream(file);
            outputStream.write(fullContent.getBytes());
            outputStream.close();
            System.out.println(file);
            fileLocation = file.toString();
        } catch (IOException e) {
            e.printStackTrace();

        }
        dataListText.setText("");
    }

    public void readFile() {
        File Root = Environment.getExternalStorageDirectory();
        File dir = new File(Root.getAbsolutePath() + "/MyAppFile");
        File dataText = new File(dir, "data.txt");

        String message;
        try {
            FileInputStream fis = new FileInputStream(dataText);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader buffRead = new BufferedReader(isr);
            StringBuffer stringBuff = new StringBuffer();

            while ((message = buffRead.readLine()) != null) {
                stringBuff.append(message + "/n");
            }

            System.out.println(stringBuff.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
