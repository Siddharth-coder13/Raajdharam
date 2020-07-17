package com.example.political_facebook.model;



public class post {

    private String user_name, text_post,image_post,date_time, post_id, publisher, profile_pic;

    public post(){

    }

    public post(String user_name, String text_post, String image_post, String date_time, String profile_pic,
                String post_id, String publisher) {
        this.user_name = user_name;
        this.text_post = text_post;
        this.image_post = image_post;
        this.date_time = date_time;
        this.profile_pic = profile_pic;
        this.post_id = post_id;
        this.publisher = publisher;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }


    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }



    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getText_post() {
        return text_post;
    }

    public void setText_post(String text_post) {
        this.text_post = text_post;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getImage_post() {
        return image_post;
    }

    public void setImage_post(String image_post) {
        this.image_post = image_post;
    }
}
