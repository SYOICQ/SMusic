package com.example.a13703.my_app.Inter_kou;

import com.example.a13703.my_app.bean.LrcRow;

/**
 * Created by 13703 on 2019/6/30.
 */

public interface ILrcViewListener {
    /**
     * 当歌词被用户上下拖动的时候回调该方法
     */
    void onLrcSeeked(int newPosition, LrcRow row);
}
