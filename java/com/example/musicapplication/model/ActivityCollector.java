package com.example.musicapplication.model;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;


public class ActivityCollector {
    private static List<Activity> activities = new ArrayList<>();
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }//添加活动
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }//移除活动
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
