package com.example.a13703.my_app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a13703.my_app.Adapter.LocalMusicAdapter;
import com.example.a13703.my_app.Inter_kou.InnerItemOnclickListener;
import com.example.a13703.my_app.Inter_kou.OnDownloadListener;
import com.example.a13703.my_app.Inter_kou.OnQueryMvListener;
import com.example.a13703.my_app.Inter_kou.OnqueryGe_dan_list_Listener;
import com.example.a13703.my_app.Inter_kou.OnqueryUserGenDanListener;
import com.example.a13703.my_app.bean.Ge_dan;
import com.example.a13703.my_app.bean.Local_music;
import com.example.a13703.my_app.bean.Online_Song;
import com.example.a13703.my_app.bean.User_Ge_dan;
import com.example.a13703.my_app.service.MusicService;
import com.example.a13703.my_app.util.BmobUtil;
import com.example.a13703.my_app.util.CompatibleUtils;
import com.example.a13703.my_app.util.DownloadUtil;
import com.example.a13703.my_app.util.MusicUtils;
import com.example.a13703.my_app.util.ScreenUtils;
import com.example.a13703.my_app.util.Utility;

import org.litepal.LitePal;
import org.w3c.dom.Text;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;

public class LocalMusicActivity extends AppCompatActivity implements View.OnClickListener,InnerItemOnclickListener {

    private int count = 0;
    private List<String> result;//歌单名称
    private List<Integer> resultId;//歌单Id;
    //查询mv的对话框
    private ProgressDialog progressDialog = null;
    //下载歌曲的对话框
    private ProgressDialog dialog = null;
    private int url_position;
    private Dialog mCameraDialog;
    //适配器的数据源
    private List<Local_music> mDataList;
    //下一批数据
    private List<Local_music> mMoreData;
    //下一批数据开始的位置
    private int mStartIndex = 0;
    //下一批数据的数量
    private int mMaxCount = 10;
    //数据总数
    private int mTotalCount = -1;
    private ListView mListView;
    private List<Local_music> list;
    private LocalMusicAdapter adapter;
    private MyApplication mApp;
    private String title_text;
    private TextView title;
    private Button back_button;
    private TextView empty_view;
    private MusicService.MusicControl musicControl;
    private MyConnection1 conn;
    private LinearLayout llProgress;
    private static final int open = 1;
    private static final int close = 2;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case open:
                    llProgress.setVisibility(View.VISIBLE);
                    break;
                case close:
                    llProgress.setVisibility(View.GONE);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏时间
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_local_music);
        mApp = MyApplication.getInstance();
        Intent intent = getIntent();
        title_text = intent.getStringExtra("title");
        initView();
        //绑定服务
        Intent intent1  = new Intent(this,MusicService.class);
        conn = new MyConnection1();
        bindService(intent1, conn, BIND_AUTO_CREATE);
    }

    private class MyConnection1 implements ServiceConnection {
        //服务启动完成后会进入到这个方法
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            //获得service中的MyBinder
            musicControl = (MusicService.MusicControl) service;
            Log.d("MusicActivity", "绑定成功！"+musicControl);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("MusicActivity","失去连接！"+musicControl);
        }
    }
    private void initView()  {
        mDataList = new ArrayList<>();
        mMoreData = new ArrayList<>();
        llProgress = (LinearLayout)findViewById(R.id.l1_progress);
        empty_view = (TextView)findViewById(R.id.empty_text);
        title = (TextView)findViewById(R.id.title_text);
        back_button = (Button)findViewById(R.id.back_btn);
        mListView = (ListView) findViewById(R.id.local_list);
        title.setText(title_text);
        list = new ArrayList<>();
        //把扫描到的音乐赋值给list
        if(title_text.equals("本地音乐")){
            CompatibleUtils.updateMedia(this, Environment.getExternalStorageDirectory().toString());
            list.addAll(MusicUtils.getMusicData(this));
        }
        if(title_text.equals("最近播放")){
            list.addAll(MusicUtils.getRecentListen(this));
        }
        if(title_text.equals("我的下载")){
            list.addAll(DownloadUtil.get().getSuffixFile(this,DownloadUtil.songRootPath,".mp3"));
        }
        if(title_text.equals("搜索结果")) {
            Intent intent = getIntent();
            final String str = intent.getStringExtra("res");
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    list.addAll(Utility.handleSongSearch(str));
                }
            });
            try {
                t.start();
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        new LoadDataTask().execute();
        mApp.musicList=list;
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mApp.position = i;
                Intent intent = new Intent(LocalMusicActivity.this,MusicActivity.class);
                startActivity(intent);
                mApp.pauseEvent = false;
                musicControl.play();
                finish();
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            // SCROLL_STATE_IDLE 闲置状态，此时没有滑动
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        // 获取屏幕上可见的最后一项
                        int position = mListView.getLastVisiblePosition();
                        // 如果屏幕上可见的最后一项是当前适配器数据源的最后一项，
                        // 并且数据还没有加载完，就加载下一批数据。
                        if (position == mDataList.size() - 1 && position != mTotalCount - 1) {
                            mStartIndex += mMaxCount;
                            // 加载下一批数据
                            new LoadDataTask().execute();
                        } else if (position == mDataList.size() - 1 && position == mTotalCount - 1) {
                            Toast.makeText(MyApplication.getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        back_button.setOnClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
    @Override
    public void itemClick(View v) {
        int position = (Integer) v.getTag();
        switch (v.getId()){
            case R.id.song_option:
                setDialog(position);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                finish();
                break;
            case R.id.pop_download:
                if("本地音乐".equals(title_text)){
                    Toast.makeText(this,"本地音乐，不用下载哦！",Toast.LENGTH_SHORT).show();
                }else if("最近播放".equals(title_text)){
                    Local_music music = list.get(url_position);
                    String u = music.getUrl();
                    if(u!=null&&!"".equals(u)){
                        StartDownLoad(music);
                    }else{
                        Toast.makeText(this,"暂无资源",Toast.LENGTH_SHORT).show();
                    }
                }else if("我的下载".equals(title_text)){
                    Toast.makeText(this,"你已经下载过了哦！",Toast.LENGTH_SHORT).show();
                }else if("搜索结果".equals(title_text)){
                    Local_music music = list.get(url_position);
                    String u = music.getUrl();
                    if(u!=null&&!"".equals(u)){
                        StartDownLoad(music);
                    }else{
                        Toast.makeText(this,"暂无资源",Toast.LENGTH_SHORT).show();
                    }
                }
                mCameraDialog.dismiss();
                break;
            case R.id.add_song_list:
                Local_music music = list.get(url_position);
                if(music.getUrl()!=null&&!"".equals(music.getUrl())) {
                    count = 0;
                    BmobUtil.queryUser_Ge_dan(mApp.currentUser.getUsername(), new OnqueryUserGenDanListener() {
                        @Override
                        public void onSucess(final List<User_Ge_dan> object1, BmobException e) {
                            result = new ArrayList<>();
                            resultId = new ArrayList<>();
                            if (object1.size() != 0) {
                                for (User_Ge_dan user : object1) {
                                    int Id = user.getGe_dan_Id();
                                    BmobUtil.query_Ge_dan_ById(Id, new OnqueryGe_dan_list_Listener() {
                                        @Override
                                        public void Onsucess(List<Ge_dan> object, BmobException e) {
                                            result.add(object.get(0).getName());
                                            resultId.add(object.get(0).getId());
                                            count++;
                                            if (count == object1.size()) {
                                                show_Ge_dan_list_Dialog();
                                            }
                                        }
                                    });
                                }
                            } else {
                                BmobUtil.showToast("你还没有属于自己的歌单哦！");
                            }

                        }
                    });
                }else{
                    BmobUtil.showToast("不能收藏非网络歌曲哦！");
                }
                mCameraDialog.dismiss();
                break;
            case R.id.pop_mv:
                Local_music m = list.get(url_position);
                if("搜索结果".equals(title_text)){
                    String u = m.getMv_url();
                    if (u != null && !"".equals(u)) {
                        startMV(m.getMv_url());
                    } else {
                        Toast.makeText(this, "暂无资源", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    queryMv(m);
                }
                mCameraDialog.dismiss();
                break;
            case R.id.pop_share:
                if("本地音乐".equals(title_text)){

                }else if("最近播放".equals(title_text)){

                }else if("我的下载".equals(title_text)){

                }else if("搜索结果".equals(title_text)){

                }
                mCameraDialog.dismiss();
                break;
            case R.id.pop_delete:
                Local_music m2 = list.get(url_position);
                if("本地音乐".equals(title_text)){
                    if(!musicControl.getPath().equals(m2.getPath())){
                        if(deleteFile(m2.getPath(),m2)) Toast.makeText(this,"删除成功！",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this,"您要删除的歌正在播放！",Toast.LENGTH_SHORT).show();
                    }
                }else if("最近播放".equals(title_text)){
                    if(!musicControl.getPath().equals(m2.getPath())){
                        if(deleteDataFile(m2.getSongId()))  Toast.makeText(this,"删除成功！",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this,"您要删除的歌正在播放！",Toast.LENGTH_SHORT).show();
                    }
                }else if("我的下载".equals(title_text)){
                    if(!musicControl.getPath().equals(m2.getPath())){
                        if(deleteFile(m2.getPath(),m2)) Toast.makeText(this,"删除成功！",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this,"您要删除的歌正在播放！",Toast.LENGTH_SHORT).show();
                    }
                }else if("搜索结果".equals(title_text)){
                    Toast.makeText(this, "不能删除哦！", Toast.LENGTH_SHORT).show();
                }
                mCameraDialog.dismiss();
                break;
        }
    }
    class LoadDataTask extends AsyncTask<Void, Void, List<Local_music>> {

        @Override
        protected void onPreExecute() {
            // 显示进度条
            mHandler.sendEmptyMessage(open);
        }

        @Override
        protected List<Local_music> doInBackground(Void... params) {
            // 模拟耗时
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 查询一共有多少数据
            if (mTotalCount == -1) {
                mTotalCount = list.size();
            }
            // 分批加载
            mMoreData = loadMore(mStartIndex, mMaxCount);
            return mMoreData;
        }

        @Override
        protected void onPostExecute(List<Local_music> strings) {
            // 隐藏进度条
            mHandler.sendEmptyMessage(close);
            // 将新增数据追加到适配器的数据源中
            mDataList.addAll(strings);
            if (adapter == null) {
                adapter = new LocalMusicAdapter(MyApplication.getContext(),R.layout.song_item,mDataList);
                set();
                mListView.setAdapter(adapter);
                mListView.setEmptyView(empty_view);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }
    private List<Local_music> loadMore(int mStartIndex, int mMaxCount){
        List<Local_music> list1 = new ArrayList<>();
        int flag = mStartIndex;
        int sum = mStartIndex+mMaxCount;
        while(flag<list.size()&&flag<sum){
            list1.add(list.get(flag));
            flag++;
        }
        return list1;
    }
    private void set(){
        adapter.setOnInnerItemOnClickListener(this);
    }
    private void setDialog(int position) {
        url_position = position;
        mCameraDialog = new Dialog(LocalMusicActivity.this, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.bottom_dialog, null);
        //初始化视图
        TextView textView = (TextView) root.findViewById(R.id.pop_title);
        String song = list.get(position).getSong();
        String singer = list.get(position).getSinger();
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
    private void startMV(String url){
        if(musicControl.isPlaying()){
            musicControl.pause();
        }
        Intent intent2 = new Intent(this, PlayMv_activity.class);
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
                        Toast.makeText(LocalMusicActivity.this, "下载成功！", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(LocalMusicActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDownloadAlready() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDownDialog();
                        Toast.makeText(LocalMusicActivity.this, "已经下载过该歌曲！", Toast.LENGTH_SHORT).show();
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
    //查询mv
    private void queryMv(Local_music music){
        showProgressDialog();
        try {
            DownloadUtil.get().queryMv(music, new OnQueryMvListener() {
                @Override
                public void onQueryMvSucess(String url) {
                    closeProgressDialog();
                    startMV(url);
                }

                @Override
                public void onQueryMvFailed() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(LocalMusicActivity.this,"暂无资源",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("查询中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    //删除本地文件
    public boolean deleteFile(String filePath,Local_music music) {
        File file = new File(filePath);
        if (file.exists()) {
             file.delete();
        }
        CompatibleUtils.updateMedia(this,filePath);
        for(int i=mDataList.size()-1;i>=0;i--){
            if(mDataList.get(i).getSongId()==music.getSongId()) {
                mDataList.remove(i);
                mTotalCount--;
                break;
            }
        }
        adapter.notifyDataSetChanged();
        return true;
    }
    //删除最近播放
    public boolean deleteDataFile(Long songId){
        LitePal.deleteAll(Local_music.class,"SongId = ?",Long.toString(songId));
        for(int i=mDataList.size()-1;i>=0;i--){
            if(mDataList.get(i).getSongId()==songId) {
                mDataList.remove(i);
                break;
            }
        }
        adapter.notifyDataSetChanged();
        return true;
    }
    private void show_Ge_dan_list_Dialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.ge_dan_list,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        ListView listView = (ListView)view.findViewById(R.id.ge_dan_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,result);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog.dismiss();
                Integer Ge_dan_Id = resultId.get(i);
                Local_music music = list.get(url_position);
                Online_Song song = Utility.copy(music);
                BmobUtil.add_Online_Song(song,Ge_dan_Id);
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/4*3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
