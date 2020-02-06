package com.fb.firebird.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpUtil {
    private static final String TAG = HttpUtil.class.getSimpleName();
    private static final String ENCODE = "UTF-8";
    private static int POOL_SIZE = 4;
    private static int READ_TIME_OUT = 5 * 1000;
    private static int CONNECT_TIME_OUT = 5 * 1000;
    private static ExecutorService sExecutorService;
    private static HttpUtil util;

    static {
        sExecutorService = Executors.newFixedThreadPool(POOL_SIZE);
        util = new HttpUtil();
    }

    public static HttpUtil getUtil() {
        return util;
    }

    public void httpGet(String url, final HttpCallback<String> callback) {
        Log.d(TAG, "HTTP GET: " + url);
        sExecutorService.execute(new HttpRunnable(url, "GET", null, callback));
    }

    public void httpPost(String url, byte[] data, final HttpCallback<String> callback) {
        Log.d(TAG, "HTTP POST: " + url + "?" + new String(data));

        sExecutorService.execute(new HttpRunnable(url, "POST", data, callback));
    }

    private class HttpRunnable implements Runnable {
        private String url;
        private String type;
        private byte[] data;
        private HttpCallback<String> callback;

        public HttpRunnable() {

        }

        public HttpRunnable(String url, String type, byte[] data, HttpCallback callback) {
            this.url = url;
            this.type = type;
            this.data = data;
            this.callback = callback;
        }

        @Override
        public void run() {
            URL url1 = null;
            try {
                url1 = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                callback.onError(e.getMessage());
                return;
            }
            BufferedReader bufferedReader = null;
            StringBuffer response = new StringBuffer();
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url1.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setReadTimeout(READ_TIME_OUT);
                urlConnection.setConnectTimeout(CONNECT_TIME_OUT);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("connection", "close");
                if ("GET".equals(type)) {
                    urlConnection.setRequestMethod("GET");
                } else if ("POST".equals(type)) {
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setInstanceFollowRedirects(false);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty("charset", "utf-8");
                    urlConnection.setRequestProperty("Content-Length", Integer.toString(data.length));
                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                    wr.write(data);
                }

                urlConnection.connect();
                int code = urlConnection.getResponseCode();
                if (code == 200) {
                    bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), ENCODE));
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }
                    callback.onSuccess(response.toString());
                } else {
                    callback.onError(String.valueOf(code));
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                callback.onError(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                callback.onError(e.getMessage());
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
