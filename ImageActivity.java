package com.munsellapp.munsellcolorrecognitionapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.munsellapp.munsellcolorrecognitionapp.R.id.spinner;
import static com.munsellapp.munsellcolorrecognitionapp.R.id.spinner2;
import static com.munsellapp.munsellcolorrecognitionapp.R.id.spinner3;

//import androidinterview.com.androidcamera.R;

/**
 * Created by Victorine on 10/12/16.
 */
//comment
//This class takes the image selected from gallery or take by the camera, get's the image's munsell value,
//displays it on the screen, and changes the background color to match the Munsell value. Three buttons
//appear at the bottom , home button, submit button ( whic will take the user to the submitForm activity ), and save 
//button (which will screenshot the result and save the image in an album labeled "photos" in the device gallery)
public class ImageActivity extends AppCompatActivity implements View.OnClickListener {
    private Button calibrate;
    static int TAKE_ANOTHERPIC = 0;
    private ImageView ResultPic;
    private ImageButton saveresult, exportresult, home, takeAnotherPic;
    Bitmap b;
    String munsellValue;
    TextView munsellChip, backgroundWarning;
    TextView iaDataStorage;
    RelativeLayout R1;
    Double smallestDif;
    int fixRed, fixGreen, fixBlue;
    private Uri photo;
    int compareRed, compareGreen, compareBlue;
    final int CROP_PIC = 3;
    int smallRed, smallGreen, smallBlue;
    int calibrateRed,calibrateGreen,calibrateBlue;
    Spinner expectedHue, expectedValue, expectedChroma;
    int red;
    int green;
    int blue;
    int i;
    String foundMunsellHue, foundMunsellValue, foundMunsellChroma;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_layout);
        R1=(RelativeLayout)findViewById(R.id.R1);
        home = (ImageButton) findViewById(R.id.homeButton);
        home.setOnClickListener(this);
        calibrate = (Button) findViewById(R.id.button3);
        ResultPic = (ImageView) findViewById(R.id.imageView1);
        saveresult = (ImageButton) findViewById(R.id.saveButton);
        saveresult.setOnClickListener(this);
        exportresult = (ImageButton) findViewById(R.id.submitButton);
        exportresult.setOnClickListener(this);
        takeAnotherPic = (ImageButton) findViewById(R.id.button4);
        Bundle extras = getIntent().getExtras();
        fixRed = extras.getInt("fixRed");
        fixBlue = extras.getInt("fixBlue");
        fixGreen = extras.getInt("fixGreen");
        calibrateRed=extras.getInt("calibrateRed");
        calibrateGreen=extras.getInt("calibrateGreen");
        calibrateBlue=extras.getInt("calibrateBlue");
        expectedHue = (Spinner)findViewById(spinner);
        expectedValue = (Spinner)findViewById(spinner2);
        expectedChroma = (Spinner)findViewById(spinner3);





        //System.out.println("redfactor: "+ fixRed+" greenfactor: "+ fixGreen+" bluefactor: "+ fixBlue);




/* Extracts image taken from camera or image selected from gallery and
passes it to imageview -JB
 */
        /* Takes bitmp image from Camera Intent, finds Munsell, and sets bitmap to imageview -JB*/
        if (getIntent().hasExtra("CameraImage")) {
            b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("CameraImage"), 0, getIntent().getByteArrayExtra("CameraImage").length);
            try {
                munsell(findViewById(R.id.musellValue),b);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ResultPic.setImageBitmap(b);
        }
        /* Takes bitmp image from gallery, finds Munsell, and resizes image to fit in imageview -JB*/
        else if (getIntent().hasExtra("GalleryImage")) {
            b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("GalleryImage"), 0, getIntent().getByteArrayExtra("GalleryImage").length);

            try {
                munsell(findViewById(R.id.musellValue),b);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ResultPic.setImageBitmap(b);
        }



    }


    /*Starts camera Intent -JB*/
    public void AnotherCameraClick(View v) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, TAKE_ANOTHERPIC);
            // we will handle the returned data in onActivityResult
        } catch (ActivityNotFoundException anfe) {
            Toast toast = Toast.makeText(this, "This device doesn't support the crop action!",
                    Toast.LENGTH_SHORT);
            toast.show();
        }


    }


    /* Distance formula for two 3D point */
    public static double getDistance(float aR, float aG, float aB, float cR, float cG, float cB) {
        float dx = aR - cR;
        float dy = aG - cG;
        float dz = aB - cB;

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }


    /*Goes through the munsell.csv file a first time and calculates the distance between the actual average RGB
         * and the RGB values in the csv file. If the distance calculated is smaller than the previous smallest distance,
          * the smallest distance value gets updated along with the smallest red, green, and blue value.
          * It then goes through the csv file again with the new RGB values and finds the line with the matching values and
          * returns the munsell color to the phone. It then changes the background to show the munsell chip color.*/
    public void munsell(View v, Bitmap bit) throws IOException {
        smallestDif=1000.0;


        TextView text = (TextView) findViewById(R.id.musellValue);

        InputStream csv;

        csv = getAssets().open("munsell.csv");

        InputStreamReader is = new InputStreamReader(csv);

        CSVReader csvReader = new CSVReader(is);
        String[] line;
        csvReader.readNext();
        getSpecs(bit);


        //This should  fix the colors from the calibration activity.
        //UNCOMMENT ONCE WE CAN GET THE SPECS OF A KNOWS COLOR.
        System.out.println("redfactor: "+ fixRed+" greenfactor: "+ fixGreen+" bluefactor: "+ fixBlue);
        red=red+fixRed;
        green=green+fixGreen;
        blue=blue+fixBlue;
        System.out.println("fixed red: "+ red + " fixed green"+ green +" fixed blue"+ blue);


        while ((line = csvReader.readNext()) != null) {
            compareRed = Integer.parseInt(line[line.length - 3]);
            compareGreen = Integer.parseInt(line[line.length - 2]);
            compareBlue = Integer.parseInt(line[line.length - 1]);
            if (getDistance(red, green, blue, compareRed, compareGreen, compareBlue) < smallestDif) {
                smallestDif = getDistance(red, green, blue, compareRed, compareGreen, compareBlue);
                smallRed = Integer.parseInt(line[3]);
                smallGreen = Integer.parseInt(line[4]);
                smallBlue = Integer.parseInt(line[5]);
            } //else
                //csvReader.readNext();
        }
        csvReader.close();
        System.out.println("smalled difference: " + Double.toString(smallestDif) + "/n smallest red: " + Double.toString(smallRed) + "/n Actual red:"
                + Integer.toString(red) + "/n Smallest green: " + Double.toString(smallGreen) + "/n Actual Green:" + Integer.toString(green) +
                "/n Actual blue:" + Integer.toString(blue) + "/n Smallest Blue: " + Double.toString(smallBlue));

        InputStream csv2;

        csv2 = getAssets().open("munsell.csv");

        InputStreamReader is2 = new InputStreamReader(csv2);

        CSVReader csvReader2 = new CSVReader(is2);
        String[] line2;
        csvReader2.readNext();


        while ((line2 = csvReader2.readNext()) != null) {
            if (smallRed == (Integer.parseInt(line2[line2.length - 3]))) {
                if (smallGreen == (Integer.parseInt(line2[line2.length - 2]))) {
                    if (smallBlue == Integer.parseInt(line2[line2.length - 1])) {
                        munsellValue = line2[0] + " " + line2[1] + "/" + line2[2];
                        foundMunsellHue=line2[0];
                        foundMunsellValue=line2[1];
                        foundMunsellChroma=line2[2];
                        text.setText(munsellValue);

                    }
                }
            }
        }
        csvReader2.close();
        //setBackground(red,green,blue);
        Toast.makeText(getApplicationContext(), " R "+red+" G "+green+" B "+blue,
                Toast.LENGTH_LONG).show();
       // System.out.println("Distance is: "+getDistance(calibrateRed,calibrateGreen,calibrateBlue,red,green,blue));
    //input expected RGBs ^^

    }


    @Override
    public void onClick(View v) {
        iaDataStorage = (TextView) findViewById(R.id.dataStorage);
        switch (v.getId()) {
            case R.id.homeButton:
                Intent i = new Intent(this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("dataList", iaDataStorage.getText().toString());
                i.putExtras(bundle);
                startActivity(i);
                break;
            case R.id.submitButton:
                String expected= expectedHue.getSelectedItem().toString()+" "+expectedValue.getSelectedItem().toString()+"/"+expectedChroma.getSelectedItem().toString();
                munsellChip = (TextView) findViewById(R.id.musellValue);
                Intent submitForm = new Intent(this, SubmitForm.class);
                Bundle submitBundle = new Bundle();
                submitBundle.putString("MunsellChip", munsellChip.getText().toString());
                submitBundle.putString("dataList", iaDataStorage.getText().toString());
                submitBundle.putString("expectedMunsellChip", expected);
                submitBundle.putString("expectedHue", expectedHue.getSelectedItem().toString());
                submitBundle.putString("expectedValue", expectedValue.getSelectedItem().toString());
                submitBundle.putString("expectedChroma", expectedChroma.getSelectedItem().toString());
                submitBundle.putString("foundMunsellHue", foundMunsellHue);
                submitBundle.putString("foundMunsellValue", foundMunsellValue);
                submitBundle.putString("foundMunsellChroma", foundMunsellChroma);
                submitForm.putExtras(submitBundle);
                startActivity(submitForm);
                break;
            case R.id.saveButton:
                /*Takes Screenshot of Activity and Saves reading to Gallery */
                View v1 =R1.getRootView();
                v1.setDrawingCacheEnabled(true);
                Bitmap savebm = v1.getDrawingCache();
                BitmapDrawable bitmapDrawable = new BitmapDrawable(savebm);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                Bitmap combination =savebm;
                MediaStore.Images.Media.insertImage
                        (getApplicationContext().getContentResolver(),combination,"test_"+ timeStamp + ".jpg",timeStamp.toString());
                Toast.makeText(getApplicationContext(), "Your Image Has Been Saved Successfully",
                        Toast.LENGTH_LONG).show();
                break;


        }

    }


    /*Changes the RGB values to hex numbers and then creates a HexString to change the background of the phone.
       * If the R,G, or B value is a single digit, it adds a zero infront. */
    public void setBackground(int red, int green, int blue) {

        View view = this.getWindow().getDecorView();

     /*   if(red==0 || green==0 || blue==0){
            backgroundWarning=(TextView) findViewById(R.id.backgroundWarning);
            backgroundWarning.setText("The correct background color was unable to be set.");
            return;
        }*/

        //else if (red > 9 && green > 9 && blue > 9) {

         if (red > 9 && green > 9 && blue > 9) {
            StringBuilder builder = new StringBuilder();
            builder.append("#");
            builder.append(Integer.toHexString(red));
            builder.append(Integer.toHexString(green));
            builder.append(Integer.toHexString(blue));
            view.setBackgroundColor(Color.parseColor(builder.toString()));
            return;
        }
        else if (green < 10 && blue < 10 && red < 10) {
            StringBuilder builder = new StringBuilder();
            builder.append("#");
            builder.append("0" + Integer.toString(red));
            builder.append("0" + Integer.toString(green));
            builder.append("0" + Integer.toString(blue));

            view.setBackgroundColor(Color.parseColor(builder.toString()));
            return;

        }
        else if (red < 10 && green < 10) {
            StringBuilder builder = new StringBuilder();
            builder.append("#");
            builder.append("0" + Integer.toString(red));
            builder.append("0" + Integer.toString(green));
            builder.append(Integer.toHexString(blue));

            view.setBackgroundColor(Color.parseColor(builder.toString()));
            return;
        } else if (red < 10 && blue < 10) {
            StringBuilder builder = new StringBuilder();
            builder.append("#");
            builder.append("0" + Integer.toString(red));
            builder.append(Integer.toHexString(green));
            builder.append("0" + Integer.toString(blue));

            view.setBackgroundColor(Color.parseColor(builder.toString()));
            return;
        } else if (green < 10 && blue < 10) {
            StringBuilder builder = new StringBuilder();
            builder.append("#");
            builder.append(Integer.toHexString(red));
            builder.append("0" + Integer.toString(green));
            builder.append("0" + Integer.toString(blue));

            view.setBackgroundColor(Color.parseColor(builder.toString()));
            return;
        }
        else if (red < 10) {
            StringBuilder builder = new StringBuilder();
            builder.append("#");
            builder.append("0" + Integer.toString(red));
            builder.append(Integer.toHexString(green));
            builder.append(Integer.toHexString(blue));

            view.setBackgroundColor(Color.parseColor(builder.toString()));
            return;
        } else if (green < 10) {
            StringBuilder builder = new StringBuilder();
            builder.append("#");
            builder.append(Integer.toHexString(red));
            builder.append("0" + Integer.toString(green));
            builder.append(Integer.toHexString(blue));

            view.setBackgroundColor(Color.parseColor(builder.toString()));
            return;
        } else if (blue < 10) {
            StringBuilder builder = new StringBuilder();
            builder.append("#");
            builder.append(Integer.toHexString(red));
            builder.append(Integer.toHexString(green));
            builder.append("0" + Integer.toString(blue));
            view.setBackgroundColor(Color.parseColor(builder.toString()));
            return;
        }

    }


    public void getSpecs(Bitmap bit) {
        //When implementing with camera, change field i to get the image taken from the camera,
        //so it's no longer pre loaded in with Android Studio
        ImageView i = new ImageView(this);
        i.setImageBitmap(bit);
        //System.out.println(w +" "+ h);
        Bitmap bitmap = ((BitmapDrawable) i.getDrawable()).getBitmap();
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int count=0;
        //System.out.println(bitmap.getWidth() + " " + bitmap.getHeight());
        for(int x = 0; x< w-1; x++){
            for(int y = 0; y< h-1; y++){
                int pixel = bitmap.getPixel(x, y);
                red += Color.red(pixel);
                blue += Color.blue(pixel);
                green += Color.green(pixel);
                count++;
                //

            }
        }
        //int ww= bitmap.getWidth();
        //int hh=bitmap.getHeight();
       // System.out.println(ww +" "+ hh);
        //int pixel = bitmap.getPixel(w, h);
        red = red/count;
        blue = blue/count;
        green = green/count;
        System.out.println("image red: "+ red + " image green: "+ green +" image blue: "+ blue);

        //array to return rgb

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_ANOTHERPIC && resultCode == RESULT_OK) {
            photo = data.getData();
            performCrop();
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            // byte[] byteArray = stream.toByteArray();
//            ResultPic.setImageBitmap(photo);
//            ResultPic.buildDrawingCache();
//            b = ResultPic.getDrawingCache();

        } else if (requestCode == CROP_PIC) {
            // get the returned data
            Bundle extras = data.getExtras();
            // get the cropped bitmap
            Bitmap croppedPic = extras.getParcelable("data");
            try {
                munsell(findViewById(R.id.musellValue), croppedPic);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ResultPic.setImageBitmap(croppedPic);

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
}
//addition