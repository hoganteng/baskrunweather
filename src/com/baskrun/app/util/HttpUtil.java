package com.baskrun.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {
	/**
	 * 发送请求	--不带参数的请求
	 * @param address	请求的地址
	 * @param listener	回调方法
	 */
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection=null;
				URL url;
				try {
					url = new URL(address);
					connection=(HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in=connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(in));
					StringBuilder response=new StringBuilder();
					String line;
					while ((line=reader.readLine())!=null) {
						response.append(line);
					}
					if (listener!=null) {
						//回调onFinish()方法
						listener.onFinish(response.toString());
					}
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
					if (listener!=null) {
						listener.onError(e);
					}
				}finally{
					if (connection!=null) {
						connection.disconnect();	//断开连接
					}
				}
			}
		}).start();
	}
}
