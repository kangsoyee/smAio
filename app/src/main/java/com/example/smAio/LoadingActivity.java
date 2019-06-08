package com.example.smAio;

import android.app.Activity;
import android.content.Intent;
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
        sleep.start(); //쓰레드 실행
    }

    //쓰레드 클래스
    private class thread_sleep extends Thread{
        Activity thisAct;
        thread_sleep(Activity theAct){
            thisAct =theAct;
        }
        public void run() {
            try{
                Thread.sleep(3000); //쓰레드 지연 시간 = 로딩 화면 표시 시간 설정(3초)
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            startActivity(new Intent(thisAct, LoginActivity.class)); //로딩이 끝난 후 로그인 화면으로 전환
            finish();
        }
    }
}
