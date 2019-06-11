<?php
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
