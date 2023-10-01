package com.example.drawabletest;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;


public class CompassDrawView extends View {
    Context context;
    private SensorManager sensorManager = null;
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        float[] orientation = new float[3];
        float[] rMat = new float[9];
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if( sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR ) {
                //calc rotation matrix
                SensorManager.getRotationMatrixFromVector(rMat, sensorEvent.values);
                //get azimuth from rotation matrix
                dAzimuthDeg = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]));
                dAzimuthDeg += 180;
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {}
    };
    private Paint circleStrokePaint;
    private Paint circleFillPaint;
    private Paint needleStrokePaint;
    private Paint needleFillPaint;
    private Paint divisionStrokePaint;
    private Paint divisionMajorStrokePaint;
    private Paint northStrokePaint;

    private Paint textStrokePaint;
    private Paint textFillPaint;
    private Paint smallTextStrokePaint;
    private Paint smallTextFillPaint;
    private int textSize;
    private int height;   //canvas height
    private int width;    //canvas width
    private int dAzimuthDeg; //device azimuth
    private double dAzimuthRad; //device azimuth
    public int tAzimuthDeg = 30; //target azimuth
    private double tAzimuthRad; //target azimuth
    private int dAzimuthDegText;
    private int radius;   //length of a needle

    public CompassDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.sensorManager = (SensorManager) this.context.getSystemService(SENSOR_SERVICE);

        Sensor rotSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        this.sensorManager.registerListener(this.sensorEventListener, rotSensor, SensorManager.SENSOR_DELAY_UI);
        setupPaint();
    }
    private void setupPaint() {
        this.radius = this.width / 2;
        this.textSize = 192;
        this.tAzimuthRad = Math.toRadians(tAzimuthDeg);

        circleStrokePaint = new Paint();
        circleStrokePaint.setAntiAlias(true);
        circleFillPaint = new Paint(circleStrokePaint);
        circleStrokePaint.setStyle(Paint.Style.STROKE);
        circleFillPaint.setStyle(Paint.Style.FILL);

        needleStrokePaint = new Paint(circleStrokePaint);
        needleFillPaint = new Paint(circleFillPaint);
        divisionStrokePaint = new Paint(circleStrokePaint);
        textStrokePaint = new Paint(circleStrokePaint);
        textFillPaint = new Paint(circleFillPaint);

        circleStrokePaint.setStrokeWidth(25);
        circleStrokePaint.setARGB(255,0,0,0);

        circleFillPaint.setARGB(64,0,0,0);

        needleStrokePaint.setStrokeWidth(30);
        needleStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        needleStrokePaint.setARGB(255,128,255,64);

        needleFillPaint.setARGB(0,0,0,0);

        divisionStrokePaint.setStrokeWidth(4);
        divisionStrokePaint.setStrokeCap(Paint.Cap.SQUARE);
        divisionStrokePaint.setARGB(192,255,255,255);

        divisionMajorStrokePaint = new Paint(divisionStrokePaint);
        divisionMajorStrokePaint.setStrokeWidth(12);

        northStrokePaint = new Paint(divisionMajorStrokePaint);
        northStrokePaint.setARGB(255,255,0,0);

        Typeface typeFace = ResourcesCompat.getFont(context, R.font.neofolia);
        textStrokePaint.setStrokeWidth(10);
        textStrokePaint.setTextSize(this.textSize);
        textStrokePaint.setTextAlign(Paint.Align.CENTER);
//        textStrokePaint.setTypeface(typeFace);
        textStrokePaint.setARGB(255,0,0,0);

        textFillPaint.setTextSize(this.textSize);
        textFillPaint.setTextAlign(Paint.Align.CENTER);
//        textFillPaint.setTypeface(typeFace);
        textFillPaint.setARGB(255,255,255,255);

        smallTextStrokePaint = new Paint(textStrokePaint);
        smallTextStrokePaint.setTextSize(textSize/3);

        smallTextFillPaint = new Paint(textFillPaint);
        smallTextFillPaint.setTextSize(textSize/3);


    }
    @Override
    protected void onDraw(Canvas canvas) {
        this.dAzimuthRad = Math.toRadians( this.dAzimuthDeg ); // phase shift to match front of the device as azimuth 0 deg
        this.dAzimuthDegText = (int) ((360 - Math.toDegrees(this.dAzimuthRad) + 180) % 360);
        int diff = dAzimuthDegText - tAzimuthDeg;

        int startX = this.width/2;
        int startY = this.height/2;
//        int endX = startX + (int) (radius * Math.cos(dAzimuthRad + Math.PI / 2));
//        int endY = startY + (int) (radius * Math.sin(dAzimuthRad + Math.PI / 2));
        int endX = startX + (int) (radius * Math.cos(0));
        int endY = startY + (int) (radius * Math.sin(0));

//        canvas.drawCircle(startX,startY,this.radius + 50, circleFillPaint);

        canvas.save();
        canvas.rotate(dAzimuthDeg + 90,startX,startY);
        for(int i = 0; i < 360; i+=5){
            int tickSX = startX + (int) ((radius + ((i == 0)? -70 : (i % 30 == 0)? -30 : -50)) * Math.cos(Math.toRadians(i)));
            int tickEX = startX + (int) ((radius + 45) * Math.cos(Math.toRadians(i)));
            int tickSY = startY + (int) ((radius + ((i == 0)? -70 : (i % 30 == 0)? -30 : -50)) * Math.sin(Math.toRadians(i)));
            int tickEY = startY + (int) ((radius + 45) * Math.sin(Math.toRadians(i)));
            canvas.drawLine(tickSX, tickSY, tickEX, tickEY, (i != 0)? (i % 30 == 0)? divisionMajorStrokePaint : divisionStrokePaint : northStrokePaint);
        }
        canvas.restore();

        canvas.save();
        canvas.rotate(-90 - diff,startX,startY);
        RectF rectF = new RectF();
        int fillRadius = radius / 3 * 2;
        rectF.set(startX - fillRadius, startY - fillRadius, startX + fillRadius, startY + fillRadius);
        int diffArc = (diff > 180)? -360 + diff : diff;
        canvas.drawArc(rectF, 0, diffArc, true, circleFillPaint);
        int targetEndX = startX + (int) ((radius + 55) * Math.cos(0));
        int targetEndY = startY + (int) ((radius + 55) * Math.sin(0));
        canvas.drawLine(startX, startY,targetEndX,targetEndY,northStrokePaint);
        canvas.restore();

        canvas.drawCircle(startX,startY,this.radius + 50, circleStrokePaint);

        for(int i = 0; i < 360; i+=5){
            if(i % 30 == 0) {
                int tickEX = startX + (int) ((radius + textSize/2) * Math.cos(Math.toRadians(i - 90 - dAzimuthDegText)));
                int tickEY = startY - (int) ((smallTextStrokePaint.descent() + smallTextStrokePaint.ascent()) / 2) + ((int) ((radius + textSize/2) * Math.sin(Math.toRadians(i - 90 - dAzimuthDegText))));
                canvas.drawText(Integer.toString(i), tickEX, tickEY, smallTextStrokePaint);
                canvas.drawText(Integer.toString(i), tickEX, tickEY, smallTextFillPaint);
            }
        }
        canvas.save();
        canvas.rotate(-90,startX,startY);
        canvas.drawLine(startX, startY,endX,endY,needleStrokePaint);
        canvas.restore();

        int smallTextX = startX + (int) ((radius - textSize/4) * Math.cos(Math.toRadians(-90 - diff)));
        int smallTextY = startY + (int) ((radius - textSize/4) * Math.sin(Math.toRadians(-90 - diff)));
        canvas.drawText(Integer.toString(tAzimuthDeg),smallTextX,smallTextY,smallTextStrokePaint);
        canvas.drawText(Integer.toString(tAzimuthDeg),smallTextX,smallTextY,smallTextFillPaint);

        canvas.drawText(Integer.toString((int) (360 - Math.toDegrees(this.dAzimuthRad) + 180) % 360), startX, height/2 - ((textStrokePaint.descent() + textStrokePaint.ascent()) / 2), textStrokePaint);
        if(diff == 0){
            textFillPaint.setARGB(255,0,255,0);
        }else{
            textFillPaint.setARGB(255,255,255,255);
        }
        canvas.drawText(Integer.toString((int) (360 - Math.toDegrees(this.dAzimuthRad) + 180) % 360), startX, height/2 - ((textStrokePaint.descent() + textStrokePaint.ascent()) / 2), textFillPaint);
        this.invalidate();
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width = w;
        this.height = h;
        this.radius = this.width / 3;
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
