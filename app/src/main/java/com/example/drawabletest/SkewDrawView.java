package com.example.drawabletest;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

public class SkewDrawView extends View {
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
                dSkewRad = SensorManager.getOrientation(rMat, orientation)[1];
                dSkewDeg = (int) Math.toDegrees(dSkewRad);
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {}
    };
    private Paint baselineStrokePaint;
    private Paint baselineFillPaint;
    private int textSize;
    private int height;   //canvas height
    private int width;    //canvas width
    private int margin;   //length of a needle
    private int dSkewDeg; //device rotation in deg
    private double dSkewRad; //device rotation in radians
    private int tSkewDeg = 15; //target rotation in deg
    private double tSkewRad; //target rotation in radians
    private Paint targetlineStrokePaint;
    private Paint targetlineFillPaint;
    private Paint devicelineStrokePaint;
    private Paint devicelineFillPaint;
    private Paint textStrokePaint;
    private Paint textFillPaint;
    private Paint textDeviceStrokePaint;
    private Paint textDeviceFillPaint;

    public SkewDrawView(Context context, AttributeSet attrs) {
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
        this.margin = 30;
        this.textSize = 128;
        this.tSkewRad = Math.toRadians(this.tSkewDeg);

        baselineStrokePaint = new Paint();
        baselineStrokePaint.setAntiAlias(true);
        baselineStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        baselineFillPaint = new Paint(baselineStrokePaint);
        baselineStrokePaint.setStyle(Paint.Style.STROKE);
        baselineFillPaint.setStyle(Paint.Style.FILL);
        targetlineStrokePaint = new Paint(baselineStrokePaint);
//        targetlineFillPaint = new Paint(baselineFillPaint);
        devicelineStrokePaint = new Paint(baselineStrokePaint);
//        devicelineFillPaint = new Paint(baselineFillPaint);
        textStrokePaint = new Paint(baselineStrokePaint);
        textFillPaint = new Paint(baselineFillPaint);


        baselineStrokePaint.setARGB(96,0,0,0);
        baselineStrokePaint.setStrokeWidth(50);

        baselineFillPaint.setARGB(64,0,0,0);

        targetlineStrokePaint.setARGB(255,255,0,0);
        targetlineStrokePaint.setStrokeWidth(10);

        devicelineStrokePaint.setARGB(255,255,168,0);
        devicelineStrokePaint.setStrokeWidth(50);

        textStrokePaint.setStrokeWidth(10);
        textStrokePaint.setTextSize(this.textSize);
        textStrokePaint.setTextAlign(Paint.Align.CENTER);
        textStrokePaint.setARGB(255,0,0,0);

        textFillPaint.setTextSize(this.textSize);
        textFillPaint.setTextAlign(Paint.Align.CENTER);
        textFillPaint.setARGB(255,255,255,255);

        textDeviceStrokePaint = new Paint(textStrokePaint);
        textDeviceFillPaint = new Paint(textFillPaint);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        int startX = width/2;
        int startY = height/2;

        canvas.drawLine(0 + margin,startY,width - margin,startY,baselineStrokePaint);

        RectF rectF = new RectF();
        int fillRadius = width/2 - margin * 7;
        rectF.set(startX - fillRadius, startY - fillRadius, startX + fillRadius, startY + fillRadius);
        int diff = tSkewDeg + dSkewDeg;
        canvas.drawArc(rectF, -tSkewDeg, diff, true, baselineFillPaint);

        canvas.drawArc(rectF, -tSkewDeg - 180, diff, true, baselineFillPaint);

        canvas.save();
        canvas.rotate(-tSkewDeg, startX,startY);
        canvas.drawLine(0 + margin * 7,startY,width - margin,startY,targetlineStrokePaint);
        canvas.restore();

        int textTX = (int) (startX + ((width/2 - margin * 3) * Math.cos(Math.toRadians(-tSkewDeg) - Math.PI)));
        int textTY = (int) (startY - ((textStrokePaint.descent() + textStrokePaint.ascent()) / 2)+ ((width/2 - margin * 3) * Math.sin(Math.toRadians(-tSkewDeg) - Math.PI)));

        canvas.save();
        canvas.rotate(dSkewDeg,startX,startY);
        canvas.drawLine(0 + margin,startY,width - margin * 7,startY, devicelineStrokePaint);
        canvas.restore();

        int textDX = (int) (startX + ((width/2 - margin * 3) * Math.cos(Math.toRadians(dSkewDeg))));
        int textDY = (int) (startY - ((textStrokePaint.descent() + textStrokePaint.ascent()) / 2) + ((width/2 - margin * 3) * Math.sin(Math.toRadians(dSkewDeg))));

        canvas.drawText(Integer.toString(tSkewDeg)+"°",textTX,textTY,textStrokePaint);
        canvas.drawText(Integer.toString(tSkewDeg)+"°",textTX,textTY,textFillPaint);

        if(-dSkewDeg == tSkewDeg){
            textDeviceFillPaint.setARGB(255,0,255,0);
        }else{
            textDeviceFillPaint.setARGB(255,255,255,255);
        }
        canvas.drawText(Integer.toString(-dSkewDeg)+"°",textDX,textDY,textDeviceStrokePaint);
        canvas.drawText(Integer.toString(-dSkewDeg)+"°",textDX,textDY,textDeviceFillPaint);

        this.invalidate();
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width = w;
        this.height = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
