package com.mnw.mapper;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.Map;

/**
 *
 * @author shaodi.chen
 * @date 2019/7/31
 */
public class CollisionMapper extends TableMapper<Text,Text> {

    Text outKey = new Text();
    Text outValue = new Text();

    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        outKey.set(Bytes.toString(key.get()));
        MapWritable mapWritable1 = new MapWritable();
        value.rawCells();

    }
}
