package com.http.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.http.Const.HttpConst;
import com.http.Helper.HttpRespons;
 
/**
 * HTTP请求对象
 * 
 * @author YYmmiinngg
 */
public class MyHttpRequester {
	public static final int cache = 10 * 1024;//设置缓存区大小
	private String defaultContentEncoding=null;
	private String contentEncoding=null;//文本内容编码方式
	private Charset ecod=null;
	private String contentType=null;//http返回的文本的类型 
	private HttpRespons httpResponser=null;
	private InputStream inputStream=null;//获取输入流用于返回
	private HashMap<String, String> default_headers=null;//用于存储在默认情况下的请求头信息
	
	public MyHttpRequester()
	{
		defaultContentEncoding = Charset.defaultCharset().name();
		default_headers=new HashMap<String, String>(HttpConst.GetHttpConst().getDefault_headers());
	}
 
	/**
	 * 发送GET请求
	 * 
	 * @param urlString
	 *            URL地址
	 * @return HttpResponsHttpRespons响应对象
	 * @throws IOException
	 */
	public HttpRespons sendGet(String urlString) throws IOException
	{
		return send(urlString, "GET", null, null);
	}
 
	/**
	 * 发送GET请求
	 * 
	 * @param urlString
	 *            URL地址
	 * @param params
	 *            参数集合
	 * @return HttpRespons响应对象
	 * @throws IOException
	 */
	public HttpRespons sendGet(String urlString, Map<String, String> params) throws IOException
	{
		return send(urlString, "GET", params, null);
	}
 
	/**
	 * 发送GET请求
	 * 
	 * @param urlString
	 *            URL地址
	 * @param params
	 *            参数集合
	 * @param propertys
	 *            请求属性
	 * @return HttpRespons响应对象
	 * @throws IOException
	 */
	public HttpRespons sendGet(String urlString, Map<String, String> params, Map<String, String> propertys) throws IOException
	{
		return send(urlString, "GET", params, propertys);
	}
 
	/**
	 * 发送POST请求
	 * 
	 * @param urlString
	 *            URL地址
	 * @return HttpRespons响应对象
	 * @throws IOException
	 */
	public HttpRespons sendPost(String urlString) throws IOException
	{
		return send(urlString, "POST", null, null);
	}
 
	/**
	 * 发送POST请求
	 * 
	 * @param urlString
	 *            URL地址
	 * @param params
	 *            参数集合
	 * @return HttpRespons响应对象
	 * @throws IOException
	 */
	public HttpRespons sendPost(String urlString, Map<String, String> params) throws IOException
	{
		return send(urlString, "POST", params, null);
	}
 
	/**
	 * 发送POST请求
	 * 
	 * @param urlString
	 *            URL地址
	 * @param params
	 *            参数集合
	 * @param propertys
	 *            请求属性
	 * @return HttpRespons响应对象
	 * @throws IOException
	 */
	public HttpRespons sendPost(String urlString, Map<String, String> params, Map<String, String> propertys) throws IOException
	{
		return this.send(urlString, "POST", params, propertys);
	}
 
	/**
	 * 发送HTTP请求
	 * 
	 * @param urlString
	 * @return HttpRespons响应对象
	 * @throws IOException
	 */
	private HttpRespons send(String urlString, String method, Map<String, String> parameters, Map<String, String> propertys) throws IOException
	{
		HttpURLConnection urlConnection = null;
		HashMap<String, String> new_Headers=new HashMap<String, String>(default_headers);
 
		if (method.equalsIgnoreCase("GET") && parameters != null) {
			StringBuffer param = new StringBuffer();
			int i = 0;
			for (String key : parameters.keySet()) {
				if (i == 0)
					param.append("?");
				else
					param.append("&");
				param.append(key).append("=").append(parameters.get(key));
				i++;
			}
			urlString += param;
		}
		URL url = new URL(urlString);
		urlConnection = (HttpURLConnection) url.openConnection();
 
		urlConnection.setRequestMethod(method);
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setUseCaches(false);
 
		if (propertys != null)
		{
			for (String key : propertys.keySet())
				new_Headers.put(key, propertys.get(key));
		}
		for (String key : new_Headers.keySet()) {
			if(key.contains("Cookie"))
			{
				urlConnection.addRequestProperty("Cookie", new_Headers.get(key));
			}else
			{
				urlConnection.addRequestProperty(key, new_Headers.get(key));
			}
		}
		urlConnection.connect();
		if (method.equalsIgnoreCase("POST") && parameters != null) {
			StringBuffer param = new StringBuffer();
			for (String key : parameters.keySet()) {
				param.append("&");
				param.append(key).append("=").append(parameters.get(key));
			}
			urlConnection.getOutputStream().write(param.toString().getBytes());
			urlConnection.getOutputStream().flush();
			urlConnection.getOutputStream().close();
		}
 
		return makeContent(urlString, urlConnection);
	}
 
	/**
	 * 得到HttpRespons响应对象
	 * 
	 * @param urlConnection
	 * @return HttpRespons响应对象
	 * @throws IOException
	 */
	private HttpRespons makeContent(String urlString, HttpURLConnection urlConnection) throws IOException
	{
		HttpRespons httpResponser = new HttpRespons();
		
		httpResponser.setHeaderFields(urlConnection.getHeaderFields());//把请求返回的消息头提取出来
		
		try {
			inputStream = urlConnection.getInputStream();//获取输入流
			List<Byte> list = new ArrayList<Byte>();
			int input = 0;
		    while ((input = inputStream.read()) != -1)
		    {
		    	list.add( (byte)input );
		    }
		    inputStream.close();
		    byte[] content = new byte[list.size()];
		    for (int i = 0; i < content.length; i++)
		    {
		    	content[i] = (byte) list.get(i);
		    }
		    
		    contentType=urlConnection.getContentType();
		    if( contentType.contains("charset=UTF-8") || contentType.contains("text/html") || contentType.contains("charset=utf-8") )
		    	ecod=Charset.forName("UTF-8");
		    else
		    {
		    	ecod=null;
		    	contentEncoding=urlConnection.getContentEncoding();
		    }
			if(ecod!=null)
			{
				/*
				 * 如果ecod不为空，说明http请求返回的数据是text文本类型
				 */
				System.out.println("这是if中的ecod输出结果（此输出来自于HttpRequester.java文件中的makeContent函数中）："+ecod);
				httpResponser.setIsText(true);
				httpResponser.setTextContent(new String(content, ecod));
			}else
			{
				/*
				 * 如果ecod为空，说明http请求返回的数据不是text文本类型，此时，只需要将输入流返回，并不需要做别的
				 */
				System.out.println("这是else中的ecod输出结果（此输出来自于HttpRequester.java文件中的makeContent函数中）："+ecod);
				httpResponser.setIsText(false);
				httpResponser.setByteContent(content);
			}
 
			httpResponser.setUrlString(urlString);
 
			httpResponser.setDefaultPort(urlConnection.getURL().getDefaultPort());
			httpResponser.setFile(urlConnection.getURL().getFile());
			httpResponser.setHost(urlConnection.getURL().getHost());
			httpResponser.setPath(urlConnection.getURL().getPath());
			httpResponser.setPort(urlConnection.getURL().getPort());
			httpResponser.setProtocol(urlConnection.getURL().getProtocol());
			httpResponser.setQuery(urlConnection.getURL().getQuery());
			httpResponser.setRef(urlConnection.getURL().getRef());
			httpResponser.setUserInfo(urlConnection.getURL().getUserInfo());
			httpResponser.setContentEncoding(urlConnection.getContentEncoding());
			httpResponser.setCode(urlConnection.getResponseCode());
			httpResponser.setMessage(urlConnection.getResponseMessage());
			httpResponser.setContentType(urlConnection.getContentType());
			httpResponser.setMethod(urlConnection.getRequestMethod());
			httpResponser.setConnectTimeout(urlConnection.getConnectTimeout());
			httpResponser.setReadTimeout(urlConnection.getReadTimeout());
			httpResponser.setContentLength(urlConnection.getContentLength());
			httpResponser.setContentLengthLong(urlConnection.getContentLengthLong());
			httpResponser.setInputStream(inputStream);
			
			
			setHttpRespons(httpResponser);
		} catch (IOException e) {
			throw e;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
		
		return this.httpResponser;
	}
 
	/**
	 * 默认的响应字符集
	 */
	public String getDefaultContentEncoding() {
		return this.defaultContentEncoding;
	}
 
	/**
	 * 设置默认的响应字符集
	 */
	public void setDefaultContentEncoding(String defaultContentEncoding) {
		this.defaultContentEncoding = defaultContentEncoding;
	}

	public HttpRespons getHttpRespons() {
		return httpResponser;
	}

	public void setHttpRespons(HttpRespons httpResponser) {
		this.httpResponser = httpResponser;
	}

}
