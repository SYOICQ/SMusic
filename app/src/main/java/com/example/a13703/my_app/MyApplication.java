package com.example.a13703.my_app;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.media.MediaPlayer;

import com.example.a13703.my_app.bean.Local_music;
import com.example.a13703.my_app.bean.Person;
import com.example.a13703.my_app.service.MusicService;

import org.litepal.LitePal;

import java.util.List;

import cn.bmob.v3.Bmob;

/**
 * Created by 13703 on 2019/6/10.
 */

public class MyApplication extends Application {
    private static Context context;
    private static MyApplication mApp;
    public boolean isStop;
    public MediaPlayer mMediaPlayer;
    public Local_music mSong;//当前播放的歌曲
    public List<Local_music> musicList;//播放列表
    public int position;//当前歌曲在播放列表的位置
    public boolean ne_pr_flag ;
    public boolean pauseEvent;
    public String username;
    public Person currentUser;
    public MusicService.MusicControl musicControl;
    public static MyApplication getInstance() {
        return mApp;
    }
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        position = 0;
        ne_pr_flag=false;
        isStop = false;
        mApp = this;
        context=getApplicationContext();
        mMediaPlayer = new MediaPlayer();
        pauseEvent = true;
        Bmob.initialize(this, "a43f886102ae117a142231b0cdec4a97");
        LitePal.initialize(context);
        username = "";
        currentUser = new Person();
    }

    public static Context getContext() {
        return context;
    }
}
