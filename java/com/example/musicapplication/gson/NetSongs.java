package com.example.musicapplication.gson;
import com.example.musicapplication.model.Song;
import com.google.gson.annotations.SerializedName;
public class NetSongs {
    @SerializedName("id")
    public String song_id;//歌曲id

    @SerializedName("name")
    public String song_name;//歌名

    public String artists_name;//歌手
    public String duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArtists_name() {
        return artists_name;
    }

    public void setArtists_name(String artists_name) {
        this.artists_name = artists_name;
    }
}
