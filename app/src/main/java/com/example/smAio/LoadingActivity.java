package com.example.smAio;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class LoadingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ImageView iv = (ImageView)findViewById(R.id.loading_image);
        Glide.with(this).load(R.raw.loading_aio).into(iv);
        
        thread_sleep sleep = new thread_sleep(this);

        sleep.start();
    }

    private class thread_sleep extends Thread{
        Activity thisAct;
        thread_sleep(Activity theAct){
            thisAct =theAct;
        }
        public void run() {
            try{
                Thread.sleep(3000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            startActivity(new Intent(thisAct, LoginActivity.class));
            finish();
        }
    }
}
