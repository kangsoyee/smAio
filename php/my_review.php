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