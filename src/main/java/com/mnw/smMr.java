package com.mnw;

import com.mnw.mapper.SmMapper1;
import com.mnw.reduce.SmReduce1;
import com.mnw.utils.HbaseUtils;
import com.mnw.writable.SmWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
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

import java.net.URI;
import java.util.Date;

/**
 * The type Sm mr.
 */
@SuppressWarnings("ALL")
public class smMr extends Configured implements Tool {
    private static final Logger LOGGER = LoggerFactory.getLogger(smMr.class);

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        Date startTime = new Date();
        LOGGER.info(String.valueOf(startTime.getTime()));
//        System.setProperty("HADOOP_USER_NAME", "hadoop");
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

        //远程debug
//        System.setProperty("hadoop.home.dir", "E:\\hadoop");
//        conf.set("mapreduce.framework.name", "yarn");
//        conf.set("fs.default.name", "hdfs://data3:9000");
//        conf.set("mapreduce.app-submission.cross-platform", "true");//跨平台提交

        conf = HbaseUtils.systemConf(conf);


        conf.set("inPath1", strings[0]);
        conf.set("inPath2", strings[1]);
        conf.set("inPath3", strings[2]);
        conf.set("inPath4", strings[3]);
        conf.set("inPath5", strings[4]);
        conf.set("WideControlMapreduce.output.fileoutputformat.compress", "false");
        conf.set(TableOutputFormat.OUTPUT_TABLE, strings[5]);
        FileSystem dfs = FileSystem.get(conf);

        // 2.Create Job
        Job job       = Job.getInstance(conf, "GetSmData");
        URI cacheFile = new URI("hdfs://data3:9000" + conf.get("inPath1"));
        job.addCacheFile(cacheFile);
        job.setJarByClass(getClass());
        job.setPartitionerClass(HashPartitioner.class);

        Path inPath1 = new Path(conf.get("inPath2"));
        FileInputFormat.addInputPath(job, inPath1);
        FileInputFormat.addInputPath(job, new Path(conf.get("inPath3")));
        FileInputFormat.addInputPath(job, new Path(conf.get("inPath4")));
        FileInputFormat.addInputPath(job, new Path(conf.get("inPath5")));
        job.setMapperClass(SmMapper1.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(SmWritable.class);

        job.setNumReduceTasks(50);
        job.setReducerClass(SmReduce1.class);
        job.setOutputFormatClass(TableOutputFormat.class);


        /*Path outPath1 = new Path(conf.get("smOut"));
        if (dfs.exists(outPath1)) {
            dfs.delete(outPath1, true);
        }*/
//        FileOutputFormat.setOutputPath(job, outPath1);

        return job.waitForCompletion(true) ? 0 : 1;
    }


}
