package com.example.a13703.my_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.a13703.my_app.Adapter.SongDetailAdapter;
import com.example.a13703.my_app.bean.Ge_dan_Online_Song;
import com.example.a13703.my_app.bean.Local_music;
import com.example.a13703.my_app.bean.Online_Song;
import com.example.a13703.my_app.customView.RecyclerViewEmptySupport;
import com.example.a13703.my_app.util.BmobUtil;
import com.example.a13703.my_app.util.StringAndBitmap;
import com.example.a13703.my_app.util.Utility;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class GeDan_Detail_Activity extends AppCompatActivity {

    private ImageView title_image_view;
    private  RecyclerViewEmptySupport recyclerView;
    private SongDetailAdapter adapter;
    private int count ;
    private ProgressDialog progressDialog;
    private String title;
    private Integer id;
    private List<Local_music> result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ge_dan__detail);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        id = intent.getIntExtra("id",0);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        title_image_view = (ImageView)findViewById(R.id.song_image_view);
        recyclerView = (RecyclerViewEmptySupport)findViewById(R.id.recycler_View);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(title);
        refresh();
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
                                        Local_music m = BmobUtil.copy(object.get(0));
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
                                        result.add(m);
                                        count++;
                                        if(count==object1.size()){
                                            if(adapter==null){
                                                adapter = new SongDetailAdapter(result);
                                                recyclerView.setAdapter(adapter);
                                                View emptyView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.empty_view_tab, null,false);
                                                recyclerView.setEmptyView(emptyView);
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
}
