package com.lixm.floatwindowdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lixm.floatwindowdemo.R;
import com.lixm.floatwindowdemo.floatwindow.FloatActionController;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PlayerActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatActionController.getInstance().stopMonkServer(getApplicationContext());
    }
}
