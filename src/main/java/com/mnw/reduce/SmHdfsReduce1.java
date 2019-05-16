package com.mnw.reduce;

import com.mnw.writable.SmWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Created by shaodi.chen on 2019/4/25.
 */
public class SmHdfsReduce1 extends Reducer<Text, SmWritable, NullWritable, Text> {

}
