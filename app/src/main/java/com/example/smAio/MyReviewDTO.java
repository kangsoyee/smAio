package com.example.smAio;

//DB에 만들어진 필드랑 1:1 대응하는 변수 + getter & setter 를 가진 클래스를 DTO 라고 합니다.
//데이터베이스의 review 테이블과 user 테이블을 사용하였으며 상점이름,사용자 id,해당 id로 남긴 리뷰를 가지고있다.
// 사용 방법은 set을 통해 값을 설정하고, get을 통해 값을 가져옵니다.

public class MyReviewDTO {
    private String place_name;
    private String username;
    private String reviewcontent;

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getmyId() {
        return username;
    }

    public void setmyId(String Username) {this.username = Username; }

    public String getmyreview_content() {
        return reviewcontent;
    }

    public void setmyreview_content(String ReviewContent) {this.reviewcontent = ReviewContent; }


}