package com.example.myapplication2;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener{

    ImageView image;  //指南针图片
    float currentDegree = 0f; //指南针图片转过的角度

    TextView myTextView1;//x方向的方向值
    TextView myTextView2;//y方向的方向值
    TextView myTextView3;//z方向的方向值
    TextView myTextView4;//是否直线运动


    SensorManager mSensorManager; //管理器

    Sensor mAccelerometer;
    float[] currentAcceleration = new float[3];
    float[] currentVelocity = new float[3];
    long lastTimestamp = 0;
    float totalSpeed = 0.0f;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTextView1 = (TextView) findViewById(R.id.myTextView1);//得到myTextView1的引用
        myTextView2 = (TextView) findViewById(R.id.myTextView2);//得到myTextView2的引用
        myTextView3 = (TextView) findViewById(R.id.myTextView3);//得到myTextView3的引用
        myTextView4 = (TextView) findViewById(R.id.myTextView4);//得到myTextView4的引用
        image = (ImageView)findViewById(R.id.znzImage);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE); //获取管理服务
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume(){
        super.onResume();
        //注册监听器
        mSensorManager.registerListener(this
                , mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
    //取消注册
    @Override
    protected void onPause(){
        mSensorManager.unregisterListener(this);
        super.onPause();
    }
    @Override
    protected void onStop(){
        mSensorManager.unregisterListener(this);
        super.onStop();
    }
    //传感器值改变
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }
    //精度改变
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        //添加三个方向的加速度
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float accelerationX = event.values[0];//x方向
            float accelerationY = event.values[1];//y方向
            float accelerationZ = event.values[2];//z方向
            long currentTimestamp = System.currentTimeMillis();
            float totalAcceleration = (float) Math.sqrt(accelerationX * accelerationX
                    + accelerationY * accelerationY + accelerationZ * accelerationZ);
            if(totalAcceleration < 10.0 && totalAcceleration > 9.5){
                totalSpeed = 0;//如果总加速度小于10.0且大于9.5，则将总速度设置为0
            }
            else if(totalAcceleration >= 10.0 || totalAcceleration <= 9.5){
                if (lastTimestamp != 0) {
                    float deltaTime = (currentTimestamp - lastTimestamp) * 0.001f;
                    //如果上一个时间戳不为0，则计算时间差并将其转换为秒

                    // 更新速度
                    currentVelocity[0] = accelerationX * deltaTime;
                    currentVelocity[1] = accelerationY * deltaTime;
                    currentVelocity[2] = accelerationZ * deltaTime;
                }
                totalSpeed = (float) Math.sqrt(Math.pow(currentVelocity[0], 2) +
                        Math.pow(currentVelocity[1], 2) + Math.pow(currentVelocity[2], 2));
                //计算总速度
                lastTimestamp = currentTimestamp;
            }
        }
        //方向
        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
            float threshold = 2.0f;//设置阈值
            float degree1 = event.values[0]; //获取z转过的角度
            float degree2 = event.values[1]; //获取y转过的角度
            float degree3 = event.values[2]; //获取x转过的角度

            //截断角度到小数点后三位
            float degreeX = (int)(degree3 * 1000.0f) / 1000.0f;
            float degreeY = (int)(degree2 * 1000.0f) / 1000.0f;
            float degreeZ = (int)(degree1 * 1000.0f) / 1000.0f;


            myTextView1.setText("x方向上的方向值为：" + degreeX);
            myTextView2.setText("y方向上的方向值为：" + degreeY);
            myTextView3.setText("z方向上的方向值为：" + degreeZ);

            //如果速度不为零的情况
            if(totalSpeed != 0){
                if(Math.abs(degreeX) > threshold && Math.abs(degreeY) > threshold){
                    myTextView4.setText("当前未处于直线运动状态\n速度为：" + totalSpeed);
                }
                else{
                    myTextView4.setText("当前处于直线运动状态\n速度为：" + totalSpeed);
                }
            }
            //如果速度为零
            else{
                myTextView4.setText("当前处于静止状态");
            }

            //穿件旋转动画
            RotateAnimation ra = new RotateAnimation(currentDegree,-degree1,Animation.RELATIVE_TO_SELF,0.5f
                    ,Animation.RELATIVE_TO_SELF,0.5f);
            ra.setDuration(100);//动画持续时间
            image.startAnimation(ra);
            currentDegree = -degree1;
        }
    }
}