package com.mnw.mapper;

import com.mnw.data.constant.PunctuationConst;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * The type Phone 2 h base mapper.
 *
 * @author shaodi.chen
 * @date 2019 /7/30
 */
public class Phone2HBaseMapper extends Mapper<LongWritable, Text, Text, Text> {
    private Text outKey   = new Text();
    private Text outValue = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] line = StringUtils.split(value.toString(), PunctuationConst.TABS, -1);
        if (line.length == 1) {
            line = StringUtils.split(value.toString(), PunctuationConst.COMMA, -1);
        }
        if (PunctuationConst.FOUR_INT == line.length) {
            outKey.set(line[4]);
            outValue.set(line[1]);
        } else if (PunctuationConst.THREE_INT == line.length) {
            outKey.set(line[1]);
            outValue.set(line[3]);
        } else {
            context.getCounter("noMatch", "nulber").increment(1);
        }
        context.write(outKey, outValue);
    }
}
