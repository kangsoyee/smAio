package com.example.smAio;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class HeartFragment extends Fragment {


    ListView list; // heartFragment에 있는 ListView 선언
    ArrayList<HeartDTO> items; // HeartDTO에서 받아온 식당 이름값을 담아줄 ArrayList서넌
    String id_text; //UserID 값을 받아줄 문자열 선언

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) { //ListAdapter를 리스트뷰에 붙여서 식당이름값을 받아온 값인 items들을
                                              // 미리 지정해둔 디자인 heart_row.xml 틀에 맞춰서 리스트뷰에 뿌려주는 역할
            super.handleMessage(msg);
            HeartFragment.HeartAdapter adapter = new HeartFragment.HeartAdapter(
                    getActivity(), //getActivity를 하는 이유는 Fragment 자체에 Context가 없기 때문입니다
                    R.layout.heart_row,
                    items);
            list.setAdapter(adapter);
        }
    };

    public HeartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_heart, container, false); //Fragment에는 기본적으로 View가 없기때문에
                                                                                               //findViewById를 해주기 위해서
                                                                                                // View 선언이 필요합니다

        list=(ListView)view.findViewById(R.id.list);
        id_text = getArguments().getString("id"); //유저 아이디 받아옴

        return view; // view 반환
    }

    @Override
    public void onResume() {
        super.onResume();
        list(); //list() 함수 실행
    }

    void list(){ // DB 연결작업
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb=new StringBuilder(); // final은 지역변수를 상수화 시켜준다. 즉, 한번 실행한 뒤 없어지는 것이 아니라 계속해서 유지 가능하게 해준다.
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    items = new ArrayList<HeartDTO>(); //HeartDTO에서 받아온 식당이름값들을 items라는 ArrayList에 저장
                    String page = Common.SERVER_URL+"/heart.php?userid="+id_text; //php 파일과 연결
                    URL url = new URL(page);
                    // 커넥션 객체 생성
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // 연결되었으면.
                    if (conn != null) {
                        //타임아웃 시간 설정
                        conn.setConnectTimeout(10000);
                        //캐쉬 사용 여부
                        conn.setUseCaches(false);
                        //url에 접속 성공하면
                        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                            //스트림 생성
                            BufferedReader br=
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    conn.getInputStream(),"utf-8"));
                            while(true){
                                String line=br.readLine(); //한 라인을 읽음
                                if(line == null) break;//더이상 내용이 없으면 종료
                                sb.append(line+"\n");
                            }
                            br.close(); //버퍼 닫기
                        }
                        conn.disconnect();
                    }
// 스트링을 json 객체로 변환
                    JSONObject jsonObj = new JSONObject(sb.toString());

// json.get("변수명")
                    JSONArray jArray = (JSONArray) jsonObj.get("sendData"); //key값을 "sendData"로 지정해서 데이터베이스와 연동
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject row = jArray.getJSONObject(i);
                        HeartDTO dto = new HeartDTO();
                        dto.setPlace_name(row.getString("choose"));/**확인필요**/ //HeartDTO에 있는 setter파일로 식당이름을
                        items.add(dto);                                               //받아와서 items에 추가해줍니다
                    }                                                               //DB파일에 식당이름 VARCHAR변수를 choose라고
                    //핸들러에게 화면 갱신 요청                                    // 정했기 때문에 choose라는 key로 정보를 오고가게합니다
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }

    class HeartAdapter extends ArrayAdapter<HeartDTO> {                 //HeartDTO를 담은 ArrayList의 정보를 뿌려줄 HeartAdapter 선언
        //ArrayList<BookDTO> item;
        public HeartAdapter(Context context, int textViewResourceId,
                            ArrayList<HeartDTO> objects) {
            super(context, textViewResourceId, objects);
//this.item= objects;
        }

        @Override
        public View getView(int position, View convertView,                 //
                            ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater li = (LayoutInflater)
                        getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.heart_row, null);
            }

            try {
                final HeartDTO dto = items.get(position); //DTO에서 리스트 위치를 받아와서
                if (dto != null) { //만약 리스트뷰의 자리가 비었으면
                    TextView place_name = (TextView) v.findViewById(R.id.place_name); //그 자리에 찜 한 식당이름을 추가해줍니다
                    place_name.setText(dto.getPlace_name());
                }
            }catch (Exception e){
                Log.e("Network Exception", e.getMessage());
                return null;
            }
            return v; //View를 반환
        }
    }
}
