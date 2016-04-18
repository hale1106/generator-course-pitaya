/* Copyright(c)2010-2014 WUDAOSOFT.COM
 * Email:changsoul.wu@gmail.com
 * QQ:275100589
 */ 
 
package com.flyfinger.wx.utils;

import java.io.IOException;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

/**
 * <p> </p>
 * @author Changsoul.Wu
 * @date 2014-3-29 下午9:07:23
 */
public class StringResponseHandler implements ResponseHandler<String> {

	public String handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity, Consts.UTF_8) : null;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
	}

}
