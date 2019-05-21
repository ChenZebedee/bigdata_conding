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
 * Created by shaodi.chen on 2019/5/7.
 */
public class BqsReduce3 extends Reducer<Text, MapWritable, NullWritable, Text> {
    @Override
    protected void reduce(Text key, Iterable<MapWritable> values, Context context) throws IOException, InterruptedException {
        MapWritable midWritable = new MapWritable();
        for (MapWritable value : values) {
            midWritable.putAll(value);
        }
        String nextKey = HbaseUtils.mapWritableRemoveData(midWritable, ColumnHeadConstant.BQS_QUERY_DATA + "f_query_data_id");
        String head    = HbaseUtils.mapWritableRemoveData(midWritable, ColumnHeadConstant.BQS_QUERY_DATA + "f_event_type");
        midWritable = HbaseUtils.mapWritableAddKeyHead(midWritable, head);
        midWritable.put(new Text(ColumnHeadConstant.BQS_QUERY_DATA + "f_query_data_id"), new Text(nextKey));
        context.write(NullWritable.get(), new Text(TableNameConst.BQS_END + PunctuationConst.SPLITTER_STR + HbaseUtils.mapWritable2JsonString(midWritable)));
    }
}
