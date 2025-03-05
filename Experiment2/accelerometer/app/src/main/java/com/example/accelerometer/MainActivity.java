package com.example.accelerometer;
import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
//public class MainActivity extends AppCompatActivity {
    TextView acceleration_x;//x方向的加速度
    TextView acceleration_y;//y方向的加速度
    TextView acceleration_z;//z方向的加速度
    TextView acceleration_total;//显示总加速度
    TextView ifmove;//显示运动情况
    SensorManager mySensorManager;//SensorManager对象引用



    //动态申请健康运动权限
    private static final String[] ACTIVITY_RECOGNITION_PERMISSION = {Manifest.permission.ACTIVITY_RECOGNITION};
    private final String TAG = "TDSSS";
    SensorManager mSensorManager;
    Sensor stepCounter;
    Sensor stepDetector;
    float mSteps = 0;
    TextView tv;


    @Override
    public void onCreate(Bundle savedInstanceState) {//重写onCreate方法
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//设置当前的用户界面
        acceleration_x = (TextView) findViewById(R.id.acceleration_x);//得到acceleration_x的引用
        acceleration_y = (TextView) findViewById(R.id.acceleration_y);//得到acceleration_y的引用
        acceleration_z = (TextView) findViewById(R.id.acceleration_z);//得到acceleration_z的引用
        acceleration_total = (TextView) findViewById(R.id.acceleration_total);//得到acceleration_total的引用

        //设置一个用于判断是否运动的控件
        ifmove = (TextView) findViewById(R.id.ifmove);//得到ifmove的引用
        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);//获得SensorManager


        // 获取SensorManager管理器实例
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        //检查权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 检查该权限是否已经获取
            int get = ContextCompat.checkSelfPermission(this, ACTIVITY_RECOGNITION_PERMISSION[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (get != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求自动开启权限
                ActivityCompat.requestPermissions(this, ACTIVITY_RECOGNITION_PERMISSION, 321);
            }
        }
        tv = (TextView)findViewById(R.id.tv_step);

        stepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if(stepDetector != null){
            // 如果sensor找到，则注册监听器
            mSensorManager.registerListener(this,stepDetector,1000000);
        }
        else{
            Log.e(TAG,"no step Detector sensor found");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //提示用户手动开启权限
                    new AlertDialog.Builder(this)
                            .setTitle("健康运动权限")
                            .setMessage("健康运动权限不可用")
                            .setPositiveButton("立即开启", (dialog12, which) -> {
                                // 跳转到应用设置界面
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, 123);
                            })
                            .setNegativeButton("取消", (dialog1, which) -> {
                                Toast.makeText(getApplicationContext(), "没有获得权限，应用无法运行！", Toast.LENGTH_SHORT).show();
                                finish();
                            }).setCancelable(false).show();
                }
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.values[0] == 1.0f){
            mSteps++;
        }
        Log.i(TAG,"Detected step changes:"+event.values[0]);

        tv.setText("您今天走了"+String.valueOf((int)mSteps)+"步");
    }


    private SensorEventListener mySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float[] values = sensorEvent.values;
                // 通过开平方和得到总加速度
                float total_acceleration = (float) Math.sqrt(
                        sensorEvent.values[0] * sensorEvent.values[0]
                                + sensorEvent.values[1] * sensorEvent.values[1]
                                + sensorEvent.values[2] * sensorEvent.values[2]);

                // 设置加速度的显示情况
                acceleration_x.setText("acceleration_x：" + sensorEvent.values[0]);
                acceleration_y.setText("acceleration_y：" + sensorEvent.values[1]);
                acceleration_z.setText("acceleration_z：" + sensorEvent.values[2]);
                acceleration_total.setText("acceleration_total：" + total_acceleration);

                // 通过与本地9.8左右的加速度进行比较从而判断手机是否运动
                // 因为实际本地加速度会在9.8-9.9之间浮动，通过物理知识可知小于9.8是在上升，大于9.9是在下降
                if (total_acceleration < 9.9 && total_acceleration > 9.8) {
                    ifmove.setText("当前用户状态：At rest\n步数大于10开始显示今日步数");
                } else if (total_acceleration >= 9.9) {
                    ifmove.setText("当前用户状态：In motion,downing\n步数大于10开始显示今日步数");
                } else if (total_acceleration <= 9.8) {
                    ifmove.setText("当前用户状态：In motion,uping\n步数大于10开始显示今日步数");
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };



    @Override
    protected void onResume() {//重写的onResume方法
        mySensorManager.registerListener(//注册监听
                mySensorListener, //监听器SensorListener对象
                mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),//传感器的类型为加速度
                SensorManager.SENSOR_DELAY_UI//传感器事件传递的频度
        );
        super.onResume();
    }

    @Override
    protected void onPause() {//重写onPause方法
        mySensorManager.unregisterListener(mySensorListener);//取消注册监听器
        super.onPause();
    }





    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}

