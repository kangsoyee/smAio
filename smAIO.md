# smAIO
## 목차
1. 주제 선정 이유
2. 유사 앱 분석
3. 기존 방식과의 차별화 전략
4. 앱을 만들기 위해 필요한 도구
5. 데이터베이스 사용방법
6. PHP를 사용한 서버 연동
7. 각 클래스 별 기능별 설명
8. 클레스 별 코드 상세 설명
9. 사용자 의견
10. 참고 자료 

---

### 1. 주제 선정 이유

* 점심시간마다 메뉴에 대한 고민

* 신입생의 경우 학교 주변 상점들에 대한 정보 없음

* 학교 커뮤니티 앱에서 가게 정보 공유하는 것을 확인

### 2. 유사 앱 분석

* 배달의 민족 특징

* 大众点评 특징

* 

### 3. 앱을 만들때 생기는 문제점 및 해결방안

문제점

*  비 이용 고객의 후기

*  식당 섭외

해결 방안

* QR 코드를 인식할 때에만 리뷰를 남길 수 있게 한다

* 좋은 리뷰, 평점이 높은 가게를 공지사항에 게시하여 사용자들에게 좋은 이미지로 

  인식될 수 있게 해준다.

### 4. 앱을 만들기 위해 필요한 도구

* 안드로이드 스튜디오

* 데이터베이스

* 서버  

* PHP

### 5. 데이터베이스 사용방법

1. Heidi SQL 다운로드
(https://www.heidisql.com/download.php)

2. (http://eileenyoo1.cafe24.com/image/explain2.PNG)  
설치를 완료했다면,위와 같이 카페24를 통해 구매한 호스트명을 적고 사용자ID, 비밀번호를  
입력하고 접속합니다.

3. (http://eileenyoo1.cafe24.com/image/explain.PNG)  
    (http://eileenyoo1.cafe24.com/image/explain3.PNG)

    쿼리문을 통해 테이블을 만들지 않고 툴을 사용하므로 보다 편리하게 직관적으로  
    데이터를 관리할 수 있습니다. 

### 6. PHP 사용방법

 데이터베이스와 안드로이드 스튜디오를 연동을 위해 서버 사이드 언어인 PHP를 사용했습니다. 

1. 연결방법
    DB와의 연동을 할 수 있는 이와 같이 코드를 작성해줍니다. connect.php 파일은  
    다른 파일에서도 반복 사용하게 됩니다.
        
```<?php
 //파일명 connect.php
  $conn = mysqli_connect("localhost","eileenyoo1","@@@@@@","eileenyoo1");   
  // ("localhost,DB아이디,DB의 비밀번호,DB이름") 순으로 입력해줍니다.
 ?>
 ``` 

2. 회원가입 정보를 DB로 전달
```<?php
  //파일명 UserSignUp.php
    if($_SERVER['REQUEST_METHOD'] =='POST'){
      $id = $_POST['id'];
      $password = $_POST['password'];
      $name = $_POST['name'];

      require_once ('connect.php');
      $sql = "INSERT INTO member(userid,pwd,name) VALUES ('$id','$password','$name')";

      if(mysqli_query($conn,$sql)){
        $result["success"] = "1";
        $result["message"] = "success";
        echo json_encode($result);
        mysqli_close($conn);
      } else{
        $result["success"] = "0";
        $result["message"] = "error";
        echo json_encode($result);
        mysqli_close($conn);
      }
    }
?>
```

3. 모든 상점 정보 가져오기
```<?php
require_once ('connect.php'); // DB를 연동할 수 있는 PHP 소스를 가져옵니다.
$sql = "select * from place"; // 모든 상점의 정보를 불러옵니다.

$rs = mysqli_query($conn, $sql); // mysqli_connect 를 통해 연결된 객체를 이용하여 MySQL 쿼리를 실행시키는 함수입니다.
while ($data = mysqli_fetch_array($rs)) { // mysqli_query 를 통해 얻은 리절트 셋(result set)에서 레코드를 1개씩 리턴해주는 함수입니다.
    //로그인 성공
    $row = array('place_idx' => $data[place_idx], 
	'category' => $data[category], 
	'place_name' => $data[place_name], 
	'start_time' => $data[start_time], 
	'end_time' => $data[end_time], 
	'address' => $data[address], 
	'tel' => $data[tel], 
	'menu' => $data[menu], 
	'price' => $data[price], 
	'image' => $data[image], 
	'latitude' => $data[latitude], 
	'longitude' => $data[longitude], 
	'qrcode' => $data[qrcode]);
    $items[] = $row;
}
$arr = array('sendData' => $items);
echo json_encode($arr); // 전달받은 값을 JSON 형식의 문자열로 변환하여 반환합니다.
mysqli_close($conn);
?>
```

4. 상점별 리뷰 가져오기
```<?php
require_once ('connect.php');

$place_idx = $_REQUEST['place_idx'];

// sql 쿼리를 이용하여 입력된 상점의 일치하는 값을 가져오고, 가장 최근의 리뷰부터 가져옵니다.
$sql = "select r.idx,m.userid,r.review_date, r.place_idx, r.review_content
		from review r, member m, place p  
		where r.userid = m.userid and r.place_idx = p.place_idx and r.place_idx={$place_idx}  
        order by r.review_date"; 
		
		
$rs = mysqli_query($conn, $sql);
while ($data = mysqli_fetch_array($rs)) {
    //로그인 성공
    $row = array(
	'idx' => $data[idx],
	'userid' => $data[userid],
	'review_date' => $data[review_date], 
	'place_idx' => $data[place_idx], 
	'review_content' => $data[review_content]
	);
    $items[] = $row;
}
$arr = array('sendData' => $items);
echo json_encode($arr);
mysqli_close($conn);
?>
```

5. 리뷰 작성, 점수 전달하기
```<?php
require_once ('connect.php');

// 안드로이드에서 리뷰를 작성한 후 PHP 를 이용하여 DB로 정보를 전달하는 것이므로  
// DB가 인식할 수 있는 데이터 명을 선언해줍니다.
$userid = $_REQUEST['userid'];
$place_idx = $_REQUEST['place_idx'];
$review_content = $_REQUEST['review_content'];
$score = $_REQUEST['score'];

// 입력된 리뷰를 실시간으로 확인할 수 있게끔 쿼리문으로 insert를 이용하였고, now()를 통해 리뷰를  
// 올린 시간을 파악할 수 있습니다.
$sql = "insert into review (userid,place_idx,review_date,review_content,score) 
		values ('{$userid}','{$place_idx}',now(),'{$review_content}','{$score}')";
		
echo "sql:{$sql}<br>"; // 안드로이드 스튜디오의 Log 와 같은 방법을 표현하고자 사용했습니다.
mysqli_query($conn, $sql);
?>
```

6. 상점별 평균 점수 가져오기
```<?php
require_once ('connect.php');
$place_idx = $_REQUEST['place_idx']; // 상점이 갖고있는 고유한 번호를 알기위해 선언을 해줍니다.

$sql= "select avg(score) as 'score_avg' from review where place_idx='{$place_idx}'";
// 쿼리를 통해 해당 상점의 평균값을 계산합니다.

$rs = mysqli_query($conn, $sql);
while ($data = mysqli_fetch_array($rs)) {
    $row = array(
	'place_idx' => $data['place_idx'], 
	'score_avg'=>$data['score_avg']);
    $items[] = $row;
}
$arr = array('sendData' => $items);
echo json_encode($arr);
mysqli_close($conn);
?>
```

7. 카테고리 별 상점 검색

    * 분류별 (식당/카페/노래방/피시방) category 의 값이 변경 됩니다. 

```<?php
require_once ('connect.php');

$category=$_REQUEST['category'];
$place_name=$_REQUEST['place_name'];

$sql = "select * from place where place_name like '%{$place_name}%'" and category='1'";
// cafe='2' / music='3' / pc='4' 

$rs = mysqli_query($conn, $sql);

while($data = mysqli_fetch_array($rs)) { //로그인 성공
	$row = array (
		'place_idx' => $data[place_idx],
		'category' => $data[category],
		'place_name' => $data[place_name],
		'start_time' => $data[start_time],
		'end_time' => $data[end_time],
		'address' => $data[address],
		'tel' => $data[tel],
		'menu' => $data[menu],
		'price' => $data[price],
		'image' => $data[image],
		'latitude' => $data[latitude], 
		'longitude' => $data[longitude]
	);
	$items[]=$row;
}
$arr = array('sendData' => $items);
echo json_encode($arr);
mysqli_close($conn);
?>
```

8. 회원가입 코드
9. 찜 기능
10. 내 리뷰 가져오기





















































### 7. 각 액티비티의 기능별 설명

| 클래스  | 기능  | layout  | manifest 추가 사항  | gradle 추가 사항|
|---|---|---|---|---|
| Common  |  URL 주소 불러오기 |   |   |   |
| Detail  | 상점 클릭시 발생하는 화면  |   |   |   |
| Email  | 회원가입시 필요한 이메일 인증  |   |   |   |
| endWrite  | 리뷰 작성 후 화면이동  |   |   |   |
| First  | 바텀 네비게이션바 구현  |   |   |   |
| HeartDTO  | 찜 목록 불러오기  |   |   |   |​
| HeartFragment  | 내가 찜한 상점을 확인   |   |   |   |
| HomeFragment  | 초기화면  |   |   |   |
| Loading  | 앱 실행시의 로딩창  |   |   |   |​
| Login  | 로그인, 로그인 세션   |   |   |   |
| MapFragment  | 등록된 상점을 마커로 확인   |   |   |   |
| MyFragment  | 내 정보를 볼 수 있는 곳   |   |   |   |​
| MyReview  | 내가 작성한 리뷰 확인   |   |   |   |
| Notice  |   |   |   |   |
| Notice  |   |   |   |   |​
| PlaceDTO  | DB에서 가져온 상점 정보를 넘겨받음  |   |   |   |
| QrScan  | QR코드를 인식하여 리뷰쓰기로 전환   |   |   |   |
| ReviewDTO  | DB의 리뷰관련 내용을 가져옴   |   |   |   |​
| ReviewWrite  | 리뷰작성시 필요한 STT(Speech TO Text), DB정보 가져오기    |   |   |   |
| SessionManager  | 로그인 유지   |   |   |   |
| SignUp  | 회원 가입   |   |   |   |
| StoreList  | 음식점에 해당하는 정보 가져오기   |   |   |   |
| StoreList2  | 카페에 해당하는 정보 가져오기   |   |   |   |
| StoreList3  | 노래방에 해당하는 정보 가져오기  |   |   |   |​
| StoreList4  | 피시방에 해당하는 정보 가져오기  |   |   |   |
| StoreMapFragment  | 카드뷰 안에서 상점별 맵 보여주기   |   |   |   |


### 8. 클래스 별 코드 상세 설명

1. Common.java 설명

* 앱의 모든 기능들이 데이터베이스의 정보를 가져오므로 번거롭게 URL 주소를 계속 쓰는 것을    막기위해 하나의 클래스를 만들었습니다.

```   public class Common {
    public static final String SERVER_URL
            = "http://eileenyoo1.cafe24.com";
    //public static String userid;
    //public static String name; 이 부분 주석으로 만들어도 되는지 체크할 것
    }
```
<br><br><br><br>


<!-- EndWriteReview.java -->
2. EndWriteReview.java 설명

* QR코드 인식후 리뷰작성 완료시 나오는 페이지 입니다.<br> 
* 리뷰작성 후 FirstActivity.java 또는 MyReviewActivity.java로 이동 할 수 있는 버튼이 있습니다.<br>
* Glide를 사용해 Gif 파일을 ImageView에서 사용하였습니다.
<br><br>

* EndWriteReview.java 전체 코드
```
package com.example.smAio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
/**
 * QR코드 인식후 리뷰 작성완료시 나오는 페이지 입니다.
 * glide를 이용하여 gif 이미지를 사용하였으며 버튼을 이용하여 activity 전환을 하였습니다.
 */
public class EndWriteReview extends AppCompatActivity {
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
                Intent intent=new Intent(EndWriteReview.this,FirstActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button_MyReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EndWriteReview.this,MyReviewActivity.class);
                intent.putExtra("id",userid);
                startActivity(intent);
                finish();
            }
        });
    }
    //BackPress 버튼 이벤트
    @Override
    public void onBackPressed() { //뒤로가기 버튼 클릭시
        Intent intent = new Intent(EndWriteReview.this,FirstActivity.class);
        startActivity(intent);
        finish();
    }
}
```
<br><br><br><br>


<!-- ReviewWriteActivity.java -->
3. ReviewWriteActivity.java 설명

* QR코드 인식후 리뷰작성하는 페이지 입니다.<br>
* 리뷰작성을 위한 EditText가 있으며 사용자 편의를 위해 음성인식 기능을 넣었습니다. <br>
* 별점 기능을 넣어 가게의 전체적인 평점을 줄 수 있도록 했습니다.<br>
* 리뷰작성을 완료하는 Button이 있습니다. <br>
* DB에 저장된 가게 ID값을 가져오도록 서버와 연동하였습니다.
<br><br>

* ReviewWriteActivity.java 전체 코드
```
package com.example.smAio;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReviewWriteActivity extends AppCompatActivity {
    /**
     * QR인식 후 리뷰 작성하는 Activity입니다.
     * 별점, 음성인식 등의 기능이 있습니다.
     */

    //가게의 id값을 가져오기위한 서버에 등록된 php문 주소
    private static String URL_getPlaceID ="http://eileenyoo1.cafe24.com/get_placeID.php/";
    //DB에서 가져온 가게의 id값을 저장하는 변수
    int place_idx;

    private static final int REQUEST_CODE = 1234;

    ImageButton Start;
    Dialog match_text_dialog;
    ListView textlist;
    ArrayList<String> matches_text;
    EditText txtReview;
    TextView txtScore;
    Button button;

    //QrScanActivity에서 가져온 값들
    String user_Id,place_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);
        setTitle("Review Write");

        //QrScanActivity에서 값 가져오는 Intent
        Intent i = getIntent();
        user_Id=i.getStringExtra("id");
        place_url=i.getStringExtra("url");

        //layout items ID 가져오기
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setColorFilter(R.color.colorblue);
        Start = (ImageButton)findViewById(R.id.imageButton);
        txtReview=(EditText)findViewById(R.id.review_message);
        txtScore=(TextView)findViewById(R.id.textView3);
        button=(Button)findViewById(R.id.button_reviewsend);

        //reviewsend button Click Event
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //txtReview에 저장된 리뷰내용 변수에 저장
                String txtReview_textCheck=txtReview.getText().toString();

                //txtReview에 값이 있을 때
                if(!txtReview_textCheck.isEmpty()) {
                    //match_url 함수를 통해 DB에서 가게 id값 가져오기
                    match_url(place_url);

                    //Intent에 아이디 값을 실어 액티비티 전환
                    Intent intent = new Intent(ReviewWriteActivity.this, EndWriteReview.class);
                    intent.putExtra("id",user_Id);
                    startActivity(intent);
                    finish();
                }
                //txtReview에 값이 없을 때
                else{
                    txtReview.setError("Please insert your Review");
                }
            }
        });

        //음성인식 버튼 클릭 이벤트
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()){
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Plese Connect to Internet", Toast.LENGTH_LONG).show();
                }}
        });


        //별점 레이팅바
        final TextView tv = (TextView) findViewById(R.id.textView3);
        RatingBar rb = (RatingBar)findViewById(R.id.ratingBar);

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //Ratingbar(별)을 드래그하거나 클릭해서 값이 변하면 텍스트뷰 tv에 별점이 몇인지 표시해준다.
                tv.setText(" " + rating);
            }
        });
    }

    //음성인식 인터넷 연결 코드
    public  boolean isConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net!=null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    //음성인식 다이얼로그 표시
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            match_text_dialog = new Dialog(ReviewWriteActivity.this);
            match_text_dialog.setContentView(R.layout.dialog_matches_frag);
            match_text_dialog.setTitle("Select Matching Text");
            textlist = (ListView)match_text_dialog.findViewById(R.id.list);
            matches_text = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, matches_text);
            textlist.setAdapter(adapter);
            textlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    txtReview.setText(matches_text.get(position));
                    match_text_dialog.hide();
                }
            });
            match_text_dialog.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void review(){
//네트워크 관련 작업은 백그라운드 스레드에서 처리합니다.
        final StringBuilder sb=new StringBuilder();
        Thread th = new Thread(new Runnable() {
            public void run() {
                try {
                    String review_content=txtReview.getText().toString();
                    String score = txtScore.getText().toString();
                    String page =
                            Common.SERVER_URL+"/review_insertt.php?"+"&userid="+user_Id
                                    +"&place_idx="+place_idx
                                    +"&review_content="+review_content
                                    +"&score="+score;
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
                        }
                        conn.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }

    @Override
    public void onBackPressed() { //뒤로가기 버튼 클릭시
        Intent intent = new Intent(ReviewWriteActivity.this,QrScanActivity.class);
        startActivity(intent);
        finish();
    }

    //QR 주소를 통해 DB에 저장된 해당 가게의 id를 가져오는 코드
    private void match_url(final String url){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_getPlaceID,
                //php문에서 온 응답에 대한 이벤트
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //성공적인 응답일 경우
                        try{
                            //php문에서의 응답을 기록한 json파일 확인을 위한 JSONObject 객체 생성
                            JSONObject jsonObject=new JSONObject(response);
                            //success라는 키에 들어있는 string값 변수에 저장
                            String success = jsonObject.getString("success");
                            //JSONObject에 저장된 Array파일 객체 생성
                            JSONArray jsonArray = jsonObject.getJSONArray("place_id");

                            //success라는 키에 들어있는 값이 "1" 일 때
                            if(success.equals("1")){
                                //jsonArray에 들어있는 데이터 확인 및 저장을 위한 for문
                                for(int i = 0; i<jsonArray.length();i++){
                                    JSONObject object=jsonArray.getJSONObject(i);
                                    //jsonArray에 "p_id"이라는 키값으로 저장된 데이터 가져오기
                                    place_idx=object.getInt("p_id");
                                    //review()함수 실행
                                    review();
                                }
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }

                })
        {
            //php문에 값을 보내는 코드
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                //HashMap 사용
                Map<String,String> params = new HashMap<>();
                //QR의 url
                params.put("url",url);
                //php문으로 return
                return params;
            }
        };
        //Volley 사용을 위한 코드
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}

```
<br><br><br><br>


<!-- LoginActivity.java -->
4. LoginActivity.java 설명

* 로그인을 하기위한 Activity입니다.<br> 
* ID,PASSWORD EditText, Login Button, SignUp Button 등 으로 이루어져 있습니다. <br>
* 로그인 기능을 위해 서버연동을 하였습니다.<br>
* 로그인 유지를 위해 Session을 만들었습니다. 
<br><br>


* LoginActivity.java 전체 코드
```
package com.example.smAio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 로그인을 하기위한 activity입니다.
 * Volley를 이용하여 Server와 연동시켰습니다.
 * SheardPreference를 이용하여 자동로그인 기능을 구현했습니다. (SessionManager Acticity 참고)
 */

public class LoginActivity extends AppCompatActivity {

    //layout의 items 변수선언
    private EditText id,password;
    private ImageButton login;
    private TextView link_signup;
    private ProgressBar loading;
    private CheckBox auto;

    //SessionManager 사용을 위한 선언
    SessionManager sessionManager;

    //자동로그인을 위한 SharedPreferences
    SharedPreferences AutoPref;
    SharedPreferences.Editor edit;

    //Server에 저장된 login.php 주소
    private final static String URL_LOGIN ="http://eileenyoo1.cafe24.com/login.php/";

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //SessionManager 객체생성
        sessionManager=new SessionManager(this);

        //layout items id 가져오기
        auto=(CheckBox)findViewById(R.id.AutoLoginCheck);
        id=(EditText)findViewById(R.id.id);
        password=(EditText)findViewById(R.id.password);
        loading=(ProgressBar)findViewById(R.id.progress_loading);
        login = (ImageButton)findViewById(R.id.login);
        link_signup=(TextView)findViewById(R.id.signupButton);

        //SharedPreferences 객체생성
        AutoPref = getSharedPreferences("auto",MODE_PRIVATE);

        //AutoPref(SharedPreferences) 사용을 위한 editor 객체 생성
        edit = AutoPref.edit();

        //MyFragment에서 logout버튼 클릭시 세션초기화를 알려주는 Boolean변수
        Boolean boolc=getIntent().getBooleanExtra("boolcheck",true);


        if(boolc==false){ //MyFragment에서 받은 값이 false일 때 (세션초기화 성공)
            //자동로그인 CheckBox 체크 false
            auto.setChecked(false);
            //AutoPref에 저장된 CheckBox의 체크여부 초기화
            edit.clear();
            //commit을 통해 AutoPref에 변경사항 저장
            edit.commit();
        }
        else{ //MyFragement에서 logout버튼을 클릭하지 않아 true(defaltValue값)이 왔을 때
            if(AutoPref.getBoolean("checkbox",false)==true){ //AutoPref에 저장 된 값이 true(체크됨)일 때
                //checkbox의 체크를 계속 유지
                auto.setChecked(true);
                //아이디칸에 AutoPref에 저장된 id값 저장
                id.setText(AutoPref.getString("id","error"));
                //비밀번호칸에 AutoPref에 저장된 password값 저장
                password.setText(AutoPref.getString("password","error"));
                //Login함수를 실행( AutoPref에 저장된 id, AutoPref에 저장된 password)
                Login(AutoPref.getString("id",null),AutoPref.getString("password",null)); //로그인 실행
            }
        }

    //login이벤트를 자판의 엔터로 하기위한 코드
    password.setOnKeyListener(new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
            if (keycode==KeyEvent.KEYCODE_ENTER){ //만약 keycode값이 KEYCOD_ENTER 일 때
                //login의 click 이벤트 불러옴
                login.callOnClick();
                return true;
            }
            return false;
        }
    });
        //login 버튼 Click Event
        login.setOnClickListener(new LoginClickListener());
        //signup TextView Click Event
        link_signup.setOnClickListener(new SignUpClickListener());
    }

    //로그인을 위한 Server연동 함수 (Volley이용)
    private void Login(final String cid, final String cpassword){
        //loading ProgressBar VISIBLE로 변경
        loading.setVisibility(View.VISIBLE);

        //Volley를 이용한 Server연동 - POST방식으로 php에 값 전달
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_LOGIN,
                //php문에서 온 응답에 대한 이벤트
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //성공적인 응답일 경우
                        try{
                            //php문에서의 응답을 기록한 json파일 확인을 위한 JSONObject 객체 생성
                            JSONObject jsonObject=new JSONObject(response);
                            //success라는 키에 들어있는 string값 변수에 저장
                            String success = jsonObject.getString("success");
                            //JSONObject에 저장된 Array파일 객체 생성
                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            //success라는 키에 들어있는 값이 "1" 일 때
                            if(success.equals("1")){
                                //jsonArray에 들어있는 데이터 확인 및 저장을 위한 for문
                                for(int i = 0; i<jsonArray.length();i++){
                                    JSONObject object=jsonArray.getJSONObject(i);
                                    //jsonArray에 "name"이라는 키값으로 저장된 데이터 가져오기
                                    String name=object.getString("name").trim();
                                    //Toast 메시지로 성공 메시지와 이름 값을 띄운다
                                    Toast.makeText(LoginActivity.this,
                                            "Success Login. \nYour NAME : "
                                                    +name,Toast.LENGTH_SHORT)
                                            .show();
                                    //loading ProgressBar GONE으로 변경
                                    loading.setVisibility(View.GONE);
                                    //SessionManager에 유저 이름과 아이디 저장
                                    sessionManager.createSession(name,cid);

                                    //자동로그인 CheckBox가 체크 됬을 때
                                    if(auto.isChecked()){
                                        //아이디, 비밀번호, 체크 여부를 AutoPref에 저장
                                        edit.putString("id",cid);
                                        edit.putString("password",cpassword);
                                        edit.putBoolean("checkbox",true);
                                        edit.commit();
                                    }
                                    //자동로그인 CheckBox가 체크가 안 됬을 때
                                    else{
                                        //아이디와 비밀번호값을 ""로 초기화하고 체크박스 여부도 false로
                                        edit.putString("id","");
                                        edit.putString("password","");
                                        edit.putBoolean("checkbox",false);
                                        edit.commit();
                                    }

                                    //인텐트를 통해서 FirstActivity로 이동
                                    Intent loginIntent = new Intent(LoginActivity.this, FirstActivity.class);
                                    LoginActivity.this.startActivity(loginIntent);
                                    finish();
                                }
                            }
                            //success값이 1이 아닐 때 (아이디에 맞는 비밀번호가 틀리다는 뜻)
                            else{
                                //password Error Massage 띄우기
                                password.setError("Please check your PASSWORD!!");
                                //loading ProgressBar GONE으로 변경
                                loading.setVisibility(View.GONE);
                            }
                        }
                        //try에서 오류 발생시 (php문에 전달한 id가 서버에 없다는 뜻)
                        catch (JSONException e){
                            //id Error Massage 띄우기
                            id.setError("Please check your ID!!");
                            //loading ProgressBar GONE으로 변경
                            loading.setVisibility(View.GONE);
                            //login Button VISIBLE로 변경
                            login.setVisibility(View.VISIBLE);

                            e.printStackTrace();
                        }
                    }
                },
                //서버 접속 오류
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //loading ProgressBar GONE으로 변경
                        loading.setVisibility(View.GONE);
                        //login Button VISIBLE로 변경
                        login.setVisibility(View.VISIBLE);
                    }

                })
        {
            //php문에 값을 보내는 코드
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                //HashMap 사용
                Map<String,String> params = new HashMap<>();
                //입력된 id
                params.put("id",cid);
                //입력된 password
                params.put("password",cpassword);
                //php문으로 return
                return params;
            }
        };
        //Volley 사용을 위한 코드
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //Login Button Click Event
    private class LoginClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //변수에 id, password EditText에 입력된 값 저장
            String mId=id.getText().toString().trim();
            String mPass = password.getText().toString().trim();

            //EditText에 id, password가 모두 입력됬을 때
            if(!mId.isEmpty() || !mPass.isEmpty()){
                //Login 함수 실행
                Login(mId,mPass);
            }
            //하나라도 비어있을 시
            else{
                id.setError("Please insert id");
                password.setError("Please insert password");
                loading.setVisibility(View.GONE);
            }
        }
    }

    //SingUp TextView Click Event
    private class SignUpClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //인텐트를 사용하여 엑티비티 전환
            Intent emailIntent = new Intent(LoginActivity.this, EmailActivity.class);
            LoginActivity.this.startActivity(emailIntent);
        }
    }
}
```
<br><br><br><br>


<!-- SignUpActivity.java -->
5. SignUpActivity.java 설명

* 회원가입을 하기위한 Activity입니다.<br> 
* ID,PASSWORD,NAME EditText, id_check Button, SignUp Button으로 이루어져 있습니다. <br>
* 회원가입, 아이디 중복체크 기능을 위해 서버연동을 하였습니다. 
<br><br>


* SignUpActivity.java 전체 코드
```
package com.example.smAio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 회원가입을 하기위한 Activity
 * 아이디 중복체크 기능과 회원가입 기능
 */
public class SignUpActivity extends AppCompatActivity {
    //회원가입과 중복체크를 위한 php문이 저장된 서버의 주소
    private static String URL_SignUp ="http://eileenyoo1.cafe24.com/UserSignUp.php/";
    private static String URL_Check ="http://eileenyoo1.cafe24.com/Idcheck.php/";

    //layout items 변수
    private EditText id,password,name;
    private Button btn_create;
    private Button btn_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //layout items id값 가져오기
        id=(EditText)findViewById(R.id.idText);
        password=(EditText)findViewById(R.id.passwordText);
        name=(EditText)findViewById(R.id.nameText);
        btn_create=(Button)findViewById(R.id.createButton);
        btn_check=(Button)findViewById(R.id.check_id);

        //onCreate시 btn_create Button 비활성화
        btn_create.setClickable(false);

        //btn_create Button Click Event
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditText값 가져와 변수에 저장
                String mId=id.getText().toString().trim();
                String mPass = password.getText().toString().trim();
                String mName = name.getText().toString().trim();

                //이름, 아이디, 비밀번호의 EditText에 값이 모두 입력되었을 때
                if(!mId.isEmpty() || !mPass.isEmpty() || !mName.isEmpty()){
                    //SignUp 함수 실행
                    SignUp();
                }
                //하나라도 입력이 안된 EditText가 있을 때
                else{
                    //에러 메시지 발생
                    id.setError("Please insert ID");
                    password.setError("Please insert PASSWORD");
                    name.setError("Please insert NAME");
                }
            }
        });

        //btn_check Button Click Event
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //아이디 EditText에 입력된 값 변수에 저장
                String mId=id.getText().toString().trim();
                //값이 있을 때
                if(!mId.isEmpty()) {
                    //Check 함수 실행
                    Check(mId);
                }
            }
        });
    }

    //ID Check를 위한 Server연동 함수 (Volley이용)
    private  void Check(final String cid){
        //Volley를 이용한 Server연동 - POST방식으로 php에 값 전달
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_Check,
                //php문에서 온 응답에 대한 이벤트
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        //성공적인 응답일 경우
                        try{
                            //php문에서의 응답을 기록한 json파일 확인을 위한 JSONObject 객체 생성
                            JSONObject jsonObject=new JSONObject(response);
                            //success라는 키에 들어있는 string값 변수에 저장
                            String success = jsonObject.getString("success");

                            //success라는 키에 들어있는 값이 "1" 일 때
                            if(success.equals("1")) {
                                //아이디를 사용할 수 있다라는 toast Message 띄우기
                                Toast.makeText(SignUpActivity.this,"You can use this ID",Toast.LENGTH_SHORT).show();

                                //id EditText 비활성화
                                id.setClickable(false);
                                id.setFocusable(false);
                                //id EditText 색 회색으로 변경
                                id.setBackground(getDrawable(R.drawable.edit_line_false));

                                //btn_check Button 비활성화
                                btn_check.setClickable(false);
                                //btn_check Button 색 회색으로 변경
                                btn_check.setBackgroundColor(getResources().getColor(R.color.colorGray));

                                //btn_create Button 활성화
                                btn_create.setClickable(true);
                                //btn_create 색 파랑색으로 변경
                                btn_create.setBackgroundColor(getResources().getColor(R.color.colorButtonOn));
                            }
                            //success라는 키에 들어있는 값이 "0" 일 때
                            else{
                                //id가 사용중이다라는 Error Message 띄우기
                                id.setError("Your ID is already in use.");
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //서버 접속오류시
                    }
                })
        {
            //php문에 값을 보내는 코드
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                //HashMap 사용
                Map<String,String> params = new HashMap<>();
                //입력된 id
                params.put("id",cid);
                //php문으로 return
                return params;

            }
        };
        //Volley 사용을 위한 코드
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    //회원가입을 위한 Server연동 함수 (Volley이용)
    private void SignUp(){

        //btn_create Button GONE으로 변경
        btn_create.setVisibility(View.GONE);

        //각 EditText에 입력된 값 변수에 저장
        final String id = this.id.getText().toString().trim();
        final String password = this.password.getText().toString().trim();
        final String name = this.name.getText().toString().trim();

        //Volley를 이용한 Server연동 - POST방식으로 php에 값 전달
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SignUp,
                //php문에서 온 응답에 대한 이벤트
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //성공적인 응답일 경우
                        try{
                            //php문에서의 응답을 기록한 json파일 확인을 위한 JSONObject 객체 생성
                            JSONObject jsonObject = new JSONObject(response);
                            //success라는 키에 들어있는 string값 변수에 저장
                            String success = jsonObject.getString("success");

                            //success라는 키에 들어있는 값이 "1" 일 때
                            if(success.equals("1")){
                                //회원가입 완료(DB에 성공적으로 값 저장) Toast Message 출력
                                Toast.makeText(SignUpActivity.this,"Reqister Success!",Toast.LENGTH_SHORT).show();
                            }
                        }
                        //try문에서 오류발생 시
                        catch (JSONException e){
                            e.printStackTrace();
                            //btn_create Button VISIBLE으로 변경
                            btn_create.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    //서버 접속 오류시
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //btn_create Button VISIBLE으로 변경
                        btn_create.setVisibility(View.VISIBLE);
                    }
                })
        {
            //php문에 값을 보내는 코드
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //HashMap 사용
                Map<String,String> params = new HashMap<>();
                //입력된 id
                params.put("id",id);
                //입력된 name
                params.put("name",name);
                //입력된 password
                params.put("password",password);
                //php문으로 return
                return params;

                //php문에 값을 보냄
            }
        };
        //Volley 사용을 위한 코드
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        finish();
    }
}
```
<br><br><br><br>


<!-- SessionManager.java -->
6. SessionManager.java 설명

* 유저 정보에 관한 Session을 관리하기 위한 클래스 입니다.<br> 
* User_ID와 User_NAME 의 정보를 SharedPreferences에 저장하고 삭제합니다.
<br><br>


* SessionManager.java 전체 코드
```
package com.example.smAio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * user의 정보를 유지하기위한 session을 관리하는 Activity
 * SharedPreferences 이용
 */
public class SessionManager {

    //세션을 만들기위한 sharedpreferences와 editor
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;


    public Context context;
    int PRIVATE_MOD = 0 ;

    //Session에 저장되는 값들 선언
    private static final String PREF_NAME="LOGIN";
    private static final String LOGIN ="IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String ID = "ID";

    //ShardPreferences를 사용하기 위한 editor를 선언하는 함수
    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME,PRIVATE_MOD);
        editor = sharedPreferences.edit();
    }

    //세션만들기 ShardPreferences에 값 저장
    public void createSession(String name,String id){
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME,name);

        editor.putString(ID,id);
        editor.apply();
    }

    //로그인 여부를 체크해 boolean값을 저장하는 함수
    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN,false);
    }

    //로그인 여부를 체크해 액티비티를 종료하는 함수
    public void checkLoggin(){
        if(!this.isLoggin()){
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((FirstActivity)context).finish();
        }
    }

    //저장된 유저 정보를 가져오는 함수
    public HashMap<String,String>getUserDetail(){
        HashMap<String,String> user = new HashMap<>();
        user.put(NAME,sharedPreferences.getString(NAME,null));
        user.put(ID,sharedPreferences.getString(ID,null));
        return user;
    }

    //로그아웃시 세션을 지우는 함수
    public void logout(){
        editor.clear();
        editor.commit();
    }
}

```
<br><br><br><br>
​



