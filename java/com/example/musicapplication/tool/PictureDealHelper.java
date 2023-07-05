package com.example.musicapplication.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.VectorDrawable;
import android.media.MediaMetadataRetriever;

import com.example.musicapplication.R;

public class PictureDealHelper {
    /**********获取歌曲专辑图片*************/
    public static Bitmap getAlbumPicture(Context context,String dataPath, int scale_height, int scale_width) {
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(dataPath);//设置路径
        byte[] data = mmr.getEmbeddedPicture();
        Bitmap albumPicture = null;//初始化对象
        if (data != null) {
            //获取bitmap对象
            albumPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
            albumPicture = scale(albumPicture,scale_width,scale_height);//对bitmap进行缩放*/
        } else {
            VectorDrawable album_svg_32 = (VectorDrawable) context.getResources().getDrawable(R.drawable.album_svg_32);
            //获取默认专辑封面
            Bitmap bitmap = Bitmap.createBitmap(album_svg_32.getIntrinsicWidth(), album_svg_32.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);//放入画布
            album_svg_32.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());//设置边界
            album_svg_32.draw(canvas);//显示画布
            albumPicture = scale(bitmap,scale_width,scale_height);//对bitmap进行缩放*/
        }
        return albumPicture;
    }
    //重载
    public static Bitmap getAlbumPicture(String dataPath, int scale_height, int scale_width) {
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(dataPath);//根据图片路径获取图片数据
        byte[] data = mmr.getEmbeddedPicture();//得到字节型数据
        Bitmap albumPicture = null;
        if (data != null) {
            //获取bitmap对象
            albumPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
            //是把网络图片的数据流读入到内存中然后用
            albumPicture = scale(albumPicture,scale_width,scale_height);//对bitmap进行缩放*/
        } else {
            albumPicture = null;
        }
        return albumPicture;
    }
    /**
     * 对bitmap进行缩放*/
    public static Bitmap scale(Bitmap bitmap,int scale_width,int scale_height){
        if(bitmap == null)
            return null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 创建操作图片用的Matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放比例
        float sx = ((float) scale_width / width);
        float sy = ((float) scale_width / height);
        // 设置缩放比例
        matrix.postScale(sx, sy);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }
}
