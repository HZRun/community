package com.gdufs.demo.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class HttpUtil {

    private static final String Charset = "utf-8";


    /**
     * 发送请求，如果失败，会返回null
     *
     * @param url
     * @param map
     * @return
     */
    public static String post(String url, Map<String, String> map) {
        // 处理请求地址
        try {
            HttpClient client = HttpClientBuilder.create().build();
            URI uri = new URI(url);
            HttpPost post = new HttpPost(uri);

            // 添加参数
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (String str : map.keySet()) {
                params.add(new BasicNameValuePair(str, map.get(str)));
            }
            post.setEntity(new UrlEncodedFormEntity(params, Charset));
            // 执行请求
            HttpResponse response = client.execute(post);

            if (response.getStatusLine().getStatusCode() == 200) {
                // 处理请求结果
                StringBuffer buffer = new StringBuffer();
                InputStream in = null;
                try {
                    in = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 关闭流
                    if (in != null)
                        try {
                            in.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }

                return buffer.toString();
            } else {
                return null;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return null;

    }

    /**
     * 发送请求，如果失败会返回null
     *
     * @param url
     * @param str
     * @return
     */
    public static String post(String url, String str) {
        // 处理请求地址
        try {
            HttpClient client = HttpClientBuilder.create().build();
            URI uri = new URI(url);
            HttpPost post = new HttpPost(uri);
            post.setEntity(new StringEntity(str, Charset));
            // 执行请求
            HttpResponse response = client.execute(post);

            if (response.getStatusLine().getStatusCode() == 200) {
                // 处理请求结果
                StringBuffer buffer = new StringBuffer();
                InputStream in = null;
                try {
                    in = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                } finally {
                    // 关闭流
                    if (in != null)
                        in.close();
                }

                return buffer.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 发送GET方式的请求，并返回结果字符串。
     * <br>
     * 时间：2017年2月27日，作者：http://wallimn.iteye.com
     *
     * @param url
     * @return 如果失败，返回为null
     */
    public static String get(String url) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            URI uri = new URI(url);
            HttpGet get = new HttpGet(uri);
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                StringBuffer buffer = new StringBuffer();
                InputStream in = null;
                try {
                    in = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                } finally {
                    if (in != null)
                        in.close();
                }

                return buffer.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}