<?php
require_once ('connect.php');

$userid=$_REQUEST['userid'];

$sql = "select * from heart where userid like '{$userid}'";

$rs = mysqli_query($conn, $sql);
while ($data = mysqli_fetch_array($rs)) {
    //로그인 성공
    $row = array('idx' => $data[idx], 
	'userid' => $data[userid], 
	'choose' => $data[choose]); 
    $items[] = $row;
}
$arr = array('sendData' => $items);
echo json_encode($arr);
mysqli_close($conn);
?>

