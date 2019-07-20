package com.example.a13703.my_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a13703.my_app.Inter_kou.InnerItemOnclickListener;
import com.example.a13703.my_app.MyApplication;
import com.example.a13703.my_app.R;
import com.example.a13703.my_app.bean.Local_music;
import com.example.a13703.my_app.util.MusicUtils;
import com.example.a13703.my_app.util.StringAndBitmap;

import java.util.List;

/**
 * Created by 13703 on 2019/6/20.
 */

public class LocalMusicAdapter extends ArrayAdapter<Local_music> implements View.OnClickListener{
    private int resourceId;
    private Context mContext;
    private InnerItemOnclickListener mListener;
    public LocalMusicAdapter(Context context,int textViewResourceId,List<Local_music> objects) {
        super(context,textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Local_music song = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.song_image = (ImageView)view.findViewById(R.id.song_image);
            viewHolder.song_name = (TextView)view.findViewById(R.id.song_name);
            viewHolder.song_detail = (TextView)view.findViewById(R.id.song_detail);
            viewHolder.option = (ImageView)view.findViewById(R.id.song_option);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.song_image.setImageBitmap(StringAndBitmap.stringToBitmap(song.getBitm()));
        viewHolder.song_name.setText(song.getSong());
        viewHolder.song_detail.setText(song.getSinger());
        viewHolder.option.setTag(position);
        viewHolder.option.setOnClickListener(this);
        return view;
    }
    public void setOnInnerItemOnClickListener(InnerItemOnclickListener listener){
        this.mListener=listener;
    }
    @Override
    public void onClick(View view) {
        mListener.itemClick(view);
    }

    class ViewHolder{
        ImageView song_image;
        TextView song_name;
        TextView song_detail;
        ImageView option;
    }

}
