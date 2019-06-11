<?php
require_once ('connect.php');

$place_idx = $_REQUEST['place_idx'];
$score = $_REQUEST['score'];

$sql = "update review set score + '{$score}' where review";

echo "sql:{$sql}<br>";

mysqli_query($sql);
?>