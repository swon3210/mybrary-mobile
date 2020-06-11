package com.example.mybrary.model;

public class ReviewModel {
    private String title;
    private String reviewerId;
    private String reviewer;
    private String text;
    private String id;

    public ReviewModel() {}

    public ReviewModel(String id, String title, String reviewerId, String reviewer, String text) {
        this.title = title;
        this.reviewerId = reviewerId;
        this.reviewer = reviewer;
        this.text = text;
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getReviewerId(){
        return reviewerId;
    }

    public String getReviewer(){
        return reviewer;
    }

    public String getText(){
        return text;
    }


}
