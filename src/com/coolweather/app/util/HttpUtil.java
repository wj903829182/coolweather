package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	public static void sendHttpRequest(final String address,
			final HttpCallbackListenter listener){
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection=null;
				LogUtil.d("debug", "HttpUtil20");
				try{
					LogUtil.d("debug", "HttpUtil22");
					URL url=new URL(address);
					LogUtil.d("debug", "HttpUtil24");
					connection=(HttpURLConnection) url.openConnection();
					LogUtil.d("debug", "HttpUtil26");
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					LogUtil.d("debug", "HttpUtil30");
					InputStream in=connection.getInputStream();
					LogUtil.d("debug", "HttpUtil32");
					BufferedReader reader=new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response=new StringBuilder();
					String line;
					while((line=reader.readLine())!=null){
						response.append(line);
					}
					LogUtil.d("debug", response.toString());
					if(listener!=null){
						//回调onFinish()方法
						listener.onFinish(response.toString());
					}
					
				}catch(Exception e){
					if(listener!=null){
						//回调onError()方法
						listener.onError(e);
						LogUtil.d("debug", "HttpUtil50");
						LogUtil.d("debug", e.toString()+"51");
					}
				}finally{
					if(connection!=null){
						connection.disconnect();
					}
				}
			}
			
		}).start();
	}
}
