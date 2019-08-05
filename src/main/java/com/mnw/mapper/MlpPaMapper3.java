package com.mnw.mapper;

import com.mnw.data.constant.ColumnHeadConstant;
import com.mnw.data.constant.PunctuationConst;
import com.mnw.data.constant.TableNameConst;
import com.mnw.utils.HbaseUtils;
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
 * Created by shaodi.chen on 2019/4/25.
 */
public class MlpPaMapper3 extends Mapper<LongWritable, Text, Text, MapWritable> {

    private Text                              outKey           = new Text();
    private MapWritable                       outValue         = new MapWritable();
    Map<String, String> keyMap = new HashMap<>();


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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(hdfs.open(new Path(URI[0].getPath())), StandardCharsets.UTF_8))) {
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
        switch (columnData[0]) {
            case TableNameConst.PA_SECOND:
            case TableNameConst.PA_FIRST:
                outValue = HbaseUtils.JsonString2MapWritable(columnData[1]);
                String getKey = HbaseUtils.mapWritableRemoveData(outValue,"f_query_data_id");
                outKey.set(keyMap.containsKey(getKey)?keyMap.get(getKey):"NULL");
                break;
            case TableNameConst.T_3RDAPI_PA_PHONE_TAG:
                outKey.set(keyMap.containsKey(columnData[2])?keyMap.get(columnData[2]):"NULL");
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
