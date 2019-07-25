package com.zebedee

import java.util.Properties

import com.mnw.data.constant.PunctuationConst
import com.mnw.utils.FileUtils
import org.apache.commons.lang3.StringUtils

/**
  *
  * Created by shaodi.chen on 2019/6/24. 
  *
  */
object TransformColumnToHBase {
    def main(args: Array[String]): Unit = {
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
}
