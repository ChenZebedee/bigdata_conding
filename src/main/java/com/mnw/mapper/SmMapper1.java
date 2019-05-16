package com.mnw.mapper;

import com.mnw.data.constant.PunctuationConst;
import com.mnw.data.constant.TableNameConst;
import com.mnw.writable.SmWritable;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
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
 * The type Sm mapper 1.
 */
public class SmMapper1 extends Mapper<LongWritable, Text, Text, SmWritable> {
    private Map<String, String> cacheData = new HashMap<>();
    private SmWritable          outValue  = new SmWritable();
    private Text                outKey    = new Text();


    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        //预处理，把要关联的文件加载到缓存中
        URI[] URI = context.getCacheFiles();
        //新的检索缓存文件的API是 context.getCacheFiles() ，而 context.getLocalCacheFiles() 被弃用
        //然而 context.getCacheFiles() 返回的是 HDFS 路径； context.getLocalCacheFiles() 返回的才是本地路径
        FileSystem hdfs = FileSystem.get(context.getConfiguration());

        //这里只缓存了一个文件，所以取第一个即可
        String line = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(hdfs.open(new Path(URI[0].getPath())), StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                String[] columnData = StringUtils.split(line, PunctuationConst.SPLITTER_USE, -1);
                if (StringUtils.equals(columnData[0], TableNameConst.T_3RDAPI_SM_RELATION)) {
                    cacheData.put(columnData[1], columnData[2]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setup(context);
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String   line       = value.toString();
        String[] columnData = line.split(PunctuationConst.SPLITTER_USE, -1);
        switch (columnData[0]) {
            case TableNameConst.T_3RDAPI_SM_LOAN:
                //t_3rdapi_sm_loan
                outKey.set(columnData[2]);
                outValue.setSmLoan(columnData[3], columnData[4], columnData[5], columnData[6], columnData[7], columnData[8], columnData[9], columnData[10], columnData[11], columnData[12], cacheData);
                outValue.setTableNameStr(TableNameConst.T_3RDAPI_SM_LOAN);
                context.getCounter("mapGet", "smLoan").increment(1);
                break;
            case TableNameConst.T_3RDAPI_SM_LENDING:
                //t_3rdapi_sm_lending
                outKey.set(columnData[2]);
                outValue.setSmLending(columnData[5], columnData[4]);
                outValue.setTableNameStr(TableNameConst.T_3RDAPI_SM_LENDING);
                context.getCounter("mapGet", "smLending").increment(1);
                break;
            case TableNameConst.T_3RDAPI_SM_HIT:
                outKey.set(columnData[2]);
                outValue.setSmHit(columnData[3], cacheData);
                outValue.setTableNameStr(TableNameConst.T_3RDAPI_SM_HIT);
                context.getCounter("mapGet", TableNameConst.T_3RDAPI_SM_HIT).increment(1);
                break;
            case TableNameConst.T_3RDAPI_ORDER_SN_QUERY_DATA:
                outKey.set(columnData[3]);
                outValue.setOrderSnStr(columnData[1]);
                outValue.setTableNameStr(TableNameConst.T_3RDAPI_ORDER_SN_QUERY_DATA);
                outValue.setBorrowerIdStr(columnData[2]);
                outValue.setOSQD(columnData[1], columnData[2]);
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
