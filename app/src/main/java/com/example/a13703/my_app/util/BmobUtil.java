package com.example.a13703.my_app.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.example.a13703.my_app.Inter_kou.OnQueryUserListener;
import com.example.a13703.my_app.Inter_kou.OnSucessLoginListener;
import com.example.a13703.my_app.Inter_kou.OnaddGe_danListener;
import com.example.a13703.my_app.Inter_kou.OnquerListener;
import com.example.a13703.my_app.Inter_kou.OnqueryGe_dan_Listener;
import com.example.a13703.my_app.Inter_kou.OnqueryGe_dan_list_Listener;
import com.example.a13703.my_app.Inter_kou.OnqueryUserGenDanListener;
import com.example.a13703.my_app.Inter_kou.query_Online_Song_ByObjectIdListener;
import com.example.a13703.my_app.LoginActivity;
import com.example.a13703.my_app.MainActivity;
import com.example.a13703.my_app.MyApplication;
import com.example.a13703.my_app.bean.Ge_dan;
import com.example.a13703.my_app.bean.Ge_dan_Online_Song;
import com.example.a13703.my_app.bean.Local_music;
import com.example.a13703.my_app.bean.Online_Song;
import com.example.a13703.my_app.bean.Person;
import com.example.a13703.my_app.bean.User_Ge_dan;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by 13703 on 2019/7/15.
 */

public class BmobUtil {
    public static final String songRootPath = Environment.getExternalStorageDirectory().toString()
            + "/SMusicPlayer/pic/";
    //弹出信息
    public static void showToast(String message){
        Toast.makeText(MyApplication.getContext(),message,Toast.LENGTH_SHORT).show();
    }
    /**
     * 账号密码登陆
     */
    public static void loginByUsername(String username,String password,final OnSucessLoginListener listener){
        Person userlogin = new Person();
        userlogin.setUsername(username);
        userlogin.setPassword(password);
        userlogin.login(new SaveListener<Person>() {
            @Override
            public void done(Person bmobUser, BmobException e) {
                if (e == null) {
                    listener.onSucessLogin(bmobUser,e);
                } else {
                   if (e.getErrorCode() == 9016)
                        showToast("网络连接超时！");
                   else
                       showToast("账号或密码错误");
                    Log.e("error",e.getMessage().toString());
                }
            }
        });
    }
    /**
     * 邮箱登陆
     */
    public static void loginByEmail(String email,String password,final OnSucessLoginListener listener){
        Person.loginByAccount(email,password, new LogInListener<Person>() {
            @Override
            public void done(Person user, BmobException e) {
                if (user != null) {
                    listener.onSucessLogin(user,e);
                }else{
                     if (e.getErrorCode() == 9016)
                        showToast("网络连接超时！");
                    else
                        showToast("邮箱或密码错误！");
                }
            }
        });
    }
    /**
     * 发送验证邮件
     */
    public static void emailVerify(final String email) {
        Person.requestEmailVerify(email, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("请求验证邮件成功，请到" + email + "邮箱中进行激活账户。");
                } else {
                    if (e.getErrorCode() == 9016)
                        showToast("网络连接超时！");
                    else
                        showToast("请求失败！");
                }
            }
        });
    }
    /**
     * 邮箱重置密码
     */
    public static void resetPasswordByEmail(final String email) {
        Person.resetPasswordByEmail(email, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("重置密码请求成功，请到 "+ email + "邮箱进行密码重置操作");
                } else {
                    if (e.getErrorCode() == 9016)
                        showToast("网络连接超时！");
                    else
                        showToast("请求失败！");
                }
            }
        });
    }
    /**
     * 用户注册(手机号)
     */
    public static void signUp(String phone_number, String password,String security_code,Bitmap head_photo) {
        //不能修改邮箱，邮箱必须为正确格式，否侧注册失败
        //由于bmob功能受限 头像功能无法实现
        Person user = new Person();
        user.setRegion("");
        user.setSex("");
        user.setBirth("");
        user.setHobby("");
        user.setPersonal_signature("");
        user.setUsername(phone_number);
        user.setPassword(password);
        user.setMobilePhoneNumber(phone_number);
        user.setNickname(phone_number);
        user.setMobilePhoneNumberVerified(true);
        user.signOrLogin(security_code, new SaveListener<Person>() {
            @Override
            public void done(Person person, BmobException e) {
                if (e == null) {
                    showToast("注册成功!");
                } else {
                    if (e.getErrorCode() == 9016)
                        showToast("网络连接超时！");
                    else if(e.getErrorCode()==202)
                        showToast("注册失败，用户名已存在，请重试！");
                    else{
                        showToast("注册失败!" );
                    }
                    Log.e("注册失败", "原因: ", e);
                }
            }
        });
    }
    /**
     * 发送手机验证码
     */
    public static void requestBmobSMs(String phone){
        BmobSMS.requestSMSCode(phone,"S乐官方客服", new QueryListener<Integer>() {
            @Override
            public void done(Integer smsId, BmobException e) {
                if (e == null) {
                    showToast("发送验证码成功!");
                } else {
                    if (e.getErrorCode() == 9016)
                        showToast("网络连接超时！");
                    else
                        showToast("发送验证码失败!");
                }
            }
        });
    }
    /**
     * 手机重置密码
     */
    public static void resetPasswordByPhone(String code,String newPassword) {
        Person.resetPasswordBySMSCode(code, newPassword, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("重置成功");
                } else {
                    if (e.getErrorCode() == 9016)
                        showToast("网络连接超时！");
                    else
                        showToast("重置失败");
                }
            }
        });
    }
    /**
     * 检验邮箱合法性
     */
    public static Boolean checkEmail(String email) {
        if (email.matches("^[a-z0-9A-Z]+[- |a-z0-9A-Z._]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$")) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 获取当前时间
     */
    public static String getNowTime(){
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        return sdf.format(date);
    }

    /**
     * 判断乐龄
     */
    public static String judgeMusicAge(Person person){
        long daysBetween = 0;
        String[] t = person.getCreatedAt().trim().split(" ");
        String dateStr1 = t[0];
        String dateStr2 = getNowTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date2 = format.parse(dateStr2);
            Date date1 = format.parse(dateStr1);
            daysBetween=(date2.getTime()-date1.getTime()+1000000)/(60*60*24*1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Long.toString(daysBetween) +" 天";
    }
    /**
     * 判断星座
     */
    public static String judgeConstellation(Person person){
        String t = person.getBirth();
        String month1 = t.substring(5,7);
        String day1 = t.substring(8);
        int month = Integer.parseInt(month1);
        int day = Integer.parseInt(day1);
        String[] starArr = {"魔羯座","水瓶座", "双鱼座", "牡羊座",
                "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座" };
        int[] DayArr = {22, 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22};  // 两个星座分割日
        int index = month;
        // 所查询日期在分割日之前，索引-1，否则不变
        if (day < DayArr[month - 1]) {
            index = index - 1;
        }
        // 返回索引指向的星座string
        return starArr[index];
    }
    /**
     * 根据用户名查询
     */
    public static void queryUserByUserName(String username,final OnQueryUserListener listener){
            BmobQuery<Person> categoryBmobQuery = new BmobQuery<>();
            categoryBmobQuery.addWhereEqualTo("username", username);
            categoryBmobQuery.findObjects(new FindListener<Person>() {
                @Override
                public void done(List<Person> object, BmobException e) {
                    if (e == null) {
                        listener.OnQueryUserSucess(object,e);
                    } else {
                        listener.OnQueryUserFailed();
                        Log.e("BMOB", e.toString());
                    }
                }
            });
    }
    /**
     * 更新昵称
     */
    public static void updateNickName(String objectId, String nickname) {
        final Person p2 = new Person();
        p2.setNickname(nickname);
        p2.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    showToast("昵称更新成功！");
                }else{
                    showToast("更新失败：" + e.getMessage());
                }
            }
        });
    }
    /**
     * 查询用户信息
     */
    public static void queryInformation(String objectId,final OnquerListener listener){
        BmobQuery<Person> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(objectId, new QueryListener<Person>() {
            @Override
            public void done(Person object,BmobException e) {
                if(e==null){
                    listener.OnqueryListenerSucess(object,e);
                }else{
                }
            }
        });
    }

    public static void updateSex(String objectId, String sex) {
        final Person p2 = new Person();
        p2.setSex(sex);
        p2.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    showToast("性别更新成功！");
                }else{
                    showToast("更新失败：" + e.getMessage());
                }
            }
        });
    }

    public static void updateRegion(String objectId, String city) {
        final Person p2 = new Person();
        p2.setRegion(city);
        p2.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    showToast("地区更新成功！");
                }else{
                    showToast("更新失败：" + e.getMessage());
                }
            }
        });
    }

    public static void updateBirth(String objectId, String birth) {
        final Person p2 = new Person();
        p2.setBirth(birth);
        p2.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    showToast("生日更新成功！");
                }else{
                    showToast("更新失败：" + e.getMessage());
                }
            }
        });
    }

    public static void updateSignature(String objectId, String newsignature) {
        final Person p2 = new Person();
        p2.setPersonal_signature(newsignature);
        p2.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    showToast("个性签名更新成功！");
                }else{
                    showToast("更新失败：" + e.getMessage());
                }
            }
        });
    }

    public static void updateHobby(String objectId, String newhobby) {
        final Person p2 = new Person();
        p2.setHobby(newhobby);
        p2.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    showToast("兴趣更新成功！");
                }else{
                    showToast("更新失败：" + e.getMessage());
                }
            }
        });
    }

    public static void updateEmail(String objectId, String newemail) {
        final Person p2 = new Person();
        p2.setEmail(newemail);
        p2.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    showToast("邮箱更新成功,请及时验证！");
                }else{
                    showToast("更新失败：" + e.getMessage());
                }
            }
        });
    }

    public static void query_Ge_dan_ById(Integer id,final OnqueryGe_dan_list_Listener listener){
        BmobQuery<Ge_dan> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("Id", id);
        categoryBmobQuery.findObjects(new FindListener<Ge_dan>() {
            @Override
            public void done(List<Ge_dan> object, BmobException e) {
                if (e == null) {
                    listener.Onsucess(object,e);
                } else {
                    Log.e("BMOB", e.toString());
                    showToast("获取不到歌单！");
                }
            }
        });
    }

    public static void query_Ge_dan_ByObjectId(String objecId, final OnqueryGe_dan_Listener listener){
        BmobQuery<Ge_dan> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(objecId, new QueryListener<Ge_dan>() {
            @Override
            public void done(Ge_dan ge_dan, BmobException e) {
                if(e==null){
                    listener.Onsucess(ge_dan,e);
                }else{
                    showToast("在查询歌单的时候出现错误");
                }
            }
        });
    }

    public static void add_Ge_dan(final String username, String name, final OnaddGe_danListener listener){
        Ge_dan p2 = new Ge_dan();
        p2.setName(name);
        p2.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e==null){
                    query_Ge_dan_ByObjectId(objectId, new OnqueryGe_dan_Listener() {
                        @Override
                        public void Onsucess(Ge_dan ge_dan, BmobException e) {
                            Integer ge_dan_id = ge_dan.getId();
                            Add_User_Ge_dan(username,ge_dan_id,listener);
                        }
                    });
                }else{
                    showToast("创建数据失败!");
                }
            }
        });
    }

    public static void Add_User_Ge_dan(String username,int id,final OnaddGe_danListener listener){
        User_Ge_dan p2 = new User_Ge_dan();
        p2.setUsername(username);
        p2.setGe_dan_Id(id);
        p2.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e==null){
                    showToast("添加歌单成功");
                    listener.Onsucess();
                }else{
                    showToast("创建歌单失败!");
                }
            }
        });
    }
    public static void queryUser_Ge_dan(String username, final OnqueryUserGenDanListener listener){
        BmobQuery<User_Ge_dan> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("username", username);
        categoryBmobQuery.findObjects(new FindListener<User_Ge_dan>() {
            @Override
            public void done(List<User_Ge_dan> object, BmobException e) {
                if (e == null) {
                    listener.onSucess(object,e);
                } else {
                    Log.e("BMOB", e.toString());
                    showToast("获取不到歌单！");
                }
            }
        });
    }
    //往歌单里添加歌曲
    public static void add_Online_Song(Online_Song song, final Integer ge_dan_id) {
        song.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e==null){
                    query_Online_Song_ByObjectId(objectId, new query_Online_Song_ByObjectIdListener() {
                        @Override
                        public void onSucess(Online_Song song, BmobException e) {
                            Integer id = song.getsId();
                            Add_Ge_dan_Online_Song(ge_dan_id,id);
                        }
                    });
                }else{
                    showToast("添加失败：" + e.getMessage());
                }
            }
        });
    }

    public static void query_Online_Song_ByObjectId(String objectId, final query_Online_Song_ByObjectIdListener listener){
        BmobQuery<Online_Song> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(objectId, new QueryListener<Online_Song>() {
            @Override
            public void done(Online_Song song, BmobException e) {
                if(e==null){
                    listener.onSucess(song,e);
                }else{
                    showToast("在查询的时候出现错误");
                }
            }
        });
    }
    public static void Add_Ge_dan_Online_Song(Integer ge_dan_id,Integer song_id){
        Ge_dan_Online_Song p2 = new Ge_dan_Online_Song();
        p2.setGe_dan_Id(ge_dan_id);
        p2.setSongId(song_id);
        p2.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e==null){
                    showToast("添加成功");
                }else{
                    showToast("创建失败!");
                }
            }
        });
    }

    public static void del_Ge_dan(final Integer id, final OnaddGe_danListener listener) {
        BmobQuery<Ge_dan> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("Id", id);
        categoryBmobQuery.findObjects(new FindListener<Ge_dan>() {
            @Override
            public void done(List<Ge_dan> object, BmobException e) {
                if (e == null) {
                    String num = object.get(0).getObjectId();
                    Ge_dan p2 = new Ge_dan();
                    p2.setObjectId(num);
                    p2.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                BmobQuery<User_Ge_dan> categoryBmobQuery = new BmobQuery<>();
                                categoryBmobQuery.addWhereEqualTo("Ge_dan_Id", id);
                                categoryBmobQuery.findObjects(new FindListener<User_Ge_dan>() {
                                    @Override
                                    public void done(List<User_Ge_dan> object, BmobException e) {
                                        if (e == null) {
                                            final String num1 = object.get(0).getObjectId();
                                            User_Ge_dan p2 = new User_Ge_dan();
                                            p2.setObjectId(num1);
                                            p2.delete(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e==null){
                                                            showToast("删除成功");
                                                            listener.Onsucess();
                                                    }else{
                                                        showToast("删除失败！");
                                                    }
                                                }

                                            });
                                        } else {
                                            Log.e("BMOB", e.toString());
                                            showToast("获取不到歌单！");
                                        }
                                    }
                                });
                            }else{
                                showToast("删除失败！");
                            }
                        }

                    });
                } else {
                    Log.e("BMOB", e.toString());
                    showToast("获取不到歌单！");
                }
            }
        });
    }

    public static Local_music copy(Online_Song music) {
        Local_music song = new Local_music();
        song.setCreatedAt(music.getCreatedAt());
        song.setsId(music.getsId());
        song.setSongId(music.getSongId());
        song.setSinger(music.getSinger());
        song.setSong(music.getSong());
        song.setImageId(music.getImageId());
        song.setAlbumId(music.getAlbumId());
        song.setPath(music.getPath());
        song.setUrl(music.getUrl());
        song.setDuration(music.getDuration());
        song.setSize(music.getSize());
        song.setSongmid(music.getSongmid());
        song.setToken(music.getToken());
        song.setLrc_url(music.getLrc_url());
        song.setMv_url(music.getMv_url());
        song.setBitm("");
        return song;
    }
}
