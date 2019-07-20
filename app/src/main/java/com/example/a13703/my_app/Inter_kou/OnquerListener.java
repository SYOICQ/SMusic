package com.example.a13703.my_app.Inter_kou;

import com.example.a13703.my_app.bean.Person;

import cn.bmob.v3.exception.BmobException;

/**
 * Created by 13703 on 2019/7/17.
 */

public interface OnquerListener {
    void OnqueryListenerSucess(Person person, BmobException e);
}
