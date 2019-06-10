package com.example.smAio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * QR코드 인식후 리뷰 작성완료시 나오는 페이지 입니다.
 * glide를 이용하여 gif 이미지를 사용하였으며 버튼을 이용하여 activity 전환을 하였습니다.
 */
public class endWriteReview extends AppCompatActivity {
    // first Activity로 돌아가는 버튼, myreview페이지로 넘어가는 버튼 생성
    Button button_home,button_MyReview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_write_review);

        //userid를 가져오는 인텐트
        Intent intent= getIntent();
        final String userid=intent.getStringExtra("id");

        //glide를 이용해 gif파일 ImageView에 set
        ImageView iv = (ImageView)findViewById(R.id.gif_image);
        Glide.with(this).load(R.raw.check2).into(iv);

        //Button에 ID값 가져오기
        button_home=(Button)findViewById(R.id.return_home);
        button_MyReview=(Button)findViewById(R.id.return_mReview);

        //Button Click Event
        button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(endWriteReview.this,FirstActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //내 리뷰함 버튼 클릭 이벤트
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

    //BackPress 버튼 이벤트
    @Override
    public void onBackPressed() { //뒤로가기 버튼 클릭시
        Intent intent = new Intent(endWriteReview.this,FirstActivity.class);
        startActivity(intent);
        finish();
    }
}
