package com.example.a13703.my_app.bean;

import com.example.a13703.my_app.util.BmobUtil;

import cn.bmob.v3.BmobObject;

/**
 * Created by 13703 on 2019/7/18.
 */

public class User_Ge_dan extends BmobObject{
    private String username;
    private Integer Ge_dan_Id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getGe_dan_Id() {
        return Ge_dan_Id;
    }

    public void setGe_dan_Id(Integer ge_dan_Id) {
        Ge_dan_Id = ge_dan_Id;
    }

    @Override
    public String toString() {
        return "User_Ge_dan{" +
                "username='" + username + '\'' +
                ", Ge_dan_Id=" + Ge_dan_Id +
                '}';
    }
}
