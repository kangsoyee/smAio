# smAIO
## 목차
1. 주제 선정 이유
2. 유사 앱 분석
3. 앱을 만들때 생기는 문제점 및 해결방안
4. 앱을 만들기 위해 필요한 도구
5. 데이터베이스 사용방법
6. PHP를 사용한 서버 연동
7. 각 액티비티의 기능별 설명
8. 클래스 별 주요 코드 상세 설명
9. 결론
10. 참고 자료 

---

### 1. 주제 선정 이유

* 점심시간마다 메뉴에 대한 고민

* 신입생의 경우 학교 주변 상점들에 대한 정보 없음

* 학교 커뮤니티 앱에서 가게 정보 공유하는 것을 확인
<br/><br/><br/>

### 2. 유사 앱 분석

* 배달의 민족 특징  
  직관적인 UI  
  선 결제를 한사람들에게만 리뷰 작성 기회 제공
* 大众点评 특징  
  고객의 리뷰를 중점으로 운영되는 앱  
  식당 뿐만 아니라 문화 생활 전반적인 것에 대한 리뷰 
<br/><br/><br/>

### 3. 앱을 만들때 생기는 문제점 및 해결방안

문제점

*  비 이용 고객의 후기
*  식당의 협조

해결 방안

* QR 코드를 인식할 때에만 리뷰를 남길 수 있게 한다

* 좋은 리뷰, 평점이 높은 가게를 공지사항에 게시하여 사용자들에게 좋은 이미지로 
  인식될 수 한다.
<br/><br/><br/>

### 4. 앱을 만들기 위해 필요한 도구

* 안드로이드 스튜디오

* 데이터베이스

* 서버  

* PHP
<br/><br/><br/>

### 5. 데이터베이스 사용방법

1. Heidi SQL 다운로드
(https://www.heidisql.com/download.php)

2. (http://eileenyoo1.cafe24.com/image/explain2.PNG)  
설치를 완료했다면,위와 같이 카페24를 통해 구매한 호스트명을 적고 사용자ID, 비밀번호를  
입력하고 접속한다.

3. (http://eileenyoo1.cafe24.com/image/explain.PNG)  
    (http://eileenyoo1.cafe24.com/image/explain3.PNG)

    쿼리문을 통해 테이블을 만들지 않고 툴을 사용하므로 보다 편리하게 직관적으로  
    데이터를 관리할 수 있다.
<br/><br/><br/>

### 6. PHP를 사용한 서버 연동

데이터베이스와 안드로이드 스튜디오를 연동을 위해 서버 사이드 언어인 PHP를 이용한다. 

1. 연결방법 : 
    DB와의 연동을 할 수 있는 이와 같이 코드를 작성해준다. connect.php 파일은  
    다른 파일에서도 반복 사용하게 된다.
        
    ``` d
    <?php
    //파일명 connect.php
    $conn = mysqli_connect("localhost","eileenyoo1","@@@@@@","eileenyoo1");   
    // ("localhost,DB아이디,DB의 비밀번호,DB이름") 순으로 입력해줍니다.
    ?>
    ``` 

2. 회원가입 정보를 DB로 전달
    ```d
    <?php
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
    ``` d
    <?php
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
    ``` d
    <?php
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
    ``` d
    <?php
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
    ``` d
    <?php
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

* 분류별 (식당/카페/노래방/피시방) category 의 값이 변경된다.

    ``` d
    <?php
    require_once ('connect.php');

    $category=$_REQUEST['category'];
    $place_name=$_REQUEST['place_name'];

    $sql = "select * from place where place_name like '%{$place_name}%' and category='1'";
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

8. 찜 기능  

* 상점이 선택된다면 insert문을 사용하여 choose에 값을 넣어주고 삭제가 된다면 delete문을 사용하여 choose의 값을 제거하여 준다.

    ``` d
    <?php
      if($_SERVER['REQUEST_METHOD'] =='POST'){
        $placeName = $_POST['name'];
        $userID = $_POST['userId'];

        require_once ('connect.php');
        $sql = "INSERT INTO heart(choose,userid) VALUES ('$placeName','$userID')";

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

    ``` d
    <?php
      if($_SERVER['REQUEST_METHOD'] =='POST'){
        $placeName = $_POST['name'];
        $userID = $_POST['userId'];

        require_once ('connect.php');
        $sql = "DELETE FROM heart WHERE userid = '$userID' AND choose = '$placeName'";

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

9. 내 리뷰 가져오기  

* 작성된 리뷰를 상점의 고유번호와 로그인된 아이디를 이용하여 불러온다.    

    ``` d
    <?php
    require_once ('connect.php');

    $userid=$_REQUEST['userid'];

    $sql = "select * from review,place 
        where review.place_idx=place.place_idx AND userid like '{$userid}'
        order by review_date desc";

    $rs = mysqli_query($conn, $sql);
    while ($data = mysqli_fetch_array($rs)) {
        //로그인 성공
        $row = array('idx' => $data[idx], 
      'place_name' => $data[place_name],
      'userid' => $data[userid], 
      'review_content' => $data[review_content]); 
        $items[] = $row;
    }
    $arr = array('sendData' => $items);
    echo json_encode($arr);
    mysqli_close($conn);
    ?>
    ```
<br/><br/><br/>


### 7. 각 액티비티의 기능별 설명

| 클래스  | 기능  | layout  |
|---|---|---|---|---|
| Common  |  URL 주소 불러오기 |   |
| Detail  | 상점 클릭시 발생하는 화면  | activity_detail.xml<br/>tab_menu.xml  |
| Email  | 회원가입시 필요한 이메일 인증  | activity_email.xml  |
| endWrite  | 리뷰 작성 후 화면이동  | activity_end_write_review.xml  |
| First  | 바텀 네비게이션바 구현  | activity_frist.xml<br/>navigation.xml  |
| HeartDTO  | 찜 목록 불러오기  |   |
| HeartFragment  | 내가 찜한 상점을 확인   | fragment_heart.xml<br/>heart_row.xml  |
| HomeFragment  | 초기화면  | fragment_home.xml  |
| Loading  | 앱 실행시의 로딩창  | activity_loding.xml  |
| Login  | 로그인, 로그인 세션   | activity_login.xml  |
| MapFragment  | 등록된 상점을 마커로 확인   | fragment_map.xml  |
| MyFragment  | 내 정보를 볼 수 있는 곳   | fragment_my.xml  |
| MyReview  | 내가 작성한 리뷰 확인   | activity_my_review.xml<br/>myreview_row.xml  |
| Notice  | 공지사항 띄우기  | notice.xml  |
| PlaceDTO  | DB에서 가져온 상점 정보를 넘겨받음  |   |
| QrScan  | QR코드를 인식하여 리뷰쓰기로 전환   | activity_qr_scan.xml<br/>custom_barcode_scanner.xml  |
| ReviewDTO  | DB의 리뷰관련 내용을 가져옴   |   |
| ReviewWrite  | 리뷰작성시 필요한 STT(Speech TO Text), DB정보 가져오기    | activity_review_write.xml  |
| SessionManager  | 로그인 유지   |   |
| SignUp  | 회원 가입   | activity_signup.xml  |
| StoreList  | 음식점에 해당하는 정보 가져오기   | activity_store_list.xml<br/>place_row.xml  |
| StoreList2  | 카페에 해당하는 정보 가져오기   | activity_store_list.xml<br/>place_row.xml  |
| StoreList3  | 노래방에 해당하는 정보 가져오기  | activity_store_list.xml<br/>place_row2.xml  |
| StoreList4  | 피시방에 해당하는 정보 가져오기  | activity_store_list.xml<br/>place_row2.xml  |
| StoreMapFragment  | 카드뷰 안에서 상점별 맵 보여주기   | fragment_map.xml  |
<br/><br/><br/>


### 8. 클래스 별 주요 코드 상세 설명

1. Common.java 설명

* 앱의 모든 기능들이 데이터베이스의 정보를 가져오므로 번거롭게 URL 주소를 계속 쓰는 것을    막기위해 하나의 클래스를 만들었습니다.

    ``` d
    public class Common {
        public static final String SERVER_URL
                = "http://eileenyoo1.cafe24.com";
    }
    ```
<br><br>


2. EmailActivity.java 설명
* 상명대학교 학생임을 인증하고 중복가입 방지를 하기 위한 클래스입니다.
* Firebase에 연동하여 학교이메일 유무를 확인합니다.
* 인증후 SignUpActivity로 넘어가면서 회원가입을 진행합니다.


    ``` d
    public class EmailActivity extends AppCompatActivity {

        // 파이어베이스 인증 객체 생성
        private FirebaseAuth firebaseAuth;
        ...

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            ...

            // 파이어베이스 인증 객체 선언
            firebaseAuth = FirebaseAuth.getInstance();

            ...
        }

        //작성한값 불러서 이메일,비밀번호 유효성 검사하기
        public void singUp(View view) {
            email = editTextEmail.getText().toString();
            password = editTextPassword.getText().toString();

            if(isValidEmail() && isValidPasswd()) {
                createUser(email, password);
            }
        }

        // 이메일 유효성 검사 메소드
        private boolean isValidEmail() {
            if (email.isEmpty()) {
                // 이메일 공백
                return false;
            } else {
                return true;
            }
        }
        // 비밀번호 유효성 검사 메소드
        private boolean isValidPasswd() {
            if (password.isEmpty()) {
                // 비밀번호 공백
                return false;
            } else {
                return true;
            }
        }

        // 회원가입
        private void createUser(String email, String password) {
            //firebase에 이메일 생성
            firebaseAuth.createUserWithEmailAndPassword(email+"@sangmyung.kr", password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()&&task.isComplete()) {
                                // 회원가입 성공
                                Toast.makeText(EmailActivity.this, "Certification", Toast.LENGTH_LONG).show();
                                //인텐트를 사용하여 엑티비티 전환
                                Intent signupIntent = new Intent(EmailActivity.this, SignUpActivity.class);
                                EmailActivity.this.startActivity(signupIntent);
                                finish();
                            } else {
                                // 회원가입 실패
                                Toast.makeText(EmailActivity.this, "Certification Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
    ```

    * build.gradle

    ``` d
    dependencies {
        ...

        // firebase
        implementation 'com.google.firebase:firebase-core:16.0.9'
        implementation 'com.google.firebase:firebase-auth:17.0.0'
        implementation 'com.google.android.gms:play-services-auth:16.0.1'

        ...
    }
    apply plugin: 'com.google.gms.google-services'
    ```
<br/><br/>


3. SignUpActivity.java 설명

* 회원가입을 하기위한 Activity이다. 
* ID,PASSWORD,NAME EditText, id_check Button, SignUp Button으로 이루어져 있다.
* 회원가입, 아이디 중복체크 기능을 위해 서버연동을 하였다. 

    #### 1. Server 연결 코드 (Volley 통신)

    * 회원가입 기능과 아이디 체크 기능을 사용하기 위해 Volley 통신을 이용하였다.
    * 회원가입 기능을 하는 UserSignUp.php를 작성한다.
    ``` d
    <?php
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
    * 같은 방식으로 아이디 중복체크를 위한 IdCheck.php를 작성한다.
    ``` d
    <?php
        //파일명 IdCheck.php
        if($_SERVER['REQUEST_METHOD']=='POST'){
        $id = $_POST['id'];

        require_once ('connect.php'); //연결

        $sql = "SELECT * FROM member WHERE userid='$id' "; //아이디 값이 일치하는 row출력

        $response = mysqli_query($conn, $sql); //실행

        if( mysqli_num_rows($response) === 0){
            $result['success'] = "1";
            $result['message'] = "success";
            echo json_encode($result);
            mysqli_close($conn);
        }
        else{
            $result['success'] = "0";
            $result['message'] = "error";
            echo json_encode($result);
            mysqli_close($conn);
        }
        }
    ?>
    ```
    * php문에 값을 보내고 result값을 가져오는 코드를 작성한다
    * 회원가입이 성공적이면 php문에서 "1"의 값을 가져오게 된다. if문을 통해 성공메시지를 띄운다.
    <br/><br/>
    * SignUp()
    ``` d
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
    ```
    * 아이디가 중복 된다면 php문에서 "0"의 값을 리턴받게 되고, 중복 메시지를 띄운다
    <br/><br/>
    * Check()
    ``` d
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
    ```
<br/><br/>


4. LoginActivity.java 설명

* 로그인을 하기위한 Activity이다.
* ID,PASSWORD EditText, Login Button, SignUp Button 등 으로 이루어져 있다.
* 로그인 기능을 위해 서버연동한다(Volley).
* 로그인 유지를 위해 Session을 사용한다. 


    #### 1) Server 연결 코드 (Volley 통신)

    * Volley통신은 사용자가 직접 스레드 핸들러를 사용해서 네트워킹을 구현하지 않고 서버와 통신할 수 있는 구글의 통신 라이브러리이다.
    * gradle에 패키지를 추가해준다.
    ``` d
    dependencies {
        ...

        // Volley 라이브러리
        implementation 'com.android.volley:volley:1.1.1'
    }
    ```
    * php문과 서버에 연결하는 코드를 효율적으로 사용하기 위해 connect.php 파일을 만들어준다.
    ``` d
    <?php
        $conn = mysqli_connect("localhost","//Server Id","//Server Password","//DB Name");
    ?>
    ```
    * 입력된 ID, Password를 DB에 비교해 값을 반환해주는 Login.php 파일을 작성한다.
    * DB의 member 테이블에 해당하는 아이디의 row가 있다면 Name정보를 array에 담아 return 한다.
    ``` d
    <?php
        if($_SERVER['REQUEST_METHOD']=='POST'){
        $id = $_POST['id'];
        $password = $_POST['password'];

        require_once ('connect.php'); //연결

        $sql = "SELECT * FROM member WHERE userid='$id' "; //아이디 값이 일치하는 row출력

        $response = mysqli_query($conn, $sql); //실행
        $result = array(); //array형태의 result
        $result['login']=array();

        //만약 실행시킨 row의 개수가 1이라면
        if( mysqli_num_rows($response) === 1 ){
            $row=mysqli_fetch_assoc($response);
            if($password == $row['pwd']){
            $index['name'] = $row['name'];
            array_push($result['login'],$index);
            $result['success'] = "1";
            $result['message'] = "success";
            echo json_encode($result);
            mysqli_close($conn);
            }else{
            $result['success'] = "0";
            $result['message'] = "error";
            echo json_encode($result);
            mysqli_close($conn);
            }
        }
        }
    ?>
    ```
    * php문과 통신하는 Login함수를 LoginActivity.java에 추가해준다.
    * Post 형식으로 id, password 값을 Login.php에 전달하며 Login.php의 return값을 저장한다. 
    ``` d
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

                                        ...

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
    ```
    #### 2. Session
    * 앱 실행 중 유저의 정보를 유지하기 위해 세션을 사용한다.
    * 세션에는 userID, userName 정보가 저장된다.
    * 세션은 SharedPreferences를 이용한 SessionManager.java에서 관리한다.


    * 세션을 관리하기 위한 SessionManager.java 파일을 작성한다.
    * SessionManager.java의 Name,ID 변수에 SharedPreferences를 사용하여 값을 저장한다. 이 때 createSession 함수가 사용된다.
    ``` d
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
    * LoginActivity.java파일에서 SessionManager.java를 사용하기 위해 선언해준다.
    ``` d
    SessionManager sessionManager;
    sessionManager=new SessionManager(this);
    ``` 
    * Login.php 파일에서 가져온 Name, ID 값을 Session에 추가하는 코드를 Login함수에 작성한다.
    ``` d
    try{
        ...

        sessionManager.createSession(name,cid);

        ...
    }
    ```

    #### 3. SharedPreferences
    * 자동로그인 기능을 위한 checkbox의 체크여부, userid, userpassword 값을 editText에 저장하기 위해 사용한다.

    * SharedPreferences와 editer를 선언한다.
    ``` d
    SharedPreferences AutoPref;
    SharedPreferences.Editor edit;

    AutoPref = getSharedPreferences("auto",MODE_PRIVATE);
    edit = AutoPref.edit();
    ```
    * Login.php파일에서 체크박스 여부에 따라서 SharedPreferences에 저장하는 값을 다르게 하기위해 Login함수에 코드를 추가한다.
    ``` d
    try{
        ...

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

        ...
    }
    ```
    * MyFragemnet에서 Logout버튼을 클릭했을때 LoginActivity에 전달되는 boolc 값을 통해 세션의 만료 여부를 확인하고 if문을 통해 SharedPreferences에 값을 사용한다.
    ``` d
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
    ```
<br/><br/>


5. NoticActivity.java 설명

* 데이터베이스에 저장된 공지사항을 띄워주는 액티비티이다.
* 공지사항의 정보를 담고 있는 NoticeDTO.java 클래스와 정보와 리스트뷰를 연결해주는 NoticeListAdapter.java, 공지사항을 띄워주는 NoticeActivity.java로 구성되어 있다.


    * NoticeDTO.java
    ``` d
    public class NoticeDTO {

        String notice; // 공지사항 내용이 담길 문자열
        String name; // 공지사항을 작성한 사람의 이름이 들어갈 문자열
        String date; // 공지사항을 작성한 날짜가 들어갈 문자열

        public NoticeDTO(String notice, String name, String date) { // 클래스의 생성자를 선언해줌
            this.notice = notice;
            this.name = name;
            this.date = date;
        }
    ```

    * NoticeListAdapter.java

    ``` d
    //NoticeListAdapter를 선언할때 BaseAdapter를 상속받게 되는데 , BaseAdapter를 상속받을때 오버라이드가 필요한 함수들이다.
    public class NoticeListAdapter extends BaseAdapter  {
        ...

        //noticelist의 Size를 반환
        @Override
        public int getCount() { return noticeList.size();}

        // noticeList의 Size를 반환하는 함수이다.
        @Override
        public Object getItem(int position) { return noticeList.get(position);}

        // 해당 위치의 NoticeDTO ID를 찾기위하여 position으로 반환
        @Override
        public long getItemId(int position) { return position;}

        // 리스트가 위치한 아이디를 리턴 받기 위한 함수이다.
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
    ```

* View를 선언하여 NoticeDTO.java파일에서 받아온 값들을 텍스트뷰에 설정시켜주는 화면이다. 마지막에 뷰를 리턴하여 화면에 띄워준다.
<br/><br/>


6. StoreListActivity.java 설명

* FirstActivity에서 식당,카페,노래방,피씨방 버튼을 클릭했을때 나오는 상점 목록 액티비티이다.
* 상점 이름을 검색할 수 있는 editText와 검색버튼, 상점 목록이 나오는 ListView로 구성되어있다.
* 리스트에 상점 이미지와 간단한 정보 등이 표시되는데 하나의 상점을 클릭하면 이 정보들을 DetailActivity로 넘겨주게 된다.

    ``` d
    public class StoreListActivity extends AppCompatActivity  {
        ...
        
        //thread 실행 결과값을 핸들러로 불러온다.
        //place_row에 있는것을 리스트 형태로 adapter에 전달한다.
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                PlaceAdapter adapter = new PlaceAdapter(
                        StoreListActivity.this,
                        R.layout.place_row,
                        items);
                list.setAdapter(adapter);
            }
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            ...

            //검색 버튼 클릭 이벤트
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //검색버튼 눌렀을때 키보드 사라지게 해준다.(밑으로 내려준다)
                    InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(editPlaceName.getWindowToken(), 0);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //editText에 입력한 상점 이름을 가져와 search()에 넣어 실행한다.
                            String placeName=editPlaceName.getText().toString();
                            search(placeName);
                        }
                    }, 100);
                }
            });
        }


        //상점 리스트를 보여주는데 사용되는 함수이다.
        void list(){
            //네트워크 관련 작업은 백그라운드 스레드에서 처리
            // final은 지역변수를 상수화 시켜준다. 즉, 한번 실행한 뒤 없어지는 것이 아니라 계속해서 유지 가능하게 해준다.
            final StringBuilder sb=new StringBuilder();
            Thread th = new Thread(new Runnable() {
                public void run() {
                    try {
                        items = new ArrayList<PlaceDTO>();
                        String page = Common.SERVER_URL+"/place_list.php";
                        Log.e("StoreListActivity","여기까지야");

                        URL url = new URL(page);
                        // 커넥션 객체 생성
                        //HTTPURLConnection을 통해 해당 URL에 출력되는 결과물을 얻어올 수 있다.
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
                        JSONArray jArray = (JSONArray) jsonObj.get("sendData"); // 이 부분 이해 안됨
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject row = jArray.getJSONObject(i);
                            PlaceDTO dto = new PlaceDTO();
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

                            if(!row.isNull("image"))
                                dto.setImage(row.getString("image"));

                            Log.e("test", dto.getImage());

                            items.add(dto);
                        }
                        //핸들러에게 화면 갱신 요청
                        handler.sendEmptyMessage(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            th.start();
        }

        //상점 검색에 사용되는 함수이다.
        void search(final String place_name){
            //네트워크 관련 작업은 백그라운드 스레드에서 처리
            final StringBuilder sb=new StringBuilder();
            Thread th = new Thread(new Runnable() {
                public void run() {
                    try {
                        items = new ArrayList<PlaceDTO>();
                        String page = Common.SERVER_URL+"/place_search_food.php?place_name="+place_name;
                        Log.e("Mainactivity","여기까진 됨");

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
                        JSONArray jArray = (JSONArray) jsonObj.get("sendData");
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject row = jArray.getJSONObject(i);
                            PlaceDTO dto = new PlaceDTO();
                            dto.setPlace_idx(row.getInt("place_idx"));
                            dto.setAddress(row.getString("address"));
                            dto.setCategory(row.getString("category"));
                            dto.setEnd_time(row.getString("end_time"));
                            dto.setStart_time(row.getString("start_time"));
                            dto.setTel(row.getString("tel"));
                            dto.setPlace_name(row.getString("place_name"));
                            dto.setMenu(row.getString("menu"));
                            dto.setPrice(row.getString("price"));
                            dto.setLongitude(row.getString("longitude"));
                            dto.setLatitude(row.getString("latitude"));

                            if(!row.isNull("image"))
                                dto.setImage(row.getString("image"));

                            items.add(dto);

                        }
                        //핸들러에게 화면 갱신 요청
                        handler.sendEmptyMessage(0);
                    } catch (Exception e) {
                        list();
                        e.printStackTrace();

                    }
                }
            });
            th.start();
        }


        //Adapter는 ListView에 아이템을 추가,수정,삭제할때 사용되고  데이터를 리스트 형태로 전달받는다.
        class PlaceAdapter extends ArrayAdapter<PlaceDTO> {
            //ArrayList<BookDTO> item;
            public PlaceAdapter(Context context, int textViewResourceId,
                                ArrayList<PlaceDTO> objects) {
                super(context, textViewResourceId, objects);
            }

            //화면이 디스플레이 되기 전에 getView() 메소드가 호출된다.
            //getView() 메소드는 화면에 보여져야 할 아이템의 수 만큼 호출된다.
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    //LayoutInflater는 xml에 정의된 resource들을 view 형태로 반환해준다.
                    //배경이 될 layout을 만들어 놓고 view 형태로 반환받아 Activity에서 실행하게 된다.
                    LayoutInflater li = (LayoutInflater)
                            getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = li.inflate(R.layout.place_row, null);
                }

                try {

                    final PlaceDTO dto = items.get(position);
                    if (dto != null) {
                        TextView place_idx = (TextView) v.findViewById(R.id.place_idx);
                        TextView place_name = (TextView) v.findViewById(R.id.place_name);
                        TextView start_time = (TextView) v.findViewById(R.id.start_time);
                        TextView end_time = (TextView) v.findViewById(R.id.end_time);
                        TextView category = (TextView) v.findViewById(R.id.category);
                        TextView address = (TextView) v.findViewById(R.id.address);
                        TextView tel = (TextView) v.findViewById(R.id.tel);
                        TextView menu = (TextView) v.findViewById(R.id.menu);
                        TextView price = (TextView) v.findViewById(R.id.price);
                        ImageView imgPlace = (ImageView) v.findViewById(R.id.imgPlace);
                        TextView latitude = (TextView)v.findViewById(R.id.latitude);
                        TextView longitude = (TextView)v.findViewById(R.id.longitude);

                        place_name.setText(dto.getPlace_name());
                        start_time.setText(dto.getStart_time());
                        end_time.setText(dto.getEnd_time());
                        address.setText(dto.getAddress());
                        tel.setText(dto.getTel());
                        menu.setText(dto.getMenu());
                        price.setText(dto.getPrice());
                        latitude.setText(dto.getLatitude());
                        longitude.setText(dto.getLongitude());

                        Glide.with(StoreListActivity.this).load(dto.getImage()).into(imgPlace);
                    }

                    //클릭하면 코드를 넘겨서 받아옴
                    v.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView address = (TextView) v.findViewById(R.id.address);
                            TextView tel = (TextView) v.findViewById(R.id.tel);
                            TextView menu = (TextView) v.findViewById(R.id.menu);
                            TextView price = (TextView) v.findViewById(R.id.price);
                            TextView placename = (TextView) v.findViewById(R.id.place_name);
                            TextView starttime = (TextView) v.findViewById(R.id.start_time);
                            TextView endtime = (TextView) v.findViewById(R.id.end_time);
                            TextView latitude = (TextView)v.findViewById(R.id.latitude);
                            TextView longitude = (TextView)v.findViewById(R.id.longitude);

                            //StoreListActivity와 DetailActivity 간에 정보를 주고받기 위해 인텐트에 데이터를 넣어 보낸다.
                            Intent intent = new Intent(StoreListActivity.this, DetailActivity.class);
                            intent.putExtra("idx", dto.getPlace_idx()); //putExtra 는 값을 전달하는 역할을 한다. 받는곳은 getExtra 가 된다.

                            //intent에 putExtra 메서드를 사용하여 데이터를 넣는다.
                            //첫번째 인자는 나중에 데이터를 꺼내기 위한 키 값이고 두번째 인자는 전달할 데이터이다.
                            intent.putExtra("address",address.getText().toString());
                            intent.putExtra("tel",tel.getText().toString());
                            intent.putExtra("menu",menu.getText().toString());
                            intent.putExtra("price",price.getText().toString());
                            intent.putExtra("placename",placename.getText().toString());
                            intent.putExtra("starttime",starttime.getText().toString());
                            intent.putExtra("endtime",endtime.getText().toString());
                            intent.putExtra("userid",userid);

                            dto.setLat(latitude.getText().toString());
                            dto.setLng(longitude.getText().toString());
                            Log.e("test", "ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ"+dto.getLatitude());

                            //startActivity 메서드 사용하여 데이터를 DetailActivity로 보낸다.
                            startActivity(intent);
                            Log.i("test_StoreListActivity","onClick 끝");
                        }
                    });

                }catch (Exception e){
                    Log.e("Network Exception", e.getMessage());
                    return null;
                }
                return v;
            }
        }
    }
    ```
<br/><br/>


7. DetailActivity.java 설명
* StoreListActivity에서 클릭한 가게에 대한 정보를 보여줍니다.
    ``` d
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...

        //getIntent 메서드를 이용해 StoreListActivity에서 보낸 데이터를 받는다.
        Intent get_info = getIntent();

        //intent의 get타입명Extra 메서드를 호출한다.
        // 이때 StoreListActivity에서 putExtra로 지정했던 데이터의 키 값을 지정하면 해당하는 데이터 값이 나오게 된다.
        //만약 지정한 키 값에 맞는 데이터가 없으면 null이 반환된다.
        String ad_data = get_info.getStringExtra("address");
        String tel_data = get_info.getStringExtra("tel");
        String menu_data = get_info.getStringExtra("menu");
        String price_data = get_info.getStringExtra("price");
        final String name_data = get_info.getStringExtra("placename");
        String start_data = get_info.getStringExtra("starttime");
        String end_data = get_info.getStringExtra("endtime");

        ...
    }
    ```
* 전화번호 클릭 시 dial로 화면 전환하여 번호를 띄워줍니다.
    ``` d
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...

        info_tel = (TextView) findViewById(R.id.info_tel);
        info_tel.setPaintFlags(info_tel.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        //전화번호가 적혀있는 info_tel 텍스트뷰의 클릭 이벤트
       info_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //전화번호가 적혀있는 텍스트뷰(info_tel)를 클릭할때만 그 텍스트뷰 값을 받아와 저장한 후 다이얼로 화면 전환하여 번호를 띄워준다.
                mnum = info_tel.getText().toString();
                String tel = "tel:" + mnum;
                switch (v.getId()){
                    case R.id.info_tel:
                        startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                        break;
                }
            }
        });

        ...
    }
    ```

    
    
* 리뷰 탭에서 가게 리뷰를 보여줍니다. 
    
    ``` d
    //쓰레드와 어댑터를 연결해 줄 핸들러입니다.
    Handler handler = new Handler() { // 네트워크 작업을 사용했으므로 쓰레드를 만들어 이 곳에서 처리한다.
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if(msg.what == 1) { // placeInfo 의 값을 받아와서 txt에 넣어준다.
                    txtAddress.setText(placeInfo.getAddress());
                    txtCategory.setText(placeInfo.getCategory());
                    txtEndTime.setText(placeInfo.getEnd_time());
                    txtStartTime.setText(placeInfo.getStart_time());
                    txtTel.setText(placeInfo.getTel());
                    txtMenu.setText(placeInfo.getMenu());
                    txtPlaceName.setText(placeInfo.getPlace_name());
                    txtPrice.setText(placeInfo.getPrice());
                }else if(msg.what == 3){ //리뷰 목록
                    ReviewAdapter adapter = new ReviewAdapter(
                            DetailActivity.this,
                            R.layout.review_row,
                            review_list);
                    list.setAdapter(adapter);
                }else if(msg.what==4){
                    txtReview.setText("");
                    review_list();
                }else  if(msg.what==5){
                    //별점
                    Log.i("test","check");
                    final TextView tv = (TextView) findViewById(R.id.textView4); // 평균점수를 나타낼 수 있는 텍스트를 설정한다.
                    RatingBar rb = (RatingBar) findViewById(R.id.ratingBar); // 점수를 별 모양으로 출력해준다.

                    float rate = avg; // 평균값을 float 형으로 변환시킨다.

                    Log.i("test_rate",rate+"");

                    rb.setRating(rate);
                    tv.setText(rate+"");

                    rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            tv.setText(rating+"");
                        }
                    });
                }
            }
        };
    
    //서버에서 review를 불러오는 메소드입니다.
    void review_list(){
            //네트워크 관련 작업은 백그라운드 스레드에서 처리
            final StringBuilder sb=new StringBuilder();

            Thread th = new Thread(new Runnable() {
                public void run() {
                    try {
                        review_list = new ArrayList<ReviewDTO>();
                        String page = Common.SERVER_URL+"/review_list.php?place_idx="+place_idx;
                        Log.e("DetailActivity","review_list 확인");

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
                        Log.i("test","review_list:"+sb);
    // json.get("변수명")
                        JSONArray jArray = (JSONArray) jsonObj.get("sendData");
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject row = jArray.getJSONObject(i);
                            ReviewDTO dto = new ReviewDTO();
                            dto.setReview_content(row.getString("review_content"));
                            review_list.add(dto);
                        }
                        //핸들러에게 화면 갱신 요청
                        handler.sendEmptyMessage(3);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            th.start();
        }
    ```
    * 리뷰를 화면에 띄워주는 리뷰어댑터입니다.
    ``` d
    class ReviewAdapter extends ArrayAdapter<ReviewDTO> {
            public ReviewAdapter(Context context, int textViewResourceId,
                                ArrayList<ReviewDTO> objects) {
                super(context, textViewResourceId, objects);
            }

            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater li = (LayoutInflater)
                            getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = li.inflate(R.layout.review_row, null);
                }
                final ReviewDTO dto = review_list.get(position);
                Log.i("test","review dto:"+dto);
                Log.i("test","review content:"+dto.getReview_content());
                if (dto != null) {
                    TextView review_content =(TextView) v.findViewById(R.id.review_content);
                    review_content.setText(dto.getReview_content());
                }
                return v;
            }
        }
    ```
    <br>
* 지도 탭에서 구글맵에 가게 위치를 표시해줍니다.
    * StoreMapFragment.java
    ``` d
    ...

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        //PlaceDTO에 저장된 위도 경도 값 불러오기
        PlaceDTO placeDTO = new PlaceDTO();
        lat = placeDTO.getLat();
        lng = placeDTO.getLng();

        //처음 화면을 가게 위치로 초기화
        LatLng currentPosition = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
        this.googleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        //가게 위치에 마커 표시
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentPosition);
        googleMap.addMarker(markerOptions);
    }
    ...
    ```
    <br>
* 가게 평가 점수의 평균을 ratingBar에 표시해줍니다.

    ``` d
    void avg() {
            //네트워크 관련 작업은 백그라운드 스레드에서 처리
            final StringBuilder sb = new StringBuilder();
            Thread th = new Thread(new Runnable() {
                public void run() {
                    try {
                        review_list = new ArrayList<ReviewDTO>();
                        String page = Common.SERVER_URL + "/score_avg.php?place_idx=" + place_idx;
                        Log.i("DetailActivity", "score_avg 확인");

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
                        Log.i("test", "score_avg:" + sb);
    // json.get("변수명")
                        JSONArray jArray = (JSONArray) jsonObj.get("sendData");
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject row = jArray.getJSONObject(i);
                            ReviewDTO dto = new ReviewDTO();
                            avg = dto.setScore_avg(row.getInt("score_avg"));
                            score_avg.add(dto);
                        }
                        //핸들러에게 화면 갱신 요청
                        handler.sendEmptyMessage(5);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            th.start();
        }
    ```
    <br/><br/>


8. MapFragment.java 설명
* 홈화면의 NavigationBar에 포함되어 있는 지도입니다.
* 구글지도를 이용하여 Map을 띄웁니다.
* Map에 현재위치와 가게들의 위치와 정보를 표시해줍니다.

    ``` d
    public class MapFragment extends Fragment implements OnMapReadyCallback {

        //마커 추가 핸들러
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                for (PlaceDTO dto : items) {
                    check_place_name = dto.getPlace_name(); //가게이름
                    menu = dto.getMenu();   //대표메뉴이름
                    addMarker(false, new LatLng(Double.parseDouble(dto.getLatitude()), Double.parseDouble(dto.getLongitude())));    //가게좌표찍기
                }
            }
        };

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.fragment_map, container, false);
            mapView = (MapView) layout.findViewById(R.id.map);
            mapView.getMapAsync(this);

            return layout;
        }

        // 필수 메소드 모음
        @Override
        public void onStart(), onStop(), onSaveInstanceState(Bundle outState), onResume(), onPause(), onLowMemory(), onDestroy() {  }

        //액티비티가 처음 생성될 때 실행되는 메소드
        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (mapView != null) {
                mapView.onCreate(savedInstanceState);
            }
        }

        //맵의 처음화면
        @Override
        public void onMapReady(final GoogleMap googleMap) {

            //처음을 현재 위치로 초기화
            SimpleLocation location = new SimpleLocation(getContext());
            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
            this.googleMap = googleMap;
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            addMarker(true, currentPosition);

            //우측 상단에 현재위치 버튼
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            }

            //확대&축소
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            //thread실행
            list();
        }

        //마커찍기
        private void addMarker(boolean refresh, LatLng location) {
            try {
                if (refresh)
                    googleMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(location);   //마커위치
                markerOptions.title(check_place_name);  //가게이름
                markerOptions.snippet(menu);    //대표메뉴
                googleMap.addMarker(markerOptions);
            } catch (Exception e) {
                Log.e("addMarker failTest", e.getMessage());
            }
        }

        void list() {
            //네트워크 관련 작업은 백그라운드 thread에서 처리
            final StringBuilder sb = new StringBuilder(); // final은 지역변수를 상수화 시켜준다. 즉, 한번 실행한 뒤 없어지는 것이 아니라 계속해서 유지 가능하게 해준다.
            Thread th = new Thread(new Runnable() {
                public void run() {
                    try {
                        String page = Common.SERVER_URL + "/place_all_list.php";
                        Log.e("StoreListActivity", "여기까지야");
                        items = new ArrayList<PlaceDTO>();
                        URL url = new URL(page);
                        // 커넥션 객체 생성
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        // 연결되었으면.
                        if (conn != null) {
                            conn.setConnectTimeout(10000);  //타임아웃 시간 설정
                            conn.setUseCaches(false);   //캐쉬 사용 여부
                            //url에 접속 성공하면
                            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                BufferedReader br = //스트림 생성
                                        new BufferedReader(
                                                new InputStreamReader(
                                                        conn.getInputStream(), "utf-8"));
                                while (true) {
                                    String line = br.readLine();    //한 라인을 읽음
                                    if (line == null) break;    //더이상 내용이 없으면 종료
                                    sb.append(line + "\n");
                                }
                                br.close(); //버퍼 닫기
                            }
                            conn.disconnect();
                        }
                        // 스트링을 json 객체로 변환
                        JSONObject jsonObj = new JSONObject(sb.toString());

                        // json.get("변수명")
                        JSONArray jArray = (JSONArray) jsonObj.get("sendData");
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
    ```
    * AndroidManifest.xml
    ``` d
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.smAio">
        ...

        <uses-permission android:name="android.permission.INTERNET" /> <!--인터넷 사용 권한-->
        <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /> <!--위치 권한(Cell-ID/WiFi)-->
        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /> <!--위치 허용(GPS)-->

        <application
            ... >

            <meta-data
            android:name="com.google.android.maps.v2.API_KEY"
            android:value="AIzaSyAhWqU7twCV7UmMQlrdhKB43kI3IL9DitE" /> <!--GoogleMap API 사용을 위한 API 이름 및 키 값-->

            ...
        </application>
    </manifest>
    ```
    * build.gradle(Module: app)
    ``` d
    dependencies {
        ...

        // map
        implementation 'com.google.android.gms:play-services-location:16.0.0'
        implementation 'com.google.android.gms:play-services-maps:16.1.0'
    }
    ```
<br/><br/>


9. QrScanActivity   

* 구글에서 제공하는 오픈 소스 라이브러리인 zxing을 사용해 QR코드 스캐너를 구현  
* QR 코드를 인식하게 되면 리뷰를 작성할 수 있는 페이지로 전환되게 하는 액티비티이다.

    ``` d
    public class QrScanActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

        private BeepManager beepManager;
        private String lastText;
        ArrayList<PlaceDTO> items;
        String url,id;

        @BindView(R.id.zxing_barcode_scanner)
        DecoratedBarcodeView barcodeScannerView;

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                for(PlaceDTO dto:items) {
                    if (lastText.equals(dto.getQrcode())) { // 데이터베이스에 등록된 QR코드 값과 현재 인식했던 QR코드의 값이 일치한다면 해당 상점의 리뷰 페이지로 전환되는 코드이다.
                        url=lastText;
                        Intent intent = new Intent(QrScanActivity.this, ReviewWriteActivity.class);

                        intent.putExtra("url",url); // reviewWrite 로 url 값을 전달해준다.
                        intent.putExtra("id",id);  // reviewWrite 로 id 값을 전달해준다.
                        startActivity(intent);
                    }
                }
            }
        };

        private BarcodeCallback callback = new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {

                //Line 100 ~ 104. 한번 인식했던 QR코드를 중복해서 인식할 수 없게 해준다.
                if (result.getText() == null || result.getText().equals(lastText)) {
                    // Prevent duplicate scans
                    return;
                }
                lastText = result.getText(); // QR 인식을 통해 얻은 URL 을 lastText 변수에 저장한다.
                Log.i("test", "lastText="+lastText);

                barcodeScannerView.setStatusText(result.getText());

                list(); // 서버와 통신하기 위한 함수를 호출한다.
            }
        }
    }
    ```
<br/><br/>


10. HeartFragment.java 설명

* DetailActivity.java에 있는 하트 버튼을 누르면 그 식당의 이름이 데이터베이스에 올라가게되고 데이터베이스에 저장된 정보를 리스트뷰에 띄워주는 기능이다.

* 하트 버튼을 한번 누르면 버튼을 재클릭해서 하트를 취소하기 전까지는 어플리케이션을 종료해도 하트를 눌렀다는 정보가 유지된다.

    ``` d
    void sendHeart(final String userId, final String place_name){ 
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_sendHeart, // 함수를 실행하면 URL_sendHeart와 연결되어있는 php와 연결되게된다
            new Response.Listener<String>(){
                
                @Override
                public void onResponse(String response) { 

                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");  

                        if(success.equals("1")){ // php연결에 성공하면 success값으로 1을 보낸다

                            Toast.makeText(DetailActivity.this,"찜 성공!",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e){ 
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {//오류발생
                        }
                    })
            {
                //Hashmap<>을 통해 정보를 php에 보낸다. php 키 값에 맞춰 찜 한 사람의 userId, 찜 한 식당 이름인 place_name을 보낸다.
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("userId",userId);
                    params.put("name",place_name);
                    return params;
                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest); //필수코드***********
    }
    ```


* deleteHeart() 함수의 코드이다.
* 함수를 실행하면 URL과 연결된 php에 값을 보내준다.
    ``` d
    //찜목록에서 가게를 삭제합니다.
    void deleteHeart(final String userId, final String place_name){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_deleteHeart, //php문에 POST형식으로, URL_SignUp 주소에 저장된 php문에 보냄
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //php문 응답에 대한 코드
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success"); //php문에서 success라는 키에 값을 저장
                            if(success.equals("1")){//그 값이 1이면(성공)
                                Toast.makeText(DetailActivity.this,"찜목록에서 삭제되었습니다.",Toast.LENGTH_SHORT).show();//찜성공 메시지
                            }
                        }catch (JSONException e){ //오류발생
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//오류발생
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userId",userId);
                params.put("name",place_name);
                return params;

                //php문에 값을 보냄
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest); //필수코드***********
    }
    ```

* 하트버튼을 한번 눌렀을 때 재클릭 하기전까지 버튼 상태를 유지하게 하는 코드이다.

    ``` d
    //하트버튼을 한번 눌렀을 때 재클릭 하기전까지 버튼 상태를 유지하게 하는 코드이다.
    void heartCheck(final String userId, final String place_name){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_heartCheck, 
                    new Response.Listener<String>() {
        @Override
        public void onResponse(String response) { //php문 응답에 대한 코드

                try{

                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success"); //php문에서 success라는 키에 값을 저장
                    if(success.equals("1")){
                        Log.e("heartCheck","true다");
                        iv.setSelected(true);
                        iv.setImageResource(R.drawable.ic_favorite_black_24dp); //이미지수정

                    }
                }

    ```

* 버튼이 클릭되었을 때의 코드이다. 버튼이 클릭되어있다면  URL_heartCheck에서 설정한 success 변수 값에 1을 보낸다. 1을 보내는 이유는 php문과 연결이 성공했는지의 여부를 알아보기 위해서다. 그리고 버튼에 boolean형 변수를 true로 설정해주고, 하트가 꽉 찬 이미지로 설정한다.

    ``` d
        catch (JSONException e){ 

            e.printStackTrace();
            Log.e("heartCheck","false다");
            iv.setSelected(false); 
            iv.setImageResource(R.drawable.ic_favorite_border_black_24dp); 

                            }
                        }
                    }
    ```

* 클릭이 안됐을 때의 코드이다. boolean 값을 false로 주고, 하트가 빈 이미지로 설정한다. 그리고 php에 저장한다.

* HeartDTO.java의 코드이다.
* 정보를 불러와주기 위해 필요한 클래스이다.
* 찜 한 식당의 이름을 저장하는 문자열과 setter and getter 함수로 이루어져 있다.

    ``` d

    public class HeartDTO { //HeartFragment에 내가 찜 한 식당이름을 띄워주기 위해 필요한 클래스입니다

        private String place_name; // 식당이름 변수 선언

        public String getPlace_name() {
            return place_name;
        } //getter and setter 선언

        public void setPlace_name(String place_name) {
            this.place_name = place_name;
        }
    }
    ```


* HeartFragment.java
* 찜 한 식당 이름을 띄워줄 ListView와 찜 한 식당 이름을 불러와 저장할 ArrayList<HeartDTO>,
데이터베이스에서 저장된 식당 이름 값을 불러오는 함수 void list() 로 이루어져 있다.
* ArrayList<HeartDTO> items; 리스트는
HeartDTO에서 받아온 식당이름 문자열인 String place_name 을 저장하는 리스트이다.
데이터베이스와 key값을 맞춰서 값을 받아와 리스트에 저장한다.


* void list() 함수에 대한 코드이다.
* ListView에 정보를 띄우기 위해 필요한 ArrayAdapter와 화면에 띄워줄 getView() 함수로 이루어져있다.
* getView() 함수에서는 미리 디자인해둔 heart_row.xml 파일을 inflate 하여 View를 생성한다.



    ``` d
    class HeartAdapter extends ArrayAdapter<HeartDTO> { //HeartDTO를 담은 ArrayList의 정보를 뿌려줄 HeartAdapter 선언
                //ArrayList<BookDTO> item;
                public HeartAdapter(Context context, int textViewResourceId,
                                    ArrayList<HeartDTO> objects) {
                    super(context, textViewResourceId, objects);
    //this.item= objects;
                }

            @Override
        public View getView(int position, View convertView,                 
                            ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater li = (LayoutInflater)
                        getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = li.inflate(R.layout.heart_row, null);
                // Fragment 자체에 context를 갖고 있지 않기 떄문에, getActivity()로 받아준다.
            }
    ```

* try, catch 문으로 HeartDTO 클래스에서 리스트 위치를 받아와, 만약에 리스트의 자리가 비었으면 그자리에 찜 한 식당을 추가해주고, 뷰를 반환해준다.

    ``` d
            try {
                final HeartDTO dto = items.get(position); //DTO에서 리스트 위치를 받아와서
                if (dto != null) { //만약 리스트뷰의 자리가 비었으면
                    TextView place_name = (TextView) v.findViewById(R.id.place_name); //그 자리에 찜 한 식당이름을 추가해준다
                    place_name.setText(dto.getPlace_name());
                }
            }catch (Exception e){
                Log.e("Network Exception", e.getMessage());
                return null;
            }
            return v; //View를 반환
        }
    ```
<br/><br/>


11. MyReviewActivity.java 설명

* MyFragment에서 내 리뷰 눌렀을때 나오는 액티비티이다.
* 사용자 id값을 받아와서 그 id에 해당하는 리뷰를 불러오는 액티비티이다.
* StoreListActivity.java에서 설명한 것과 같은 원리로 Handler와 list(),Adapter와 getView를 사용하였다.

    * 나머지 부분은 StoreListActivity.java와 같은 원리이다.
    * 추가된 부분은 아래의 코드이다. onCreate 안에서 getIntent 메서드를 이용해 MyFragment와 endWriteReview에서 보낸 데이터를 받아온다.
    ``` d
    Intent get_myreview = getIntent();
    id_text = get_myreview.getStringExtra("id");
    ```
<br/><br/>


12. MyReviewDTO.java 설명

* DB에 만들어진 필드랑 1:1 대응하는 변수 + getter & setter 를 가진 클래스를 DTO 라고 합니다.
* 데이터베이스의 review 테이블과 user 테이블을 사용하였으며 상점이름,사용자 id,해당 id로 남긴 리뷰를 가지고있다.
* 사용 방법은 set을 통해 값을 설정하고, get을 통해 값을 가져온다.

    ``` d
    public class MyReviewDTO {
        private String place_name;
        private String username;
        private String reviewcontent;

        public String getPlace_name() {
            return place_name;
        }

        public void setPlace_name(String place_name) {
            this.place_name = place_name;
        }

        public String getmyId() {
            return username;
        }

        public void setmyId(String Username) {this.username = Username; }

        public String getmyreview_content() {
            return reviewcontent;
        }

        public void setmyreview_content(String ReviewContent) {this.reviewcontent = ReviewContent; }
    }
    ```
<br/><br/><br/>

### 9. 결론

* 본 논문은 상명대학교 학생들을 위한 학교 주변 가게 리뷰 어플리케이션이다. 회원가입시 학교 홈페이지 아이디를 통해 인증받기 때문에 상명대학교 학생이 아닌 사람들은 가입을 할 수 없다.<br/>
가게에 대한 리뷰를 남기기 위해선 가게 내부에 있는 QR코드를 인식해야하기 때문에 허위 리뷰를 작성하는 것을 방지할 수 있다. 음식점에만 한정되어있지 않고 노래방, 피씨방, 카페까지 다양한 가게에 대해 리뷰를 남길 수 있다는 점에서 기존의 어플리케이션과의 차별성이 있다.<br/>
데이터베이스를 중점적으로 활용하여 가게 목록, 평균 점수, 가게 검색, QR코드 인식, 카테고리별 분류, 리뷰 작성 및 점수 전달, 찜 기능, 내 리뷰 가져오기 등을 구현하였다.

* 통신 방식으로는 HttpURLConnection 과 Volley 방식을 사용하여 코드의 복잡성은 있지만
다양한 방법이 사용될 수 있다는 것을 확인하였다.<br/> 
게의 이미지 불러오기, QR코드 스캐너, 지도 보기기능을 구현할 때 API 를 활용하여 보다 수월하게 어플리케이션을 만들 수 있었다.

* 여러 명이 동시에 작업하며 코드를 구성한 것이 처음이라 복잡하지만 앱 실행이 원활하게 되는 것으로 보아 추후 코드의 간결성만 보안한다면 앱 마켓에서도 좋은 성과를 낼 것이라 확신한다.
<br/><br/><br/><br/><br/>


### 10. 참고자료


* volley 통신방식<br/>
https://gist.github.com/benelog/5981448

* HttpURLConnection 통신방식<br/>
http://rabbitpd.blogspot.com/2017/06/urlconnection.html


* php를 활용한 데이터베이스와 안드로이드 연동<br/>
https://twinw.tistory.com/29

* JSON 파싱<br/>
https://dpdpwl.tistory.com/23

* Glide Library<br/>
http://blog.naver.com/PostView.nhn?blogId=soiar777&logNo=221273726164

* 로그인, 회원가입 기능<br/>
https://www.youtube.com/watch?v=zkiGwNiSKLI

* 세션<br/>
https://www.youtube.com/watch?v=bBJo0Gj69Ug

* Firebase 이메일인증<br/>
http://firebase.google.com/docs/auth/android/email-link-auth?hl=ko

* BottonNavigationBar<br/>
https://webnautes.tistory.com/1221

* Tab<br/>
https://farmerkyh.tistory.com/700
https://recipes4dev.tistory.com/115

* RatingBar<br/>
https://bitsoul.tistory.com/30

* Action Dial<br/>
https://mainia.tistory.com/4884
