package com.zebedee.utils

import java.util

import com.alibaba.fastjson.JSON
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils

/**
  *
  * Created by shaodi.chen on 2019/5/16. 
  *
  */
class DataUtilsScala {

    def str2Int(inData:String):Int={
        Integer.parseInt(inData)
    }

    def StrSplitUtil(inData:String,splitStr:String):Array[String]={
        StringUtils.split(inData,splitStr,-1)
    }
}
