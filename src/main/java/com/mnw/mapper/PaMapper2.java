package com.mnw.mapper;

import com.mnw.data.constant.ColumnHeadConstant;
import com.mnw.data.constant.PunctuationConst;
import com.mnw.data.constant.TableNameConst;
import com.mnw.utils.HbaseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by shaodi.chen on 2019/4/25.
 */
public class PaMapper2 extends Mapper<LongWritable, Text, Text, MapWritable> {

    private Text                              outKey           = new Text();
    private MapWritable                       outValue         = new MapWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String   line       = value.toString();
        String[] columnData = line.split(PunctuationConst.SPLITTER_USE, -1);
        switch (columnData[0]) {
            case TableNameConst.PA_FIRST:
                outValue = HbaseUtils.JsonString2MapWritable(columnData[1]);
                outKey.set(HbaseUtils.mapWritableRemoveData(outValue, ColumnHeadConstant.T_3RDAPI_PA_OVERDUE_RECORD + "f_query_data_id"));
                break;
            case TableNameConst.T_3RDAPI_PA_OVERDUE_QUERY_DATA:
                outKey.set(columnData[1]);
                outValue.put(new Text( "f_query_data_id"), new Text(columnData[2]));
                break;
            default:
                outKey.set("N");
                context.getCounter("mapGet", "pnNoMatch").increment(1);
                break;
        }
        if (!StringUtils.equals(outKey.toString(), "N") || !StringUtils.equals(outKey.toString(), "\\N") || !StringUtils.equals(outKey.toString(), "NULL")) {
            context.write(outKey, outValue);
            outValue.clear();
        }
    }
}
