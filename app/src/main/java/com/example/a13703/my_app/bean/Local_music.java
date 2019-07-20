package com.example.a13703.my_app.bean;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;

import org.litepal.crud.DataSupport;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * Created by 13703 on 2019/6/20.
 */

public class Local_music extends LitePalSupport implements Serializable {
    private long Id;
    private long SongId;
    private String singer;
    private String song;
    private int ImageId;
    private String albumId;
    private String path;
    private String url;
    public int duration;
    public long size;
    //private Bitmap bitmap;
    private String bitm;
    private String songmid;
    private String token;
    private String lrc_url;
    private String mv_url;

    public String getMv_url() {
        return mv_url;
    }

    public void setMv_url(String mv_url) {
        this.mv_url = mv_url;
    }

    public String getLrc_url() {
        return lrc_url;
    }

    public void setLrc_url(String lrc_url) {
        this.lrc_url = lrc_url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSongmid() {
        return songmid;
    }

    public void setSongmid(String songmid) {
        this.songmid = songmid;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getBitm() {
        return bitm;
    }

    public void setBitm(String bitm) {
        this.bitm = bitm;
    }

    public long getSongId() {
        return SongId;
    }

    public void setSongId(long songId) {
        SongId = songId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //public Bitmap getBitmap() {
    //    return bitmap;
    //}

    //public void setBitmap(Bitmap bitmap) {
      //  this.bitmap = bitmap;
    //}

    public long getId() {
        return Id;
    }

    public void setId(long longId) {
        Id = longId;
    }

    public Local_music(){}

    public int getImageId() {
        return ImageId;
    }

    public void setImageId(int imageId) {
        ImageId = imageId;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Local_music{" +
                "Id=" + Id +
                ", SongId=" + SongId +
                ", singer='" + singer + '\'' +
                ", song='" + song + '\'' +
                ", ImageId=" + ImageId +
                ", albumId='" + albumId + '\'' +
                ", path='" + path + '\'' +
                ", url='" + url + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", songmid='" + songmid + '\'' +
                ", token='" + token + '\'' +
                ", lrc_url='" + lrc_url + '\'' +
                ", mv_url='" + mv_url + '\'' +
                ", bitm='" + bitm + '\'' +
                '}';
    }
}
