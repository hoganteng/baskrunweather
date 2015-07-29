package com.baskrun.app.util;

/**
 * http回调接口
 */
public interface HttpCallbackListener {
	void onFinish(String response);

	void onError(Exception e);
}
