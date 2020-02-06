package com.fb.firebird.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil {
    private final static String TAG = "JsonUtil";

    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        //获得assets资源管理器
        AssetManager assetManager = context.getAssets();
        //使用IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName), StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            Log.d(TAG, "json error", e);
        }
        return stringBuilder.toString();
    }

    public static <T> T JsonToObject(String json, Class<T> clazz) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            Log.d(TAG, "json error", e);
            return null;
        }
    }

    public static <T> T JsonToObject(String json, Type type) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            Log.d(TAG, "json error", e);
            return null;
        }
    }

    public static <T> T JsonFileToObject(Context context, String fileName, Class<T> clazz) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(getJson(context, fileName), clazz);
        } catch (Exception e) {
            Log.d(TAG, "json error", e);
            return null;
        }
    }

    public static <T> T JsonFileToObject(Context context, String fileName, Type type) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(getJson(context, fileName), type);
        } catch (Exception e) {
            Log.d(TAG, "json error", e);
            return null;
        }
    }

    public static String toJSONString(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static Map<String, Object> objToMap(Object obj) {
        if (obj == null) {
            return Collections.emptyMap();
        }

        try {
            Map<String, Object> map = new HashMap<>();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
            return map;
        } catch (Exception e) {
            Log.e(TAG, "objToMap error", e);
            return Collections.emptyMap();
        }
    }

    public static Object map2Obj(Map<String, Object> map, Class<?> clz) {
        try {
            Object obj = clz.newInstance();
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }
            return obj;
        } catch (Exception e) {
            Log.e(TAG, "map2Obj error", e);
            return null;
        }
    }
}