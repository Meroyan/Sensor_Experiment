package com.example.light;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.renderscript.Sampler.Value;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;
import android.view.WindowManager;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensor;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensor = (SensorManager) getSystemService(SENSOR_SERVICE);
        text = (TextView) findViewById(R.id.textView1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
// TODO Auto-generated method stub
        sensor.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        sensor.registerListener(this, sensor.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_GAME);
        super.onResume();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        sensor.unregisterListener(this);
        super.onStop();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub


    }





    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub


        float[] values = event.values;
        int sensorType = event.sensor.TYPE_LIGHT;
        if (sensorType == Sensor.TYPE_LIGHT) {

            float lightIntensity = values[0];

            // 根据光照强度调整屏幕亮度
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            float brightness = lightIntensity / SensorManager.LIGHT_SUNLIGHT_MAX;
            if (brightness > 1.0f) {
                brightness = 1.0f;
            } else if (brightness < 0.1f) {
                brightness = 0.1f;
            }
            layoutParams.screenBrightness = brightness;
            getWindow().setAttributes(layoutParams);

            String light_intensity = "传感器名称SensorName: " + event.sensor.getName() + '\n';
            light_intensity += "光照强度LightIntensity: " + String.valueOf(values[0]) + '\n';
            light_intensity += "耗电量PowerDraw: " + event.sensor.getPower() + "mA" + '\n';
            light_intensity += "最大测量范围MaximumRange: " + event.sensor.getMaximumRange() + '\n';

            text.setText(light_intensity);
        }
    }

}

