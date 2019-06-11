<?php
require_once ('connect.php');

$sql = " select avg(r.score)\r\n 
		 from review r\r\n
		 where r.place_idx = 1"; 

$rs = mysqli_query($conn, $sql);

echo "rs:{$rs}<br>";
mysqli_close($conn);
?>