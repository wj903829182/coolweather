package com.coolweather.app.util;

public interface HttpCallbackListenter {

	void onFinish(String response);
	
	void onError(Exception e);
}
