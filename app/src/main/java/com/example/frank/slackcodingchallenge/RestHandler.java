package com.example.frank.slackcodingchallenge;

/**
 * Created by Frank on 4/12/2016.
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

public class RestHandler {

    public final static int GET = 0;
    public final static int POST = 1;

    public RestHandler(){

    }

    /***
     * Makes a HTTP call
     * @param url - String representation of the url of http call
     * @param method - integer representing whether the call is a GET,POST,etc.
     * @param params - additional parameters for the call
     * @return - a String representation of the http response
     */
    public String makeCall(String url, int method, List<NameValuePair> params){
        String response = null;
        try{
            // http Client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            if(method == GET){
                // Append params to API call
                if(params != null){
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                }

                HttpGet httpGet = new HttpGet(url);
                httpResponse = httpClient.execute(httpGet);
            }
            if(method == POST){
                //TODO - can be built for future revs
            }

            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

}
