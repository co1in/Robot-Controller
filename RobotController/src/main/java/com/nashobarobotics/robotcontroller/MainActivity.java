package com.nashobarobotics.robotcontroller;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import android.graphics.PointF;

public class MainActivity extends Activity implements SensorEventListener
{
    private TextView xText, yText, zText;
    private int nums = 0;
    private SensorManager sensorManager;
	private ArrayList<PointF> accelerometerValues;

    private boolean isCalibrating = false;
    private SharedPreferences prefs;
    private final String PREFS_NAME = "RobotControllerPreferences", PREF_Y = "defaultY", PREF_Z = "defaultZ";
    private RelativeLayout rootLayout;

    private TextView[] secondValues;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		accelerometerValues = new ArrayList<PointF>();

        rootLayout = (RelativeLayout)findViewById(R.id.root);
        rootLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(isCalibrating)
                {
                    Toast.makeText(MainActivity.this, "Calibrating not implemented yet", Toast.LENGTH_SHORT).show();
                    isCalibrating = false;
                }
            }
        });

        prefs = this.getSharedPreferences(PREFS_NAME, 0);

        xText = (TextView)findViewById(R.id.xtext);
        yText = (TextView)findViewById(R.id.ytext);
        zText = (TextView)findViewById(R.id.ztext);

        secondValues = new TextView[3];
        secondValues[0] = (TextView)findViewById(R.id.x2text);
        secondValues[1] = (TextView)findViewById(R.id.y2text);
        secondValues[2] = (TextView)findViewById(R.id.z2text);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_calibrate:
                isCalibrating = true;
                Toast.makeText(this,"Click Anywhere to Calibrate", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    float[] values;

    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE)
        {
            /*values = sensorEvent.values;
            values = getSmoothValues(sensorEvent.values, values);
            for(int i = 0; i < 3; i++)
            {
                values[i] *= 10f;
                values[i] = Math.round(values[i]);
                values[i] /= 10f;
            }
            xText.setText("x: " + values[0]);
            yText.setText("y: " + values[1]);
            zText.setText("z: " + values[2]);

            /*
            float[] cuttOffvalues = cutOffBadValues(values.clone());

            secondValues[0].setText("x: " + cuttOffvalues[0]);
            secondValues[1].setText("y: " + cuttOffvalues[1]);
            secondValues[2].setText("z: " + cuttOffvalues[2]);*/
        }
        else if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            System.arraycopy(sensorEvent.values, 0, mLastAccelerometer, 0, sensorEvent.values.length);
            mLastAccelerometerSet = true;
        }
        else if(sensorEvent.sensor == mag)
        {
            System.arraycopy(sensorEvent.values, 0, mLastMagnetometer, 0, sensorEvent.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet)
        {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            mOrientation = getSmoothValues(SensorManager.getOrientation(mR, mOrientation), mOrientation);
            //mOrientation = SensorManager.getOrientation(mR, mOrientation);
            for(int i = 0; i < 3; i++)
            {
                mOrientation[i] *= 100f;
                mOrientation[i] = Math.round(mOrientation[i]);
                mOrientation[i] /= 100f;
            }

            xText.setText("x: " + mOrientation[0]);
            yText.setText("y: " + mOrientation[1]);
            zText.setText("z: " + mOrientation[2]);

            float[] sendValues = processValues(mOrientation.clone());

            secondValues[0].setText("x: " + sendValues[0]);
            secondValues[1].setText("y: " + sendValues[1] * -1);
            secondValues[2].setText("z: " + sendValues[2]);
        }
    }

    private float[] processValues(float[] input)
    {
        //Process the z axis values
        values[2] += 1.0f;
        if(values[2] > -0.2f && values[2] < 0.2f)
            values[2] = 0f;
        else
        {
            if(values[2] > 1.0)
            {
                if(values[2] < 1.4f)
                    values[2] = 1.0f;
                else
                    values[2] = 0.0f;
            }
            else if(values[2] < -1f)
            {
                if(values[2] > -1.4f)
                    values[2] = -1f;
                else
                    values[2] = 0f;
            }
        }

        //Process the Y value
        if(values[0] > 0)
        {
            values[1] = 0f;
        }
        else
        {
            if(values[1] > -1f && values[1] < 1f)
                values[1] = 0f;
            else
            {
                if(values[1] < -.7 && values[1] > -1.5)
                    values[1] = -.7f;
                else if(values[1] > .7 && values[1] < 1.5)
                    values[1] = 0.7f;
            }
        }

        return values;
    }

    private float[] cutOffBadValues(float[] input)
    {
        /*if(input[0] < -6 && input[2] >= 0)
            input[2] = 0f;
        else if(input[0] < 0 && input[2] > 0)
            input[2] = 10.0f;

        if(input[0] < -6 && input[2] < 0)
            input[0] = 0.0f;
        else if(input[0] < 0 && input[2] < 0)
            input[0] = -10;

        if(input[0] < 0 && input[1] < 0)
            input[1] = -10;
        else if(input[0] < 0 && input[1] >= 0)
            input[1] = 10f;
        else if(input[0] < -4)
            input[1] = 0f;*/

        input[0] -= 9.7;
        input[0] = Math.abs(input[0]);
        input[0] *= 10;
        input[0] = Math.round(input[0]);
        input[0] /= 10f;
        if(input[1] < 0)
            input[0] *= -1;

        return input;
    }

    static final float ALPHA = 0.1f;
	protected float[] getSmoothValues(float[] newValues, float[] oldValues)
	{
		if(oldValues == null) return newValues;

        for(int i = 0; i < newValues.length; i++)
        {
            if((i == 0 || i == 2) && (oldValues[i] < -1f && newValues[i] > 1f) || (oldValues[i] > 1f && newValues[i]  < -1f));
                //nothin to change
            else
                oldValues[i] += ALPHA * (newValues[i] - oldValues[i]);
        }
        return oldValues;
	}
	
    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }

    Sensor accel, mag;
    @Override
    protected void onResume()
    {
        super.onResume();


        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if(!sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME))
            Toast.makeText(this, "Accel not supported", Toast.LENGTH_SHORT).show();
        if(!sensorManager.registerListener(this, mag, SensorManager.SENSOR_DELAY_GAME))
            Toast.makeText(this, "Mag not supported", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
