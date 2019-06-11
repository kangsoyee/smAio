<?php
    //파일명 get_placeID.php

    if($_SERVER['REQUEST_METHOD']=='POST'){
      $url = $_POST['url'];

      require_once ('connect.php'); //연결

      $sql = "SELECT * FROM place WHERE qrcode='$url'"; //url값 일치

      $response = mysqli_query($conn, $sql); //실행


      $result = array(); //array형태의 result
      $result['place_info']=array();

      if( mysqli_num_rows($response) === 1 ){ //만약 실행시킨 row의 개수가 1이라면
        $row=mysqli_fetch_assoc($response);
        if($password == $row['pwd']){
          $index['p_name'] = $row['place_name'];
          array_push($result['place_info'],$index);
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