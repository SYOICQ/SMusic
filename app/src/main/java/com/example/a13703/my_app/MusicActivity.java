package com.example.a13703.my_app;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.a13703.my_app.Inter_kou.ILrcBuilder;
import com.example.a13703.my_app.Inter_kou.ILrcViewListener;
import com.example.a13703.my_app.bean.Local_music;
import com.example.a13703.my_app.bean.LrcRow;
import com.example.a13703.my_app.customView.LrcView;
import com.example.a13703.my_app.service.MusicService;
import com.example.a13703.my_app.util.DefaultLrcBuilder;
import com.example.a13703.my_app.util.MusicUtils;
import com.example.a13703.my_app.util.OnlineLrcUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView lrc_btn;
    private TextView music_title;
    private TextView music_singer;
    private TextView music_song;
    private ImageView back_option;
    private ImageView preSong;
    private ImageView nextSong;//"下一首"
    private ImageView Cd;//"唱片"
    private ImageView zhizheng;
    private ImageView pause;//“暂停”
    private MyApplication App;//"MediaPlayer"
    private ObjectAnimator animator;//运用ObjectAnimator实现转动
    private RotateAnimation rotateAnimation = null;
    private RotateAnimation rotateAnimation2 = null;
    private TextView currentTv;//"当前时间"
    private TextView totalTv;//“歌曲总时间”
    private int totalTime;//“歌曲总时长，用于获取歌曲时长”
    private SeekBar jindutiaoSb; //"进度条"
    private MusicService.MusicControl musicControl;
    private MyConnection1 conn;
    private IntentFilter intentFilter;
    private SongChangeReceiver songChangeReceiver;
    private SongStop songStop;
    private IntentFilter intentFilter1;
    private ImageView my_love;


    private static final int UPDATE_PROGRESS = 1;
    private static final int UPDATE_TEXT = 2;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    updateProgress();
                    break;
                case UPDATE_TEXT:
                    refresh();
                    break;
            }
        }
    };

    private class MyConnection1 implements ServiceConnection {
        //服务启动完成后会进入到这个方法
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            //获得service中的MyBinder
            musicControl = (MusicService.MusicControl) service;
            Log.d("MusicActivity", "绑定成功！"+musicControl);
            mhandler.sendEmptyMessage(UPDATE_PROGRESS);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("MusicActivity","失去连接！"+musicControl);
        }
    }
    //更新进度条
    private void updateProgress(){
        int currentPosition = musicControl.getCurrenPostion();
        int duration = musicControl.getDuration();
        Log.d("MusicActivity", duration + ":" + currentPosition);
        jindutiaoSb.setProgress(currentPosition);
        jindutiaoSb.setMax(duration);
        currentTv.setText(formatTime(currentPosition));
        //使用Handler每500毫秒更新一次进度条
        mhandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        if(App==null) {
            App = MyApplication.getInstance();
        }
        register_receiver();
        //绑定服务
        Intent intent  = new Intent(this,MusicService.class);
        conn = new MyConnection1();
        bindService(intent, conn, BIND_AUTO_CREATE);
        Link();
        jindutiaoSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                musicControl.seekTo(progress);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //指针初始化
        zhizheng_action();
        //唱片初始化
        cd_action();
        register_receiver();
        if(App==null) {
            App = MyApplication.getInstance();
        }
        //刷新界面
        if(App.musicList!=null){
            refresh();
        }
        if(!App.pauseEvent){
            pause.setImageResource(R.drawable.play);
            animator.resume();
            zhizheng.startAnimation(rotateAnimation);
        }
    }
    private void register_receiver(){
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.suyong.SongChange");
        songChangeReceiver = new SongChangeReceiver();
        registerReceiver(songChangeReceiver,intentFilter);

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("com.suyong.SongStop");
        SongStop songStop= new SongStop();
        registerReceiver(songStop,intentFilter1);
    }
    private void refresh(){
        Local_music music = App.musicList.get(App.position);
        music_singer.setText(music.getSinger());
        music_title .setText(music.getSong());
        music_song.setText(music.getSong());
        totalTv.setText(formatTime(music.getDuration()));
        if(musicControl!=null){
            totalTv.setText(formatTime(musicControl.getDuration()));
        }
    }
    private void Link() {
        lrc_btn = (TextView)findViewById(R.id.lrc);
        lrc_btn.setOnClickListener(this);
        my_love = (ImageView)findViewById(R.id.my_love);
        my_love.setOnClickListener(this);
        music_singer = (TextView)findViewById(R.id.music_singer);
        music_title = (TextView)findViewById(R.id.music_title) ;
        music_song = (TextView)findViewById(R.id.music_song1);
        back_option = (ImageView)findViewById(R.id.music_back);
        zhizheng = (ImageView)findViewById(R.id.listen_zhizhen_iv);
        preSong = (ImageView)findViewById(R.id.listen_back_img);
        nextSong = (ImageView)findViewById(R.id.listen_next_img);
        Cd = (ImageView)findViewById(R.id.cd);
        Cd.setOnClickListener(this);
        pause = (ImageView)findViewById(R.id.listen_pause1_img);
        currentTv =(TextView)findViewById(R.id.listen_current_tv);
        totalTv = (TextView)findViewById(R.id.listen_length_tv);
        jindutiaoSb = (SeekBar)findViewById(R.id.listen_jindutiao_sb);
        preSong.setOnClickListener(this);
        nextSong.setOnClickListener(this);
        pause.setOnClickListener(this);
        back_option.setOnClickListener(this);
        jindutiaoSb.getThumb().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
    }
    private void play() {
        Thread t1= new Thread(new Runnable() {
            @Override
            public void run(){
            Local_music music1 = App.musicList.get(App.position);
            boolean b = OnlineLrcUtil.wrtieContentFromUrl(music1);
            //解析歌词构造器
            ILrcBuilder builder = new DefaultLrcBuilder();
            //解析歌词返回LrcRow集合
            List<LrcRow> rows = builder.getLrcRows(getFromFile(OnlineLrcUtil.getLrcPath(music1.getSong(),music1.getSinger())));
            Intent intent = new Intent(MusicActivity.this,LrcActicity.class);
            intent.putExtra("rows",(Serializable) rows);
            startActivity(intent);
        }});
        try {
            t1.start();
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String formatTime(int length) {
        Date date = new Date(length);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        String TotalTime = simpleDateFormat.format(date);

        return TotalTime;

    }
    private void zhizheng_action(){
        rotateAnimation = new RotateAnimation(-20f, 20f, Animation.RELATIVE_TO_SELF, 0.3f, Animation.RELATIVE_TO_SELF, 0.1f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(0);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setStartOffset(500);
        zhizheng.setAnimation(rotateAnimation);
        rotateAnimation.cancel();
        rotateAnimation2 = new RotateAnimation(20f, -20f, Animation.RELATIVE_TO_SELF, 0.3f, Animation.RELATIVE_TO_SELF, 0.1f);
        rotateAnimation2.setDuration(500);
        rotateAnimation2.setInterpolator(new LinearInterpolator());
        rotateAnimation2.setRepeatCount(0);
        rotateAnimation2.setFillAfter(true);
        zhizheng.setAnimation(rotateAnimation2);
        rotateAnimation2.cancel();
    }

    private void cd_action(){
        animator = ObjectAnimator.ofFloat(Cd, "rotation", 0f, 360.0f);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());//匀速
        animator.setRepeatCount(-1);//设置动画重复次数（-1代表一直转）
        animator.setRepeatMode(ValueAnimator.RESTART);//动画重复模式
        animator.start();
        animator.pause();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.listen_back_img:
                animator.resume();
                zhizheng.startAnimation(rotateAnimation);
                musicControl.preSong();
                pause.setImageResource(R.drawable.play);
                refresh();
                break;
            case R.id.listen_next_img:
                animator.resume();
                zhizheng.startAnimation(rotateAnimation);
                musicControl.nextSong();
                pause.setImageResource(R.drawable.play);
                refresh();
                break;
            case R.id.listen_pause1_img:
                if(musicControl.isPlaying()){
                    musicControl.pause();
                    pause.setImageResource(R.drawable.pause);
                    animator.pause();
                    zhizheng.startAnimation(rotateAnimation2);
                }else {
                    musicControl.play();
                    pause.setImageResource(R.drawable.play);
                    animator.resume();
                    zhizheng.startAnimation(rotateAnimation);
                }
                refresh();
                break;
            case R.id.music_back:
                finish();
                break;
            case R.id.cd:
                if(musicControl.isPlaying()){
                    musicControl.pause();
                    pause.setImageResource(R.drawable.pause);
                    animator.pause();
                    zhizheng.startAnimation(rotateAnimation2);
                }else {
                    musicControl.continue_play();
                    pause.setImageResource(R.drawable.play);
                    animator.resume();
                    zhizheng.startAnimation(rotateAnimation);
                }
                break;
            case R.id.my_love:
                if (my_love.getTag().equals("unselect")) {
                    my_love.setTag("select");
                    my_love.setImageResource(R.drawable.heart);
                } else {
                    my_love.setTag("unselect");
                    my_love.setImageResource(R.drawable.no_heart);
                }
                break;
            case R.id.lrc:
                play();
                break;
            default:
                break;


        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mediaPlayer.reset();
        unbindService(conn);
        mhandler.removeCallbacksAndMessages(null);
    }
    class SongChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mhandler.sendEmptyMessage(UPDATE_TEXT);
        }
    }
    class SongStop extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            musicControl.pause();
            pause.setImageResource(R.drawable.pause);
            animator.pause();
            zhizheng.startAnimation(rotateAnimation2);

        }
    }
    //从文件里读取歌词
    public String getFromFile(String path){
        try {
            File f = new File(path.replace(".mp3", ".lrc"));
            //创建一个文件输入流对象
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            String result="";
            while((line = br.readLine())!=null){
                if(line.trim().equals(""))
                    continue;
                result += line + "\r\n";
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
