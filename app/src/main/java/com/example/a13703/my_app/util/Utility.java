package com.example.a13703.my_app.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.a13703.my_app.MyApplication;
import com.example.a13703.my_app.R;
import com.example.a13703.my_app.bean.Local_music;
import com.example.a13703.my_app.bean.Online_Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 13703 on 2019/7/8.
 */

public class Utility {
    private static String albumid = "http://imgcache.qq.com/music/photo/album_300/%i1/300_albumpic_%i2_0.jpg";
    private static String albumid_1 ="http://v1.itooi.cn/tencent/pic?id=%sy";
    private static String token = "http://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?format=json205361747&platform=yqq&cid=205361747&songmid=%sy1&filename=C400%sy2.m4a&guid=126548448";
    private static String url ="http://link.hhtjim.com/qq/%sy.mp3";
    private static String url_another = "http://v1.itooi.cn/tencent/url?id=%sy&quality=128";
    private static String lrc ="http://v1.itooi.cn/tencent/lrc?id=%sy";
    public static String top_list = "http://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?g_tk=5381&uin=0&format=json&inCharset=utf-8&outCharset=utf-8%C2%ACice=0&platform=h5&needNewCode=1&tpl=3&page=detail&type=top&topid=27&_=1519963122923";
    public static String mv_url="https://v1.itooi.cn/tencent/mvUrl?id=%sy&quality=1080";
    //处理歌曲搜索的结果
    public static List<Local_music> handleSongSearch(String response){
        if(!TextUtils.isEmpty(response)){
            List<Local_music> music = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject data = jsonObject.getJSONObject("data");
                JSONObject song = data.getJSONObject("song");
                JSONArray list = song.getJSONArray("list");
                for(int i=0;i<list.length();i++){
                    JSONObject object = list.getJSONObject(i);
                    Local_music m = new Local_music();
                    m.setSongId(object.getLong("songid"));
                    long j = object.getLong("albumid");
                    String result = albumid.replace("%i1",Long.toString(j%100)).replace("%i2",Long.toString(j));
                    //String result = albumid_1.replace("%sy",object.getString("songmid"));
                    m.setAlbumId(result);
                    String mv_url1 = object.getString("vid");
                    try {
                        if(getRource(result)){
                            URL u = new URL(result);
                            Bitmap bitmap = BitmapFactory.decodeStream(u.openStream());
                            m.setBitm(StringAndBitmap.bitmapToString(bitmap));
                        }else{
                            Bitmap bmp= BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.local_music);
                            m.setBitm(StringAndBitmap.bitmapToString(bmp));
                        }
                        if("".equals(mv_url1)){
                            m.setMv_url("");
                        }else{
                            String res = mv_url.replace("%sy",mv_url1);
                            if(getRource(res)){
                                m.setMv_url(res);
                            }else{
                                m.setMv_url("");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    m.setSong(object.getString("songname"));
                    m.setSinger(object.getJSONArray("singer").getJSONObject(0).getString("name"));
                    m.setSongmid(object.getString("songmid"));
                    String j1 = object.getString("songmid");
                    String result1 = token.replace("%sy",j1).replace("%sy1",j1);
                    m.setToken(result1);
                    String r = url_another.replace("%sy",j1);
                    m.setUrl(r);
                    String lrcurl = lrc.replace("%sy",j1);
                    m.setLrc_url(lrcurl);
                    Log.d("Utility",m.toString());
                    music.add(m);
                }
                return music;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
      return null;
    }
    //判断资源是否存在
     public static boolean getRource(String source) {
             try {
                 URL url = new URL(source);
                 URLConnection uc = url.openConnection();
                 InputStream in = uc.getInputStream();
                 if (source.equalsIgnoreCase(uc.getURL().toString()))
                     in.close();
                 return true;
             } catch (Exception e) {
                 return false;
             }
    }
    //处理top100
    public static List<Local_music> handleTopMusic(String response) {
        if(!TextUtils.isEmpty(response)){
            List<Local_music> music = new ArrayList<>();
            try{
                JSONObject jsonObject = new JSONObject(response);
                JSONArray list = jsonObject.getJSONArray("songlist");
                for(int i=0;i<list.length();i++){
                    JSONObject object = list.getJSONObject(i);
                    JSONObject o = object.getJSONObject("data");
                    Local_music m = new Local_music();
                    m.setSongId(o.getLong("songid"));
                    m.setSong(o.getString("songname"));
                    m.setSinger(o.getJSONArray("singer").getJSONObject(0).getString("name"));
                    m.setSongmid(o.getString("songmid"));
                    long j = o.getLong("albumid");
                    String result = albumid.replace("%i1",Long.toString(j%100)).replace("%i2",Long.toString(j));
                    m.setAlbumId(result);
                    String mv_url1 = o.getString("vid");
                    try {
                        if(getRource(result)){
                            URL u = new URL(result);
                            Bitmap bitmap = BitmapFactory.decodeStream(u.openStream());
                            m.setBitm(StringAndBitmap.bitmapToString(bitmap));
                        }else{
                            Bitmap bmp= BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.local_music);
                            m.setBitm(StringAndBitmap.bitmapToString(bmp));
                        }
                        if("".equals(mv_url1)){
                            m.setMv_url("");
                        }else{
                            String res = mv_url.replace("%sy",mv_url1);
                            if(getRource(res)){
                                m.setMv_url(res);
                            }else{
                                m.setMv_url("");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String j1 = o.getString("songmid");
                    String result1 = token.replace("%sy",j1).replace("%sy1",j1);
                    m.setToken(result1);
                    String r = url_another.replace("%sy",j1);
                    m.setUrl(r);
                    String lrcurl = lrc.replace("%sy",j1);
                    m.setLrc_url(lrcurl);
                    //Log.d("Utility", m.toString());
                    music.add(m);
                }
                return music;
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Online_Song copy(Local_music music) {
        Online_Song song = new Online_Song();
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
        return song;
    }
}
