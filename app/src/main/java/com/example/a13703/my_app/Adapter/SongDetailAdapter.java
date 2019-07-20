package com.example.a13703.my_app.Adapter;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a13703.my_app.R;
import com.example.a13703.my_app.bean.Local_music;
import com.example.a13703.my_app.util.StringAndBitmap;

import java.util.List;

/**
 * Created by 13703 on 2019/7/20.
 */

public class SongDetailAdapter extends RecyclerView.Adapter<SongDetailAdapter.ViewHolder>{
    private List<Local_music> list;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView song;
        TextView singer;
        ImageView option;
        public ViewHolder(View view){
            super(view);
            image = (ImageView) view.findViewById(R.id.song_image);
            song = (TextView)view.findViewById(R.id.song_name);
            singer = (TextView)view.findViewById(R.id.song_detail);
            option = (ImageView)view.findViewById(R.id.song_option);
        }
    }

    public SongDetailAdapter(List<Local_music> songlist){
        list = songlist;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item1,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Local_music music = list.get(position);
        holder.image.setImageBitmap(StringAndBitmap.stringToBitmap(music.getBitm()));
        holder.song.setText(music.getSong());
        holder.singer.setText(music.getSinger());
        holder.option.setImageResource(R.drawable.option);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
