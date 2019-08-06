package com.mnw.reduce;

import com.mnw.data.constant.ColumnHeadConstant;
import com.mnw.data.constant.PunctuationConst;
import com.mnw.data.constant.TableNameConst;
import com.mnw.utils.HBaseUtils;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shaodi.chen on 2019/4/30.
 */
public class BqsReduce2 extends Reducer<Text, MapWritable, NullWritable, Text> {

    /**
     * The Cache data.
     */
    Map<String, String> cacheData = new HashMap<>();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        URI[] URI = context.getCacheFiles();
        cacheData = HBaseUtils.getBqsCacheMap(URI, context.getConfiguration());
        super.setup(context);
    }

    @Override
    protected void reduce(Text key, Iterable<MapWritable> values, Context context) throws IOException, InterruptedException {
        MapWritable midWritable = new MapWritable();
        for (MapWritable value : values) {
            midWritable.putAll(value);
        }
        String nextKey = HBaseUtils.mapWritableRemoveData(midWritable, ColumnHeadConstant.BQS_STRATEGY + "f_query_data_id");
        String head    = cacheData.get(HBaseUtils.mapWritableRemoveData(midWritable, ColumnHeadConstant.BQS_STRATEGY + "f_strategy_name"));
        midWritable = HBaseUtils.mapWritableAddKeyHead(midWritable, head);
        midWritable.put(new Text(ColumnHeadConstant.BQS_STRATEGY + "f_query_data_id"), new Text(nextKey));
        context.write(NullWritable.get(), new Text(TableNameConst.BQS_SECOND + PunctuationConst.SPLITTER_STR + HBaseUtils.mapWritable2JsonString(midWritable)));
    }
}
