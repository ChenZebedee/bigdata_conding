package com.zebedee

import java.util
import java.util.Properties

import com.alibaba.fastjson.JSON
import com.mnw.data.constant.PunctuationConst
import com.mnw.utils.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  *
  * Created by shaodi.chen on 2019/5/15. 
  *
  */
object ColumnChangeToLine {
  def main(args: Array[String]): Unit = {
    val configFileData1 = getFileData(args(0))
    val conf = new SparkConf().setAppName("paSpark")
    val fileData1 = getRdd4File(configFileData1._1, conf, configFileData1._2, configFileData1._3, configFileData1._4)
    val configFileData2 = getFileData(args(1))
    val fileData2 = getRdd4File(configFileData2._1, conf, configFileData2._2, configFileData2._3, configFileData2._4)
    val outData = fileData1.join(fileData2).map(value => {
      val outKey = value._1
      val outValue = value._2._1 ++ value._2._2
      (outKey, outValue)
    }).reduceByKey((a, b) => a ++ b).map(outData => {
      JSON.toJSONString(outData._2)
    }).saveAsTextFile(args(2))
  }


  def getFileData(prefixName: String): (String, Int, Array[String], Map[Int, String]) = {
    val fileUtils: Properties = FileUtils.getPropertiesData()
    val filePath: String = String.valueOf(fileUtils.get(prefixName + "_file_path"))
    val keyColumnIndex = Integer.parseInt(String.valueOf(fileUtils.get(prefixName + "_key_index")))
    val data4ColumnIndex = fileUtils.get(prefixName + "_column_index")
    val data4ColumnIndexArray: Array[String] = StringUtils.split(String.valueOf(data4ColumnIndex), PunctuationConst.COMMA, -1)
    val data4DataColumnIndex = String.valueOf(fileUtils.get(prefixName + "_data_column"))

    val data4DataColumnMap: Map[Int, String] = Map()
    for (combinationData <- StringUtils.split(data4DataColumnIndex, PunctuationConst.COMMA, -1)) {
      val kvData = StringUtils.split(combinationData, PunctuationConst.VERTICAL_LINE, -1)
      data4DataColumnMap.+(Integer.parseInt(kvData(0)) -> kvData(1))
    }

    (filePath, keyColumnIndex, data4ColumnIndexArray, data4DataColumnMap)
  }


  def getRdd4File(filePath: String, conf: SparkConf, keyIndex: Int, data4ColumnIndex: Array[String], data4DataColumnMap: Map[Int, String]): RDD[(String, Map[String, String])] = {
    val sc = new SparkContext(conf)
    val dataFile = sc.textFile(filePath)
    val value: RDD[(String, Map[String, String])] = dataFile.map(line => line.split(PunctuationConst.SPLITTER_USE)).map(dataArray => {
      val outKey = dataArray(keyIndex)
      val outMap: Map[String, String] = Map()

      val outMapKeyList: util.ArrayList[String] = new util.ArrayList[String]()
      for (columnIndex <- data4ColumnIndex) {
        outMapKeyList.add(dataArray(Integer.parseInt(columnIndex)))
      }

      val headColumnName = StringUtils.join(outMapKeyList, "__")
      for (dataIndex <- data4DataColumnMap.keySet) {
        outMap.+(headColumnName + "__" + data4DataColumnMap.get(dataIndex) -> dataArray(dataIndex))
      }

      (outKey, outMap)
    })

    value
  }


}
