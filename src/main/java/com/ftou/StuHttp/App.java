package com.ftou.StuHttp;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://www.jb51.net/books/329772.html#download");
        CloseableHttpResponse response1 = null;
        try {
            response1 = httpclient.execute(httpGet);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(response1.getStatusLine());
        HttpEntity entity = response1.getEntity();
        InputStream in = null;
        try {
            in = entity.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader buff = new BufferedReader(new InputStreamReader(in, "utf-8"));
        String line = null;
        while ((line = buff.readLine()) != null) {
            System.out.println(line);
        }

    }
}
