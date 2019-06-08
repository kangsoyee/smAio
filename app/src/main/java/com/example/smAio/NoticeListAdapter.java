package com.example.smAio;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.net.ContentHandler;
import java.util.List;

public class NoticeListAdapter extends BaseAdapter  { //BaseAdapter를 상속받아 사용해줌

    Context context;
    List<Notice> noticeList; //Notice 클래스가 들어갈 리스트 선언

    public NoticeListAdapter(Context context, List<Notice> noticeList) { //NoticeListAdapter에 생성자 선언
        this.context = context;
        this.noticeList = noticeList;
    }

    //BaseAdapter를 상속받았을때 뜨는 오류를 implements 하여 필요한 함수들을 불러와줍니다
    @Override
    public int getCount() {
        return noticeList.size(); //noticelist의 Size를 반환
    }

    @Override
    public Object getItem(int position) {
        return noticeList.get(position); // 해당 위치에 있는 Notice들을 반환
    }

    @Override
    public long getItemId(int position) {
        return position; //해당 위치의 Notice ID를 찾기위하여 position으로 반환
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       View v = View.inflate(context, R.layout.notice, null);

        TextView noticeText = (TextView) v.findViewById(R.id.noticeText);
        TextView nameText = (TextView) v.findViewById(R.id.nameText);
        TextView dateText = (TextView) v.findViewById(R.id.dateText);

        noticeText.setText(noticeList.get(position).getNotice());
        nameText.setText(noticeList.get(position).getName());
        dateText.setText(noticeList.get(position).getDate());

        v.setTag(noticeList.get(position).getNotice());
        return v;

        //View v라는 하나의 뷰를 만들어 Notice들을 notice.xml에 맞춰 띄워주는 역할의 getView 함수입니다.
        //공지사항, 이름, 날짜를 띄워줄 각각의 텍스트뷰를 선언하여 Notice 클래스의 getter 함수로 값을 받아와서
        //띄워주는 역할을 합니다. 마지막에 만든 뷰를 반환함으로써 화면에 띄워주게 됩니다.
    }
}
