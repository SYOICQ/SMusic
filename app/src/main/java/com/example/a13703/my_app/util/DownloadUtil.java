package com.example.a13703.my_app.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.a13703.my_app.Inter_kou.OnDownloadListener;
import com.example.a13703.my_app.Inter_kou.OnQueryMvListener;
import com.example.a13703.my_app.MyApplication;
import com.example.a13703.my_app.R;
import com.example.a13703.my_app.bean.Local_music;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 13703 on 2019/7/13.
 */

public class DownloadUtil {
    private String result = null;
    private static DownloadUtil  downloadUtil = null;
    public static final String songRootPath = Environment.getExternalStorageDirectory().toString()
            + "/SMusicPlayer/Song/";
    private String url = "http://c.y.qq.com/soso/fcgi-bin/client_search_cp?aggr=1&cr=1&flag_qc=0&p=1&n=30&w=suyong";
    private DownloadUtil(){}
    public static DownloadUtil get(){
        if (downloadUtil == null) {
            synchronized (DownloadUtil.class) {
                if (downloadUtil == null) {
                    downloadUtil = new DownloadUtil();
                }
            }
        }
        return downloadUtil;
    }
    /**
     * @param music 下载的歌曲对象
     * @param listener 下载监听
     */
    public void download(final Local_music music, final OnDownloadListener listener) {
        String url = music.getUrl();
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onDownloadFailed();
            }
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = null;
                byte[] buf = new byte[2048];
                int len = -1;
                // 储存下载文件的目录
                String savePath = isExistDir(music);
                if("已存在!".equals(savePath)) {listener.onDownloadAlready();return;}
                try {
                    OnlineLrcUtil.wrtieContentFromUrl(music);
                    inputStream = response.body().byteStream();
                    long total = response.body().contentLength();
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(savePath));
                    long sum = 0;
                    while ((len = bis.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onDownloading(progress);
                    }
                    bos.flush();
                    // 下载完成
                    listener.onDownloadSuccess();
                    //关闭流
                    inputStream.close();
                    bis.close();
                    bos.close();
                }catch (Exception e) {
                    listener.onDownloadFailed();
                }
            }
        });
    }

    private String isExistDir(Local_music music) throws IOException {
        //存放歌曲的目录
        File file = new File(songRootPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //存放歌曲的文件
        File downloadFile = new File(songRootPath,music.getSong() + " - " + music.getSinger() + ".mp3");
        if(downloadFile.exists()){
            return "已存在!";
        }
        downloadFile.createNewFile();
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }
    //获取以suffere结尾的文件
    public List<Local_music> getSuffixFile(Context context,String filePath, String suffere) {
        List<Local_music> result = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) {
            return result;
        }
        File[] subFiles = f.listFiles();
        for (File subFile : subFiles) {
            if(subFile.isFile() && subFile.getName().endsWith(suffere)){
                Local_music m = new Local_music();
                String path = subFile.getAbsolutePath();
                int start=path.lastIndexOf("/");
                int end=path.lastIndexOf(".");
                String s1 = path.substring(start+1,end);
                String[] s = s1.split("-");
                m.setPath(path);
                m.setSinger(s[1].trim());
                m.setSong(s[0].trim());
                Resources res = context.getResources();
                Bitmap bmp= BitmapFactory.decodeResource(res, R.drawable.download_music_logo);
                m.setBitm(StringAndBitmap.bitmapToString(bmp));
                result.add(m);
            }
        }
        return result;
    }
    //本地音乐获取mv
    public void queryMv(Local_music music,final OnQueryMvListener listener) throws UnsupportedEncodingException {
        String info = music.getSinger()+" "+music.getSong();
        String song1 = URLEncoder.encode(info, "utf-8");
        String address = url.replace("suyong",song1);
        Log.d("Listening", address);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onQueryMvFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                String str = responseText.substring(9, responseText.length() - 1);
                List<Local_music>res = Utility.handleSongSearch(str);
                result = res.get(0).getMv_url();
                if(result!=null&&!"".equals(result)){
                    listener.onQueryMvSucess(result);
                }else{
                    listener.onQueryMvFailed();
                }

            }
        });
    }
}
