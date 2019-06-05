package com.example.smAio;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.net.ContentHandler;
import java.util.List;

public class NoticeListAdapter extends BaseAdapter {

    Context context;
    List<Notice> noticeList;

    public NoticeListAdapter(Context context, List<Notice> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
    }

    @Override
    public int getCount() {
        return noticeList.size();
    }

    @Override
    public Object getItem(int position) {
        return noticeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
    }
}
