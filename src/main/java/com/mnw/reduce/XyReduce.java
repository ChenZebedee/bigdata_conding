package com.mnw.reduce;

import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

/**
 * The type Xy reduce.
 *
 * @author shaodi.chen
 * @date 2019 /7/26
 */
public class XyReduce extends TableReducer<Text, MapWritable, NullWritable> {

    @Override
    protected void reduce(Text key, Iterable<MapWritable> values, Context context) {
        int         i                   = 1;
        String[]    transformColumnList = context.getConfiguration().get("transformColumns").split("xx");
        MapWritable outMap              = new MapWritable();
        for (MapWritable value : values) {
            for (String transformColumn : transformColumnList) {
                outMap.put(new Text(transformColumn + i), value.get(new Text(transformColumn)));
            }
            i++;
        }

    }
}
