package com.mnw;

import com.mnw.mapper.BqsMapper1;
import com.mnw.mapper.BqsMapper2;
import com.mnw.mapper.BqsMapper3;
import com.mnw.mapper.BqsMapper4;
import com.mnw.reduce.BqsReduce1;
import com.mnw.reduce.BqsReduce2;
import com.mnw.reduce.BqsReduce3;
import com.mnw.reduce.BqsReduce4;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.chain.ChainReducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Date;

/**
 * Created by shaodi.chen on 2019/4/29.
 */
public class BqsMr extends Configured implements Tool {

    private static final Logger LOGGER = LoggerFactory.getLogger(BqsMr.class);

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
            int status = ToolRunner.run(configuration, new BqsMr(), args);
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

        Date startTime = new Date();
        LOGGER.info(String.valueOf(startTime.getTime()));

        // 1.Get Configuration
        Configuration conf = new Configuration();
        /*conf.set("mapreduce.framework.borrowerName", "local");
        conf.set("fs.defaultFS", "file:///");
        System.setProperty("hadoop.home.dir", "/home/dragon/hadoop-3.1.1");
        System.setProperty("HADOOP_USER_NAME", "hadoop");*/

        //conf.set("inPath1",strings[0]);

        conf.set("cacheFile", strings[0]);
        conf.set("inPath1", strings[1]);
        conf.set("inPath2", strings[2]);
        conf.set("inPath3", strings[3]);
        conf.set("inPath4", strings[4]);
        conf.set("inPath5", strings[5]);
        conf.set("inPath6", strings[6]);
        conf.set(TableOutputFormat.OUTPUT_TABLE, strings[7]);
        conf.set("outPath1", "/bqsOut1/");
        conf.set("outPath2", "/bqsOut2/");
        conf.set("outPath3", "/bqsOut3/");
        conf.set("outPath4", "/bqsOut4/");
        //conf.set("mapreduce.reduce.memory.mb","8190");
        FileSystem dfs       = FileSystem.get(conf);
        URI        cacheFile = new URI("hdfs://data3:9000" + conf.get("cacheFile"));

        // 2.Create Job
        Job job1 = Job.getInstance(conf, "GetBqsData");
        job1.setJarByClass(BqsMr.class);
        //job.setNumReduceTasks(1);
        Path inPath1 = new Path(conf.get("inPath1"));
        job1.addCacheFile(cacheFile);
        FileInputFormat.addInputPath(job1, inPath1);
        FileInputFormat.addInputPath(job1, new Path(conf.get("inPath2")));
        FileInputFormat.addInputPath(job1, new Path(conf.get("inPath3")));
        JobConf mapConf1 = new JobConf(false);
        ChainMapper.addMapper(job1,
                BqsMapper1.class,
                LongWritable.class,
                Text.class,
                Text.class,
                MapWritable.class,
                mapConf1);
        JobConf reduceConf1 = new JobConf(false);
        ChainReducer.setReducer(job1,
                BqsReduce1.class,
                Text.class,
                MapWritable.class,
                NullWritable.class,
                Text.class,
                reduceConf1);
        Path outPath1 = new Path(conf.get("outPath1"));
        if (dfs.exists(outPath1)) {
            dfs.delete(outPath1, true);
        }
        FileOutputFormat.setOutputPath(job1, outPath1);


        Job job2 = Job.getInstance(conf, "joinBqsData");
        job2.setJarByClass(BqsMr.class);
        job2.addCacheFile(cacheFile);
//        job1.setNumReduceTasks(11);
        FileInputFormat.addInputPath(job2, new Path(conf.get("outPath1")));
        FileInputFormat.addInputPath(job2, new Path(conf.get("inPath4")));
        JobConf mapConf2 = new JobConf(false);
        ChainMapper.addMapper(job2,
                BqsMapper2.class,
                LongWritable.class,
                Text.class,
                Text.class,
                MapWritable.class,
                mapConf2);
        JobConf reduceConf2 = new JobConf(false);
        ChainReducer.setReducer(job2,
                BqsReduce2.class,
                Text.class,
                MapWritable.class,
                NullWritable.class,
                Text.class,
                reduceConf2);
        Path outPath2 = new Path(conf.get("outPath2"));
        if (dfs.exists(outPath2)) {
            dfs.delete(outPath2, true);
        }
        FileOutputFormat.setOutputPath(job2, outPath2);


        Job job3 = Job.getInstance(conf, "joinBqsData");
        job3.addCacheFile(cacheFile);
        job3.setJarByClass(BqsMr.class);
//        job1.setNumReduceTasks(11);
        FileInputFormat.addInputPath(job3, new Path(conf.get("outPath2")));
        FileInputFormat.addInputPath(job3, new Path(conf.get("inPath5")));
        JobConf mapConf3 = new JobConf(false);
        ChainMapper.addMapper(job3,
                BqsMapper3.class,
                LongWritable.class,
                Text.class,
                Text.class,
                MapWritable.class,
                mapConf3);
        JobConf reduceConf3 = new JobConf(false);
        ChainReducer.setReducer(job3,
                BqsReduce3.class,
                Text.class,
                MapWritable.class,
                NullWritable.class,
                Text.class,
                reduceConf3);
        Path outPath3 = new Path(conf.get("outPath3"));
        if (dfs.exists(outPath3)) {
            dfs.delete(outPath3, true);
        }
        FileOutputFormat.setOutputPath(job3, outPath3);


        Job job4 = Job.getInstance(conf, "joinBqsData");
        job4.addCacheFile(cacheFile);
        job4.setJarByClass(BqsMr.class);
        FileInputFormat.addInputPath(job4, new Path(conf.get("outPath3")));
        FileInputFormat.addInputPath(job4, new Path(conf.get("inPath6")));

        job4.setMapperClass(BqsMapper4.class);
        job4.setMapOutputKeyClass(Text.class);
        job4.setMapOutputValueClass(MapWritable.class);

        job4.setNumReduceTasks(50);
        job4.setReducerClass(BqsReduce4.class);
        job4.setOutputFormatClass(TableOutputFormat.class);

        Date endTime = new Date();
        LOGGER.info(String.valueOf(endTime.getTime()));
        return (job1.waitForCompletion(true) && job2.waitForCompletion(true)) && job3.waitForCompletion(true) && job4.waitForCompletion(true) ? 0 : 1;
    }
}
