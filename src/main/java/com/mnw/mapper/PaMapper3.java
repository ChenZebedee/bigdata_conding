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
 * Created by shaodi.chen on 2019/4/25.
 */
public class PaMapper3 extends Mapper<LongWritable, Text, Text, MapWritable> {

    private Text        outKey   = new Text();
    private MapWritable outValue = new MapWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String   line       = value.toString();
        String[] columnData = line.split(PunctuationConst.SPLITTER_USE, -1);
        switch (columnData[0]) {
            case TableNameConst.PA_SECOND:
            case TableNameConst.PA_FIRST:
                outValue = HBaseUtils.JsonString2MapWritable(columnData[1]);
//                outKey.set(HbaseUtils.mapWritableRemoveData(outValue, ColumnHeadConstant.T_3RDAPI_PA_OVERDUE_RECORD + "f_query_data_id"));
                outKey.set(HBaseUtils.mapWritableRemoveData(outValue, "f_query_data_id"));
                break;
            case TableNameConst.T_3RDAPI_ORDER_SN_QUERY_DATA:
                outKey.set(columnData[3]);
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_ORDER_SN_QUERY_DATA + "order_sn"), new Text(columnData[1]));
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_ORDER_SN_QUERY_DATA + "borrower_id"), new Text(columnData[2]));
                break;
            case TableNameConst.T_3RDAPI_PA_PHONE_TAG:
                outKey.set(columnData[2]);
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_PA_PHONE_TAG + "f_tag"), new Text(columnData[8]));
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_PA_PHONE_TAG + "f_times"), new Text(columnData[9]));
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
