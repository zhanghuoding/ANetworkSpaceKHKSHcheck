package com.ToolClass;

import java.util.HashMap;
import java.util.Iterator;

import net.sf.json.JSONObject;


public class MyJson{
    
    /**
     * 将String对象转换成Map
     * 
     * @param String对象
     * @return Map对象
     * @throws JSONException
     */
    
	public static HashMap<String, String> toMap(String jsonString){

        JSONObject jsonObject=null;
		do{
			jsonObject =JSONObject.fromObject(jsonString);
		}while(jsonObject==null);
        
        HashMap<String, String> result = new HashMap<String, String>();
        
		Iterator<String> iterator = jsonObject.keys();
        String key = null;
        String value = null;
        
        while (iterator.hasNext()) {

            key = (String) iterator.next();
            value = jsonObject.getString(key);
            result.put(key, value);
            key=null;
            value=null;

        }
        return result;

    }

}