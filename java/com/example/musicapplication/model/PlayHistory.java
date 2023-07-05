package com.example.musicapplication.model;

import java.util.ArrayList;
import java.util.List;
//历史播放
public class PlayHistory {
    private static List<Song> songs = new ArrayList<>();
    public static void addSong(Song song) {
        if(songs.contains(song))
            songs.remove(song);
            songs.add(song);
    }
    public static void removeSong(Song song) {
        songs.remove(song);
    }

    public static List<Song> getSongs() {
        return songs;
    }

    public static void removeAll() {
        for (Song song : songs) {
          removeSong(song);
        }
    }
}
