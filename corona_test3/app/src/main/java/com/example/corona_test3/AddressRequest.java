package com.example.corona_test3;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AddressRequest extends StringRequest {

    // 서버 URL 설정 (php 파일 연동)
    final static private String URL = "http://192.168.0.17/receive.php"; // 우리 서버 주소
    private static final Object TAG ="corona_test3" ;
    private Map<String, String> map;

    public AddressRequest(int Count,String Latitude, String Longitude, String Time, Response.Listener<String> listener){ // POST 형식으로 응답 보내기
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();

        map.put("Latitude",Latitude);
        map.put("Longitude",Longitude);
        map.put("Time",Time);
        Log.d((String) TAG,"SUCCESS"+Latitude+Longitude+Time);
        Log.d((String) TAG,"MAP"+map);

        map.put("Count", Count+"");

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
