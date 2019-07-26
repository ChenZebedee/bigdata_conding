package com.mnw.mapper;

import com.mnw.data.constant.PunctuationConst;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;


/**
 * Created by shaodi.chen on 2019/7/26.
 */
public class XyMapper extends Mapper<LongWritable, Text, Text, MapWritable> {
    private Text        outKey   = new Text();
    private MapWritable outValue = new MapWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String   line       = value.toString();
        String[] columnData = line.split(PunctuationConst.SPLITTER_USE, -1);
        outKey.set(columnData[0]);

        if (!StringUtils.equals(outKey.toString(), "N") || !StringUtils.equals(outKey.toString(), "\\N") || !StringUtils.equals(outKey.toString(), "NULL")) {
            context.write(outKey, outValue);
        }
    }
}
