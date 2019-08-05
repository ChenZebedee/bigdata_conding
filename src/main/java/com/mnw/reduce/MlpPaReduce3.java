package com.mnw.reduce;

import com.mnw.data.constant.DataConstant;
import com.mnw.utils.HbaseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

/**
 * Created by shaodi.chen on 2019/4/25.
 */
public class MlpPaReduce3 extends TableReducer<Text, MapWritable, NullWritable> {
    @Override
    protected void reduce(Text key, Iterable<MapWritable> values, Context context) throws IOException, InterruptedException {
        Configuration configuration = context.getConfiguration();
        MapWritable   midWritable   = new MapWritable();
        for (MapWritable value : values) {
            midWritable.putAll(value);
        }
        String rowKey = key.toString();
        if (!StringUtils.equals(rowKey, DataConstant.NULL_STR)) {
            Put put = HbaseUtils.map2Put(new Text(rowKey), new Text(configuration.get("ColumnName")), midWritable);
            if (!put.isEmpty()) {
                context.write(NullWritable.get(), put);
            }
        }


    }
}
