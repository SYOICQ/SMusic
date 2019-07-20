package com.example.a13703.my_app;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a13703.my_app.bean.Person;
import com.example.a13703.my_app.util.BmobUtil;
import com.example.a13703.my_app.util.StringAndBitmap;

import org.w3c.dom.Text;

public class UserInfoActivity extends AppCompatActivity {

    private MyApplication App;
    private ImageView myinfo_back;
    private ImageView myinfo_touxiang;
    private TextView myinfo_username;
    private Button myinfo_region;
    private ImageView myinfo_code;
    private Button myinfo_edit;
    private TextView myinfo_personal_signature;
    private TextView myinfo_music_age;
    private TextView myinfo_constellation;
    private TextView myinfo_sex;
    private TextView myinfo_hobby;

    private void BindView(){
        myinfo_back = (ImageView) findViewById(R.id.myinfo_back);
        myinfo_touxiang = (ImageView)findViewById(R.id.myinfo_touxiang);
        myinfo_username  = (TextView)findViewById(R.id.myinfo_username);
        myinfo_region  = (Button)findViewById(R.id.myinfo_region);
        myinfo_code = (ImageView)findViewById(R.id.myinfo_code);
        myinfo_edit  = (Button)findViewById(R.id.myinfo_edit);
        myinfo_personal_signature = (TextView)findViewById(R.id.myinfo_personal_signature);
        myinfo_music_age = (TextView)findViewById(R.id.myinfo_music_age);
        myinfo_constellation  = (TextView)findViewById(R.id.myinfo_constellation);
        myinfo_sex  = (TextView)findViewById(R.id.myinfo_sex);
        myinfo_hobby  = (TextView)findViewById(R.id.myinfo_hobby);
    }
   private void refresh(){
       if(App.currentUser!=null) {
           Person person = App.currentUser;
           myinfo_touxiang.setImageResource(R.drawable.p1);
           myinfo_username.setText(person.getNickname());
           if(!"".equals(person.getRegion())) {
               myinfo_region.setText(person.getRegion());
           }
           if(!"".equals(person.getPersonal_signature())) {
               myinfo_personal_signature.setText(person.getPersonal_signature());
           }
           myinfo_music_age.setText(BmobUtil.judgeMusicAge(person));
           if(!"".equals(person.getBirth())&&person.getBirth()!=null) {
               myinfo_constellation.setText(BmobUtil.judgeConstellation(person));
           }
           if(!"".equals(person.getSex())){
               myinfo_sex.setText(person.getSex());
           }
           if(!"".equals(person.getHobby())) {
               myinfo_hobby.setText(person.getHobby());
           }
       }
    }
    private void regiseter_event(){
        myinfo_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        myinfo_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        myinfo_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (UserInfoActivity.this,UserInfoDetailActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        App = MyApplication.getInstance();
        BindView();
        regiseter_event();
        refresh();
    }
    @Override
    protected void onResume(){
        super.onResume();
        refresh();
    }
}
