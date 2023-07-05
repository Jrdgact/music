package com.example.musicapplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.musicapplication.R;
import com.example.musicapplication.gson.NetSongs;
import com.example.musicapplication.model.BaseActivity;
import com.example.musicapplication.model.SongsCollector;
import com.example.musicapplication.service.DownMusicService;
import com.example.musicapplication.service.MusicService;
import com.example.musicapplication.tool.HttpUtil;
import com.example.musicapplication.tool.NetSongAdapter;

import com.example.musicapplication.tool.SongAdapter;
import com.example.musicapplication.tool.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class SearchNetActivity extends BaseActivity {
    public List<NetSongs> search_netsong_list = new ArrayList<>();//用来装网络查询结果
    private String actual_name;//获取当前点击歌曲名字
    private String actual_actname;//获取当前歌手名
    private String actual_duration;//当前歌曲时长
    private LinearLayout search_LinearLayout;//搜索结果的整个布局
    private String actual_number;//在search_view里面点击的歌曲在display_activity的list里面的位置
    private NetSongAdapter adapter_search;//网络歌曲列表的适配器
    private ListView listView_search ;//歌曲显示列表
    private String actual_songurl;//下载路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_netsong);
        /*解决软键盘弹起时，底部控件被顶上去的问题*/
        Toolbar toolbar = findViewById(R.id.toolbar_activity_netsong_display);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_back = new Intent(SearchNetActivity.this, DisplayActivity.class);
                startActivity(intent_back);
            }
        });
        search_LinearLayout = findViewById(R.id.search_netsong_LinearLayout);
        listView_search = findViewById(R.id.list_search_netsong);
        /***设置search_list歌曲item点击事件  */
        listView_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //列表点击监听
                actual_number =search_netsong_list.get(position).song_id;
                //通过获取点击列表位置获取歌曲id，以便通过api中的url获取歌曲mp3路径
                actual_name=search_netsong_list.get(position).song_name;
                //获取点击位置的歌名
                actual_actname=search_netsong_list.get(position).artists_name;
                //获取点击位置的歌手名
                actual_duration=search_netsong_list.get(position).duration;
                showDialog(actual_name,position);//提示框

            }
        });

        ImageView close_search = findViewById(R.id.image_netclose_search);//x 按钮 关闭搜索结果
        close_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_LinearLayout.setVisibility(View.GONE);
                search_netsong_list.clear();//清除搜索结果
            }
        });

    }

    /***********toolbar的menu***********/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_detail_activity, menu);
        MenuItem search_item = menu.getItem(0);
        SearchView searchView = (SearchView) search_item.getActionView();
        searchView.onActionViewExpanded();//展开模式
        //搜索框提示文字
        searchView.setQueryHint("搜索网络歌曲");
        //SearchView搜索框设置
        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setTextColor(this.getResources().getColor(R.color.white_color));
        searchAutoComplete.setHintTextColor(this.getResources().getColor(R.color.white_color));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                if(!TextUtils.isEmpty(query)){//注意判空
                    search_netsong_list.clear();//清空结果
                    //adapter_search.notifyDataSetChanged();//可以在修改适配器绑定的数组后，不用重新刷新Activity
                    listView_search.deferNotifyDataSetChanged();
                    requestnetsong(query);
                }
                return false;
            }
        });
        return true;
    }

    //menu点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                break;
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏操作
            Log.w("SearchDetailActivity","竖屏");
        }
        else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏操作
            Log.w("SearchDetailActivity","横屏");
        }
    }
    /*询问歌曲信息*/
    public void requestnetsong(final String adCode){
        //请求获取歌曲信息
        String netUrl = "http://api.we-chat.cn/search?keywords=" + adCode;
        HttpUtil.sendOkHttpRequest(netUrl, new Callback() {
            public void onFailure(Call call, IOException e) {
                //获取歌曲信息失败，抛出异常
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SearchNetActivity.this, "获取网络歌曲失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            public void onResponse(Call call, Response response) throws IOException {
                //响应
                final String responseText = response.body().string();
                //获取网页响应的主体
                search_netsong_list = Utility.handleNetSongsResponse(responseText);//使用gson解析api的内容
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (search_netsong_list != null) {
                            show(search_netsong_list);//显示获取结果
                            Toast.makeText(SearchNetActivity.this, "获取网络歌曲成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SearchNetActivity.this, "获取网络歌曲失败,请重新输入！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        });
    }
    public void show(List<NetSongs> netSongs){
        adapter_search = new NetSongAdapter(SearchNetActivity.this, R.layout.netsong_list_item, netSongs);
        adapter_search.notifyDataSetChanged();
        listView_search.setAdapter(adapter_search);//设置适配器
        search_LinearLayout.setVisibility(View.VISIBLE);//显示搜素结果
    }
    void showDialog(String actual_name,final int position){
        final androidx.appcompat.app.AlertDialog.Builder dialog =
                new AlertDialog.Builder(SearchNetActivity.this);
        dialog.setTitle("提示");
        //提示符
        dialog.setMessage("确定要下载"+actual_actname+"的"+actual_name+"吗？");//提示信息显示
        dialog.setPositiveButton("下载",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestnetsongurl(actual_number);//启动
                    }
                });
        dialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog.show();
    }
    public void requestnetsongurl(final String adCode){
        //请求获取歌曲信息
        String netUrl = "http://api.we-chat.cn/song/url?id=" + adCode;
        HttpUtil.sendOkHttpRequest(netUrl, new Callback() {
            public void onFailure(Call call, IOException e) {
                //获取歌曲信息失败，抛出异常
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SearchNetActivity.this, "获取网络歌曲失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            public void onResponse(Call call, Response response) throws IOException {
                //响应
                final String responseText = response.body().string();
                //获取网页响应的主体
                actual_songurl= Utility.handleNetSongsUrlResponse(responseText);//使用gson解析api的内容
                Intent intent = new Intent(SearchNetActivity.this, DownMusicService.class);
                intent.putExtra("path", actual_songurl);//歌曲下载路径
                intent.putExtra("songname",actual_name);//歌名
                intent.putExtra("actname",actual_actname);//歌手名
                intent.putExtra("duration",actual_duration);//歌曲时长
                startService(intent);
            }

        });
    }

}
