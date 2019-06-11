<?php
  //파일명 IdCheck.php
    if($_SERVER['REQUEST_METHOD']=='POST'){
      $id = $_POST['id'];

      require_once ('connect.php'); //연결

      $sql = "SELECT * FROM member WHERE userid='$id' "; //아이디 값이 일치하는 row출력

      $response = mysqli_query($conn, $sql); //실행

      if( mysqli_num_rows($response) === 0){
        $result['success'] = "1";
        $result['message'] = "success";
        echo json_encode($result);
        mysqli_close($conn);
      }
      else{
        $result['success'] = "0";
        $result['message'] = "error";
        echo json_encode($result);
        mysqli_close($conn);
      }


      }



?>