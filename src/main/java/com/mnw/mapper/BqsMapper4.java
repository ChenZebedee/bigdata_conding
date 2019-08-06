package com.mnw.mapper;

import com.mnw.data.constant.ColumnHeadConstant;
import com.mnw.data.constant.PunctuationConst;
import com.mnw.data.constant.TableNameConst;
import com.mnw.utils.HBaseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by shaodi.chen on 2019/5/7.
 */
public class BqsMapper4 extends Mapper<LongWritable, Text, Text, MapWritable> {

    /**
     * The Out key.
     */
    Text        outKey   = new Text();
    /**
     * The Out value.
     */
    MapWritable outValue = new MapWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String   line       = value.toString();
        String[] columnData = line.split(PunctuationConst.SPLITTER_USE, -1);
        switch (columnData[0]) {
            case TableNameConst.BQS_END:
                outValue = HBaseUtils.JsonString2MapWritable(columnData[1]);
                outKey.set(outValue.get(new Text(ColumnHeadConstant.BQS_QUERY_DATA + "f_query_data_id")).toString());
                outValue.remove(new Text(ColumnHeadConstant.BQS_QUERY_DATA + "f_query_data_id"));
                break;
            case TableNameConst.T_3RDAPI_ORDER_SN_QUERY_DATA:
                outKey.set(columnData[3]);
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_ORDER_SN_QUERY_DATA + "order_sn"), new Text(columnData[1]));
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_ORDER_SN_QUERY_DATA + "borrower_id"), new Text(columnData[2]));
                break;
            default:
                outKey.set("N");
                context.getCounter("mapGet", "NoMatch").increment(1);
                break;
        }
        if (!StringUtils.equals(outKey.toString(), "N") || !StringUtils.equals(outKey.toString(), "\\N") || !StringUtils.equals(outKey.toString(), "NULL")) {
            context.write(outKey, outValue);
        }

    }
}
