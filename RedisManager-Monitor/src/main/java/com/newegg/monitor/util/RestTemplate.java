package com.newegg.monitor.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

public class RestTemplate {
	/**
     * 向指定 URL 发送POST请求,不接收返回值
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @throws IOException
     */
    public static void send(String url, Map<String,String> params) throws IOException{
        PrintWriter out = null;
        try {
            URL realUrl = new URL(url);
            HttpURLConnection httpConn = (HttpURLConnection) realUrl.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setUseCaches(false);
            httpConn.setRequestMethod("POST");
            out = new PrintWriter(httpConn.getOutputStream());
            StringBuffer param = null;
            for (Entry<String, String> entry : params.entrySet()) {
            	if(param != null){
            		param = param.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            	}else{
            		param = new StringBuffer().append(entry.getKey()).append("=").append(entry.getValue());
            	}
			}
            out.print(param.toString());
            out.flush();
            int responseCode = httpConn.getResponseCode();
            System.out.println(responseCode);
        }finally{
        	if(out!=null){ out.close(); }
        }
    }
}
