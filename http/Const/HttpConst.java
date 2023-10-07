package com.http.Const;

import java.util.HashMap;

import com.ToolClass.MyJson;

public class HttpConst {
	
	private static HttpConst instance=null;//单例模式对象
	private final static Object synLock=new Object();//用于在初始化该类的唯一实例的时候实施线程锁
	
	public final String my_API_Key="f74C8xmxfpD6QmY9Pz2PfeMx";
	public final String my_Secret_Key="21XEhoD72wODIRG7ENxj6bbdNam1NQ44";
	public final String my_Client_ID="5491725";
	
	public final int timeout=10;//在连网尝试中，如果连接不成功的重试次数
	
	public final String BAIDU_URL="http://www.baidu.com";
	public final String PASSPORT_BASE = "https://passport.baidu.com/";
	public final String PASSPORT_URL = PASSPORT_BASE + "v2/api/";
	public final String PASSPORT_LOGIN = PASSPORT_BASE + "v2/api/?login";
	public final String REFERER = PASSPORT_BASE + "v2/?login";
	public final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64; rv:31.0) Gecko/20100101 Firefox/31.0 Iceweasel/31.2.0";
	public final String PAN_URL = "http://pan.baidu.com/";
	public final String PAN_API_URL = PAN_URL + "api/";
	public final String PAN_REFERER = "http://pan.baidu.com/disk/home";
	public final String SHARE_REFERER = PAN_URL + "share/manage";
	public final String PCS_URL = "http://pcs.baidu.com/rest/2.0/pcs/";//一般服务器名
	public final String PCS_URL_C = "http://c.pcs.baidu.com/rest/2.0/pcs/";//上传服务器名http
	public final String PCS_URLS_C = "https://c.pcs.baidu.com/rest/2.0/pcs/";//上传服务器名https
	public final String PCS_URL_D = "http://d.pcs.baidu.com/rest/2.0/pcs/";//下载服务器名
	
	//以下常量是模拟的PC客户端的参数
	public final String CHANNEL_URL = "https://channel.api.duapp.com/rest/2.0/channel/channel?";
	public final String PC_USER_AGENT = "netdisk;4.5.0.7;PC;PC-Windows;5.1.2600;WindowsBaiduYunGuanJia";
	public final String PC_DEVICE_ID = "08002788772E";
	public final String PC_DEVICE_NAME = "08002788772E";
	public final String PC_DEVICE_TYPE = "2";
	public final String PC_CLIENT_TYPE = "8";
	public final String PC_APP_ID = my_Client_ID;
	public final String PC_DEVUID = "BDIMXV2%2DO%5FFD60326573E54779892088D1378B27C6%2DC%5F0%2DD%5F42563835636437366130302d6662616539362064%2DM%5F08002788772E%2DV%5F0C94CA83";
	public final String PC_VERSION = "1.0";
	
	//HTTP 请求时的一些常量
	public final String CONTENT_FORM = "application/x-www-form-urlencoded";
	public final String CONTENT_FORM_UTF8 = CONTENT_FORM + "; charset=UTF-8";
	public final String ACCEPT_HTML = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
	public final String ACCEPT_JSON = "application/json, text/javascript, */*; q=0.8";
	
	//下载状态常量
	public final int downloadState_DOWNLOADING = 0;
	public final int downloadState_WAITING = 1;
	public final int downloadState_PAUSED = 2;
	public final int downloadState_FINISHED = 3;
	public final int downloadState_CANCELED = 4;
	public final int downloadState_ERROR = 5;
	
	//上传状态常量
	public final int uploadState_UPLOADING = 0;
	public final int uploadState_WAITING = 1;
	public final int uploadState_PAUSED = 2;
	public final int uploadState_FINISHED = 3;
	public final int uploadState_CANCELED = 4;
	public final int uploadState_ERROR = 5;
	
	//上传时, 如果服务器端已存在同名文件时的操作方式
	public final int UploadMode_IGNORE = 0;
	public final int UploadMode_OVERWRITE = 1;
	public final int UploadMode_NEWCOPY = 2;
	public final String[] UPLOAD_ONDUP = {"", "overwrite", "newcopy"};
	
	//下载时, 如果本地已存在同名文件时的操作方式
	public final int downloadMode_IGNORE = 0;
	public final int downloadMode_OVERWRITE = 1;
	public final int downloadMode_NEWCOPY = 2;
	
	//视图模式
	public final int ICON_VIEW = 0;
	public final int TREE_VIEW = 1;
	
	//文件路径检验结果
	public final int ValidatePathState_OK = 0;
	public final int ValidatePathState_LENGTH_ERROR = 1;
	public final int ValidatePathState_CHAR_ERROR2 = 2;
	public final int ValidatePathState_CHAR_ERROR3 = 3;
	public final String[] ValidatePathStateText = {
		    "",
		    "Max characters in filepath shall no more than 1000",
		    "Filepath should not contain \\ ? | \" > < : *",
		    "\\r \\n \\t \\0 \\x0B or SPACE should not appear in start or end of filename"
	};
	
	//拖放类型编号
	public final int TargetInfo_URI_LIST = 0;
	public final int TargetInfo_PLAIN_TEXT = 1;
	public final int TargetInfo_RAW = 2;
	public final int TargetInfo_TEXT_JSON = 3;
	
	//拖放类型
	public final String TargetType_URI_LIST = "text/uri-list";
	public final String TargetType_PLAIN_TEXT = "text/plain";
	public final String TargetType_RAW = "application/octet-stream";
	public final String TargetType_TEXT_JSON = "application/json";

	//public final String openapi_URL_0="http://openapi.baidu.com/oauth/2.0/authorize?client_id=f74C8xmxfpD6QmY9Pz2PfeMx&response_type=token&redirect_uri=oob&display=mobile";
	//public final String passport_URL_0="https://passport.baidu.com/v2/api/?getapi&tpl=mn&apiver=v3&tt=";
	//public final String passport_URL_1="&class=login&logintype=basicLogin";
	public final String default_headers_String="{\"User-agent\":\"" + USER_AGENT +"\",\"Referer\":\"" + PAN_REFERER +
			//"\"x-requested-with\":\"XMLHttpRequest\""
			"\",\"Accept\":\"" + ACCEPT_JSON + "\",\"Accept-language\":\"zh-cn,zh;q=0.5\"" +
			",\"Accept-encoding\":\"gzip,deflate\"" + ",\"Pragma\":\"no-cache\"" +
			",\"Cache-control\":\"no-cache\"}";
	
	private HashMap<String, String> default_headers=null;//用于存储在默认情况下的请求头信息
	
	private HttpConst()
	{
		setDefault_headers(MyJson.toMap(default_headers_String));
	}
	public static HttpConst GetHttpConst()
	{
		if(instance==null)
		{
			synchronized(synLock){
				if(instance==null)
					instance=new HttpConst();
			}
		}
		return instance;
	}
	public static void update()
	{
		instance=null;
	}
	public HashMap<String, String> getDefault_headers() {
		return default_headers;
	}
	public void setDefault_headers(HashMap<String, String> default_headers) {
		this.default_headers = default_headers;
	}
}
