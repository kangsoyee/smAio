package com.example.smAio;

public class HeartDTO { //HeartFragment에 내가 찜 한 식당이름을 띄워주기 위해 필요한 클래스입니다

    private String place_name; // 식당이름 변수 선언

    public String getPlace_name() {
        return place_name;
    } //getter and setter 선언

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }


}