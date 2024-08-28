package com.example.carati;

public interface ResponseCallback {
    //in order to get the response for the  gemini ai
    void onResponse(String response);
    void onError(Throwable throwable);

}
