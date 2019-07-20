package com.example.a13703.my_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a13703.my_app.Inter_kou.OnquerListener;
import com.example.a13703.my_app.bean.Person;
import com.example.a13703.my_app.customView.DataPicker;
import com.example.a13703.my_app.util.BmobUtil;
import com.example.a13703.my_app.util.ScreenUtils;
import com.example.a13703.my_app.util.StringAndBitmap;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.exception.BmobException;

import static android.R.attr.y;

public class UserInfoDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText new_email;
    private EditText new_hobby;
    private TextView new_nickname;
    private TextView new_city;
    private ImageView user_info_detail_back;
    private ImageView user_detail_touxiang;
    private ImageView user_detail_touxiang_arrow;
    private TextView user_detail_nickname;
    private ImageView user_detail_nickname_arrow;
    private TextView user_detail_sex;
    private ImageView user_detail_sex_arrow;
    private TextView user_detail_region;
    private ImageView user_detail_region_arrow;
    private TextView user_detail_birth;
    private ImageView user_detail_birth_arrow;
    private TextView user_detail_email;
    private ImageView user_detail_email_arrow;
    private ImageView user_detail_hobby_arrow;
    private ImageView user_detail_personal_signature_arrow;
    private TextView user_detail_account;
    private TextView user_detail_music_age;
    private MyApplication App;

    private void onBindView(){
        user_info_detail_back = (ImageView)findViewById(R.id.user_info_detail_back);
        user_detail_touxiang = (ImageView)findViewById(R.id.user_detail_touxiang);
        user_detail_touxiang_arrow = (ImageView)findViewById(R.id.user_detail_touxiang_arrow);
        user_detail_nickname = (TextView)findViewById(R.id.user_detail_nickname);
        user_detail_nickname_arrow = (ImageView)findViewById(R.id.user_detail_nickname_arrow);
        user_detail_sex = (TextView)findViewById(R.id.user_detail_sex);
        user_detail_sex_arrow = (ImageView)findViewById(R.id.user_detail_sex_arrow);
        user_detail_region = (TextView)findViewById(R.id.user_detail_region);
        user_detail_region_arrow = (ImageView)findViewById(R.id.user_detail_region_arrow);
        user_detail_birth = (TextView)findViewById(R.id.user_detail_birth);
        user_detail_birth_arrow = (ImageView)findViewById(R.id.user_detail_birth_arrow);
        user_detail_email = (TextView)findViewById(R.id.user_detail_email);
        user_detail_email_arrow = (ImageView)findViewById(R.id.user_detail_email_arrow);
        user_detail_hobby_arrow = (ImageView)findViewById(R.id.user_detail_hobby_arrow);
        user_detail_personal_signature_arrow = (ImageView)findViewById(R.id.user_detail_personal_signature_arrow);
        user_detail_account = (TextView)findViewById(R.id.user_detail_account);
        user_detail_music_age = (TextView)findViewById(R.id.user_detail_music_age);
    }

    private void initEvent(){
        user_detail_touxiang_arrow.setOnClickListener(this);
        user_detail_nickname_arrow.setOnClickListener(this);
        user_detail_sex_arrow.setOnClickListener(this);
        user_detail_region_arrow.setOnClickListener(this);
        user_detail_birth_arrow.setOnClickListener(this);
        user_detail_email_arrow.setOnClickListener(this);
        user_detail_hobby_arrow.setOnClickListener(this);
        user_detail_personal_signature_arrow.setOnClickListener(this);
        user_info_detail_back.setOnClickListener(this);
    }
    private String isVerifyEmail(Person person){
        if("".equals(person.getEmail())) return "邮箱未填写";
        else if(person.getEmailVerified()) return "已激活";
        return "未激活";
    }
    private void refresh(){
        if(App.currentUser!=null) {
            Person person = App.currentUser;
            user_detail_touxiang.setImageResource(R.drawable.p1);
            user_detail_nickname.setText(person.getNickname());
            if(!"".equals(person.getRegion())) {
                user_detail_region.setText(person.getRegion());
            }
            if(!"".equals(person.getBirth())&&person.getBirth()!=null) {
                user_detail_birth.setText(person.getBirth());
            }
            if(person.getEmail()!=null&&!"".equals(person.getEmail())){
                user_detail_email.setText(isVerifyEmail(person));
            }
            user_detail_account.setText(person.getUsername());
            user_detail_music_age.setText(BmobUtil.judgeMusicAge(person));
            if(!"".equals(person.getSex())) {
                user_detail_sex.setText(person.getSex());
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_detail);
        App = MyApplication.getInstance();
        onBindView();
        initEvent();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.user_detail_touxiang_arrow:
                BmobUtil.showToast("暂时不能修改头像");
                break;
            case R.id.user_detail_nickname_arrow:
                showUpdateNickNameDialog();
                break;
            case R.id.user_detail_sex_arrow:
                showSexListDialog();
                break;
            case R.id.user_detail_region_arrow:
                showUpdateRegionDialog();
                break;
            case R.id.user_detail_birth_arrow:
                selectDateDialog();
                break;
            case R.id.user_detail_email_arrow:
                showUpdateEmailDialog();
                break;
            case R.id.user_detail_hobby_arrow:
                 showUpdateHobbyDialog();
                break;
            case R.id.user_detail_personal_signature_arrow:
                Intent intent = new Intent(UserInfoDetailActivity.this,EditSignNatureActivity.class);
                startActivity(intent);
                break;
            case R.id.user_info_detail_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void showUpdateEmailDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.edit_email,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        Button btn_cancel_high_opion = (Button) view.findViewById(R.id.no);
        Button btn_agree_high_opion = (Button) view.findViewById(R.id.yes);

        btn_cancel_high_opion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_agree_high_opion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new_email = (EditText) view.findViewById(R.id.new_email);
                String newemail = new_email.getText().toString();
                if(BmobUtil.checkEmail(newemail)){
                    BmobUtil.updateEmail(App.currentUser.getObjectId(),newemail);
                    BmobUtil.queryInformation(App.currentUser.getObjectId(), new OnquerListener() {
                        @Override
                        public void OnqueryListenerSucess(Person person, BmobException e) {
                            App.currentUser = person;
                            refresh();
                        }
                    });
                }else{
                    BmobUtil.showToast("邮箱格式不正确！");
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/4*3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void showUpdateHobbyDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.edit_hobby,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        Button btn_cancel_high_opion = (Button) view.findViewById(R.id.no);
        Button btn_agree_high_opion = (Button) view.findViewById(R.id.yes);

        btn_cancel_high_opion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_agree_high_opion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new_hobby = (EditText) view.findViewById(R.id.new_hobby);
                String newhobby = new_hobby.getText().toString();
                BmobUtil.updateHobby(App.currentUser.getObjectId(),newhobby);
                BmobUtil.queryInformation(App.currentUser.getObjectId(), new OnquerListener() {
                    @Override
                    public void OnqueryListenerSucess(Person person, BmobException e) {
                        App.currentUser = person;
                        refresh();
                    }
                });
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/4*3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private int isSelecId(String sex){
        int flag = 2;
        switch (sex) {
            case "男":
                flag=0;
                break;
            case "女":
                flag=1;
                break;
            case "保密":
                flag=2;
                break;
            default:
                break;
        }
        return flag;
    }
    private void selectDateDialog() {
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd");
        Date date = new Date();
        String today = sdf.format(date);
        int year = Integer.parseInt(today.substring(0,4));
        int month = Integer.parseInt(today.substring(5,7));
        int day = Integer.parseInt(today.substring(8));
         DataPicker dialog= new DataPicker(this, new DataPicker.OnDateClick() {
             @Override
             public void onDataSelect(Date date) {
                 SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
                 sdf.applyPattern("yyyy-MM-dd");
                 String birth =sdf.format(date);
                 BmobUtil.updateBirth(App.currentUser.getObjectId(),birth);
                 BmobUtil.queryInformation(App.currentUser.getObjectId(), new OnquerListener() {
                     @Override
                     public void OnqueryListenerSucess(Person person, BmobException e) {
                         App.currentUser = person;
                         refresh();
                     }
                 });
             }
         },year,month,day);
        dialog.getDatePicker().setCalendarViewShown(false);
        dialog.show();
    }
    private void showUpdateRegionDialog(){
        final View view = LayoutInflater.from(this).inflate(R.layout.edit_region,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        Button btn_cancel_high_opion = (Button) view.findViewById(R.id.no);
        Button btn_agree_high_opion = (Button) view.findViewById(R.id.yes);

        btn_cancel_high_opion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_agree_high_opion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new_city = (TextView)view.findViewById(R.id.new_city);
                String city = new_city.getText().toString();
                BmobUtil.updateRegion(App.currentUser.getObjectId(),city);
                BmobUtil.queryInformation(App.currentUser.getObjectId(), new OnquerListener() {
                    @Override
                    public void OnqueryListenerSucess(Person person, BmobException e) {
                        App.currentUser = person;
                        refresh();
                    }
                });
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/4*3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    public void showSexListDialog() {
        int checkedItemId = isSelecId(App.currentUser.getSex());
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("性别");
        /*利用android.R.drawable.xxx调用安卓系统内置图标*/
        builder.setIcon(R.drawable.sex);
        /*利用setItems方法为Dialog设置列表项,setItems(数据源的资源ID[这里的资源是一个定义在string.xml中的字符串数组],点击item中的选项时触发的监听事件)*/
        builder.setSingleChoiceItems(R.array.sex_list,checkedItemId,new DialogInterface.OnClickListener() {

            /*int which 表示点击的item在string.xml文件字符串数组中的下标*/
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sex="";
                switch (which) {
                    case 0:
                        sex="男";
                        break;
                    case 1:
                        sex="女";
                        break;
                    case 2:
                        sex="保密";
                        break;
                }
                BmobUtil.updateSex(App.currentUser.getObjectId(),sex);
                BmobUtil.queryInformation(App.currentUser.getObjectId(), new OnquerListener() {
                    @Override
                    public void OnqueryListenerSucess(Person person, BmobException e) {
                        App.currentUser = person;
                        refresh();
                    }
                });

            }
        });
        builder.create().show();
    }

    private void showUpdateNickNameDialog(){
        final View view = LayoutInflater.from(this).inflate(R.layout.edit_username,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        Button btn_cancel_high_opion = (Button) view.findViewById(R.id.no);
        Button btn_agree_high_opion = (Button) view.findViewById(R.id.yes);

        btn_cancel_high_opion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_agree_high_opion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                new_nickname = (TextView)view.findViewById(R.id.new_username);
                String nickname = new_nickname.getText().toString();
                BmobUtil.updateNickName(App.currentUser.getObjectId(),nickname);
                BmobUtil.queryInformation(App.currentUser.getObjectId(), new OnquerListener() {
                    @Override
                    public void OnqueryListenerSucess(Person person, BmobException e) {
                        App.currentUser = person;
                        refresh();
                    }
                });
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/4*3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    @Override
    protected void onResume(){
        super.onResume();
        refresh();
    }
}
