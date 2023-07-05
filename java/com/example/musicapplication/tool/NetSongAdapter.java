package com.example.musicapplication.tool;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicapplication.R;
import com.example.musicapplication.gson.NetSongs;


import java.util.List;

public class NetSongAdapter  extends ArrayAdapter<NetSongs> {
    private int resourceId;//用来放置布局文件的id
    private Context context;

    //适配器的构造函数
    public NetSongAdapter(Context context, int resourceId, List<NetSongs> objects) {
        super(context, resourceId, objects);
        this.context = context;
        this.resourceId = resourceId;
    }

    static class ViewHolder {
        //ImageView songImage;//歌曲图像
        TextView songName;//歌名
        TextView songAuthor;//歌手
        ImageView more_options;//
    }

    //这个方法在每个子项被滚动到屏幕内的时候会被调用
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        NetSongs netsong = getItem(position); // 获取当前项的Song实例
        View view;//子项布局对象
        NetSongAdapter.ViewHolder viewHolder;//内部类对象
        if (convertView == null) {//如果是第一次加载
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);//布局对象化
            viewHolder = new NetSongAdapter.ViewHolder();
            //把布局文件里面的4个对象加载出来
           // viewHolder.songImage = view.findViewById(R.id.netsong_image);//歌曲封面
            viewHolder.songName = view.findViewById (R.id.net_title);//歌名
            viewHolder.songAuthor=view.findViewById(R.id.net_artist);//歌手
            viewHolder.more_options = view.findViewById(R.id.net_more_options);//更多操作
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {//不是第一次加载，即布局文件已经加载，可以利用
            view = convertView;
            viewHolder = (NetSongAdapter.ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        if(netsong!=null && viewHolder!=null){
            //传入具体信息
           // viewHolder.songImage.setImageBitmap();//列表每一项的图标
            viewHolder.songName.setText(netsong.song_name);//歌名
            viewHolder.songAuthor.setText(netsong.getArtists_name());//歌手
            //设置两个文本的字体style
            viewHolder.songName.setTypeface(Typeface.DEFAULT_BOLD);
            viewHolder.songAuthor.setTypeface(Typeface.DEFAULT_BOLD);
            //设定更多选项按钮的点击事件
            viewHolder.more_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(m != null){
                        m.onMoreOptionsClick(position);
                    }
                }
            });
        }
        return view;
    }
    /*
     * 经典接口回调
     * 外部调用setOnItemMoreOptionsClickListener时势必会传入onItemMoreOptionsListener的实例
     * 所以其中的抽象方法onMoreOptionsClick也会要求重写
     * */
    public interface onItemMoreOptionsListener {
        void onMoreOptionsClick(int position);
    }

    private onItemMoreOptionsListener m;

    public void setOnItemMoreOptionsClickListener(onItemMoreOptionsListener m) {
        this.m = m;
    }
}
