package com.example.musicapplication.tool;

import android.text.TextUtils;

import com.example.musicapplication.gson.NetSongs;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utility {
    //解析和处理从服务器返回的省市县数据
    public static List<NetSongs> handleNetSongsResponse(String response) {
        List<NetSongs> songsList =new ArrayList<NetSongs>();
        try {

            JSONObject jsonObject = new JSONObject(response);
            //JSONObject只是一种数据结构，可以理解为JSON格式的数据结构（key-value 结构）
            JSONObject jsonObject1 = jsonObject.getJSONObject("result");
            //json数组，使用中括号[ ],只不过数组里面的项也是json键值对格式的
            JSONArray jsonArray1=jsonObject1.getJSONArray("songs");
            for(int i = 0; i < jsonArray1.length(); i++) {
                JSONObject x = jsonArray1.getJSONObject(i);//循环获取
                JSONArray jsonArray2 = x.getJSONArray("artists");//剥离出作者信息
                String duration=x.getString("duration");//获取歌曲时间
                JSONObject jsonObject2=jsonArray2.getJSONObject(0);//获取第一项
                String jsonObject3=jsonObject2.getString("name");//获取歌手名
                String NetSongsContent = x.toString();//获取json字符串*/
                NetSongs netSongs=new Gson().fromJson(NetSongsContent, NetSongs.class);
                String artists_name=jsonObject3;
                netSongs.setArtists_name(artists_name);//设置歌手名
                netSongs.setDuration(duration);
                songsList.add(netSongs);
                //Gson提供了fromJson()方法来实现从Json相关对象到Java实体的方法
            }}catch (Exception e) {
            e.printStackTrace();
        }
        return songsList;
    }
    public static String handleNetSongsUrlResponse(String response) {
       String songsurl = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            //JSONObject只是一种数据结构，可以理解为JSON格式的数据结构（key-value 结构）
            JSONArray jsonObject1 = jsonObject.getJSONArray("data");
            //json数组，使用中括号[ ],只不过数组里面的项也是json键值对格式的
            JSONObject getJsonObj = jsonObject1.getJSONObject(0);//获取json数组中的第一项
            //JSONObject jsonObject2=getJsonObj.getJSONArray("songs");
            songsurl= getJsonObj.getString("url");//获取json字符串*/

        }catch (Exception e) {
            e.printStackTrace();
        }
        return songsurl;
    }

}
