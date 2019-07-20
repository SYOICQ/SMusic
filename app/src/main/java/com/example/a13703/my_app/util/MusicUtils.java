package com.example.a13703.my_app.util;



import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import com.example.a13703.my_app.MyApplication;
import com.example.a13703.my_app.R;
import com.example.a13703.my_app.bean.Local_music;
import com.example.a13703.my_app.bean.Song;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.crud.LitePalSupport;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 13703 on 2019/6/20.
 */

public class MusicUtils {
    //获取专辑封面的Uri
    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
    public static List<Local_music> getMusicData(Context context){
        List<Local_music> list  = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if(cursor!=null){
            while(cursor.moveToNext()){
                Local_music song = new Local_music();
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                song.setSongId(id);
                song .setSong(name);
                song.setImageId(albumId);
                song.setSinger(singer);
                song.setDuration(duration);
                song.setPath(path);
                song.setSize(size);
                song.setBitm(StringAndBitmap.bitmapToString(getMusicBitmap(context,id,albumId)));
                //根据专辑ID获取到专辑封面图
                //song.setBitmap(getMusicBitmap(context,id,albumId));
                if(song.getSize()>1000*800){
                    if(song.getSong().contains("-")){
                        String[] str = song.getSong().split("-");
                        song.setSinger(str[0]);
                        song.setSong(str[1]);
                    }
                    Log.d("MusicUtils", song.toString());
                    list.add(song);
                }
            }
            cursor.close();
        }
        return list;
    }
    public static List<Local_music>getRecentListen(Context context){
        List<Local_music> list = LitePal.findAll(Local_music.class);
        Collections.reverse(list);
        return list;
    }
    public static String FormatTime(int time){
        if(time/1000%60<10){
            return time/1000/60 + ":0"+time/1000%60;
        }else{
            return time/1000/60+":"+time/1000%60;
        }
    }
    private static Bitmap getAlbumArt(Context mContext,int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = mContext.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        Bitmap bm = null;
        if (album_art != null) {
            bm = BitmapFactory.decodeFile(album_art);
        } else {
            bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.local_music);
        }
        return bm;
    }

    public static Bitmap getMusicBitmap(Context context,long songid,long albumid){
        Bitmap bm = null;
        //专辑id和歌曲id小于0说明没有专辑，歌曲，并抛出异常
        if(albumid<0&&songid<0){
            throw new IllegalArgumentException("Must specfiy an album or a song id");
        }
        try{
            if(albumid<0){
                Uri uri = Uri.parse("content://media/external/audio/media/"+songid+"/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri,"r");
                if(pfd!=null){
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            }else{
                Uri uri = ContentUris.withAppendedId(albumArtUri,albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri,"r");
                if(pfd!=null){
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }else{
                    return null;
                }
            }
        }catch(FileNotFoundException ex){

        }
        if(bm==null){
            Resources resources = context.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.local_music);
            BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
            bm = bitmapDrawable.getBitmap();
        }
        return Bitmap.createScaledBitmap(bm,150,150,true);
    }
}

