<?php
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