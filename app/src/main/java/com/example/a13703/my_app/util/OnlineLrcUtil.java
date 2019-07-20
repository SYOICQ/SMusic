package com.example.a13703.my_app.util;

import android.os.Environment;
import android.util.Log;

import com.example.a13703.my_app.bean.Local_music;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

/**
 * Created by 13703 on 2019/6/30.
 */

public class OnlineLrcUtil {
    private static String TAG = "OnlineLrcUtil";
    private static OnlineLrcUtil instance;
    public static final String lrcRootPath = Environment.getExternalStorageDirectory().toString()
            + "/SMusicPlayer/Lyrics/";
    public static OnlineLrcUtil getInstance() {
        if (null == instance) {
            instance = new OnlineLrcUtil();
        }
        return instance;
    }
    // 歌词文件网络地址，歌词文件本地缓冲地址
    public static boolean wrtieContentFromUrl(Local_music music) {
        try {
            //存放歌词的目录
            File file = new File(lrcRootPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            //存放歌词的文件
            File file1 = new File(lrcRootPath,music.getSong() + " - " + music.getSinger() + ".lrc");
            if(file1.exists()){
                return true;
            }
            file1.createNewFile();

            String url1 = music.getLrc_url();//获取歌词的IP地址
            HttpURLConnection conn = (HttpURLConnection) new URL(url1).openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            InputStream inputStream = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file1));
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
                bos.flush();
            }
            bis.close();
            bos.close();
            inputStream.close();
            Log.d(TAG, "获取歌词成功");
                return true;
            // System.out.println("getFile:"+str);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    public static String getLrcPath(String title, String artist) {
        return lrcRootPath + title + " - " + artist + ".lrc";
    }

}
