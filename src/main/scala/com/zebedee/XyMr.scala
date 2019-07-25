package com.zebedee

import java.util

import com.mnw.data.constant.PunctuationConst
import com.mnw.utils.DataUtils
import org.apache.commons.lang3.StringUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  *
  * Created by shaodi.chen on 2019/7/25. 
  *
  */
object XyMr {
    def main(args: Array[String]): Unit = {
        val conf = new SparkConf().setAppName("xySpark")
        val sc = new SparkContext(conf)

        val fileData = sc.textFile("hdfs://192.168.1.71:9092/dataxxxx")
        val transformColumnArray:Array[String] = DataUtils.StrSplitUtil(args(1),PunctuationConst.VERTICAL_LINE)
        val inColumnList:Array[String] = DataUtils.StrSplitUtil(args(2),PunctuationConst.SPLITTER_USE)

        val outColumnMap:Map[String,String]=Map()
        val value: RDD[(String, Map[String, String])] = fileData.map(line =>{
            val dataArray = DataUtils.StrSplitUtil(line,PunctuationConst.SPLITTER_USE))
            val outKey = dataArray(0)
            val outMap: Map[String, String] = Map()

            val oldDataMap:Map[String,String] = Map()

            var i:Int = 0
            for (i <- 0 to inColumnList.size){
                oldDataMap.+(inColumnList(i)->dataArray(i))
            }
            (outKey, outMap)
        }).combineByKey(a=>{})
    }
}
