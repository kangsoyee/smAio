package com.example.smAio;

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