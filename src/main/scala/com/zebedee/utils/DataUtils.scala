package com.zebedee.utils

import com.alibaba.fastjson.JSON

/**
  *
  * Created by shaodi.chen on 2019/5/16. 
  *
  */
class DataUtils {

  def map2JsonStr(map: Map[String,String]): String ={
    JSON.toJSONString(map)
  }

}
