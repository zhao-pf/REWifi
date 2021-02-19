package com.zhaopf.wifi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/**
 * @Author: 赵鹏飞
 * @Github: https://github.com/zhao-pf
 * @Date: 2021/2/19 10:01
 * @Description: TODO
 */
public class MainActivity extends AppCompatActivity {
    public static int TIME = 0;
    public static int FIRST_TIME = 6;
    public static int FOR_TIME = 6;
    public static int RE_TIME = 6;
    public static Boolean IS_FIRST = true;
    public static boolean IS_BOOM = false;
    Boolean isShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText firstTime = findViewById(R.id.firstTime);
        EditText forTime = findViewById(R.id.forTime);
        EditText reTime = findViewById(R.id.reTime);

        Switch openSwitch = findViewById(R.id.openSwitch);
        openSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                IS_BOOM = isChecked;
                Toast.makeText(MainActivity.this, "震动" + (isChecked ? "开启" : "关闭"), Toast.LENGTH_SHORT).show();
            }
        });
        EventBus.getDefault().register(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_al, null, false);
        FloatWindow.with(getApplicationContext())
                .setView(view)
                .setX(Screen.width, 0.15f)                                   //设置控件初始位置
                .setDesktopShow(true)                        //桌面显示
                .setMoveType(MoveType.inactive)
                .build();
        FloatWindow.get().hide();

        Button button = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        button.setOnClickListener(v -> {
            isShow = !isShow;
            if (isShow) {
                FloatWindow.get().show();
            } else {
                FloatWindow.get().hide();
            }

        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FIRST_TIME = Integer.parseInt(firstTime.getText().toString());
                FOR_TIME = Integer.parseInt(forTime.getText().toString());
                RE_TIME = Integer.parseInt(reTime.getText().toString());
                Intent intent = new Intent(MainActivity.this, MyIntentService.class);
                startService(intent);
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void start(String str) {
        if (!str.equals(MyIntentService.TYPE_TOAST)) {
            TextView textView = FloatWindow.get().getView().findViewById(R.id.textView);
            textView.setText("断网倒计时:" + str);
        }
    }

    @Subscribe()
    public void startToast(String str) {
        if (str.equals(MyIntentService.TYPE_TOAST)) {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "开始断网" + RE_TIME + "秒后开启网络", Toast.LENGTH_SHORT).show());
            reStart();
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "正在开启网络", Toast.LENGTH_SHORT).show());

        }
    }

    private void reStart() {
        new WifiHelper(MainActivity.this).closeWifi(this);
        MainActivity.TIME = MainActivity.RE_TIME;
        while (MainActivity.TIME != 0) {
            Log.e("tags", MainActivity.TIME + "");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView textView = FloatWindow.get().getView().findViewById(R.id.textView);
                    textView.setText("连网倒计时:" + MainActivity.TIME--);
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        new WifiHelper(MainActivity.this).openWifi(this);
        MainActivity.TIME = MainActivity.FOR_TIME;
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}