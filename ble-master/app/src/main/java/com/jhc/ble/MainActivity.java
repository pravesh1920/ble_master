package com.jhc.ble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.BarUtils;

public class MainActivity extends AppCompatActivity {


    private ImageView imageView;
    private Button button1;
    private Button button2;
    private Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
        BarUtils.transparentStatusBar(this);
    }
    public void initUi(){
        imageView = findViewById(R.id.imageview);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button1.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, BleConnectActivity.class)));
        button2.setOnClickListener(view -> Toast.makeText(MainActivity.this, getString(R.string.app_text_stay_tuned), Toast.LENGTH_LONG).show());
        button3.setOnClickListener(view -> Toast.makeText(MainActivity.this, getString(R.string.app_text_stay_tuned), Toast.LENGTH_LONG).show());
    }
}