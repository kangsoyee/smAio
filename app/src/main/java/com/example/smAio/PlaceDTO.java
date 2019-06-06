package com.example.smAio;

/* DB에 만들어진 필드랑 1:1 대응하는 변수 + getter & setter 를 가진 클래스를 DTO 라고 합니다.* */
// 데이터베이스의 place 테이블엔 상점의 고유 번호, 카테고리, 이름, 영업의 시작 시간, 종료 시간, 주소, 전화번호, 대표 메뉴,
// 상점의 위치를 지도로 표현하기 위한 위도와 경도 값을 갖고 있으며, 사용자가 리뷰를 남기기 위해 접근할 수 있는 QR코드 값,
// 그리고 상점별 이미지 주소값이 있습니다.
// 객체 지향 프로그래밍에서 객체의 데이터는 객체 외부에서 직접적으로 접근하는 것을 막기 때문에 외부에서 마음대로 변경할 경우
// 객체의 무경성이 깨질 수 있어 메소드를 통해서 데이터를 변경하는 방법을 선호합니다.
// 사용 방법은 set을 통해 값을 설정하고, get을 통해 값을 가져옵니다.

public class PlaceDTO {
    private int place_idx;
    private String category;
    private String place_name;
    private String start_time;
    private String end_time;
    private String address;
    private String tel;
    private String menu;
    private String price;
    private String latitude;
    private String longitude;
    private String image;
    private String qrcode;
    static String lat;
    static String lng;

    //Line 27 ~ 34. 소이야 이것좀 알려줘
    public String getLat(){ return lat; }

    public void setLat(String lat){ this.lat = lat; }

    public String getLng(){ return lng; }

    public void setLng(String lng){ this.lng = lng; }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getPlace_idx() { return place_idx; }

    public void setPlace_idx(int place_idx) {
        this.place_idx = place_idx;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMenu() { return menu; }

    public void setMenu(String menu) { this.menu = menu; }

    public String getPrice() { return price; }

    public void setPrice(String price) { this.price = price;}

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

}