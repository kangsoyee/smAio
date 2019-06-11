<?php
    if($_SERVER['REQUEST_METHOD'] =='POST'){
      $placeName = $_POST['name'];
      $userID = $_POST['userId'];

      require_once ('connect.php');
      $sql = "DELETE FROM heart WHERE userid = '$userID' AND choose = '$placeName'";

      if(mysqli_query($conn,$sql)){
        $result["success"] = "1";
        $result["message"] = "success";
        echo json_encode($result);
        mysqli_close($conn);
      } else{
        $result["success"] = "0";
        $result["message"] = "error";
        echo json_encode($result);
        mysqli_close($conn);
      }
    }
?>