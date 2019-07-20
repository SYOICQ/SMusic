package com.example.a13703.my_app.Inter_kou;

import com.example.a13703.my_app.bean.Person;

import java.util.List;

import cn.bmob.v3.exception.BmobException;

/**
 * Created by 13703 on 2019/7/17.
 */

public interface OnQueryUserListener {
    void OnQueryUserSucess(List<Person> object, BmobException e);
    void OnQueryUserFailed();
}
