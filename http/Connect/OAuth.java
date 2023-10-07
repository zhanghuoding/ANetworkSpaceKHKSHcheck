package com.http.Connect;

import java.awt.Toolkit;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sun.misc.BASE64Decoder;

import com.ToolClass.MyJson;
import com.http.Const.HttpConst;
import com.http.Helper.MyHttpRequester;
import com.http.Helper.HttpRespons;

public class OAuth {
	
	private static OAuth instance=null;//单例模式对象
	private final static Object synLock=new Object();//用于在初始化该类的唯一实例的时候实施线程锁
	
	private HttpConst httpConst=null;
	
	private String ultima_URL=null;
	private MyHttpRequester httpRequester=null;//一个连接对象，用来发送http请求
	private HttpRespons httpRespons=null;//连接之后服务器的所有响应数据
	private HashMap<String, String> parameterGetFromBaidu=null;//用于存放接收到的百度服务器返回的响应消息中的所有在后期访问继续需要的键值对

	private String BAIDUID=null;
	private String Token=null;
	private String displayname=null;
	private String Set_Cookie_apply_UBI=null;
	private String codeString=null;//如果登陆时需要验证码，则这里是验证码的链接
	private String vcodetype=null;//如果需要验证码，此变量用于刷新验证码
	private String verifycode=null;//登陆时用户填写的验证码
	private String username="梦境无凭准";//登陆用户名
	private String encodeUsername=null;//用URLEncoder.encode编码后的用户名
	private String password="352205ymmjrysx";//用户密码
	private String RsaPassword=null;//使用RSA加密后的密码
	private String encodeRsaPassword=null;//用URLEncoder.encode编码后的RSA加密密码
	private String RSA_public_key=null;//RSA加密用的public_key
	private String RSA_key=null;//与public_key相匹配的rsakey
	
	private boolean needCodeString=false;//判断是否需要验证码
	private String login_Err_no=null;//最后登陆时返回的登陆状态码
	private boolean isLogin=false;//判断是否已经登陆成功
	private String oauth_Token=null;//最后登陆成功侯获得的访问令牌
	private String bd_Token=null;//登陆成功侯返回的用于访问的令牌
	
	private OAuth()
	{
		init();
	}
	public static OAuth GetOAuth()
	{
		if(instance==null)
		{
			synchronized(synLock){
				if(instance==null)
					instance=new OAuth();
			}
		}
		return instance;
	}
	public static void update()
	{
		instance=null;
	}
	
	private void init()
	{
		httpRequester=new MyHttpRequester();
		httpConst=HttpConst.GetHttpConst();
		parameterGetFromBaidu=new HashMap<String, String>();
		
		//编码用户名和用RSA加密后的用户密码
		try {
			encodeUsername=URLEncoder.encode(username, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	public void apply_BAIDUID()
	{//获取一个cookie - BAIDUID。这里, 我们访问百度首页, 返回的response header里面有我们需要的cookie
		httpRespons=null;//	清除上次连接时服务器的响应消息
		ultima_URL=httpConst.PASSPORT_URL+"?getapi&tpl=mn&apiver=v3&tt="+getCurrentTime()+"&class=login&logintype=basicLogin";
		String headers_String="{\"Referer\":\"\",\"Accept-encoding\":\"\"}";
		HashMap<String, String> headers=MyJson.toMap(headers_String);
		String key=null,value=null;
		for(int times=0; times<httpConst.timeout; times++)
		{
			httpRespons=null;
			do{
				try {
					setHttpRespons(httpRequester.sendGet(ultima_URL, null, headers));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}while(httpRespons==null);
			
			Iterator<String> keyIterator=httpRespons.getHeaderFields().keySet().iterator();//得到一个指向该HeaderFields的所有键的set集合的迭代器
			Iterator<String> listIterator=null;
			while(keyIterator.hasNext())
			{
				key=keyIterator.next();
				listIterator=httpRespons.getHeaderFields().get(key).iterator();
				if(key!=null && key.equalsIgnoreCase("Set-Cookie"))
				{
					while(listIterator.hasNext())
					{
						value=listIterator.next();
						if(value!=null && value.startsWith("BAIDUID="))
						{
							parameterGetFromBaidu.put("Set-Cookie-apply_BAIDUID", value);
							BAIDUID=new String(value);
							return;
						}
						value=null;
					}
				}
				key=null;
			}
		}
	}
	public String getBAIDUID()
	{//获取并返回BAIDUID
		apply_BAIDUID();
		return BAIDUID;
	}
	public void apply_TOKEN()
	{//获取一个页面访问的token, 这里需要之前得到的BAIDUID 这个cookie值,这个token的有效期还不确定。我们需要的token的值在InputStream流中
	/*
	 * 返回的数据如下:
    {"errInfo":{"no": "0"},
     "data": {
         "rememberedUserName" : "",
         "codeString" : "",
         "token" : "xxxxx",
         "cookie" : "1",
         "usernametype":"2",
         "spLogin" : "rate",
         "disable":"",
         "loginrecord":{ 'email':[ ], 'phone':[]}
    }}
	 */
		httpRespons=null;//	清除上次连接时服务器的响应消息
		ultima_URL=httpConst.PASSPORT_URL+"?getapi&tpl=pp&apiver=v3&tt="+getCurrentTime()+"&class=login&logintype=basicLogin";
		String headers_String="{\"Cookie\":\""+BAIDUID+"\",\"Accept\":\""+httpConst.ACCEPT_HTML
				+"\",\"Cache-control\":\"max-age=0\",\"Accept-encoding\":\"\"}";
		HashMap<String, String> headers=MyJson.toMap(headers_String);
		HashMap<String, String> temp1=null, temp2=null;
		for(int i=0; i<httpConst.timeout; i++)
		{
			try {
				httpRespons=httpRequester.sendGet(ultima_URL, null, headers);//连接百度连接并取得token
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(httpRespons!=null)
			{
				temp1=MyJson.toMap(MyJson.toMap(httpRespons.getTextContent()).get("errInfo"));
				if(temp1.get("no").equals("0"))
					break;
			}
		}
		temp2=MyJson.toMap(MyJson.toMap(httpRespons.getTextContent()).get("data"));
		parameterGetFromBaidu.put("data-apply_TOKEN", temp2.toString());
		Iterator<String> keyIterator=temp2.keySet().iterator();//得到一个指向服务器返回的json串中的迭代器
		String key=null, value=null;
		while(keyIterator.hasNext())
		{
			key=keyIterator.next();
			value=temp2.get(key);
			if( key.equalsIgnoreCase("token") )
			{
				parameterGetFromBaidu.put(key, value);
				Token=value;
			}
			else if(  key.equalsIgnoreCase("loginrecord")  )
				parameterGetFromBaidu.put(key, value);
			key=null; value=null;
		}
		Iterator<String> keyIterator_headers=httpRespons.getHeaderFields().keySet().iterator();//得到一个指向该HeaderFields的所有键的set集合的迭代器
		Iterator<String> listIterator=null;
		while(keyIterator_headers.hasNext())
		{
			key=keyIterator_headers.next();
			listIterator=httpRespons.getHeaderFields().get(key).iterator();
			if(key!=null && key.equalsIgnoreCase("Set-Cookie"))
			{
				while(listIterator.hasNext())
				{
					value=listIterator.next();
					if(value!=null && value.startsWith("HOSUPPORT="))
					{
						parameterGetFromBaidu.put("Set-Cookie-apply_TOKEN", value);
						return;
					}
					value=null;
				}
			}
			key=null;			
		}
	}
	public String getToken()
	{
		apply_TOKEN();
		return Token;
	}
	public void apply_UBI()
	{//检查登录历史，获得返回值包含一个Cookie-UBI,在调用这个函数之前需要调用apply_BAIDUID()方法和apply_TOKEN()方法
	/*
	 * UBI信息类似于Set-Cookie	UBI=fi_PncwhpxZ%7ETaJc3BcrtRTrCqKpDWIPjU3; expires=Fri, 15-Sep-2023 09:17:03 GMT; path=/; domain=passport.baidu.com; httponly
	 * 返回的信息类似于: {"errInfo":{ "no": "0" }, "data": {'displayname':['xxx@163.com']}}
	 */
		httpRespons=null;//	清除上次连接时服务器的响应消息
		ultima_URL=httpConst.PASSPORT_URL+"?loginhistory&token="+Token+"&tpl=pp&apiver=v3&tt="+getCurrentTime();
		String headers_String="{\"Cookie\":\""+BAIDUID+"\",\"Referer\":\""+httpConst.REFERER+"\",\"Accept-encoding\":\"\"}";
		HashMap<String, String> headers=MyJson.toMap(headers_String);
		HashMap<String, String> temp1=null, temp2=null;
		for(int i=0; i<httpConst.timeout; i++)
		{
			try {
				httpRespons=httpRequester.sendGet(ultima_URL, null, headers);//连接百度
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(httpRespons!=null)
			{
				temp1=MyJson.toMap(MyJson.toMap(httpRespons.getTextContent()).get("errInfo"));
				if(temp1.get("no").equals("0"))
					break;
			}
		}
		temp2=MyJson.toMap(MyJson.toMap(httpRespons.getTextContent()).get("data"));
		parameterGetFromBaidu.put("data-apply_UBI", temp2.toString());
		Iterator<String> keyIterator=temp2.keySet().iterator();//得到一个指向服务器返回的json串中的迭代器
		String key=null, value=null;
		while(keyIterator.hasNext())
		{
			key=keyIterator.next();
			value=temp2.get(key);
			if(key.equalsIgnoreCase("displayname"))
			{
				parameterGetFromBaidu.put(key, value);
				setDisplayname(value);
				break;
			}
			key=null; value=null;
		}
		Iterator<String> keyIterator_headers=httpRespons.getHeaderFields().keySet().iterator();//得到一个指向该HeaderFields的所有键的set集合的迭代器
		Iterator<String> listIterator=null;
		while(keyIterator_headers.hasNext())
		{
			key=keyIterator_headers.next();
			listIterator=httpRespons.getHeaderFields().get(key).iterator();
			if(key!=null && key.equalsIgnoreCase("Set-Cookie"))
			{
				while(listIterator.hasNext())
				{
					value=listIterator.next();
					if(value!=null && value.startsWith("UBI="))
					{
						parameterGetFromBaidu.put("Set-Cookie-apply_UBI", value);
						Set_Cookie_apply_UBI=value;
					}
					value=null;
				}
			}
			key=null;			
		}
	}
	public String getUBI()
	{
		apply_UBI();
		return Set_Cookie_apply_UBI;
	}
	public void check_login()
	{
		/*
		 * 本函数用于进行登录验证, 主要是在服务器上验证这个帐户的状态。
		 * 如果帐户不存在, 或者帐户异常, 就不需要再进行最后一步的登录操作了。
		 * 在此函数之前需先执行apply_BAIDUID()和函数apply_TOKEN()
		 * 这一步有可能需要输入验证码
		 * 返回的信息类似：{"errInfo":{ "no": "0" }, "data": { "codeString" : "", "vcodetype" : "" }}
		 */
		httpRespons=null;//	清除上次连接时服务器的响应消息
		ultima_URL=httpConst.PASSPORT_URL+"?logincheck&token="+Token+"&tpl=mm&apiver=v3&tt="+getCurrentTime()
				+"&username="+encodeUsername+"&isphone=false";
		String headers_String="{\"Cookie\":\""+BAIDUID+"\",\"Referer\":\""+httpConst.REFERER+"\",\"Accept-encoding\":\"\"}";
		HashMap<String, String> headers=MyJson.toMap(headers_String);
		HashMap<String, String> temp1=null, temp2=null;
		for(int i=0; i<httpConst.timeout; i++)
		{
			try {
				httpRespons=httpRequester.sendGet(ultima_URL, null, headers);//连接百度连接
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(httpRespons!=null)
			{
				temp1=MyJson.toMap(MyJson.toMap(httpRespons.getTextContent()).get("errInfo"));
				if(temp1.get("no").equals("0"))
					break;
			}
		}
		temp2=MyJson.toMap(MyJson.toMap(httpRespons.getTextContent()).get("data"));
		parameterGetFromBaidu.put("data-check_login", temp2.toString());
		String key=null, value=null;
		
		setCodeString(temp2.get("codeString"));
		parameterGetFromBaidu.put("codeString", getCodeString());
		setVcodetype(temp2.get("vcodetype"));
		parameterGetFromBaidu.put("vcodetype", getVcodetype());
		System.out.println("codeString:\t"+getCodeString()+"\t\tvcodetype\t"+getVcodetype());
		Iterator<String> keyIterator_headers=httpRespons.getHeaderFields().keySet().iterator();//得到一个指向该HeaderFields的所有键的set集合的迭代器
		Iterator<String> listIterator=null;
		while(keyIterator_headers.hasNext())
		{
			key=keyIterator_headers.next();
			listIterator=httpRespons.getHeaderFields().get(key).iterator();
			if(key!=null && key.equals("Set-Cookie"))
			{
				while(listIterator.hasNext())
				{
					value=listIterator.next();
					if(value!=null && value.startsWith("UBI="))
					{
						parameterGetFromBaidu.put("Set-Cookie-check_login", value);
						return;
					}
					value=null;
				}
			}
			key=null;			
		}
	}
	public void get_signin_vcode()
	{
		/*
		 * 获取登录时的验证码图片
		 * codeString - 调用check_login()或者try_login()时返回的codeString
		 */
		httpRespons=null;//	清除上次连接时服务器的响应消息
		ultima_URL=httpConst.PASSPORT_BASE+"cgi-bin/genimage?"+getCodeString();
		String headers_String="{\"Cookie\":\""+BAIDUID+"\",\"Referer\":\""+httpConst.REFERER+"\"}";
		HashMap<String, String> headers=MyJson.toMap(headers_String);
		for(int i=0; i<httpConst.timeout; i++)
		{
			try {
				httpRespons=httpRequester.sendGet(ultima_URL, null, headers);//连接百度连接
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if( httpRespons!=null && httpRespons.getCode()==200)
				break;
		}
		Toolkit toolkit=Toolkit.getDefaultToolkit();
		ImageIcon image=new ImageIcon(httpRespons.getByteContent());
		JLabel label=new JLabel();
		label.setIcon(image);
		label.setBounds(0, 0, 60, 30);
		JPanel panel=new JPanel();
		panel.add(label);
		panel.setSize(100, 100);
		JFrame jFrame=new JFrame();
		jFrame.setSize(100, 100);
		jFrame.add(panel);
		jFrame.setVisible(true);
	}
	public void refresh_signin_vcode()
	{
		/*
		 * 刷新验证码
		 * vcodetype - 在调用check_login()时返回的vcodetype
		 */
		httpRespons=null;//	清除上次连接时服务器的响应消息
		ultima_URL=httpConst.PASSPORT_BASE+"v2/?reggetcodestr&token="+Token
				+"&tpl=pp&apiver=v3&tt="+getCurrentTime()+"&fr=ligin&vcodetype="+vcodetype;
		String headers_String="{\"Cookie\":\""+BAIDUID+"\",\"Referer\":\""+httpConst.REFERER+"\"}";
		HashMap<String, String> headers=MyJson.toMap(headers_String);
		HashMap<String, String> temp1=null, temp2=null;
		for(int i=0; i<httpConst.timeout; i++)
		{
			try {
				httpRespons=httpRequester.sendGet(ultima_URL, null, headers);//连接百度连接并取得token
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(httpRespons!=null)
			{
				temp1=MyJson.toMap(MyJson.toMap(httpRespons.getTextContent()).get("errInfo"));
				if(temp1.get("no").equals("0"))
					break;
			}
		}
		/*
		 * 此函数有错误，返回的验证码图片应该不是json串===================================================================
		 * 
		 * 
		 * =====================================================================================================
		 */
		temp2=MyJson.toMap(MyJson.toMap(httpRespons.getTextContent()).get("data"));
		parameterGetFromBaidu.put("data-refresh_signin_vcode", temp2.toString());
	}
	public void get_RSAKey()
	{
		/*
		 * 获取RSA公钥, 这个用于加密用户的密码
		 * 返回的数据如下JSON串:
		 * {"errno":'0',"msg":'',"pubkey":'-----BEGIN PUBLIC KEY-----
		 * MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDk\/ufXg3IBW8+h5i8L8NoXUzcN
		 * MeKrh4zEupGBkyrURIPUXKDFLWjrv4n2j3RpMZ8GQn\/ETcfoIHGBoCUKJWcfcvmi
		 * G+OkYeqT6zyJasF0OlKesKfz0fGogMtdCQ6Kqq7X2vrzBPL+4SNU2wgU31g\/tVZl\n3zy5qAsBFkC70vs5FQIDAQAB
		 * -----END PUBLIC KEY-----\n',"key":'lwCISJnvs7HRNCTxpX7vi25bV9YslF2J'}
		 */
		httpRespons=null;//	清除上次连接时服务器的响应消息
		ultima_URL=httpConst.PASSPORT_BASE+"v2/getpublickey?token="+Token+"&tpl=pp&apiver=v3&tt="+getCurrentTime();
		String headers_String="{\"Cookie\":\""+BAIDUID+"\",\"Referer\":\""+httpConst.REFERER+"\",\"Accept-encoding\":\"\"}";
		HashMap<String, String> headers=MyJson.toMap(headers_String);
		HashMap<String, String> temp1=null;
		for(int i=0; i<httpConst.timeout; i++)
		{
			try {
				httpRespons=httpRequester.sendGet(ultima_URL, null, headers);//连接百度连接并取得token
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(httpRespons!=null)
			{
				temp1=MyJson.toMap(httpRespons.getTextContent());
				if(temp1.get("errno").equals("0"))
					break;
			}
		}
		//parameterGetFromBaidu.put("data-get_public_key", temp1.toString());
		RSA_public_key=temp1.get("pubkey");
		parameterGetFromBaidu.put("RSA-public_key", temp1.get("pubkey"));
		RSA_key=temp1.get("key");
		parameterGetFromBaidu.put("RSA-key", temp1.get("key"));
	}
	public boolean try_login()
	{
		/*
		 * 本函数先进尝试登录验证,如果登陆成功返回真，否则返回假
		 * 服务器返回的数据为，其中, status表示返回的状态:
		 * 0 - 正常, 这里, info里面存放的是auth_cookie
		 * -1 - 未知异常
		 * 4 - 密码错误
		 * 400031 - 短信验证
		 * 257 - 需要输入验证码, 此时需要构造vcodetype和codeString
		 */
		httpRespons=null;//	清除上次连接时服务器的响应消息
		ultima_URL=httpConst.PASSPORT_LOGIN;
		String headers_String="{\"Accept\":\""+httpConst.ACCEPT_HTML
				+"\",\"Set-Cookie-apply_BAIDUID\":\""+BAIDUID
				+"\",\"Set-Cookie-apply_UBI\":\""+parameterGetFromBaidu.get("Set-Cookie-apply_UBI")
				+"\",\"Set-Cookie-apply_TOKEN\":\""+parameterGetFromBaidu.get("Set-Cookie-apply_TOKEN")
				+"\",\"Referer\":\""+httpConst.REFERER+"\",\"Accept-encoding\":\"\",\"Connection\":\"Keep-Alive\"}";
		
		
		//用RSA算法和百度服务器返回的公钥加密用户明文密码
		RsaPassword=RSA_Encode(password, RSA_public_key);
		
		
		
		//编码用RSA加密后的用户密码
		try {
			encodeRsaPassword=URLEncoder.encode(RsaPassword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//以下为构造登陆时需要post的登陆参数映射
		HashMap<String, String> parameters=new HashMap<String, String>();
		parameters.put("staticpage", "https%3A%2F%2Fpassport.baidu.com%2Fstatic%2Fpasspc-account%2Fhtml%2Fv3Jump.html");
		parameters.put("charset", "UTF-8");
		parameters.put("token", Token);
		parameters.put("tpl=pp&subpro=&apiver=v3&tt", getCurrentTime());
		parameters.put("codestring", getCodeString());
		parameters.put("safeflg=0&u=http%3A%2F%2Fpassport.baidu.com%2F&isPhone", "false");
		parameters.put("quick_user=0&logintype=basicLogin&logLoginType=pc_loginBasic&idc", "");
		parameters.put("loginmerge", "true");
		parameters.put("username", encodeUsername);
		parameters.put("password", encodeRsaPassword);
		parameters.put("verifycode", verifycode);
		parameters.put("mem_pass", "on");
		parameters.put("rsakey", RSA_key);
		parameters.put("crypttype", "12");
		parameters.put("ppui_logintime", String.valueOf(getRandom()));
		parameters.put("callback", "parent.bd__pcbs__28g1kg");
		
		//连接百度服务器实现登陆
		HashMap<String, String> headers=MyJson.toMap(headers_String);
		for(int i=0; i<httpConst.timeout; i++)
		{
			try {
				httpRespons=httpRequester.sendPost(ultima_URL, parameters, headers);//连接百度连接并取得token
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(httpRespons!=null)
			{
				break;
			}
		}
		String returnData=httpRespons.getTextContent();
		//用正则表达式取出err_no字符串
		Pattern p=Pattern.compile("(err_no[^\"]+)");
		Matcher m = p.matcher(returnData);
		if(m.find())
			returnData=m.group();
		else
			returnData=null;
		parameterGetFromBaidu.put("data-try_login", returnData);
		//将err_no字符串分解成键值对形式
		String[] temp1=returnData.split("&");
		List<String[]> temp2=new ArrayList<String[]>();
		for(String temp:temp1)
		{
			temp2.add(temp.split("="));
		}
		Iterator<String[]> iter=temp2.iterator();
		String key="", value="";
		String[] temp3=null;
		while(iter.hasNext())
		{
			temp3=iter.next();
			key=temp3[0];
			if(temp3.length==2)
				value=temp3[1];
			parameterGetFromBaidu.put("data-try-login-"+key, value);
			switch(key)
			{
			case "err_no":
				setLogin_Err_no(value);
				if(value.equalsIgnoreCase("0"))
					setIsLogin(true);
				else if(value.equalsIgnoreCase("257"))
					setNeedCodeString(true);
				break;
			case "codeString":
				if(value.isEmpty())
				{
					setNeedCodeString(true);
					setCodeString(value);System.out.println("codeString="+getCodeString());
				}
				break;
			case "authtoken":
				setOauth_Token(value);
				break;
			case "vcodetype":
				if(!value.isEmpty())
					setVcodetype(value);
				break;
			case "bdToken":
				setBd_Token(value);
				break;
			default :
					break;
			}
			key=""; value="";
		}
		return isLogin;
	}
	public String RSA_Encode(String str, String RSA_public_key)
	{
		/*
		 * 本方法用于对字符串（明文密码）用公钥pubkey进行加密，并返回加密后的字符串
		 */
		//处理传入的公钥字符串RSA_public_key，将其中的-----BEGIN PUBLIC KEY-----和-----END PUBLIC KEY-----去掉，只留下中间的密钥
		RSA_public_key=RSA_public_key.substring("-----BEGIN PUBLIC KEY-----\n".length());
		RSA_public_key=RSA_public_key.substring(0, RSA_public_key.length()-"\n-----END PUBLIC KEY-----\n".length());
		
		//将传入的公钥字符串转换为公钥对象
		byte[] keyBytes=null;
		X509EncodedKeySpec keySpec = null;
		KeyFactory keyFactory=null;
		PublicKey pubkey=null;
		try {
			keyBytes = (new BASE64Decoder()).decodeBuffer(RSA_public_key);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		keySpec = new X509EncodedKeySpec(keyBytes);
		
		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			pubkey = keyFactory.generatePublic(keySpec);
		} catch (InvalidKeySpecException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		Cipher cipher=null;//用于RSA加密的类
		byte[] plainText=null;
		byte[] enBytes = null;
		try {
			cipher = Cipher.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		plainText = str.getBytes();
		try {
			cipher.init(Cipher.ENCRYPT_MODE, pubkey);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			enBytes = cipher.doFinal(plainText);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String(enBytes);
	}
	
	
	public void displayHeaderField()
	{//输出每次请求之后的HeaderField域的信息
		if(httpRespons==null)
			return ;
		String key=null;
		Iterator<String> keyIterator=httpRespons.getHeaderFields().keySet().iterator();//得到一个指向该HeaderFields的所有键的set集合的迭代器
		Iterator<String> listIterator=null;
		while(keyIterator.hasNext())
		{
			key=keyIterator.next();
			listIterator=httpRespons.getHeaderFields().get(key).iterator();
			while(listIterator.hasNext())
			{
				System.out.println(key+"\t"+listIterator.next());
			}
			key=null;			
		}
	}
	public void displayInputStream()
	{//输出请求响应数据
		System.out.println(httpRespons.getTextContent());
	}
	private String getCurrentTime()
	{//在发送请求时所需要用到的格林尼治时间的毫秒数
		long tt=System.currentTimeMillis();
		return Long.toString(tt);
	}
	public int getRandom()
	{//获得一个7000~10000之间的随机数
		Random random=new Random();
		return 7000+random.nextInt(3000);
	}
	public HttpRespons getHttpRespons()
	{
		return httpRespons;
	}
	public HttpRespons setHttpRespons(HttpRespons httpRespons)
	{
		return this.httpRespons=httpRespons;
	}
	public HashMap<String, String> getParameterGetFromBaidu()
	{//返回从服务器返回的，以后每次连接都需要的信息的映射集合
		return parameterGetFromBaidu;
	}
	public void displayParameterGetFromBaidu()
	{
		Iterator<String> iterator=parameterGetFromBaidu.keySet().iterator();
		String key=null, value=null;
		System.out.println("\n=============================================================================");
		System.out.println("\t\t\t以下是从百度获取的比较重要的键值对");
		while(iterator.hasNext())
		{
			key=iterator.next();
			value=parameterGetFromBaidu.get(key);
			System.out.println(key+"\t"+value);
			key=null;
			value=null;
		}
		System.out.println("=============================================================================");
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	public boolean needCodeString() {
		return needCodeString;
	}
	public void setNeedCodeString(boolean needCodeString) {
		this.needCodeString = needCodeString;
	}
	public String getLogin_Err_no() {
		return login_Err_no;
	}
	public void setLogin_Err_no(String login_Err_no) {
		this.login_Err_no = login_Err_no;
	}
	public boolean isLogin() {
		return isLogin;
	}
	public void setIsLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	public String getOauth_Token() {
		return oauth_Token;
	}
	public void setOauth_Token(String oauth_Token) {
		this.oauth_Token = oauth_Token;
	}
	public String getBd_Token() {
		return bd_Token;
	}
	public void setBd_Token(String bd_Token) {
		this.bd_Token = bd_Token;
	}
	public String getCodeString()
	{
		if(needCodeString())
			return codeString;
		else
			return null;
	}
	public void setCodeString(String codeString)
	{
		if(codeString!=null && !codeString.isEmpty())
		{
			setNeedCodeString(true);
			this.codeString=codeString;
		}
	}
	public String getVcodetype()
	{
		if(needCodeString())
			return vcodetype;
		else
			return null;
	}
	public void setVcodetype(String vcodetype)
	{
		if(vcodetype!=null && !vcodetype.isEmpty())
		{
			setNeedCodeString(true);
			this.vcodetype=vcodetype;
		}
	}
}
