package com.example.smAio;

public class NoticeDTO {

    String notice; // 공지사항 내용이 담길 문자열
    String name; // 공지사항을 작성한 사람의 이름이 들어갈 문자열
    String date; // 공지사항을 작성한 날짜가 들어갈 문자열

    public NoticeDTO(String notice, String name, String date) { // 클래스의 생성자를 선언해줌
        this.notice = notice;
        this.name = name;
        this.date = date;
    }

    public String getNotice() {
        return notice;
    } //각 문자열들의 getter and setter 함수
                                                    // 공지사항을 띄울 NoticeActivity에서 값을 불러오기 위하여 만들어줍니다
    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
