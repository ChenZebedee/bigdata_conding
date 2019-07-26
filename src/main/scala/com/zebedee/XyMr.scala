package com.zebedee


import com.mnw.data.constant.PunctuationConst
import com.mnw.utils.DataUtils
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
        val transformColumnArray: Array[String] = DataUtils.StrSplitUtil(args(1), PunctuationConst.VERTICAL_LINE)
        val inColumnList: Array[String] = DataUtils.StrSplitUtil(args(2), PunctuationConst.SPLITTER_USE)

        val value: RDD[(Map[String,String], Map[String, String])] = fileData.map(line => {
            val dataArray = DataUtils.StrSplitUtil(line, PunctuationConst.SPLITTER_USE)
            var outKey:Map[String,String] = Map()
            val outMap: Map[String, String] = Map()

            val oldDataMap: Map[String, String] = Map()

            for (i <- 0 to inColumnList.length) {
                oldDataMap.+(inColumnList(i) -> dataArray(i))
            }
            outKey=oldDataMap
            for (transformColumn<-transformColumnArray){
                outMap.+(transformColumn->oldDataMap(transformColumn))
                outKey.-(transformColumn)
            }
            (outKey, outMap)
        })

       value.groupByKey(2).map(a => {
            val outputKeys:Map[String,String] = a._1
            val outMap: Map[String, String] = Map()
            var i: Int = 1
            for (value <- a._2) {
                for (transformColumn: String <- transformColumnArray) {
                    outMap.+(transformColumn + i -> value(transformColumn))
                }
                i += 1
            }
            outMap++outputKeys
        })


    }
}
