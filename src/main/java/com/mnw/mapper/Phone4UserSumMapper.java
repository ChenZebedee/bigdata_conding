package com.mnw.mapper;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 *
 * @author shaodi.chen
 * @date 2019/7/31
 */
public class Phone4UserSumMapper extends TableMapper<ImmutableBytesWritable, Put> {

    private ImmutableBytesWritable outKey = new ImmutableBytesWritable();

    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        outKey=key;
        long sum = 0;
        for (Cell data:value.rawCells()){
            sum+=Double.parseDouble(Bytes.toString(CellUtil.cloneValue(data)));
        }
        Put outValue = new Put(key.get());
        outValue.addColumn(Bytes.toBytes("info"),Bytes.toBytes("sum"),Bytes.toBytes(sum));

        context.write(outKey,outValue);
    }
}
