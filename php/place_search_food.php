<?php
require_once ('connect.php');

$category=$_REQUEST['category'];
$place_name=$_REQUEST['place_name'];

//$sql = "select * from place where category like '%$category%' and place_name like '%$place_name%' ";
$sql = "select * from place where place_name like '%{$place_name}%' and category='1'";
//$sql = "select * from place where category = '%$category%' and place_name like '%$place_name%' ";
//echo $sql;
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






















