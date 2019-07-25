package com.zebedee

import java.util

import com.mnw.data.constant.PunctuationConst
import com.zebedee.ColumnChangeToLine.getFileConfigData
import org.apache.commons.lang3.StringUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
  *
  * Created by shaodi.chen on 2019/7/25. 
  *
  */
object Column2Line {
    def main(args: Array[String]): Unit = {
        val configData = getConfig(args);
        val conf = new SparkConf().setAppName("paSpark")
        val sc = new SparkContext(conf)
        getRdd4File(configData._1,sc,configData._2,configData._3)
    }

    def getConfig(args: Array[String])={
        val filePath:String = args(0);
        val keyColumnIndex:Int = Integer.parseInt(args(1))
        val transformColumnArray:Array[String] = StringUtils.split(args(2),PunctuationConst.VERTICAL_LINE,-1)
        (filePath,keyColumnIndex,transformColumnArray)
    }


    def getRdd4File(filePath: String, sc: SparkContext, keyIndex: Int, data4ColumnIndex: Array[String]): RDD[(String, Map[String, String])] = {
        val dataFile = sc.textFile(filePath)
        val value: RDD[(String, Map[String, String])] = dataFile.map(line =>
            line.split(PunctuationConst.SPLITTER_USE)).map(dataArray => {
            val outKey = dataArray(keyIndex)
            val outMap: Map[String, String] = Map()

            val outMapKeyList: util.ArrayList[String] = new util.ArrayList[String]()
            for (columnIndex <- data4ColumnIndex) {
                outMapKeyList.add(dataArray(Integer.parseInt(columnIndex)))
            }

            val headColumnName = StringUtils.join(outMapKeyList, "__")


            (outKey, outMap)
        })

        value
    }

}
