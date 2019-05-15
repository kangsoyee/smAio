package com.example.smAio;

public class ReviewDTO {
    private int idx;
    private String userid;
    private String name;
    private String review_date;
    private int place_idx;
    private String review_content;

    @Override
    public String toString() {          // 이 소스코드가 무얼 의미하는지 모르겠음
        return "ReviewDTO{" +
                "idx=" + idx +
                ", userid='" + userid + '\'' +
                ", name='" + name + '\'' +
                ", review_date='" + review_date + '\'' +
                ", place_idx=" + place_idx +
                ", review_content='" + review_content + '\'' +
                '}';
    }
    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReview_date() {
        return review_date;
    }

    public void setReview_date(String review_date) {
        this.review_date = review_date;
    }

    public int getPlace_idx() {
        return place_idx;
    }

    public void setPlace_idx(int place_idx) {
        this.place_idx = place_idx;
    }


    public String getReview_content() {
        return review_content;
    }

    public void setReview_content(String review_content) {
        this.review_content = review_content;
    }
}

