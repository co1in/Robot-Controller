package com.nashobarobotics.robotcontroller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		accelerometerValues = new ArrayList<PointF>();
        xText = (TextView)findViewById(R.id.xtext);
        yText = (TextView)findViewById(R.id.ytext);
        zText = (TextView)findViewById(R.id.ztext);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while(true)
				{
					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e)
					{}
					MainActivity.this.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							updateAccelerometer();
						}
					});
				}
			}
		});

    }

    public void updateAccelerometer()
	{
		float sumy = 0, sumz = 0;
		
		sumy *= 10f;
		sumz *= 10f;
		sumy = Math.round(sumy);
		sumz = Math.round(sumz);
		sumy /= 10f;
		sumz /= 10f;
		
		yText.setText("y: " + sumy);
		zText.setText("z: " + sumz);
		accelerometerValues.clear();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            float[] values = sensorEvent.values;
            for(int i = 0; i < 3; i++)
            {
                values[i] *= 10f;
                values[i] = Math.round(values[i]);
                values[i] /= 10f;
            }
            //xText.setText("x: " + values[0])
            //yText.setText("y: " + values[1]);
            //zText.setText("z: " + values[2]);
        }
    }

	public void getSmoothValues(float[] newValues, float[] oldValues)
	{
		
	}
	
    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
