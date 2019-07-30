package com.mnw.utils;

import com.alibaba.fastjson.JSON;
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
            Object object = JSON.parseObject(inStr);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
}
