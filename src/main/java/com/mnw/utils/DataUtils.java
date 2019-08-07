package com.mnw.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mnw.data.constant.DataConstant;
import org.apache.commons.lang3.StringUtils;
import scala.collection.immutable.Map;

/**
 * Created by shaodi.chen on 2019/5/23.
 */
public class DataUtils {
    /**
     * Map 2 json string string.
     *
     * @param map the map
     * @return the string
     */
    public static String map2JSONString(Map<String, String> map) {
        return JSON.toJSONString(map);
    }

    /**
     * Json string 2 map map.
     *
     * @param jsonString the json string
     * @return the map
     */
    public static Map<String, String> jsonString2Map(String jsonString) {
        return JSON.parseObject(jsonString, Map.class);
    }

    /**
     * Str 2 int int.
     *
     * @param inData the in data
     * @return the int
     */
    public static int str2Int(String inData) {
        return Integer.parseInt(inData);
    }

    /**
     * Str split util string [ ].
     *
     * @param inData   the in data
     * @param splitStr the split str
     * @return the string [ ]
     */
    public static String[] StrSplitUtil(String inData, String splitStr) {
        return StringUtils.split(inData, splitStr, -1);
    }

    /**
     * Is json boolean.
     *
     * @param inStr the in str
     * @return the boolean
     */
    public static boolean isJson(String inStr) {
        boolean result = false;
        try {
            JSONObject object = JSON.parseObject(inStr);
            result = true;
        } catch (Exception ignored) {
        }
        return result;
    }

    public static boolean isJsonArray(String inStr) {
        boolean result = false;
        try {
            JSONArray jsonArray = JSONArray.parseArray(inStr);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public static String phoneFormat(String phone) {
        return phone.replace("\\u00a0", "").
                replace(" ", "").
                replace("+86", "").
                replace("-", "").
                replace("(","").
                replace(")","").
                replaceAll("^86", "");
    }

    public final static boolean isNumeric(String s) {
        if (s != null && !"".equals(s.trim()))
            return s.matches("^[0-9]*$");
        else
            return false;
    }

    public final static boolean isPhone(String phone){
        if (phone.matches(DataConstant.PHONE_REGULAR)||phone.matches(DataConstant.PHONE_2_REGULAR)||phone.matches(DataConstant.PHONE_3_REGULAR)){
            return true;
        }else{
            return false;
        }
    }

    public final static boolean isNotPhone(String phone){
        if (phone.matches(DataConstant.PHONE_REGULAR)||phone.matches(DataConstant.PHONE_2_REGULAR)||phone.matches(DataConstant.PHONE_3_REGULAR)){
            return false;
        }else{
            return true;
        }
    }

    public static void main(String[] args) {
        String s = "(182\\u00a08354\\u00a07688)";
        System.out.println(phoneFormat(s));
    }
}
