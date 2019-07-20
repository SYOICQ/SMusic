package com.example.a13703.my_app.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 13703 on 2019/7/18.
 */

public class Ge_dan extends BmobObject {
    private Integer Id;
    private String name;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Ge_dan{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                '}';
    }
}
