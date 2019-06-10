<?php
require_once ('connect.php');

if (!isset($_REQUEST['userid']) || !isset($_REQUEST['passwd'])) {
    die;
}
$userid = $_REQUEST['userid'];
$passwd = $_REQUEST['passwd'];
$sql = "select * from member \r\n\twhere userid='{$userid}' and pwd='{$passwd}' ";
$rs = mysqli_query($conn, $sql);
$message = '';
if ($row = mysqli_fetch_array($rs)) {
    //로그인 성공
    $message = 'success';
    $data = array('message' => $message, 'userid' => $userid, 'name' => $row[name]);
} else {
    //로그인 실패
    $message = '아이디 또는 비밀번호가 잘못되었습니다.';
    $data = array('message' => $message);
}

function han($s)
{
    return reset(json_decode('{"s":"' . $s . '"}'));
}
function to_han($str)
{
    return preg_replace('/(\\\\u[a-f0-9]+)+/e', 'han("$0")', $str);
}
$result = to_han(json_encode($data));

echo $result;
mysqli_close($conn);
?>























