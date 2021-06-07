package com.tuya.ypframework;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tuya.mylibrary.service.MicroService;
import com.tuya.mylibrary.service.MicroServiceManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MicroServiceManager.getInstance().findServiceByInterface(TestService.class.getName());
    }
}