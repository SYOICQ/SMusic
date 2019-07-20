package com.example.a13703.my_app.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.a13703.my_app.MainActivity;
import com.example.a13703.my_app.MusicActivity;
import com.example.a13703.my_app.MyApplication;
import com.example.a13703.my_app.R;
import com.example.a13703.my_app.bean.Local_music;
import com.example.a13703.my_app.util.BmobUtil;
import com.example.a13703.my_app.util.MusicUtils;
import com.example.a13703.my_app.util.StringAndBitmap;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.InputStream;
import java.util.List;

/**
 * Created by 13703 on 2019/6/19.
 */

public class MusicService extends Service {

    private String current_song_path ="";
    private static int MusicSecvic_Flag = 1;
    private String TAG = "MusicService";
    private MyApplication App;
    private MusicControl mBinder = new MusicControl();
    private int limit_size = 50;
    private NotificationManager notificationManager;
    private Notification.Builder builder ;
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MusicControl extends Binder {
        //返回当前的歌名
        public String songName(){
            return App.musicList.get(App.position).getSong();
        }
        //返回当前歌曲的封面
        public Bitmap songBitmap(){
            return StringAndBitmap.stringToBitmap(App.musicList.get(App.position).getBitm());
        }
        //判断是否处于播放状态
        public boolean isPlaying(){
            return App.mMediaPlayer.isPlaying();
        }
        //播放歌曲
        public void play(){
            final Local_music music = App.musicList.get(App.position);
            notificationManager.notify(MusicSecvic_Flag, getNotify(music.getSong(),"播放中..."));
            Log.d("MusicControl", "play:" + music.toString());
            App.mMediaPlayer.reset();
            try {
                if(music.getPath()!=null){
                App.mMediaPlayer.setDataSource(music.getPath());
                    current_song_path = music.getPath();
                }else if(music.getUrl()!=null){
                    App.mMediaPlayer.setDataSource(music.getUrl());
                    current_song_path = music.getUrl();
                }else{
                    BmobUtil.showToast("此歌曲好像出错，请搜索欣赏");
                    return;
                }

                App.mMediaPlayer.prepare();
                App.mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        App.mMediaPlayer.start();
                        App.pauseEvent = false;
                        Intent intent = new Intent("com.suyong.SongChange");
                        sendBroadcast(intent);
                    }
                });
                App.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        //正常结束
                        App.pauseEvent = true;
                        Intent intent = new Intent("com.suyong.SongStop");
                        sendBroadcast(intent);
                    }
                });

                //存储最近播放
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Local_music> list = LitePal.findAll(Local_music.class);
                        int len = list.size();
                        //如果已经到达最近播放的限制数
                        if(len==limit_size){
                            Local_music mu = LitePal.findFirst(Local_music.class);
                            long SongId = mu.getSongId();
                            LitePal.deleteAll(Local_music.class,"SongId = ?",Long.toString(SongId));
                        }
                        String SongId = Long.toString(music.getSongId());
                        List<Local_music> res = LitePal.where("SongId = ?", SongId).find(Local_music.class);
                        if(res.size()==0) {
                            music.save();
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //下一首歌曲
        public void nextSong(){
            App.position++;
            if (App.position == App.musicList.size()) {
                App.position = 0;
            }
            play();
        }
        //上一首歌曲
        public void preSong(){
            App.position--;
            if (App.position == -1) {
                App.position = App.musicList.size() - 1;
            }
            play();
        }
        //暂停歌曲
        public void pause(){
            Local_music music = App.musicList.get(App.position);
            notificationManager.notify(MusicSecvic_Flag, getNotify(music.getSong(),"暂停中..."));
            App.mMediaPlayer.pause();
            App.pauseEvent = true;
        }
        //继续播放
        public void continue_play(){
            App.mMediaPlayer.start();
        }
        //返回歌曲长度，单位毫秒
        public int getDuration(){
            return App.mMediaPlayer.getDuration();
        }
        //设置播放进度
        public void seekTo(int process){
            App.mMediaPlayer.seekTo(process);
        }
        //返回歌曲目前的进度，单位为毫秒
        public int getCurrenPostion(){
            return App.mMediaPlayer.getCurrentPosition();
        }
        //返回当前播放歌曲的路径
        public String getPath(){
            return current_song_path;
        }
    }
    @Override
    public void onCreate(){
        Log.d("MusicService", "服务已创建");
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        builder = new Notification.Builder(this);
        if(App==null){
            App = MyApplication.getInstance();
        }
        startNotification(getNotify("S乐","欢迎使用..."));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MusicService", "on destroy service");
    }
    private Notification getNotify(String title,String content){
        Intent intent = new Intent (this, MusicActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        builder.setContentTitle(title)
        .setContentText(content)
        .setWhen(System.currentTimeMillis())
        .setSmallIcon(R.drawable.local_music)
        .setContentIntent(pi);
        Notification notify = builder.build();
        return notify;
    }
    private void startNotification(Notification notify){
        startForeground(MusicSecvic_Flag,notify);
    }
}
