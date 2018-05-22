package com.example.mina.movieapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {


    //splashscreen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread threadnew = new Thread() {
            @Override
            public void run() {
                try {
                    //continue for 5000 miilisecond
                    Thread.sleep(2000);
                    Intent intent = new Intent(SplashScreen.this, displayMovies.class);//go to diplay movies class
                    startActivity(intent);
                    finish();//finish activity

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        threadnew.start();

    }
}
