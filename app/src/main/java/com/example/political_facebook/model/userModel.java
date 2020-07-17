package com.example.political_facebook.model;

public class userModel {

    private String User_id;
    private String name;
    private String ImageUrl;
    private String designation;
    private String bio;

    public userModel(){

    }

    public userModel(String User_id, String name, String imageUrl, String designation, String bio) {
        this.User_id = User_id;
        this.name = name;
        this.ImageUrl = imageUrl;
        this.designation = designation;
        this.bio = bio;
    }

    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String user_id) {
        this.User_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
