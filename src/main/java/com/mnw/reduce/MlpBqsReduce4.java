package com.mnw.reduce;


import com.mnw.data.constant.DataConstant;
import com.mnw.utils.HBaseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by shaodi.chen on 2019/5/7.
 */
public class MlpBqsReduce4 extends TableReducer<Text, MapWritable, NullWritable> {

    @Override
    protected void reduce(Text key, Iterable<MapWritable> values, Reducer.Context context) throws IOException, InterruptedException {
        MapWritable midWritable = new MapWritable();
        for (MapWritable value : values) {
            midWritable.putAll(value);
        }

        String rowKey = key.toString();
        if (!StringUtils.equals(rowKey, DataConstant.NULL_STR)) {
            Put put = HBaseUtils.map2Put(new Text(rowKey), new Text("bqsOut"), midWritable);
            if (!put.isEmpty()) {
                context.write(NullWritable.get(), put);
            }
        }
    }
}
