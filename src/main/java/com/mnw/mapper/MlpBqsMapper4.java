package com.mnw.mapper;

import com.mnw.data.constant.ColumnHeadConstant;
import com.mnw.data.constant.PunctuationConst;
import com.mnw.data.constant.TableNameConst;
import com.mnw.utils.HBaseUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shaodi.chen on 2019/5/7.
 */
public class MlpBqsMapper4 extends Mapper<LongWritable, Text, Text, MapWritable> {

    /**
     * The Out key.
     */
    Text                outKey   = new Text();
    /**
     * The Out value.
     */
    MapWritable         outValue = new MapWritable();
    /**
     * The Key map.
     */
    Map<String, String> keyMap   = new HashMap<>();


    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        URI[]      URI  = context.getCacheFiles();
        FileSystem hdfs = null;
        try {
            hdfs = FileSystem.get(context.getConfiguration());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(hdfs.open(new Path(URI[1].getPath())), StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                String[] columnData = StringUtils.split(line, PunctuationConst.SPLITTER_USE, -1);
                keyMap.put(columnData[2], columnData[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String   line       = value.toString();
        String[] columnData = line.split(PunctuationConst.SPLITTER_USE, -1);
        if (columnData[0].equals(TableNameConst.BQS_END)) {
            outValue = HBaseUtils.JsonString2MapWritable(columnData[1]);
            String getKey = outValue.get(new Text(ColumnHeadConstant.BQS_QUERY_DATA + "f_query_data_id")).toString();
            outKey.set(keyMap.containsKey(getKey) ? keyMap.get(getKey) : "N");
            outValue.remove(new Text(ColumnHeadConstant.BQS_QUERY_DATA + "f_query_data_id"));
        }
        if (StringUtils.equals(outKey.toString(), "N") || StringUtils.equals(outKey.toString(), "\\N") || StringUtils.equals(outKey.toString(), "NULL")) {
            return;
        }
        context.write(outKey, outValue);

    }
}
