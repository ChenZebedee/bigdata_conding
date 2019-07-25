package com.mnw.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import scala.collection.immutable.Map;

/**
 * Created by shaodi.chen on 2019/5/23.
 */
public class DataUtils {
    public static String map2JSONString(Map<String,String> map){
        return JSON.toJSONString(map);
    }

    public static Map<String,String> jsonString2Map(String jsonString){
        return JSON.parseObject(jsonString,Map.class);
    }

    public static int str2Int(String inData){
        return Integer.parseInt(inData);
    }

    public static String[] StrSplitUtil(String inData,String splitStr){
        return StringUtils.split(inData,splitStr,-1);
    }
}
