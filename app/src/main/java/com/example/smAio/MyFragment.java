package com.example.smAio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 내 정보를 보여주는 Fragment
 * FirstActivity에 속한 Fragment
 * 이름, ID 정보를 보여주고 CouponActivity, MyReviewActivity로의 액티비티 전환과 LOGOUT을 할 수 있다
 */
public class MyFragment extends Fragment {

    public MyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //fragment에 아이디 값을 받아오기 위한 View 객체 선언
        View view = inflater.inflate(R.layout.fragment_my, container, false);

        //FirstActivity에서 보낸 name과 id 정보가 담긴 Bundle을 getArguments를 통해 받음
        final String id_text = getArguments().getString("id");
        String name_text = getArguments().getString("name");

        //View를 이용하여 layout의 name,id TextView id값 가져오기
        TextView Name = (TextView) view.findViewById(R.id.name_info);
        TextView Id = (TextView) view.findViewById(R.id.id_info);

        //name,id TextView에 값 띄우기
        Name.setText(name_text);
        Id.setText(id_text);

        //리스트뷰 띄우기
        String[] list_menu = {"내 쿠폰함", "내가 쓴 리뷰함","LOGOUT"};
        ListView listView = (ListView) view.findViewById(R.id.info_menu);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                list_menu
        );

        //리스트 아이템 선택시 이벤트 생성
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
                        //인텐트를 통해 id값 전달
                        intent1.putExtra("id",id_text);
                        startActivity(intent1);
                        break;
                    case 2:
                        //로그아웃 버튼. FirstActivity를 참조해 sessionout()함수 실행
                        ((FirstActivity)getActivity()).sessionout();
                        break;
                }
            }
        });
        return view;
    }
}
