package com.example.a13703.my_app.Inter_kou;

import com.example.a13703.my_app.bean.Online_Song;

import cn.bmob.v3.exception.BmobException;

/**
 * Created by 13703 on 2019/7/19.
 */

public interface query_Online_Song_ByObjectIdListener {
    void onSucess(Online_Song song, BmobException e);
}
