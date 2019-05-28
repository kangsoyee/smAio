package com.example.smAio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MyFragment extends Fragment {

    final private static String TAG = "from activity to fragment data";

    public MyFragment() {

        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my, container, false);
        //FirstActivity에서 이름과 id 정보 Arguments를 통해 받음
        String id_text = getArguments().getString("id");
        String name_text = getArguments().getString("name");

        //확인을 위한 로그
        Log.e(TAG, id_text);
        Log.e(TAG, name_text);
        //view를 이용 fragment에서 id를 가져올수있도록
        TextView Name = (TextView) view.findViewById(R.id.name_info);
        TextView Id = (TextView) view.findViewById(R.id.id_info);

        //setDATA
        Name.setText(name_text);
        Id.setText(id_text);

        /**최정미 list 코드 붙혀 넣어 주세용
         */

        String[] list_menu = {"내 쿠폰함", "내가 쓴 리뷰함","LOGOUT"};
        ListView listView = (ListView) view.findViewById(R.id.info_menu);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                list_menu
        );

        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                switch (position) {
                    case 0:
                        Intent intent = new Intent(getActivity(), CouponActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent1 = new Intent(getActivity(), MyReviewActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:

                        ((FirstActivity)getActivity()).sessionout();
                        break;
                }
            }
        });
        return view;
    }
}

