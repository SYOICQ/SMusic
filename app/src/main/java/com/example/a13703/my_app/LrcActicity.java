package com.example.a13703.my_app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a13703.my_app.Inter_kou.ILrcViewListener;
import com.example.a13703.my_app.bean.LrcRow;
import com.example.a13703.my_app.customView.LrcView;
import com.example.a13703.my_app.service.MusicService;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LrcActicity extends AppCompatActivity {
    public final static String TAG = "LrcActivity";
    //自定义LrcView，用来展示歌词
    LrcView mLrcView;
    //更新歌词的频率，每秒更新一次
    private int mPalyTimerDuration = 1000;
    //更新歌词的定时器
    private Timer mTimer;
    //更新歌词的定时任务
    private TimerTask mTask;
    private MusicService.MusicControl musicControl;
    private MyConnection con;
    private ImageView lrc_back_btn;
    private TextView lrc_song_name;

    private class MyConnection implements ServiceConnection {
        //服务启动完成后会进入到这个方法
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            //获得service中的MyBinder
            musicControl = (MusicService.MusicControl) service;
            Log.d(TAG, "绑定成功！"+musicControl);
            start_lrc();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG,"失去连接！"+musicControl);
        }
    }
    private void init(){
        lrc_back_btn = (ImageView)findViewById(R.id.lrc_back_btn);
        lrc_song_name = (TextView)findViewById(R.id.lrc_song_name);
        lrc_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lrc_acticity);
        Intent intent1 = getIntent();
        List<LrcRow> rows= (List<LrcRow>) intent1.getSerializableExtra("rows");
        mLrcView=(LrcView)findViewById(R.id.lrcView);
        mLrcView.setLrc(rows);
        mLrcView.setListener(new ILrcViewListener() {
            //当歌词被用户上下拖动的时候回调该方法,从高亮的那一句歌词开始播放
            public void onLrcSeeked(int newPosition, LrcRow row) {
                    musicControl.seekTo((int) row.time);
            }
        });
        Intent intent  = new Intent(this,MusicService.class);
        con = new MyConnection();
        bindService(intent, con, BIND_AUTO_CREATE);
        init();
    }
    private void start_lrc(){
        if(mTimer == null){
            mTimer = new Timer();
            mTask = new LrcTask();
            mTimer.scheduleAtFixedRate(mTask, 0, mPalyTimerDuration);
        }
    }
    public void stopLrcPlay(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLrcPlay();
        unbindService(con);
    }

    class LrcTask extends TimerTask{
        @Override
        public void run() {
            //获取歌曲播放的位置
            final long timePassed = musicControl.getCurrenPostion();
           runOnUiThread(new Runnable() {
                public void run() {
                    //滚动歌词
                    mLrcView.seekLrcToTime(timePassed);
                    lrc_song_name.setText(musicControl.songName());
                }
            });

        }
    };

}
