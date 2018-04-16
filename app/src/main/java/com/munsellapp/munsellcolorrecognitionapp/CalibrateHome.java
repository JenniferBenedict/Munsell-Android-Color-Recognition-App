package com.munsellapp.munsellcolorrecognitionapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.munsellapp.munsellcolorrecognitionapp.R.id.button5;
import static com.munsellapp.munsellcolorrecognitionapp.R.id.spinner5;

import static com.munsellapp.munsellcolorrecognitionapp.R.id.spinner4;
import static com.munsellapp.munsellcolorrecognitionapp.R.id.spinner6;


public class CalibrateHome  extends AppCompatActivity implements View.OnClickListener{
    Spinner expectedHue;
    Spinner expectedValue;
    Spinner expectedChroma;
    String expectedHueString, expectedValueString, expectedChromaString;
    String[] line4;
    int actualRed, actualGreen, actualBlue;

    private static int CALIBRATE_PIC=2;
    final int CROPCALI_PIC = 4;
    private Button next;
    private Uri caliPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate_home);
        expectedHue = (Spinner) findViewById(spinner5);
        expectedValue = (Spinner) findViewById(spinner6);
        expectedChroma = (Spinner) findViewById(spinner4);
        next=(Button) findViewById(button5);
        next.setOnClickListener(this);

    }
    public void PassBitmapToNextActivity (Bitmap bm, Class myClass, String extraName ){
        Intent intent = new Intent(this, myClass);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        intent.putExtra(extraName, stream.toByteArray());
        Bundle bundle=new Bundle();
        bundle.putString("actualRed", Integer.toString(actualRed));
        bundle.putString("actualGreen", Integer.toString(actualGreen));
        bundle.putString("actualBlue", Integer.toString(actualBlue));
        bundle.putString("calibrateHueString", expectedHueString);
        bundle.putString("calibrateValueString", expectedValueString);
        bundle.putString("calibrateChromaString", expectedChromaString);
        System.out.println("actual red: "+ actualRed +" actual green: "+ actualGreen +" actual blue:  "+ actualBlue);

        intent.putExtras(bundle);
        startActivity(intent);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == CALIBRATE_PIC && resultCode == RESULT_OK){
            caliPhoto = data.getData();
            performCaliCrop();
        }
        if (requestCode == CROPCALI_PIC) {
            // get the returned data
            Bundle extras = data.getExtras();
            // get the cropped bitmap
            Bitmap theCaliPic = extras.getParcelable("data");
            PassBitmapToNextActivity(theCaliPic,Calibrate.class,"CalibrateImage");
        }

    }
    private void performCaliCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(caliPhoto, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 2);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROPCALI_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    public void getRGB() throws IOException {

        InputStream csv4;

        csv4 = getAssets().open("munsell.csv");

        InputStreamReader is4 = new InputStreamReader(csv4);

        CSVReader csvReader4 = new CSVReader(is4);
        expectedHueString = expectedHue.getSelectedItem().toString();
        expectedChromaString = expectedChroma.getSelectedItem().toString();
        expectedValueString = expectedValue.getSelectedItem().toString();
        csvReader4.readNext();
        while ((line4 = csvReader4.readNext()) != null) {
            if (expectedHueString.equals((line4[line4.length - 6]))) {
                if (expectedValueString.equals((line4[line4.length - 5]))) {
                    if (expectedChromaString.equals((line4[line4.length - 4]))) {
                        actualRed = Integer.parseInt(line4[3]);
                        actualGreen = Integer.parseInt(line4[4]);
                        actualBlue = Integer.parseInt(line4[5]);

                    }
                }
            }
        }
        csvReader4.close();
    }

    @Override
    public void onClick(View v) {
        try {
            getRGB();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CALIBRATE_PIC);
        }catch (ActivityNotFoundException anfe) {
            Toast toast = Toast.makeText(this, "This device doesn't support the crop action!",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}