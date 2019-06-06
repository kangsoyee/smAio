package com.example.smAio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class endWriteReview extends AppCompatActivity {

    Button button_home,button_MyReview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_write_review);

        Intent intent= getIntent();
        final String userid=intent.getStringExtra("id");
        ImageView iv = (ImageView)findViewById(R.id.gif_image);
        //iv.setImageResource(R.drawable.img);
        Glide.with(this).load(R.raw.check2).into(iv);

        button_home=(Button)findViewById(R.id.return_home);
        button_MyReview=(Button)findViewById(R.id.return_mReview);

        button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(endWriteReview.this,FirstActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button_MyReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(endWriteReview.this,MyReviewActivity.class);
                intent.putExtra("id",userid);
                startActivity(intent);
                finish();
            }
        });






    }

    @Override
    public void onBackPressed() { //뒤로가기 버튼 클릭시
        Intent intent = new Intent(endWriteReview.this,FirstActivity.class);
        startActivity(intent);
        finish();
    }
}
