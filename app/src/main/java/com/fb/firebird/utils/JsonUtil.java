package com.fb.firebird.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public class JsonUtil {

    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        //获得assets资源管理器
        AssetManager assetManager = context.getAssets();
        //使用IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName), "utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static <T> T JsonToObject(String json, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }

    public static <T> T JsonToObject(String json, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    public static <T> T JsonFileToObject(Context context, String fileName, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(getJson(context, fileName), clazz);
    }

    public static <T> T JsonFileToObject(Context context, String fileName, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(getJson(context, fileName), type);
    }

    public static String toJSONString(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static Map<String, Object> toMap(Object obj){
        if(obj == null){
            return Collections.emptyMap();
        }
        Gson gson = new Gson();

        return gson.fromJson(gson.toJson(obj), Map.class);
    }
}