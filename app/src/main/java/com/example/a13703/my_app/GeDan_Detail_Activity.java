package com.example.a13703.my_app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a13703.my_app.Adapter.SongDetailAdapter;
import com.example.a13703.my_app.Inter_kou.OnDownloadListener;
import com.example.a13703.my_app.Inter_kou.OnRecyclerViewItemClickListener;
import com.example.a13703.my_app.Inter_kou.OnquerListener;
import com.example.a13703.my_app.bean.Ge_dan;
import com.example.a13703.my_app.bean.Ge_dan_Online_Song;
import com.example.a13703.my_app.bean.Local_music;
import com.example.a13703.my_app.bean.Online_Song;
import com.example.a13703.my_app.bean.Person;
import com.example.a13703.my_app.customView.RecyclerViewEmptySupport;
import com.example.a13703.my_app.service.MusicService;
import com.example.a13703.my_app.util.BmobUtil;
import com.example.a13703.my_app.util.DownloadUtil;
import com.example.a13703.my_app.util.ScreenUtils;
import com.example.a13703.my_app.util.StringAndBitmap;
import com.example.a13703.my_app.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class GeDan_Detail_Activity extends AppCompatActivity implements View.OnClickListener{
    private String TAG="GeDan_Detail_Activity";

    private  CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton edit_name;
    //下载歌曲的对话框
    private ProgressDialog dialog = null;
    private int url_position;
    private Dialog mCameraDialog;
    private ImageView title_image_view;
    private  RecyclerViewEmptySupport recyclerView;
    private SongDetailAdapter adapter;
    private int count ;
    private ProgressDialog progressDialog;
    private String title;
    private Integer id;
    private List<Local_music> result;
    private MusicService.MusicControl musicControl;
    private GeDan_Detail_Activity.MyConnection con;
    private MyApplication App;

    private class MyConnection implements ServiceConnection {
        //服务启动完成后会进入到这个方法
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            //获得service中的MyBinder
            musicControl = (MusicService.MusicControl) service;
            Log.d(TAG, "绑定成功！" + musicControl);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "失去连接！" + musicControl);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ge_dan__detail);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        id = intent.getIntExtra("id",0);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        title_image_view = (ImageView)findViewById(R.id.song_image_view);
        recyclerView = (RecyclerViewEmptySupport)findViewById(R.id.recycler_View);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        edit_name = (FloatingActionButton)findViewById(R.id.fab);
        edit_name.setOnClickListener(this);
        View emptyView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.empty_view_tab, null,false);
        recyclerView.setEmptyView(emptyView);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(title);
        refresh();
        App = MyApplication.getInstance();
        //绑定服务
        Intent intent1  = new Intent(this,MusicService.class);
        con = new MyConnection();
        bindService(intent1, con, BIND_AUTO_CREATE);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(con);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh(){
        showProgressDialog();
        result = new ArrayList<>();
        count = 0;
        BmobQuery<Ge_dan_Online_Song> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("Ge_dan_Id", id);
        categoryBmobQuery.findObjects(new FindListener<Ge_dan_Online_Song>() {
            @Override
            public void done(final List<Ge_dan_Online_Song> object1, BmobException e) {
                if (e == null) {
                    if(object1.size()!=0){
                        for(Ge_dan_Online_Song song:object1){
                            int sId = song.getSongId();
                            BmobQuery<Online_Song> categoryBmobQuery = new BmobQuery<>();
                            categoryBmobQuery.addWhereEqualTo("sId", sId);
                            categoryBmobQuery.findObjects(new FindListener<Online_Song>() {
                                @Override
                                public void done(List<Online_Song> object, BmobException e) {
                                    if (e == null) {
                                        final Local_music m = BmobUtil.copy(object.get(0));
                                        Thread t1 = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    if(Utility.getRource(m.getAlbumId())){
                                                        URL u = new URL(m.getAlbumId());
                                                        Bitmap bitmap = BitmapFactory.decodeStream(u.openStream());
                                                        m.setBitm(StringAndBitmap.bitmapToString(bitmap));
                                                    }else{
                                                        Bitmap bmp= BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.local_music);
                                                        m.setBitm(StringAndBitmap.bitmapToString(bmp));
                                                    }
                                                } catch (IOException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                        });
                                        t1.start();
                                        try {
                                            t1.join();
                                        } catch (InterruptedException e1) {
                                            e1.printStackTrace();
                                        }

                                        result.add(m);
                                        count++;
                                        if(count==object1.size()){
                                            if(adapter==null){
                                                Collections.sort(result);
                                                adapter = new SongDetailAdapter(result);
                                                recyclerView.setAdapter(adapter);
                                                adapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
                                                    @Override
                                                    public void onItemClick(View view,int position) {
                                                        if(view.getId()==R.id.song_option){
                                                            setDialog(position);
                                                        }else{
                                                            App.position = position;
                                                            App.musicList = result;
                                                            Intent intent = new Intent(GeDan_Detail_Activity.this,MusicActivity.class);
                                                            startActivity(intent);
                                                            App.pauseEvent = false;
                                                            musicControl.play();
                                                            finish();
                                                        }
                                                    }
                                                });
                                                title_image_view.setImageBitmap(StringAndBitmap.stringToBitmap(result.get(0).getBitm()));
                                            }else{
                                                adapter.notifyDataSetChanged();
                                            }
                                            closeProgressDialog();
                                        }
                                    } else {
                                        Log.e("BMOB", e.toString());
                                        BmobUtil.showToast(e.getMessage().toString());
                                        closeProgressDialog();
                                    }
                                }
                            });
                        }
                    }else{
                        BmobUtil.showToast("哼！一首歌也没有，快去添加吧！");
                        Bitmap bmp= BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.drawable.beauty);
                        title_image_view.setImageBitmap(bmp);
                        closeProgressDialog();
                    }
                } else {
                    Log.e("BMOB", e.toString());
                    BmobUtil.showToast(e.getMessage().toString());
                    closeProgressDialog();
                }
            }
        });
    }
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("请稍等...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    private void setDialog(int position) {
        url_position = position;
        mCameraDialog = new Dialog(GeDan_Detail_Activity.this, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.bottom_dialog, null);
        //初始化视图
        TextView movesong = (TextView)root.findViewById(R.id.add_song_list);
        TextView textView = (TextView) root.findViewById(R.id.pop_title);
        String song = result.get(position).getSong();
        String singer = result.get(position).getSinger();
        textView.setText(singer+"-"+song);
        root.findViewById(R.id.add_song_list).setOnClickListener(this);
        root.findViewById(R.id.pop_download).setOnClickListener(this);
        root.findViewById(R.id.pop_mv).setOnClickListener(this);
        root.findViewById(R.id.pop_share).setOnClickListener(this);
        root.findViewById(R.id.pop_delete).setOnClickListener(this);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.pop_download:
                Local_music music = result.get(url_position);
                String u1 = music.getUrl();
                if(u1!=null&&!"".equals(u1)){
                    StartDownLoad(music);
                }else{
                    Toast.makeText(GeDan_Detail_Activity.this,"暂无资源哦！",Toast.LENGTH_SHORT).show();
                }
                mCameraDialog.dismiss();
                break;
            case R.id.add_song_list:
                    BmobUtil.showToast("暂不支持该功能！请删除在添加到另一个歌单！");
                mCameraDialog.dismiss();
                break;
            case R.id.pop_mv:
                Local_music m = result.get(url_position);
                String u = m.getMv_url();
                if(u!=null&&!"".equals(u)){
                    startMV(m.getMv_url());
                }else{
                    Toast.makeText(GeDan_Detail_Activity.this,"暂无资源哦！",Toast.LENGTH_SHORT).show();
                }
                mCameraDialog.dismiss();
                break;
            case R.id.pop_share:

                mCameraDialog.dismiss();
                break;
            case R.id.pop_delete:
                DelGeDan();
                mCameraDialog.dismiss();
                break;
            case R.id.fab:
                showUpdateDeDanNameDialog();
                break;
        }

    }
    private void startMV(String url){
        if(musicControl.isPlaying()){
            musicControl.pause();
        }
        Intent intent2 = new Intent(GeDan_Detail_Activity.this, PlayMv_activity.class);
        intent2.putExtra("mv_url", url);
        startActivity(intent2);
    }
    private void StartDownLoad(Local_music music){
        showDownDialog();
        DownloadUtil.get().download(music, new OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDownDialog();
                        Toast.makeText(GeDan_Detail_Activity.this, "下载成功！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDownloading(final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setProgress(progress);
                    }
                });
            }

            @Override
            public void onDownloadFailed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDownDialog();
                        Toast.makeText(GeDan_Detail_Activity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDownloadAlready() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDownDialog();
                        Toast.makeText(GeDan_Detail_Activity.this, "已经下载过该歌曲！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    //下载对话框
    private void showDownDialog(){
        if(dialog ==null){
            dialog= new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
            dialog.setTitle("正在下载中");
            dialog.setMessage("请稍后...");
            dialog.setProgress(0);
        }
        dialog.show();
    }
    private void closeDownDialog(){
        if(dialog!=null){
            dialog.dismiss();
        }
    }
    private void showUpdateDeDanNameDialog(){
        final View view = LayoutInflater.from(this).inflate(R.layout.edit_ge_dan_name,null,false);
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
                final TextView new_name = (TextView)view.findViewById(R.id.new_ge_dan_name);
                final String nickname = new_name.getText().toString().trim();
                BmobQuery<Ge_dan> categoryBmobQuery = new BmobQuery<>();
                categoryBmobQuery.addWhereEqualTo("Id", id);
                categoryBmobQuery.findObjects(new FindListener<Ge_dan>() {
                    @Override
                    public void done(List<Ge_dan> object, BmobException e) {
                        if (e == null) {
                            String objetId = object.get(0).getObjectId();
                            Ge_dan p2 = new Ge_dan();
                            p2.setName(nickname);
                            p2.update(objetId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        BmobUtil.showToast("更新成功");
                                        collapsingToolbarLayout.setTitle(nickname);
                                    }else{
                                        BmobUtil.showToast("更新失败：" + e.getMessage());
                                    }
                                }

                            });
                        } else {
                            Log.e("BMOB", e.toString());
                            BmobUtil.showToast("查询时错误！");
                        }
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
    private void DelGeDan(){
        final Local_music m = result.get(url_position);
        final Integer sId = m.getsId();
        BmobQuery<Ge_dan_Online_Song> categoryBmobQuery = new BmobQuery<>();
        categoryBmobQuery.addWhereEqualTo("SongId", sId);
        categoryBmobQuery.findObjects(new FindListener<Ge_dan_Online_Song>() {
            @Override
            public void done(List<Ge_dan_Online_Song> object, BmobException e) {
                if (e == null) {
                    String objetId = object.get(0).getObjectId();
                    final Ge_dan_Online_Song p2 = new Ge_dan_Online_Song();
                    p2.setObjectId(objetId);
                    p2.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                BmobQuery<Online_Song> categoryBmobQuery = new BmobQuery<>();
                                categoryBmobQuery.addWhereEqualTo("sId", sId);
                                categoryBmobQuery.findObjects(new FindListener<Online_Song>() {
                                    @Override
                                    public void done(List<Online_Song> object, BmobException e) {
                                        if (e == null) {
                                            String objetId = object.get(0).getObjectId();
                                            Online_Song p2 = new Online_Song();
                                            p2.setObjectId(objetId);
                                            p2.delete(new UpdateListener() {
                                                @Override
                                                public void done(BmobException e) {
                                                    if(e==null){
                                                        BmobUtil.showToast("删除成功!");
                                                        for(int i=result.size()-1;i>=0;i--){
                                                            if(result.get(i).getsId()==m.getsId()) {
                                                                result.remove(i);
                                                                break;
                                                            }
                                                        }
                                                        adapter.notifyDataSetChanged();
                                                    }else{
                                                        BmobUtil.showToast("删除失败：" + e.getMessage());
                                                    }
                                                }

                                            });
                                        } else {
                                            Log.e("BMOB", e.toString());
                                            BmobUtil.showToast("查询时错误！");
                                        }
                                    }
                                });
                            }else{
                                BmobUtil.showToast("删除失败：" + e.getMessage());
                            }
                        }

                    });
                } else {
                    Log.e("BMOB", e.toString());
                    BmobUtil.showToast("查询时错误！");
                }
            }
        });
    }
}
