package com.example.a13703.my_app;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a13703.my_app.Adapter.SongAdapter;
import com.example.a13703.my_app.Inter_kou.InnerItemOnclickListener;
import com.example.a13703.my_app.Inter_kou.OnDownloadListener;
import com.example.a13703.my_app.Inter_kou.OnquerListener;
import com.example.a13703.my_app.Inter_kou.OnqueryGe_dan_list_Listener;
import com.example.a13703.my_app.Inter_kou.OnqueryUserGenDanListener;
import com.example.a13703.my_app.bean.Ge_dan;
import com.example.a13703.my_app.bean.Local_music;
import com.example.a13703.my_app.bean.Online_Song;
import com.example.a13703.my_app.bean.Person;
import com.example.a13703.my_app.bean.Song;
import com.example.a13703.my_app.bean.User_Ge_dan;
import com.example.a13703.my_app.service.MusicService;
import com.example.a13703.my_app.util.BmobUtil;
import com.example.a13703.my_app.util.DownloadUtil;
import com.example.a13703.my_app.util.HttpUtil;
import com.example.a13703.my_app.util.ScreenUtils;
import com.example.a13703.my_app.util.Utility;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;
import rx.internal.operators.OperatorMapNotification;


/**
 * Created by 13703 on 2019/6/12.
 */

public class Listening extends Fragment implements InnerItemOnclickListener,View.OnClickListener{

    private int count = 0;
    private List<String> result;//歌单名称
    private List<Integer> resultId;//歌单Id;
    //下载歌曲的对话框
    private ProgressDialog dialog = null;
    private int url_position;
    private Dialog mCameraDialog;
    private String TAG="Listening";
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
    private SongAdapter mAdapter;
    private EditText search_input;
    private ImageView search_btn;
    private ListView song_list;
    private ProgressDialog progressDialog;
    private LinearLayout llProgress;
    private String url = "http://c.y.qq.com/soso/fcgi-bin/client_search_cp?aggr=1&cr=1&flag_qc=0&p=1&n=30&w=suyong";
    private List<Local_music> data;
    private MyApplication mApp;
    private MusicService.MusicControl musicControl;
    private MyConnection con;
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
    private void initData(){
        data = new ArrayList<>();
        mDataList = new ArrayList<>();
        mMoreData = new ArrayList<>();
    }
    private void initEvent(){
        mHandler.sendEmptyMessage(open);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search_info = search_input.getText().toString().trim();
                if(!"".equals(search_info)) {
                    try {
                        //收起键盘
                        InputMethodManager inputMethodManager =(InputMethodManager)MyApplication.getContext().
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(search_input.getWindowToken(), 0);
                        queryFromServer(search_info);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(MyApplication.getContext(),"搜索内容为空！",Toast.LENGTH_SHORT).show();
            }
        });
        song_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mApp.musicList = data;
                mApp.position = i;
                musicControl.play();
            }
        });
        song_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            // SCROLL_STATE_IDLE 闲置状态，此时没有滑动
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        // 获取屏幕上可见的最后一项
                        int position = song_list.getLastVisiblePosition();
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

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fg_listening,container,false);
        search_input = (EditText)view.findViewById(R.id.input_search);
        search_btn = (ImageView)view.findViewById(R.id.btn_search);
        song_list = (ListView)view.findViewById(R.id.song_list);
        llProgress = (LinearLayout) view.findViewById(R.id.ll_progress);
        if(mApp==null) mApp = MyApplication.getInstance();
        queryTopSong();
        initEvent();
        initData();
        Intent service=new Intent(getActivity().getApplicationContext(),MusicService.class);
        con = new MyConnection();
        getActivity().getApplicationContext().bindService(service, con, Context.BIND_AUTO_CREATE);
        return view;
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
    }
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
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
    private void queryFromServer(String song) throws UnsupportedEncodingException {
        showProgressDialog();
        String song1 = URLEncoder.encode(song, "utf-8");
        String address = url.replace("suyong",song1);
        Log.d("Listening", address);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"查询失败！",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                String str = responseText.substring(9, responseText.length() - 1);
                closeProgressDialog();
                Intent intent2 = new Intent(MyApplication.getContext(), LocalMusicActivity.class);
                String title2 = "搜索结果";
                intent2.putExtra("title", title2);
                intent2.putExtra("res", str);
                startActivity(intent2);
            }


        });
    }
  private void queryTopSong(){
      HttpUtil.sendOkHttpRequest(Utility.top_list, new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
              getActivity().runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      Toast.makeText(getContext(),"查询失败！",Toast.LENGTH_SHORT).show();
                  }
              });
          }
          @Override
          public void onResponse(Call call, Response response) throws IOException {
              String responseText = response.body().string();
              String str = responseText;
              data.addAll(Utility.handleTopMusic(str));
              mHandler.sendEmptyMessage(close);
              new LoadDataTask().execute();
          }
      });
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
        switch(view.getId()) {
            case R.id.pop_download:
                Local_music music = data.get(url_position);
                String u1 = music.getUrl();
                if(u1!=null&&!"".equals(u1)){
                    StartDownLoad(music);
                }else{
                    Toast.makeText(getActivity(),"暂无资源哦！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.add_song_list:
                count = 0;
                BmobUtil.queryUser_Ge_dan(mApp.currentUser.getUsername(), new OnqueryUserGenDanListener() {
                    @Override
                    public void onSucess(final List<User_Ge_dan> object1, BmobException e) {
                        result = new ArrayList<>();
                        resultId = new ArrayList<>();
                        if(object1.size()!=0) {
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
                        }else{
                            BmobUtil.showToast("你还没有属于自己的歌单哦！");
                        }

                    }
                });
                break;
            case R.id.pop_mv:
                Local_music m = data.get(url_position);
                String u = m.getMv_url();
                if(u!=null&&!"".equals(u)){
                    startMV(m.getMv_url());
                }else{
                    Toast.makeText(getContext(),"暂无资源哦！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.pop_share:

                break;
            case R.id.pop_delete:
                    Toast.makeText(getContext(),"推荐歌单不能删除哦！",Toast.LENGTH_SHORT).show();
                break;
        }
        mCameraDialog.dismiss();
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
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 查询一共有多少数据
            if (mTotalCount == -1) {
                mTotalCount = data.size();
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
            if (mAdapter == null) {
                mAdapter = new SongAdapter(MyApplication.getContext(),R.layout.song_item,mDataList);
                set();
                song_list.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    private List<Local_music> loadMore(int mStartIndex, int mMaxCount){
        List<Local_music> list = new ArrayList<>();
        int flag = mStartIndex;
        int sum = mStartIndex+mMaxCount;
        while(flag<data.size()&&flag<sum){
            list.add(data.get(flag));
            flag++;
        }
        return list;
    }
    private void set(){
        mAdapter.setOnInnerItemOnClickListener(this);
    }
    private class MyConnection implements ServiceConnection {
        //服务启动完成后会进入到这个方法
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            //获得service中的MyBinder
            musicControl = (MusicService.MusicControl) service;
            Log.d(TAG, "绑定成功！"+musicControl);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG,"失去连接！"+musicControl);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getApplicationContext().unbindService(con);
    }
    private void setDialog(int position) {
        url_position = position;
        mCameraDialog = new Dialog(getContext(), R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.bottom_dialog, null);
        //初始化视图
        TextView textView = (TextView) root.findViewById(R.id.pop_title);
        String song = data.get(position).getSong();
        String singer = data.get(position).getSinger();
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
        Intent intent2 = new Intent(getContext(), PlayMv_activity.class);
        intent2.putExtra("mv_url", url);
        startActivity(intent2);
    }
    private void StartDownLoad(Local_music music){
        showDownDialog();
        DownloadUtil.get().download(music, new OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDownDialog();
                        Toast.makeText(getActivity(), "下载成功！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDownloading(final int progress) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setProgress(progress);
                    }
                });
            }

            @Override
            public void onDownloadFailed() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDownDialog();
                        Toast.makeText(getActivity(), "下载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDownloadAlready() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeDownDialog();
                        Toast.makeText(getActivity(), "已经下载过该歌曲！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void showDownDialog(){
        if(dialog ==null){
            dialog= new ProgressDialog(getActivity());
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
    private void show_Ge_dan_list_Dialog() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.ge_dan_list,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).create();
        ListView listView = (ListView)view.findViewById(R.id.ge_dan_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,result);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog.dismiss();
                Integer Ge_dan_Id = resultId.get(i);
                Local_music music = data.get(url_position);
                Online_Song song = Utility.copy(music);
                BmobUtil.add_Online_Song(song,Ge_dan_Id);
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(getContext())/4*3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }
}
