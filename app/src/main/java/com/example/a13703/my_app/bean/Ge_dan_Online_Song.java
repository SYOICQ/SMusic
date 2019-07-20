package com.example.a13703.my_app.bean;

import android.content.Intent;

import com.example.a13703.my_app.util.BmobUtil;

import cn.bmob.v3.BmobObject;

/**
 * Created by 13703 on 2019/7/19.
 */

public class Ge_dan_Online_Song extends BmobObject {
    private Integer SongId;
    private Integer Ge_dan_Id;

    public Integer getSongId() {
        return SongId;
    }

    public void setSongId(Integer songId) {
        SongId = songId;
    }

    public Integer getGe_dan_Id() {
        return Ge_dan_Id;
    }

    public void setGe_dan_Id(Integer ge_dan_Id) {
        Ge_dan_Id = ge_dan_Id;
    }
}
