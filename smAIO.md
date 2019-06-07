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
  $conn = mysqli_connect("localhost","eileenyoo1","@@@@@@","eileenyoo1"); // ("localhost,db아이디,dbPW,db이름")
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
require_once ('connect.php');
$sql = "select * from place";

$rs = mysqli_query($conn, $sql);
while ($data = mysqli_fetch_array($rs)) {
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
echo json_encode($arr);
mysqli_close($conn);
?>
```

4. 상점별 리뷰 가져오기
```<?php
require_once ('connect.php');

$place_idx = $_REQUEST['place_idx'];

$sql = "select r.idx,m.userid,r.review_date, r.place_idx, r.review_content\r\n
		from review r, member m, place p\r\n
		where r.userid = m.userid and r.place_idx = p.place_idx and r.place_idx={$place_idx}\r\norder by r.review_date"; 
		
		
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

5. 리뷰 작성, 평점 전달하기
```<?php
require_once ('connect.php');

$userid = $_REQUEST['userid'];
$place_idx = $_REQUEST['place_idx'];
$review_content = $_REQUEST['review_content'];
$score = $_REQUEST['score'];
/* $sql = "insert into review (userid,place_idx,review_date,review_content) 
		values ('{$userid}','{$place_idx}',now(),'{$review_content}')";
 */
$sql = "insert into review (userid,place_idx,review_date,review_content,score) 
		values ('{$userid}','{$place_idx}',now(),'{$review_content}','{$score}')";
		
echo "sql:{$sql}<br>";
mysqli_query($conn, $sql);
//mysqli_close($conn);
//mysqli_query($sql);
?>
```

6. 상점별 평균 점수 가져오기
```<?php
require_once ('connect.php');
$place_idx = $_REQUEST['place_idx'];

$sql= "select avg(score) as 'score_avg' from review where place_idx='{$place_idx}'";

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


  






























### 7. 각 액티비티의 기능별 설명

| 클래스  | 기능  | 설명  |   |   |
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
| ReviewWrite  | 리뷰작성시 필요한 STT(Speech TO Text), DB정보 가져오기   |   |   |   |
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

​



