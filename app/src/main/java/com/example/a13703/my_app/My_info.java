package com.example.a13703.my_app;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.BoringLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a13703.my_app.Adapter.MyExtendableListViewAdapter;
import com.example.a13703.my_app.Inter_kou.InnerItemOnclickListener;
import com.example.a13703.my_app.Inter_kou.OnaddGe_danListener;
import com.example.a13703.my_app.Inter_kou.OnqueryGe_dan_list_Listener;
import com.example.a13703.my_app.Inter_kou.OnqueryUserGenDanListener;
import com.example.a13703.my_app.bean.Ge_dan;
import com.example.a13703.my_app.bean.User_Ge_dan;
import com.example.a13703.my_app.customView.MyExpandableListView;
import com.example.a13703.my_app.util.BmobUtil;
import com.example.a13703.my_app.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;

import static android.R.attr.name;

/**
 * Created by 13703 on 2019/6/12.
 */

public class My_info extends Fragment implements View.OnClickListener,InnerItemOnclickListener {

    private ProgressDialog progressDialog;
    private MyExtendableListViewAdapter adapter1;
    private int count = 0;
    private List<String> result;//歌单名称
    private List<Integer> resultId;//歌单Id;
    private EditText new_songlist_name;
    private LocalBroadcastManager broadcastManager;
    private MyApplication App;
    private TextView username1;
    private MyExpandableListView expandableListView;
    private ImageView message;
    private ImageView local_music;
    private ImageView recent_play;
    private ImageView my_favourite;
    private List<String> mGroup;           //组列表
    private List<List<String>> mChild;     //子列表
    public String[] groupString = {"自建歌单"};
    public String[][] childString = {
            {"孙尚香", "后羿", "马可波罗", "狄仁杰"},
    };
    private static final int UPDATE_TEXT = 1;
    public  Handler handler = new Handler(){
        //在主线程中处理从子线程发送过来的消息
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    username1.setText(App.currentUser.getNickname());
                    break;
            }
        }
    };
   private void initData(){
       mGroup = new ArrayList<>();
       mChild = new ArrayList<>();
       for(int i=0;i<groupString.length;i++){
           mGroup.add(groupString[i]);
           List<String>  data = new ArrayList<>();
           for(int j=0;j<childString.length;j++){
               data.add(childString[i][j]);
           }
           mChild.add(data);
       }
   }
    private void init_Event(){
        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (ExpandableListView.getPackedPositionType(l) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    long packedPos = ((ExpandableListView)adapterView).getExpandableListPosition(i);
                    int groupPos = ExpandableListView.getPackedPositionGroup(packedPos);
                    int childPos = ExpandableListView.getPackedPositionChild(packedPos);
                    showDeleteGeDanDialog(childPos);
                    return true;
                }

                return false;
            }

        });
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                //Toast.makeText(MyApplication.getContext(), mGroup.get(groupPosition), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        //设置子项布局监听
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String name = mChild.get(groupPosition).get(childPosition);
                Integer i = resultId.get(childPosition);
                Intent intent = new Intent(getContext(),GeDan_Detail_Activity.class);
                intent.putExtra("title",name);
                intent.putExtra("id",i);
                startActivity(intent);
                return true;
            }
        });
        my_favourite.setOnClickListener(this);
        local_music.setOnClickListener(this);
        recent_play.setOnClickListener(this);
        message.setOnClickListener(this);
    }
    private void register(){
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.suyong.update_text");
        broadcastManager.registerReceiver(mAdDownLoadReceiver, intentFilter);
    }
    @Override
    public void onResume(){
        super.onResume();
        register();
        refresh(false);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fg_my_info, container, false) ;
        App = MyApplication.getInstance();
        initData();
        username1 =(TextView)view.findViewById(R.id.username1);
        expandableListView = (MyExpandableListView)view.findViewById(R.id.expend_list);
        my_favourite = (ImageView)view.findViewById(R.id.my_favourite);
        recent_play = (ImageView)view.findViewById(R.id.recent_play);
        local_music = (ImageView)view.findViewById(R.id.local_music);
        message = (ImageView)view.findViewById(R.id.message);
        adapter1 = new MyExtendableListViewAdapter(mGroup,mChild);
        adapter1.setOnInnerItemOnClickListener(this);
        expandableListView.setAdapter(adapter1);
        init_Event();
        refresh(true);
        return view;
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.local_music :
                //Toast.makeText(MyApplication.getContext(),"本地音乐", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyApplication.getContext(), LocalMusicActivity.class);
                String title = "本地音乐";
                intent.putExtra("title",title);
                startActivity(intent);
                break;
            case R.id.recent_play :
                Intent intent1 = new Intent(MyApplication.getContext(), LocalMusicActivity.class);
                String title1 = "最近播放";
                intent1.putExtra("title",title1);
                startActivity(intent1);
                break;
            case R.id.my_favourite :
                Intent intent2 = new Intent(MyApplication.getContext(), LocalMusicActivity.class);
                String title2 = "我的下载";
                intent2.putExtra("title",title2);
                startActivity(intent2);
                break;
            case R.id.message :
                Intent intent3 = new Intent(MyApplication.getContext(), LocalMusicActivity.class);
                String title3 = "我的消息";
                intent3.putExtra("title",title3);
                startActivity(intent3);
                Toast.makeText(MyApplication.getContext(),"我的消息", Toast.LENGTH_SHORT).show();
                break;

        }
    }
    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                       int pos, long id) {
            /*int groupPos = (Integer)view.getTag(R.id.group); //参数值是在setTag时使用的对应资源id号
            int childPos = (Integer)view.getTag(R.id.item);
            Toast.makeText(MyApplication.getContext(),"groupPos:" + groupPos + ",childPos:" + childPos,Toast.LENGTH_LONG).show();
            if(childPos == -1){//长按的是父项
                //根据groupPos判断你长按的是哪个父项，做相应处理（弹框等）
             // Toast.makeText(MyApplication.getContext(),mGroup.get(groupPos),Toast.LENGTH_SHORT).show();
            } else {
                //根据groupPos及childPos判断你长按的是哪个父项下的哪个子项，然后做相应处理。
             //   Toast.makeText(MyApplication.getContext(),mChild.get(groupPos).get(childPos),Toast.LENGTH_SHORT).show();
            }*/
            if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                long packedPos = ((ExpandableListView)arg0).getExpandableListPosition(pos);
                int groupPos = ExpandableListView.getPackedPositionGroup(packedPos);
                int childPos = ExpandableListView.getPackedPositionChild(packedPos);
                Toast.makeText(MyApplication.getContext(),"groupPos:" + groupPos + ",childPos:" + childPos,Toast.LENGTH_LONG).show();

            }
            return false;
        }
    };
    @Override
    public void onDestroy(){
        super.onDestroy();
        broadcastManager.unregisterReceiver(mAdDownLoadReceiver);
    }
    BroadcastReceiver mAdDownLoadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          handler.sendEmptyMessage(UPDATE_TEXT);
        }
    };

    @Override
    public void itemClick(View v) {
        showAddSongListDialog();
    }
    private void showAddSongListDialog(){
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.add_song_list,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).create();

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
                new_songlist_name = (EditText) view.findViewById(R.id.new_songlist_name);
                String list_name = new_songlist_name.getText().toString();
                if(!"".equals(list_name)) {
                    BmobUtil.add_Ge_dan(App.currentUser.getUsername(), list_name, new OnaddGe_danListener() {
                        @Override
                        public void Onsucess() {
                            refresh(false);
                        }
                    });
                }else{
                    BmobUtil.showToast("名称不为空！");
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(getContext())/4*3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    private void refresh(final boolean flag){
        if(flag) showProgressDialog();
        expandableListView.collapseGroup(0);
        count = 0;
        mChild.clear();
        BmobUtil.queryUser_Ge_dan(App.currentUser.getUsername(), new OnqueryUserGenDanListener() {
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
                                    mChild.add(result);
                                    adapter1.notifyDataSetChanged();
                                    expandableListView.expandGroup(0);
                                    if(flag) closeProgressDialog();
                                }
                            }
                        });
                    }
                } else {
                    BmobUtil.showToast("你还没有歌单哦！");
                    if(flag) closeProgressDialog();
                }

            }
        });
    }
    private void showDeleteGeDanDialog(int pos){
        String name = result.get(pos);
        final Integer id = resultId.get(pos);
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.del_ge_dan,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).create();
        TextView title = (TextView)view.findViewById(R.id.del_text);
        title.setText("确定要删除"+name+"歌单吗？");
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
                    BmobUtil.del_Ge_dan(id, new OnaddGe_danListener() {
                        @Override
                        public void Onsucess() {
                            refresh(false);
                        }
                    });
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(getContext())/4*3), LinearLayout.LayoutParams.WRAP_CONTENT);
    }
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getContext());
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
