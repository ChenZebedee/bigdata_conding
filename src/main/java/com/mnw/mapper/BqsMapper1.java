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
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shaodi.chen on 2019/4/29.
 */
public class BqsMapper1 extends Mapper<LongWritable, Text, Text, MapWritable> {

    private Text                outKey    = new Text();
    private MapWritable         outValue  = new MapWritable();
    private Map<String, String> cacheData = new HashMap<>();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        URI[] URI = context.getCacheFiles();
        cacheData = HbaseUtils.getBqsCacheMap(URI, context.getConfiguration());
        super.setup(context);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String   line       = value.toString();
        String[] columnData = line.split(PunctuationConst.SPLITTER_USE, -1);
        switch (columnData[0]) {
            case TableNameConst.T_3RDAPI_BQS_RULE:
                outKey.set(columnData[1]);
                outValue.put(new Text(ColumnHeadConstant.BQS_RULE + "f_strategy_id"), new Text(columnData[2]));
                outValue.put(new Text(ColumnHeadConstant.BQS_RULE + "f_rule_id"), new Text(columnData[4]));
                outValue.put(new Text(ColumnHeadConstant.BQS_RULE + "f_score"), new Text(columnData[5]));
                outValue.put(new Text(ColumnHeadConstant.BQS_RULE + "f_decision"), new Text(columnData[6]));
                outValue.put(new Text(ColumnHeadConstant.BQS_RULE + "f_template"), new Text(columnData[7]));
                outValue.put(new Text(ColumnHeadConstant.BQS_RULE + "f_memo"), new Text(columnData[8]));
                break;
            case TableNameConst.T_3RDAPI_BQS_LOSE_CREDIT:
                outKey.set(columnData[2]);
                outValue.put(new Text(ColumnHeadConstant.BQS_LOSE + cacheData.get(columnData[3]) + "__" + cacheData.get(columnData[4]) + "__" + cacheData.get(columnData[5])), new Text("1"));
                break;
            case TableNameConst.T_3RDAPI_BQS_LOAN:
                outKey.set(columnData[2]);
                outValue.put(new Text(ColumnHeadConstant.BQS_LOAN + cacheData.get(columnData[3])), new Text(columnData[5]));
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
