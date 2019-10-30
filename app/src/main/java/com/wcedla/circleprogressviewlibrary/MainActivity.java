package com.wcedla.circleprogressviewlibrary;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.wcedla.circleprogressview.CircleProgressView;

public class MainActivity extends AppCompatActivity {

    CircleProgressView circleProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
