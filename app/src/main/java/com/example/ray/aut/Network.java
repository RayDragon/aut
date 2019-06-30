package com.example.ray.aut;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Network{
    Context context;
    RequestQueue requestQueue;
    String ip;
    public Network(Context context, String ip){
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        this.ip=ip;
    }
    public void get_req(String addr){
        Request r = new StringRequest(Request.Method.GET,
                "http://" + ip + "/"+addr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        OnResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        OnResponse(error.getMessage());
                    }
                }
        );
        requestQueue.add(r);
    }
    public void OnResponse(String response){

    }
}
