package com.zebedee

import org.apache.spark.{SparkConf, SparkContext}

/**
 *
 * Created by shaodi.chen on 2019/8/21. 
 *
 * @author shaodi.chen
 * @date 2019/8/21
 *
 */
object PhoneFuck {
    def main(args: Array[String]): Unit = {
        val conf = new SparkConf().setAppName("PhoneFuck")
        val sc = new SparkContext(conf)
        val line = sc.textFile("hdfs://192.168.1.71:8082/phoneInput/*")
    }

}
