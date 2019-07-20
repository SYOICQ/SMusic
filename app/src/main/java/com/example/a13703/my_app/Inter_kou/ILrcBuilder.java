package com.example.a13703.my_app.Inter_kou;

import com.example.a13703.my_app.bean.LrcRow;

import java.util.List;

/**
 * Created by 13703 on 2019/6/30.
 */

public interface ILrcBuilder {
    /**
     * 解析歌词，得到LrcRow的集合
     */
    List<LrcRow> getLrcRows(String rawLrc);

}
