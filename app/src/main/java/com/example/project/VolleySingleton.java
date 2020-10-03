package com.example.project;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton intanciaVolley;
    private RequestQueue requestQueue;
    private static Context contexto;

    private VolleySingleton(Context context) {
        contexto = context;
        requestQueue = getRequestQueue();
    }


    public static synchronized VolleySingleton getIntanciaVolley(Context context) {
        if (intanciaVolley == null) {
            intanciaVolley = new VolleySingleton(context);
        }

        return intanciaVolley;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(contexto.getApplicationContext());
        }

        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> requestQueue) {
        getRequestQueue().add(requestQueue);
    }

}