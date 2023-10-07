package com.http.Helper;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * 响应对象
 */
public class HttpRespons {
 
	private String urlString=null;
	private int defaultPort=0;
	private String file=null;
	private String host=null;
	private String path=null;
	private int port=0;
	private String protocol=null;
	private String query=null;
	private String ref=null;
	private String userInfo=null;
	private String contentEncoding=null;
	private int contentLength=-1;//请求的网络资源的大小
	private long contentLengthLong=-1;//所请求的网络资源的大小
	private String textContent=null;//如果http请求返回的数据为text文本，则该字符串为文本的内容
	private boolean isText=false;//判断http请求返回的数据是否为text文本，是则为true，不是则为false
	private String contentType=null;
	private int code=0;
	private String message=null;
	private String method=null;
	private int connectTimeout;
	private int readTimeout;
	
	private Map<String, List<String>> headerFields=null;
	
	private InputStream inputStream=null;//用于获取输入流
	private byte[] byteContent=null;//如果服务器返回的数据不是test文本类型，那么输入流将输入的数据以字节数组的形式存放在这里
	
	public String getUrlString() {
		return urlString;
	}
	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}
	public int getDefaultPort() {
		return defaultPort;
	}
	public void setDefaultPort(int defaultPort) {
		this.defaultPort = defaultPort;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}
	public String getContentEncoding() {
		return contentEncoding;
	}
	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}
	public String getTextContent() {
		if(isText)
			return textContent;
		else
			return null;
	}
	public void setTextContent(String textContent) {
		if(isText)
			this.textContent = textContent;
	}
	public String getContentType() {
		if(isText)
			return textContent;
		else
			return null;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public int getConnectTimeout() {
		return connectTimeout;
	}
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	public int getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	public Map<String, List<String>> getHeaderFields() {
		return headerFields;
	}
	public void setHeaderFields(Map<String, List<String>> headerFields) {
		this.headerFields = headerFields;
	}
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	public boolean isText() {
		return isText;
	}
	public void setIsText(boolean isText) {
		this.isText = isText;
	}
	public byte[] getByteContent() {
		if(!isText)
			return byteContent;
		else
			return null;
	}
	public void setByteContent(byte[] byteContent) {
		if(!isText)
			this.byteContent = byteContent;
	}
	public int getContentLength() {
		return contentLength;
	}
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	public long getContentLengthLong() {
		return contentLengthLong;
	}
	public void setContentLengthLong(long contentLengthLong) {
		this.contentLengthLong = contentLengthLong;
	}
}
