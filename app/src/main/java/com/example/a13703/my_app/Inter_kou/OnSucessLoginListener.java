package com.example.a13703.my_app.Inter_kou;

import com.example.a13703.my_app.bean.Person;

import cn.bmob.v3.exception.BmobException;

/**
 * Created by 13703 on 2019/7/15.
 */

public interface OnSucessLoginListener {
    /**
     * 登陆成功
     */
    void onSucessLogin(Person bmobUser, BmobException e);
}
