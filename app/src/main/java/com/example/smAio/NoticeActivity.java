package com.example.smAio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class NoticeActivity extends AppCompatActivity {

    ListView noticeListView;
    NoticeListAdapter adapter;
    List<Notice> noticeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        noticeListView = (ListView) findViewById(R.id.noticeListView);
        noticeList = new ArrayList<Notice>();

        noticeList.add(new Notice("공지사항입니다" , "최정미" , "2019-05-28"));
        noticeList.add(new Notice("배고파요" , "김수연" , "2019-05-28"));
        noticeList.add(new Notice("코딩 화이팅" , "유재준" , "2019-05-28"));

        adapter = new NoticeListAdapter(getApplicationContext(), noticeList);
        noticeListView.setAdapter(adapter);

    }
}
