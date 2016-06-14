package com.topsports.tootwo2.helper;


import com.loopj.android.http.*;
/**
 * Created by tootwo2 on 16/5/23.
 */
public class UpdateRestClient {
    private static final String BASE_URL = StaticVar.URL_BASE;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
