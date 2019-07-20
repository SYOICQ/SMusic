package com.example.a13703.my_app.Inter_kou;

import com.example.a13703.my_app.bean.Ge_dan;

import java.util.List;

import cn.bmob.v3.exception.BmobException;

/**
 * Created by 13703 on 2019/7/18.
 */

public interface OnqueryGe_dan_list_Listener {
    void Onsucess(List<Ge_dan> object, BmobException e);
}
