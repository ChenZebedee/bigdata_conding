package com.mnw;

import com.mnw.mapper.XyMapper;
import com.mnw.reduce.XyReduce;
import com.mnw.utils.HBaseUtils;
import com.mnw.writable.SmWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.partition.HashPartitioner;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by shaodi.chen on 2019/7/25.
 */
public class XyMr extends Configured implements Tool {
    private static final Logger LOGGER = LoggerFactory.getLogger(XyMr.class);

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
            int status = ToolRunner.run(configuration, new smMr(), args);
            LOGGER.info(String.valueOf(status));
        } catch (Exception e) {
            LOGGER.error("ToolRunner 错误");
            e.printStackTrace();
        }
        Date endTime = new Date();
        LOGGER.info(String.valueOf(endTime.getTime()));
    }

    @Override
    public int run(String[] strings) throws Exception {

        // 1.Get Configuration
        Configuration conf = super.getConf();

        conf = HBaseUtils.systemConf(conf);


        conf.set("inPath1", strings[0]);
        conf.set("inPath2", strings[1]);
        conf.set("WideControlMapreduce.output.fileoutputformat.compress", "false");
        conf.set(TableOutputFormat.OUTPUT_TABLE, strings[2]);

        // 2.Create Job
        Job job = Job.getInstance(conf, "GetXyData");
        job.setJarByClass(getClass());
        job.setPartitionerClass(HashPartitioner.class);

        Path inPath1 = new Path(conf.get("inPath1"));
        FileInputFormat.addInputPath(job, inPath1);
        FileInputFormat.addInputPath(job, new Path(conf.get("inPath2")));
        job.setMapperClass(XyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(SmWritable.class);

        job.setNumReduceTasks(50);
        job.setReducerClass(XyReduce.class);
        job.setOutputFormatClass(TableOutputFormat.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

}
