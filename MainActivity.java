package com.munsellapp.munsellcolorrecognitionapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

//import androidinterview.com.androidcamera.R;
/* THis activity displays the main screen of the app, with three buttons (Take picture, Select from gallery and calibrate camera)
NOTE calibrate camera button does not work yet, will have to be integrated when ImageSelction and Calibration activities work
as expected. Take Picture Button and Select from Gallery Button will prompt the camera intent, which will pass the image to Image
Activity. NOTE select from gallery option currently only works on images that were not previously taken by the camera, but does
work for images that are downloaded. */
public class MainActivity extends Activity implements View.OnClickListener {


    private static int TAKE_PIC = 0;
    private static int CALIBRATE_PIC=2;
    private static int SELECT_FILE = 1;
    final int CROP_PIC = 3;
    final int CROPCALI_PIC = 4;
    private ImageView imageView, img;
    private Button calibrateButton, chooseImage;
    private ImageButton getMunsellButton;
    private TextView Munsell;
    private Uri photo;
    private Uri caliPhoto;
    protected final static String TAG = "ColorUtils";
    //Bitmap bitmapphoto;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chooseImage = (Button) findViewById(R.id.ChooseImage);
        chooseImage.setOnClickListener(this);
        calibrateButton = (Button) findViewById(R.id.button3);
        //calibrateButton.setOnClickListener(this);
        getMunsellButton = (ImageButton) findViewById(R.id.munsellButton);
     /*   Munsell = (TextView) findViewById(R.id.textView2);



        *//*Creates text view with different colored text*//*
        String text = "<font color=#960202>M</font> " +
                "<font color=#E6790C>U</font> " +
                "<font color=#E6A627>N</font> " +
                "<font color=#E6DC4A>S</font> " +
                "<font color=#B3AE12>E</font> " +
                "<font color=#284F00>L</font> " +
                "<font color=#03447D>L</font>";
        Munsell.setText(Html.fromHtml(text));*/

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Alert")
                .setMessage("If you would like to use the location feature of this app, please turn your" +
                        " location on.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .show();



    }


    /*Starts camera Intent -JB*/
    public void CameraClick(View v) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, TAKE_PIC);
            // we will handle the returned data in onActivityResult
        } catch (ActivityNotFoundException anfe) {
            Toast toast = Toast.makeText(this, "This device doesn't support the crop action!",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /*Starts camera Intent -JB*/
    public void CalibrateCameraClick(View v) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CALIBRATE_PIC);
        }catch (ActivityNotFoundException anfe) {
            Toast toast = Toast.makeText(this, "This device doesn't support the crop action!",
                    Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    /*Opens gallery view, then sets Result Code signaling that
    image has been selected -JB
     */
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }


    /*Gets result code from camera and gallery intents
    if result is from camera intent, sends image to ImageActivity;
    if result is from gallery intent, calls function to send image to
    ImageActivity -JB
      */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TAKE_PIC && resultCode == RESULT_OK) {
             photo = data.getData();
             performCrop();
           // PassBitmapToNextActivity(photo,ImageActivity.class,"CameraImage");
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
        startActivity(intent);

    }

    /*
    Either calls galleryIntent when ChooseImage button is clicked
    or opens cameraIntent -JB
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ChooseImage) {
            galleryIntent();
        } else {
            Intent intent=new Intent(this, ImageActivity.class);
            startActivity(intent);

        }


    }
    private void performCrop() {
        // take care of exceptions
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


}





