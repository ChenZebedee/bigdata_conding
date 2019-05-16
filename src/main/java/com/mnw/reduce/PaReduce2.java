package com.mnw.reduce;

import com.mnw.data.constant.ColumnHeadConstant;
import com.mnw.data.constant.PunctuationConst;
import com.mnw.data.constant.TableNameConst;
import com.mnw.utils.HbaseUtils;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by shaodi.chen on 2019/4/25.
 */
public class PaReduce2 extends Reducer<Text, MapWritable, NullWritable, Text> {
    @Override
    protected void reduce(Text key, Iterable<MapWritable> values, Context context) throws IOException, InterruptedException {
        MapWritable midWritable = new MapWritable();
        for (MapWritable value : values) {
            midWritable.putAll(value);
        }
        String nextKey = HbaseUtils.mapWritableRemoveData(midWritable, ColumnHeadConstant.T_3RDAPI_PA_OVERDUE_QUERY_DATA + "f_query_data_id");
        context.write(NullWritable.get(), new Text(TableNameConst.PA_SECOND + PunctuationConst.SPLITTER_STR + HbaseUtils.mapWritable2JsonString(midWritable)));


    }
}
