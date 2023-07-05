package com.example.musicapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.example.musicapplication.model.Song;
import com.example.musicapplication.tool.PictureDealHelper;

public class MyDbFunctions {
    //数据库名
    public static final String DB_NAME = "Mung_Music";
    //数据库版本
    public static final int VERSION = 1;
    private volatile static MyDbFunctions myDbFunctions;
    private SQLiteDatabase db;
    private WeakReference<Context> weakReference;//弱引用方式引入context
    //私有化构造方法,单例模式
    private MyDbFunctions(Context context){
        weakReference = new WeakReference<>(context);
        db = new MyDbHelper(weakReference.get(),DB_NAME,null,VERSION).getWritableDatabase();
    }
    /*双重锁模式*/
    public static MyDbFunctions getInstance(Context context){
        if(myDbFunctions == null){//为了避免不必要的同步
            synchronized (MyDbFunctions.class){
                if(myDbFunctions ==null){//为了在实例为空时才创建实例
                    myDbFunctions = new MyDbFunctions(context);
                }
            }
        }
        return myDbFunctions;
    }
    /**
     * 将Song实例存储到数据库的SONGS表中*/
    public void saveSong(Song song){
        if(song != null && db != null){
            ContentValues values = new ContentValues();
            values.put("title",song.getTitle());//歌名
            values.put("artist",song.getArtist());//歌手
            values.put("duration",song.getDuration());//歌曲时长
            values.put("dataPath",song.getDataPath());//歌曲路径
            if(song.isLove())
                values.put("isLove","true");//是否是习惯歌曲
            else
                values.put("isLove","false");
            if(song.isDefaultAlbumIcon())//是否使用的默认专辑图片
                values.put("isDefaultAlbumIcon","true");
            else
                values.put("isDefaultAlbumIcon","false");
            db.insert("SONGS",null,values);
        }
    }
    /**
     * 将Song实例从数据库的MyLoveSongs表中删除*/
    public void removeSong(String dataPath){
        if(dataPath != null && db != null){
            //db.execSQL("delete from lxrData where name=?", new String[] { name });
            db.delete("SONGS","dataPath=?",new String[]{dataPath});//根据路径移除歌曲
        }
    }
    /**
     * 给SONGS表中的某个歌曲修改isLove标志*/
    public void setLove(String dataPath,String flag){
        ContentValues values = new ContentValues();
        values.put("isLove",flag);
        db.update("SONGS",values,"dataPath=?",new String[]{dataPath});
    }
    /**
     * 从数据库读取SONGS表中所有的我喜爱的歌曲*/
    public ArrayList<Song> loadMyLoveSongs(){
        ArrayList<Song> list = new ArrayList<>();
        if(db != null){
            Cursor cursor = db.query("SONGS",null,"isLove =?",new String[]{"true"},null,null,null);
            //查询所有喜欢标志为true的歌曲进行遍历
            if(cursor.moveToFirst()){
                do{
                    Song song = new Song();
                    song.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    song.setArtist(cursor.getString(cursor.getColumnIndex("artist")));
                    song.setDuration(cursor.getLong(cursor.getColumnIndex("duration")));
                    song.setDataPath(cursor.getString(cursor.getColumnIndex("dataPath")));
                    song.setLove(true);
                    String flag2 = cursor.getString(cursor.getColumnIndex("isDefaultAlbumIcon"));
                    if(flag2.equals("true"))
                        song.setFlagDefaultAlbumIcon(true);
                    else
                        song.setFlagDefaultAlbumIcon(false);
                    song.setAlbum_icon(PictureDealHelper.getAlbumPicture(weakReference.get(),song.getDataPath(),96,96));
                    list.add(song);//添加进列表，便于显示
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }
    /**
     * 读取数据库中的所有歌曲*/
    public ArrayList<Song> loadAllSongs(){
        ArrayList<Song> list = new ArrayList<>();
        if(db != null){
            Cursor cursor = db.query("SONGS",null,null,null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    Song song = new Song();
                    song.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    song.setArtist(cursor.getString(cursor.getColumnIndex("artist")));
                    song.setDuration(cursor.getLong(cursor.getColumnIndex("duration")));
                    song.setDataPath(cursor.getString(cursor.getColumnIndex("dataPath")));
                    String flag1 = cursor.getString(cursor.getColumnIndex("isLove"));
                    if(flag1.equals("true"))
                        song.setLove(true);//是喜爱的歌曲
                    else
                        song.setLove(false);//不是喜爱的歌曲
                    String flag2 = cursor.getString(cursor.getColumnIndex("isDefaultAlbumIcon"));
                    if(flag2.equals("true")){
                        song.setFlagDefaultAlbumIcon(true);//设置默认专辑图像
                    }
                    else{
                        song.setFlagDefaultAlbumIcon(false);
                    }
                    //设置专辑封面
                    song.setAlbum_icon(PictureDealHelper.getAlbumPicture(weakReference.get(),song.getDataPath(),96,96));
                    list.add(song);//添加进列表
                }while(cursor.moveToNext());
            }
            cursor.close();//游标关闭
        }
        return list;
    }

    /**
     * 判断当前SONGS表中是否有数据*/
    public boolean isSONGS_Null(){
        if(db != null){
            Cursor cursor = db.query("SONGS",null,null,null,null,null,null);
            if(cursor.moveToFirst()){
                return false;//不为空
            }
            cursor.close();//游标关闭
        }
        return true;//空
    }
}
