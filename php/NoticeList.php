<?php
require_once ('connect.php');

$sql = "select * from notice order by noticeDate desc;";
$rs = mysqli_query($conn, $sql);
$response = array();

while ($row = mysqli_fetch_array($rs)) {
    array_push($response,array("noticeContent"=>$row[0],
	"noticeName"=>$row[1],
	"noticeDate"=>$row[2]));
}
echo json_encode(array("response"=>$response));
mysqli_close($conn);
?>
