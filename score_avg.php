<?php
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