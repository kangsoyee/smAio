package com.example.smAio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import im.delight.android.location.SimpleLocation;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView = null;
    private GoogleMap googleMap;

    //    public loginMysql(String id,String pw,String url){
//        mHandler=new Handler();
//        userId=id;
//        userPw=pw;
//        this.url=url+"?id="+userId;
//    }

    ArrayList<PlaceDTO> items;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            for (PlaceDTO dto : items) {
                addMarker(false, new LatLng(Double.parseDouble(dto.getLatitude()), Double.parseDouble(dto.getLongitude())));
            }
        }
    };

    public MapFragment() {
        // required
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) layout.findViewById(R.id.map);
        mapView.getMapAsync(this);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //액티비티가 처음 생성될 때 실행되는 함수

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        //처음을 현재 위치로 초기화
        SimpleLocation location = new SimpleLocation(getContext());
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        this.googleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        addMarker(true, currentPosition);

        //우측 상단에 위치 버튼
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        list();
    }

    /**
     * 마커 추가
     *
     * @param refresh: true-모든 마커 지움, false-기존 마커 유지하고 마커 추가
     *                 location: 추가할 마커 위치
     */
    private void addMarker(boolean refresh, LatLng location) {

        try {
            if (refresh)
                googleMap.clear();

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location);
            googleMap.addMarker(markerOptions);
        } catch (Exception e) {
            Log.e("addMarker failTest", e.getMessage());
        }
    }

    void list() {
        //네트워크 관련 작업은 백그라운드 스레드에서 처리
        final StringBuilder sb = new StringBuilder(); // final은 지역변수를 상수화 시켜준다. 즉, 한번 실행한 뒤 없어지는 것이 아니라 계속해서 유지 가능하게 해준다.
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    String page = Common.SERVER_URL + "/place_list.php";
                    Log.e("StoreListActivity", "여기까지야");
                    items = new ArrayList<PlaceDTO>();
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
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            //스트림 생성
                            BufferedReader br =
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    conn.getInputStream(), "utf-8"));
                            while (true) {
                                String line = br.readLine(); //한 라인을 읽음
                                if (line == null) break;//더이상 내용이 없으면 종료
                                sb.append(line + "\n");
                            }
                            br.close(); //버퍼 닫기
                        }
                        conn.disconnect();
                    }
// 스트링을 json 객체로 변환
                    JSONObject jsonObj = new JSONObject(sb.toString());

// json.get("변수명")
                    JSONArray jArray = (JSONArray) jsonObj.get("sendData"); // 이 부분 이해 안됨
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject row = jArray.getJSONObject(i);
                        final PlaceDTO dto = new PlaceDTO();
                        dto.setPlace_idx(row.getInt("place_idx"));
                        dto.setCategory(row.getString("category"));
                        dto.setPlace_name(row.getString("place_name"));
                        dto.setStart_time(row.getString("start_time"));
                        dto.setEnd_time(row.getString("end_time"));
                        dto.setAddress(row.getString("address"));
                        dto.setTel(row.getString("tel"));
                        dto.setMenu(row.getString("menu"));
                        dto.setPrice(row.getString("price"));
                        dto.setLatitude(row.getString("latitude"));
                        dto.setLongitude(row.getString("longitude"));

                        items.add(dto);

                    }
                    //핸들러에게 화면 갱신 요청
                    handler.sendEmptyMessage(0);

                } catch (Exception e) {
                    Log.e("list failTest", e.getMessage());
                }
            }
        });
        th.start();
    }
}