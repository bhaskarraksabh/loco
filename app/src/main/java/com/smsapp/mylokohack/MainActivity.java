package com.smsapp.mylokohack;

import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity  {

    private Button loco;
    private Button bb;
    private Button hq;
    private Button locoPrice;
    private Button stop;
    FrameLayout mLayout;
    Button getAnswer;
    private WindowManager wm;
    ServiceConnection mServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loco=  (Button)findViewById(R.id.loco);

        bb=  (Button)findViewById(R.id.bb);

        hq=  (Button)findViewById(R.id.hq);

        locoPrice=  (Button)findViewById(R.id.locoPrice);

        locoPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FloatingWindow.class);
                i.putExtra("gameType", "loco");
                i.putExtra("subType", "price");
                startService(i);
            }
        });

        loco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FloatingWindow.class);
                i.putExtra("gameType", "loco");
                startService(i);
            }
        });

        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FloatingWindow.class);
                i.putExtra("gameType", "bb");
                startService(i);
            }
        });

        hq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FloatingWindow.class);
                i.putExtra("gameType", "hq");
                startService(i);
            }
        });


    }




}
