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

/**
 * Created by shaodi.chen on 2019/4/25.
 */
public class PaMapper1 extends Mapper<LongWritable, Text, Text, MapWritable> {

    private Text                              outKey           = new Text();
    private MapWritable                       outValue         = new MapWritable();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String   line       = value.toString();
        String[] columnData = line.split(PunctuationConst.SPLITTER_USE, -1);
        switch (columnData[0]) {
            case TableNameConst.T_3RDAPI_PA_OVERDUE_CLASSIFICATION:
                outKey.set(columnData[2]);
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_PA_OVERDUE_CLASSIFICATION +  columnData[8] + "__" + columnData[9] + "__f_record_nums"), new Text(columnData[3]));
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_PA_OVERDUE_CLASSIFICATION +  columnData[8] + "__" + columnData[9] + "__f_org_nums"), new Text(columnData[4]));
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_PA_OVERDUE_CLASSIFICATION +  columnData[8] + "__" + columnData[9] + "__f_max_amount"), new Text(columnData[5]));
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_PA_OVERDUE_CLASSIFICATION +  columnData[8] + "__" + columnData[9] + "__f_longest_days"), new Text(columnData[6]));
                break;
            case TableNameConst.T_3RDAPI_PA_OVERDUE_RECORD:
                outKey.set(columnData[1]);
                outValue.put(new Text(ColumnHeadConstant.T_3RDAPI_PA_OVERDUE_RECORD + "f_query_data_id"), new Text(columnData[2]));
                break;
            default:
                outKey.set("N");
                context.getCounter("mapGet", "smNoMatch").increment(1);
                break;
        }
        if (!StringUtils.equals(outKey.toString(), "N") || !StringUtils.equals(outKey.toString(), "\\N") || !StringUtils.equals(outKey.toString(), "NULL")) {
            context.write(outKey, outValue);
            outValue.clear();
        }
    }
}
