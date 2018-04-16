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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

// This class is currently not integrated into the app, however it has been
//set up for when image selection is implemented completely. This activith will
//recieve a bitmap image of a Munsell Chip passed from Main Activity, which will be compared to it's known Munsell
//RGB values, and an adjustment will be made to the RGB value of image to match what the RGB values
//are supposed to be. This adustment will be mad on all RGB-Munsell combinations according to the environment
//each time the camera is calibrated.
public class Calibrate extends AppCompatActivity implements View.OnClickListener {

    Bitmap b;
    ImageView caliPic;
    int actualRed, actualGreen, actualBlue;
    int specRed, specGreen, specBlue;
    static int fixRed, fixGreen, fixBlue;
    ImageButton calibrateButton;
    Button takePic ,choosePic;
    private static int TAKE_PIC = 0;
    private static int SELECT_FILE = 1;
    private Uri photo;
    final int CROP_PIC = 3;
    String calibrateHue,calibrateValue,calibrateChroma;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);
        caliPic=(ImageView) findViewById(R.id.caliPic);
        calibrateButton=(ImageButton) findViewById(R.id.calibrateImageButton);
        calibrateButton.setOnClickListener(this);
        takePic = (Button) findViewById(R.id.button);
        choosePic = (Button) findViewById(R.id.button2);
        Bundle extras = getIntent().getExtras();
        actualRed=Integer.parseInt(extras.getString("actualRed"));
        actualGreen=Integer.parseInt(extras.getString("actualGreen"));
        actualBlue=Integer.parseInt(extras.getString("actualBlue"));
        calibrateHue=extras.getString("calibrateHueString");
        calibrateValue=extras.getString("calibrateValueString");
        calibrateChroma=extras.getString("calibrateChromaString");
        System.out.println("actual red: "+ actualRed +" actual green: "+ actualGreen +" actual blue:  "+ actualBlue);
        if (getIntent().hasExtra("CalibrateImage")) {
            b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("CalibrateImage"), 0, getIntent().getByteArrayExtra("CalibrateImage").length);

            caliPic.setImageBitmap(b);
        }

    }


    public void getSpecs() {
        //When implementing with camera, change field i to get the image taken from the camera,
        //so it's no longer pre loaded in with Android Studio
        ImageView i = new ImageView(this);
        i.setImageBitmap(b);
        Bitmap bitmap = ((BitmapDrawable) i.getDrawable()).getBitmap();
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int count=0;
        for(int x = 0; x< w-1; x++){
            for(int y = 0; y< h-1; y++){
                int pixel = bitmap.getPixel(x, y);
                specRed += Color.red(pixel);
                specBlue += Color.blue(pixel);
                specGreen += Color.green(pixel);
                count++;
                //

            }
        }

        specRed = specRed/count;
        specBlue = specBlue/count;
        specGreen = specGreen/count;
       // System.out.println("calibrate image red: "+ specRed + " calibrate image green: "+ specGreen +" calibrate image blue: "+ specBlue);


    }

    public void fixColors(int Red, int Green, int Blue){
        fixRed=actualRed-Red;
        fixGreen=actualGreen-Green;
        fixBlue=actualBlue-Blue;
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.calibrateImageButton){
            choosePic.setVisibility(View.VISIBLE);
            takePic.setVisibility(View.VISIBLE);getSpecs();
            fixColors(specRed,specGreen,specBlue);
        }
    }

    /*Starts camera Intent -JB*/
    public void CamClick(View v) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, TAKE_PIC);
            //  will handle the returned data in onActivityResult
        } catch (ActivityNotFoundException anfe) {
            Toast toast = Toast.makeText(this, "This device doesn't support the crop action!",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void galleryIntent(View v)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TAKE_PIC && resultCode == RESULT_OK) {
            photo = data.getData();
            performCrop();
        }

        if (requestCode == SELECT_FILE && resultCode == RESULT_OK) {
            Bitmap bm;
            if (data != null) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    PassBitmapToNextActivity(bm,ImageActivity.class,"GalleryImage");
                } catch (IOException e) {
                    e.printStackTrace();
                }}}
        else if (requestCode == CROP_PIC) {
            // get the returned data
            Bundle extras = data.getExtras();
            // get the cropped bitmap
            Bitmap thePic = extras.getParcelable("data");
            PassBitmapToNextActivity(thePic,ImageActivity.class,"CameraImage");

        }
    }
    /*Passes Bitmap from any intent (camera, gallery, or calibrate camera) and passes it to specified activity)*/
    public void PassBitmapToNextActivity (Bitmap bm, Class myClass, String extraName ){
        Intent intent = new Intent(this, myClass);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        intent.putExtra(extraName, stream.toByteArray());
        intent.putExtra("fixRed",fixRed);
        intent.putExtra("fixBlue",fixBlue);
        intent.putExtra("fixGreen",fixGreen);
        intent.putExtra("calibrateRed",actualRed);
        intent.putExtra("calibrateGreen",actualGreen);
        intent.putExtra("calibrateBlue",actualBlue);
        intent.putExtra("calibrateHue",calibrateHue);
        intent.putExtra("calibrateValue",calibrateValue);
        intent.putExtra("calibrateChroma",calibrateChroma);




        startActivity(intent);

    }

    private void performCrop() {
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(photo, "image/*");
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
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }



}

