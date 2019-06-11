<?php
  //파일명 UserSignUp.php
    if($_SERVER['REQUEST_METHOD'] =='POST'){
      $id = $_POST['id'];
      $password = $_POST['password'];
      $name = $_POST['name'];

      require_once ('connect.php');
      $sql = "INSERT INTO member(userid,pwd,name) VALUES ('$id','$password','$name')";

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