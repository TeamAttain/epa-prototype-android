package com.smashingboxes.epa_prototype_android.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.smashingboxes.epa_prototype_android.network.parsing.StringParseStrategy;

import java.io.UnsupportedEncodingException;
import java.util.Map;


public class BaseRequest<T> extends Request<T> {

    private static final String TAG = BaseRequest.class.getName();

    public interface ResponseParseStrategy<T> {
        T parseResponse(String rawResponse) throws VolleyError;
    }

    public static final ResponseParseStrategy<String> NO_PARSE_STRAT = new StringParseStrategy();

    private ResponseParseStrategy<T> mParseStrategy;
    private Listener<T> listener;
    private Map<String, String> params;
    private Map<String, String> headers;

    public BaseRequest(int method, String url, Listener<T> mListener, ErrorListener errorListener,
                       ResponseParseStrategy<T> mParseStrategy) {
        super(method, url, errorListener);
        this.listener = mListener;
        this.mParseStrategy = mParseStrategy;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String toParse;
        try {
            toParse = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            toParse = new String(response.data);
        }

        try {
            T mResponse = mParseStrategy.parseResponse(toParse);
            return Response.success(mResponse, parseCacheEntry(response));
        } catch (VolleyError parseError) {
            return Response.error(parseError);
        }

    }

    protected Cache.Entry parseCacheEntry(NetworkResponse response) {
        return HttpHeaderParser.parseCacheHeaders(response);
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    public void setRequestParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    protected Map<String, String> getPostParams() throws AuthFailureError {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers == null ? super.getHeaders() : headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

}
