package com.mnw;

import com.mnw.mapper.Phone2HBaseMapper;
import com.mnw.reduce.Phone2HBaseReduce;
import com.mnw.utils.HbaseUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * The type Phone 2 hbase mr.
 * 数据导入两张 HBase 表的 MapReduce
 *
 * @author shaodi.chen
 * @date 2019 /7/30
 */
public class Phone2HBaseMr extends Configured implements Tool {

    private static final Logger LOGGER = LoggerFactory.getLogger(Phone2HBaseMr.class);

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        Date startTime = new Date();
        LOGGER.info(String.valueOf(startTime.getTime()));
        Configuration configuration = new Configuration();
        try {
            int status = ToolRunner.run(configuration, new Phone2HBaseMr(), args);
            LOGGER.info(String.valueOf(status));
        } catch (Exception e) {
            LOGGER.error("ToolRunner 错误");
            e.printStackTrace();
        }
        Date endTime = new Date();
        LOGGER.info(String.valueOf(endTime.getTime()));
    }

    @Override
    public int run(String[] args) throws Exception {
        Date startTime = new Date();
        LOGGER.info(String.valueOf(startTime.getTime()));

        // 1.Get Configuration
        Configuration conf = new Configuration();
        conf.set("mapred.job.queue.name", "hadoop");

        conf.set("inPath1", args[1]);
        conf.set("inPath2", args[2]);
        conf.set("outPath1", "/bqsOut1/");
        //conf.set("mapreduce.reduce.memory.mb","8190");

        // 2.Create Job
        HbaseUtils.systemConf(conf);
        Job job1 = Job.getInstance(conf, "Phone2HBase");
        job1.setJarByClass(MlpBqsMr.class);
//        job1.setNumReduceTasks(8);
        Path inPath1 = new Path(conf.get("inPath1"));
        FileInputFormat.addInputPath(job1, inPath1);
        FileInputFormat.addInputPath(job1, new Path(conf.get("inPath2")));

        job1.setMapperClass(Phone2HBaseMapper.class);
        job1.setMapOutputKeyClass(Text.class);
        job1.setMapOutputValueClass(MapWritable.class);

        job1.setReducerClass(Phone2HBaseReduce.class);
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(Text.class);

        Date endTime = new Date();
        LOGGER.info(String.valueOf(endTime.getTime()));
        return (job1.waitForCompletion(true)) ? 0 : 1;

    }
}
