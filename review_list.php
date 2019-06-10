<?php
require_once ('connect.php');

$place_idx = $_REQUEST['place_idx'];

$sql = "select r.idx,m.userid,r.review_date, r.place_idx, r.review_content, r.score\r\n
		from review r, member m, place p\r\n
		where r.userid = m.userid and r.place_idx = p.place_idx and r.place_idx={$place_idx}\r\n
		order by r.review_date"; 
		
		
$rs = mysqli_query($conn, $sql);
while ($data = mysqli_fetch_array($rs)) {
    //로그인 성공
    $row = array(
	'idx' => $data[idx],
	'userid' => $data[userid],
	'review_date' => $data[review_date], 
	'place_idx' => $data[place_idx], 
	'review_content' => $data[review_content],
	'score'=>$data[score]
	);
    $items[] = $row;
}
$arr = array('sendData' => $items);
echo json_encode($arr);
mysqli_close($conn);
?>