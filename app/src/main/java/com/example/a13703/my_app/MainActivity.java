package com.example.a13703.my_app;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a13703.my_app.Adapter.MyFragmentAdapter;
import com.example.a13703.my_app.Inter_kou.OnQueryUserListener;
import com.example.a13703.my_app.bean.Person;
import com.example.a13703.my_app.service.MusicService;
import com.example.a13703.my_app.util.BmobUtil;

import org.litepal.LitePal;

import java.nio.channels.NetworkChannel;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener,View.OnClickListener,ViewPager.OnPageChangeListener{

    private LocalBroadcastManager localBroadcastManager;
    private NavigationView nav_view;
    private Button home;
    private Button more;
    private DrawerLayout drawerLayout;
    private ViewPager vp_content;
    private TextView meItem;
    private TextView listenIteml;
    private MenuPopupHelper menuHelper;
    private NavigationView navView ;
    private static ImageView pause1;
    private ImageView nextsong1;
    private static MyApplication mApp;
    private static SeekBar sb;
    private static TextView song_name1 ;
    private static ImageView song_image;
    private MusicService.MusicControl musicControl;
    private MyConnection conn;
    private IntentFilter intentFilter;
    private SongChangeReceiver songChangeReceiver;
    private TextView username;
    private TextView mail;
    private ProgressDialog progressDialog;

    private static final int UPDATE_PROGRESS = 0;//更新进度条
    private static final int UPDATE_TEXT  = 1; //更新文字

    public  Handler handler = new Handler(){
        //在主线程中处理从子线程发送过来的消息
        @Override
        public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_PROGRESS:
                        updateProgress();
                        break;
                    case UPDATE_TEXT:
                        updateSongName();
                        break;
                }
        }
    };

    private class MyConnection implements ServiceConnection {
        //服务启动完成后会进入到这个方法
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            //获得service中的MyBinder
            musicControl = (MusicService.MusicControl) service;
            Log.d("MainActivity", "绑定成功！"+musicControl);
            handler.sendEmptyMessage(UPDATE_PROGRESS);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("MainActivity", "失去连接！"+musicControl);
        }
    }
    //更新进度条
     private void updateProgress() {
         int currentPosition = musicControl.getCurrenPostion();
         int duration = musicControl.getDuration();
         Log.d("MainActivity", duration + ":" + currentPosition);
         sb.setProgress(currentPosition);
         sb.setMax(duration);
         //使用Handler每500毫秒更新一次进度条
         handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
     }
    //更新歌名已经封面
    private void updateSongName() {
        String name = musicControl.songName();
        Bitmap bitmap = musicControl.songBitmap();
        song_name1.setText(name);
        song_image.setImageBitmap(bitmap);
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
    private void init(){
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        song_image = (ImageView)findViewById(R.id.song_image);
        song_name1 = (TextView)findViewById(R.id.song_name1);
        sb = (SeekBar)findViewById(R.id.song_process);
        nextsong1 = (ImageView)findViewById(R.id.nextsong1);
        pause1 = (ImageView)findViewById(R.id.pause1);
        navView = (NavigationView)findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        vp_content = (ViewPager)findViewById(R.id.vp_content);
        meItem = (TextView)findViewById(R.id.my_info);
        listenIteml = (TextView)findViewById(R.id.listening);
        meItem.setOnClickListener(this);
        listenIteml.setOnClickListener(this);
        home = (Button) findViewById(R.id.btn_home);
        home.setOnClickListener(this);
        more = (Button)findViewById(R.id.btn_more);
        more.setOnClickListener(this);
        mApp = MyApplication.getInstance();
        song_image.setOnClickListener(this);
        nav_view=(NavigationView)findViewById(R.id.nav_view);
        View headerView=nav_view.getHeaderView(0);
        username = (TextView)headerView.findViewById(R.id.username);
        mail = (TextView)headerView.findViewById(R.id.mail);
    }
    private void initFragment(){
        ArrayList<Fragment> mFragmentlist = new ArrayList<>();
        mFragmentlist.add(new My_info());
        mFragmentlist.add(new Listening());
        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(),mFragmentlist);
        vp_content.setAdapter(adapter);
        vp_content.setOffscreenPageLimit(2);
        vp_content.addOnPageChangeListener(this);
        nextsong1.setOnClickListener(this);
        pause1.setOnClickListener(this);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //当滑动条中的进度改变后,此方法被调用
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
            //滑动条刚开始滑动,此方法被调用
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            //当滑动条停止滑动,此方法被调用
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int process = seekBar.getProgress();
                musicControl.seekTo(process);
            }
        });
        //申请访问storge的权限
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else{
            //绑定服务
            Intent intent  = new Intent(this,MusicService.class);
            conn = new MyConnection();
            bindService(intent, conn, BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch(requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Intent intent  = new Intent(this,MusicService.class);
                    conn = new MyConnection();
                    bindService(intent, conn, BIND_AUTO_CREATE);
                }else{
                    Toast.makeText(this,"拒绝权限，无法使用！",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏时间
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏标题
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) actionBar.hide();
        setContentView(R.layout.activity_main);
        LitePal.getDatabase();
        init();
        initFragment();
        register_receiver();
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                switch(item.getItemId()){
                    case R.id.person_information:
                        drawerLayout.closeDrawers();
                          Intent intent = new Intent(MainActivity.this,UserInfoActivity.class);
                            startActivity(intent);
                        break;
                    case R.id.my_photo:
                        drawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this,"我的相册",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.friends:
                        drawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this,"我的朋友",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.skin_center:
                        drawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this,"皮肤中心",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.vip:
                        drawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this,"会员中心",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sets:
                        drawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this,"设置",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.alarm_close:
                        drawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this,"定时关闭",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.exit:
                        drawerLayout.closeDrawers();
                        Toast.makeText(MainActivity.this,"退出登陆",Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        //进入到界面后开始更新进度条
        if (mApp == null) {
            mApp = MyApplication.getInstance();
        }
        if(!mApp.pauseEvent){
            pause1.setImageResource(R.drawable.play1);
            song_image.startAnimation(AnimationUtils.loadAnimation(this, R.anim.imageview_rotate));
            song_name1.setText(mApp.musicList.get(mApp.position).getSong());
        }else{
            pause1.setImageResource(R.drawable.pause1);
            song_image.clearAnimation();
        }
        refresh();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_more:
                Context wrapper = new ContextThemeWrapper(this, R.style.mainstyle);
                PopupMenu menu = new PopupMenu(wrapper, view);
                menu.setOnMenuItemClickListener(this);
                menu.inflate(R.menu.main);
                menuHelper = new MenuPopupHelper(wrapper, (MenuBuilder) menu.getMenu(), view);
                menuHelper.setForceShowIcon(true);
                menuHelper.show();
                break;
            case R.id.btn_home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.my_info:
                if(vp_content.getCurrentItem()!=0){
                    setCurrentItem(0);
                }
                break;
            case R.id.listening:
                if(vp_content.getCurrentItem()!=1){
                    setCurrentItem(1);
                }
                break;
            case R.id.pause1:
                if(musicControl.isPlaying()){
                    song_image.clearAnimation();
                    musicControl.pause();
                    pause1.setImageResource(R.drawable.pause1);
                    mApp.pauseEvent = true;
                }else{
                    musicControl.play();
                    pause1.setImageResource(R.drawable.play1);
                    song_image.startAnimation(AnimationUtils.loadAnimation(this, R.anim.imageview_rotate));
                    mApp.pauseEvent = false;
                }
                handler.sendEmptyMessage(UPDATE_TEXT);
                break;
            case R.id.nextsong1:
                musicControl.nextSong();
                handler.sendEmptyMessage(UPDATE_TEXT);
                song_image.startAnimation(AnimationUtils.loadAnimation(this, R.anim.imageview_rotate));
                break;
            case R.id.song_image:
                Intent intent = new Intent(this,MusicActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    private void setCurrentItem(int position){
        vp_content.setCurrentItem(position);
        meItem.setTextSize(22);
        listenIteml.setTextSize(22);
        switch (position){
            case 0:
                meItem.setTextSize(27);
                break;
            case 1:
                listenIteml.setTextSize(27);
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()){
            case R.id.scan:
                Toast.makeText(this,"扫码",Toast.LENGTH_SHORT).show();
                break;
            case R.id.friends:
                Toast.makeText(this,"添加朋友",Toast.LENGTH_SHORT).show();
                break;
            case R.id.code:
                Toast.makeText(this,"我的二维码",Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
         if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        handler.removeCallbacksAndMessages(null);
    }
    @Override
    protected void onStop(){
        super.onStop();
    }
    class SongChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            handler.sendEmptyMessage(UPDATE_TEXT);
        }
    }
    class SongStop extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            song_image.clearAnimation();
            musicControl.pause();
            pause1.setImageResource(R.drawable.pause1);
            mApp.pauseEvent = true;
        }
    }
    private void refresh(){
        //更新用户信息
        BmobUtil.queryUserByUserName(mApp.currentUser.getUsername(), new OnQueryUserListener() {
            @Override
            public void OnQueryUserSucess(final List<Person> object, BmobException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mApp.currentUser = object.get(0);
                        username.setText(object.get(0).getNickname());
                        if(object.get(0).getEmail()!=null) {
                            mail.setText(object.get(0).getEmail());
                        }
                    }
                });
                Intent intent = new Intent("com.suyong.update_text");
                localBroadcastManager.sendBroadcast(intent);
            }
            @Override
            public void OnQueryUserFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BmobUtil.showToast("更新用户信息失败！");
                    }
                });
            }
        });
    }
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("查询中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}