package com.mnw.mapper;

import com.mnw.data.constant.ColumnHeadConstant;
import com.mnw.data.constant.PunctuationConst;
import com.mnw.data.constant.TableNameConst;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shaodi.chen on 2019/4/25.
 */
public class PaMapper31 extends Mapper<LongWritable, Text, Text, MapWritable> {

    private Map<String, Map<String, Integer>> tableColumnIndex = new HashMap<>();
    private Text                              outKey           = new Text();
    private MapWritable                       outValue         = new MapWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String   line       = value.toString();
        String[] columnData = line.split(PunctuationConst.SPLITTER_USE, -1);
        switch (columnData[0]) {
            case TableNameConst.T_3RDAPI_PA_BLACKLIST_OTHERS:
                outKey.set(columnData[2]);
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_PA_BLACKLIST_OTHERS + "f_org_overdue_period"), new Text(columnData[6]));
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_PA_BLACKLIST_OTHERS + "f_bank_overdue_period"), new Text(columnData[7]));
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_PA_BLACKLIST_OTHERS + "f_org_litigation"), new Text(columnData[8]));
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_PA_BLACKLIST_OTHERS + "f_bank_litigation"), new Text(columnData[9]));
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_PA_BLACKLIST_OTHERS + "f_org_one_month_overdue"), new Text(columnData[10]));
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_PA_BLACKLIST_OTHERS + "f_org_lost_contact"), new Text(columnData[11]));
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_PA_BLACKLIST_OTHERS + "f_bank_lost_contact"), new Text(columnData[12]));
                break;
            case TableNameConst.T_3RDAPI_PA_BLACKLIST_QUERY_DATA:
                outKey.set(columnData[1]);
                outValue.put(new Text("f_query_data_id"), new Text(columnData[2]));
                break;
            default:
                outKey.set("N");
                context.getCounter("mapGet", "smNoMatch").increment(1);
                break;
        }
        if (!StringUtils.equals(outKey.toString(), "N") || !StringUtils.equals(outKey.toString(), "\\N") || !StringUtils.equals(outKey.toString(), "NULL")) {
            context.write(outKey, outValue);
        }
    }
}
