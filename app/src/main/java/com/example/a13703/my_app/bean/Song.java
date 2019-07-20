package com.example.a13703.my_app.bean;

/**
 * Created by 13703 on 2019/6/18.
 */

public class Song {
   private int ImageId;
   private String song;
    private String song_detail;

    public Song(int imageId, String song, String song_detail) {
        ImageId = imageId;
        this.song = song;
        this.song_detail = song_detail;
    }

    public int getImageId() {
        return ImageId;
    }

    public void setImageId(int imageId) {
        ImageId = imageId;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSong_detail() {
        return song_detail;
    }

    public void setSong_detail(String song_detail) {
        this.song_detail = song_detail;
    }
}
