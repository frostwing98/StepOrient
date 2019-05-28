	# 1. 描述

本项目是移动通讯安全的课程项目。基于github上记步的demo实现。

本项目添加了wifimanager模块，记录每一步对应wifi的信号强度并按颜色标记。

注意：当前版本的wifi名称是硬编码的，运行前需要手动指定待测wifi名。

使用方法与参数：

>Wifi名
>
>在Java.Life.StepView.java的autoaddpoint中（122行），修改“if(scanResult.SSID.equals("rabbi1"))”中的wifi名称rabbi1,
>
>方向传感器敏感度
>
>在Java.Life.Orient.OrientSensor.java 的onSensorChanged 中，修改degree=degree*5/5，修改方向角跳动的步长
>
>在同文件的RegisterOrient中修改SensorManager.SENSOR_DELAY_GAME的参数，修改传感器获取信息的频率

注意：app启动之后立刻走动可能因为没有wifi信息导致退出，请等5s。

注意：一开始可能一直显示为青色， 一般最多等20-30s即可出结果。

TODO：下一步添加分析数据并画出大概范围的功能。

TODO：wifi名字以外，添加根据硬件id记录的功能。

TODO：提供建议之后让用户手动重新指定开始位置的功能。

---
# 2. 以下是原作者的描述内容。
---

注：我将很多AS项目配置都删了，导入项目后有可能gradle插件版本与Android studio不匹配，
记得在build.gradle修改gradle插件版本，与Android studio版本匹配
dependencies {
		classpath 'com.android.tools.build:gradle:2.3.1'
}	 
一般情况，Android studio版本与gradle插件版本一致，
例如Android studio版本2.3，gradle插件版本2.3.0(必须是3位)
    Android studio版本2.3.1，gradle插件版本2.3.1
    Android studio版本2.3.2，gradle插件版本2.3.2

### 利用Android计步和方向传感传感器组合使用,可以在地图上记录人行走的轨迹图
![](计步截图.jpg)

#### step包(计步功能):
	StepSensorBase.java 计步传感器抽象类，计步公用方法和变量
	StepSensorAcceleration.java 加速度传感器实现计步功能
	StepSensorPedometer.java 直接使用内置计步传感器实现计步功能

#### orient包(方向功能):
	OrientSensor.java 方向功能

#### SensorUtil.java 传感器工具方法，主要是修正方向算法(即转动停止后的方向,才作为行走轨迹的方向)

#### 使用实例

```java

public class MainActivity extends AppCompatActivity implements StepSensorBase.StepCallBack, OrientSensor.OrientCallBack {
    private TextView mStepText;
    private TextView mOrientText;
    private StepView mStepView;
    private StepSensorBase mStepSensor; // 计步传感器
    private OrientSensor mOrientSensor; // 方向传感器
    private int mStepLen = 50; // 步长

    @Override
    public void Step(int stepNum) {
        //  计步回调
        mStepText.setText("步数:" + stepNum);
        mStepView.autoAddPoint(mStepLen);
    }

    @Override
    public void Orient(int orient) {
        // 方向回调
        mOrientText.setText("方向:" + orient);
//        获取手机转动停止后的方向
//        orient = SensorUtil.getInstance().getRotateEndOrient(orient);
        mStepView.autoDrawArrow(orient);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SensorUtil.getInstance().printAllSensor(this); // 打印所有可用传感器
        setContentView(R.layout.activity_main);
        mStepText = (TextView) findViewById(R.id.step_text);
        mOrientText = (TextView) findViewById(R.id.orient_text);
        mStepView = (StepView) findViewById(R.id.step_surfaceView);
        // 注册计步监听
//        mStepSensor = new StepSensorPedometer(this, this);
//        if (!mStepSensor.registerStep()) {
        mStepSensor = new StepSensorAcceleration(this, this);
        if (!mStepSensor.registerStep()) {
            Toast.makeText(this, "计步功能不可用！", Toast.LENGTH_SHORT).show();
        }
//        }
        // 注册方向监听
        mOrientSensor = new OrientSensor(this, this);
        if (!mOrientSensor.registerOrient()) {
            Toast.makeText(this, "方向功能不可用！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销传感器监听
        mStepSensor.unregisterStep();
        mOrientSensor.unregisterOrient();
    }
}
	
```
