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
 * Created by shaodi.chen on 2019/4/30.
 */
public class BqsMapper2 extends Mapper<LongWritable, Text, Text, MapWritable> {

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
            case TableNameConst.BQS_FIRST:
                outValue = HbaseUtils.JsonString2MapWritable(columnData[1]);
                outKey.set(outValue.get(new Text("rule__f_strategy_id")).toString());
                outValue.remove(new Text("rule__f_strategy_id"));
                break;
            case TableNameConst.T_3RDAPI_BQS_STRATEGY:
                outKey.set(columnData[1]);
                outValue.put(new Text(ColumnHeadConstant.BQS_STRATEGY + "f_query_data_id"), new Text(columnData[2]));
                outValue.put(new Text(ColumnHeadConstant.BQS_STRATEGY + "f_strategy_name"), new Text(columnData[3]));
                outValue.put(new Text(ColumnHeadConstant.BQS_STRATEGY + "f_strategy_decision"), new Text(columnData[5]));
                outValue.put(new Text(ColumnHeadConstant.BQS_STRATEGY + "f_strategy_mode"), new Text(columnData[6]));
                outValue.put(new Text(ColumnHeadConstant.BQS_STRATEGY + "f_strategy_score"), new Text(columnData[7]));
                outValue.put(new Text(ColumnHeadConstant.BQS_STRATEGY + "f_reject_value"), new Text(columnData[8]));
                outValue.put(new Text(ColumnHeadConstant.BQS_STRATEGY + "f_review_value"), new Text(columnData[9]));
                outValue.put(new Text(ColumnHeadConstant.BQS_STRATEGY + "f_risk_type"), new Text(columnData[10]));
                outValue.put(new Text(ColumnHeadConstant.BQS_STRATEGY + "f_tips"), new Text(columnData[11]));
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
