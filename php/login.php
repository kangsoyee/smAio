<?php
    //파일명 login.php

    if($_SERVER['REQUEST_METHOD']=='POST'){
      $id = $_POST['id'];
      $password = $_POST['password'];

      require_once ('connect.php'); //연결

      $sql = "SELECT * FROM member WHERE userid='$id' "; //아이디 값이 일치하는 row출력

      $response = mysqli_query($conn, $sql); //실행
      $result = array(); //array형태의 result
      $result['login']=array();

      if( mysqli_num_rows($response) === 1 ){ //만약 실행시킨 row의 개수가 1이라면
        $row=mysqli_fetch_assoc($response);
        if($password == $row['pwd']){
          $index['name'] = $row['name'];
          array_push($result['login'],$index);
          $result['success'] = "1";
          $result['message'] = "success";
          echo json_encode($result);
          mysqli_close($conn);
        }else{
          $result['success'] = "0";
          $result['message'] = "error";
          echo json_encode($result);
          mysqli_close($conn);
        }
      }
    }
?>