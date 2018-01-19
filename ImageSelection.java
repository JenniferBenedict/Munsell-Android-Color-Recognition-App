package com.munsellapp.munsellcolorrecognitionapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
//NOTE this activity has not yet been integrated with the rest of the app, but has been set up to try and implement image selection
//This will allow the user to select a certain part of an image to get it's Munsell Value of just that portion.
//This does not work completely. NOTE activity this will allow calibration to be done, so that the user can take an image
//of a Munsell chip ( which is very small ) and implement calibration for all RGB-Munsell values.

public class ImageSelection extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private Bitmap DrawBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint DrawBitmapPaint, mPaint;
    private RelativeLayout Rl;
    private CustomView customView;
    private Display Disp;
    private SeekBar seekbar;
    private Rect rectShape = new Rect();
    private float mX, mY;
    private ImageView imageView;
    private TextView textView;
    Bitmap bitmap;
    Bitmap b;
    int red0,green0,blue0;
    Button munsellReading;
    Bitmap test, secondBitmap;

    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_image_selection);
        customView = new CustomView(this);
        Rl = (RelativeLayout) findViewById(R.id.activity_image_selection);
        Rl.addView(customView);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.MAGENTA);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(5);

        imageView = (ImageView) findViewById(R.id.imageView1);

        textView = (TextView) findViewById(R.id.textView);
        seekbar = (SeekBar) findViewById(R.id.seekBar);

        munsellReading=(Button) findViewById(R.id.munsellReading);
        munsellReading.setOnClickListener(this);

        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setProgress(30);

        b = BitmapFactory.decodeByteArray(
                getIntent().getByteArrayExtra("CameraImage"), 0, getIntent().getByteArrayExtra("CameraImage").length);

        bitmap = b;
//        BitmapDrawable ob=new BitmapDrawable(getResources(),bitmap);


        imageView.setImageBitmap(bitmap);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

    }


    /**
     * Changing bound via the seekbar.
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        customView.setBounds(progress);
        customView.invalidate();

        //textView.setText("Width: " + progress);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        Intent imageActivity=new Intent(this, ImageActivity.class);
        System.out.println(red0+" "+green0+" "+blue0);
        String redString=Integer.toString(red0);
        String greenString=Integer.toString(green0);
        String blueString=Integer.toString(blue0);

        Bundle bundle=new Bundle();
        bundle.putString("redString", redString);
        bundle.putString("greenString", greenString);
        bundle.putString("blueString", blueString);

        imageActivity.putExtras(bundle);

        PassBitmapToNextActivity(bitmap,ImageActivity.class,"CameraImage");

//        imageActivity.putExtras(bundle);


        startActivity(imageActivity);

    }

    /*Passes Bitmap from any intent (camera, gallery, or calibrate camera) and passes it to specified activity)*/
    public void PassBitmapToNextActivity (Bitmap bm, Class myClass, String extraName ){
        Intent intent = new Intent(this, myClass);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        intent.putExtra(extraName, stream.toByteArray());
        startActivity(intent);

    }

    public class CustomView extends View {
        private int bound;

        @SuppressWarnings("deprecation")
        public CustomView(Context c) {

            super(c);
            Disp = getWindowManager().getDefaultDisplay();

            DrawBitmap = Bitmap.createBitmap(Disp.getWidth(), Disp.getHeight() - 400,
                    Bitmap.Config.ARGB_4444);

            mCanvas = new Canvas(DrawBitmap);

            mPath = new Path();
            DrawBitmapPaint = new Paint(Paint.DITHER_FLAG);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            setDrawingCacheEnabled(true);
            canvas.drawBitmap(DrawBitmap, 0, 0, DrawBitmapPaint);
            canvas.drawPath(mPath, mPaint);
            canvas.drawRect(mY, 0, mY, 0, DrawBitmapPaint);
        }

        /**
         * Touch event for the entire screen; where the code goes to adjust circle size, etc.
         */
        @Override
        public boolean onTouchEvent(MotionEvent event) {

            float x = event.getX();
            float y = event.getY();

            rectShape.set((int) x - getBounds(), (int) y - getBounds(), getBounds() + (int) x, getBounds() + (int) y);

            if (y <Disp.getHeight()-200) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        clear_canvas();

                        mCanvas.drawRect(rectShape, mPaint);

                        secondBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                         test = Bitmap.createBitmap(secondBitmap, (int) x-300, (int) y-500, rectShape.width(), rectShape.height());


                        int red = 0;
                        int blue = 0;
                        int green = 0;
                        int pixelCount = 0;

                        for (int yy = 0; yy < test.getHeight(); ++yy) {
                            for (int xx = 0; xx < test.getWidth(); ++xx) {
                                int pixel = test.getPixel(xx, yy);

                                red += Color.red(pixel);
                                blue += Color.blue(pixel);
                                green += Color.green(pixel);
                                ++pixelCount;

                            }
                        }

                        red0 = (red / pixelCount);
                        blue0 = (blue / pixelCount);
                        green0 = (green / pixelCount);



                        invalidate();
                        break;

                }
                return false;

            }
            return false;
        }

        private void clear_canvas() {
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }

        private int setBounds(int bound) {
            return this.bound = bound;
        }

        private int getBounds() {
            return bound;
        }

    }
}
