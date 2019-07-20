package com.example.a13703.my_app;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a13703.my_app.Inter_kou.OnSucessLoginListener;
import com.example.a13703.my_app.bean.Person;
import com.example.a13703.my_app.customView.CustomVideoView;
import com.example.a13703.my_app.util.BmobUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
/**
 * Created by 13703 on 2019/7/15.
 */

public class LoginActivity extends AppCompatActivity{
    private CustomVideoView customVideoView;
    private Button login;
    private EditText account;
    private EditText password;
    private TextView register;
    private TextView find_password;
    private MyApplication App;
    private void initView(){
        find_password = (TextView)findViewById(R.id.tv_find_pwd);
        register = (TextView)findViewById(R.id.tv_register);
        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);
        //找VideoView控件
        customVideoView = (CustomVideoView)findViewById(R.id.videoview);
        login = (Button) findViewById(R.id.login);
        App = MyApplication.getInstance();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏时间
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //隐藏标题
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        initView();
        //加载视频文件
        customVideoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.sport));
        //播放
        customVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //静音，如需声音可去掉
                mp.setVolume(0f, 0f);
                customVideoView.start();
            }
        });
        //循环播放
        customVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                customVideoView.start();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if("".equals(account.getText().toString())){
                    showToast("用户名不为空！");
                    return;
                }else if("".equals(password.getText().toString())){
                    showToast("密码不为空！");
                    return;
                }
                if(BmobUtil.checkEmail(account.getText().toString())) {
                    //邮箱登陆
                    BmobUtil.loginByEmail(account.getText().toString(),password.getText().toString(), new OnSucessLoginListener() {
                        @Override
                        public void onSucessLogin(Person bmobUser, BmobException e) {
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    });
                } else if(account.getText().length()==11){
                    //账号密码登陆
                    BmobUtil.loginByUsername(account.getText().toString(),password.getText().toString(), new OnSucessLoginListener() {
                        @Override
                        public void onSucessLogin(Person bmobUser, BmobException e) {
                            App.username = account.getText().toString();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }else{
                    showToast("账号格式错误！");
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //注册
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        find_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //找回密码
                Intent intent = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
    private void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
    //返回重启加载
    @Override
    protected void onRestart() {
        initView();
        super.onRestart();
    }

    //防止锁屏或者切出的时候，音乐在播放
    @Override
    protected void onStop() {
        customVideoView.stopPlayback();
        super.onStop();
    }





}
