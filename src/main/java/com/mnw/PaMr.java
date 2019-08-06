package com.mnw;

import com.mnw.mapper.*;
import com.mnw.reduce.*;
import com.mnw.utils.HBaseUtils;
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
 * Created by shaodi.chen on 2019/4/25.
 */
public class PaMr extends Configured implements Tool {

    private static final Logger LOGGER = LoggerFactory.getLogger(smMr.class);

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
            int status = ToolRunner.run(configuration, new PaMr(), args);
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
        conf.set("inPath3", strings[2]);
        conf.set("inPath4", strings[3]);
        conf.set("inPath5", strings[4]);
        conf.set("inPath6", strings[5]);
        conf.set("inPath7", strings[6]);
        conf.set("inPath8", strings[7]);
        conf.set("inPath9", strings[8]);
        conf.set("inPath10", strings[9]);
        conf.set(TableOutputFormat.OUTPUT_TABLE, strings[10]);
        conf.set("ColumnName", strings[11]);
        conf.set("outPath1", "/OVERDUE_GET1");
        conf.set("outPath2", "/OVERDUE_GET2");
        conf.set("outPath3", "/LOAN_GET1");
        conf.set("outPath4", "/LOAN_GET2");
        conf.set("outPath5", "/BLACKLIST_GET");
        FileSystem dfs = FileSystem.get(conf);

        // 2.Create Job
        Job job1 = Job.getInstance(conf, "OVERDUE_GET1");
        job1.setJarByClass(PaMr.class);
        //job.setNumReduceTasks(1);
        Path inPath1 = new Path(conf.get("inPath1"));
        FileInputFormat.addInputPath(job1, inPath1);
        FileInputFormat.addInputPath(job1, new Path(conf.get("inPath2")));
        JobConf mapConf1 = new JobConf(false);
        ChainMapper.addMapper(job1,
                PaMapper1.class,
                LongWritable.class,
                Text.class,
                Text.class,
                MapWritable.class,
                mapConf1);
        JobConf reduceConf1 = new JobConf(false);
        ChainReducer.setReducer(job1,
                PaReduce1.class,
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


        Job job2 = Job.getInstance(conf, "OVERDUE_GET2");
        job2.setJarByClass(PaMr.class);
        //job.setNumReduceTasks(1);
        FileInputFormat.addInputPath(job2, new Path(conf.get("inPath3")));
        FileInputFormat.addInputPath(job2, new Path(conf.get("outPath1")));
        JobConf mapConf2 = new JobConf(false);
        ChainMapper.addMapper(job2,
                PaMapper2.class,
                LongWritable.class,
                Text.class,
                Text.class,
                MapWritable.class,
                mapConf2);
        JobConf reduceConf2 = new JobConf(false);
        ChainReducer.setReducer(job2,
                PaReduce2.class,
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


        Job job3 = Job.getInstance(conf, "LOAN_GET1");
        job3.setJarByClass(PaMr.class);
        //job.setNumReduceTasks(1);
        FileInputFormat.addInputPath(job3, new Path(conf.get("inPath4")));
        FileInputFormat.addInputPath(job3, new Path(conf.get("inPath5")));
        JobConf mapConf3 = new JobConf(false);
        ChainMapper.addMapper(job3,
                PaMapper21.class,
                LongWritable.class,
                Text.class,
                Text.class,
                MapWritable.class,
                mapConf3);
        JobConf reduceConf3 = new JobConf(false);
        ChainReducer.setReducer(job3,
                PaReduce21.class,
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


        Job job4 = Job.getInstance(conf, "LOAN_GET2");
        job4.setJarByClass(PaMr.class);
        //job.setNumReduceTasks(1);
        FileInputFormat.addInputPath(job4, new Path(conf.get("outPath3")));
        FileInputFormat.addInputPath(job4, new Path(conf.get("inPath6")));
        JobConf mapConf4 = new JobConf(false);
        ChainMapper.addMapper(job4,
                PaMapper22.class,
                LongWritable.class,
                Text.class,
                Text.class,
                MapWritable.class,
                mapConf4);
        JobConf reduceConf4 = new JobConf(false);
        ChainReducer.setReducer(job4,
                PaReduce22.class,
                Text.class,
                MapWritable.class,
                NullWritable.class,
                Text.class,
                reduceConf4);
        Path outPath4 = new Path(conf.get("outPath4"));
        if (dfs.exists(outPath4)) {
            dfs.delete(outPath4, true);
        }
        FileOutputFormat.setOutputPath(job4, outPath4);


        Job job5 = Job.getInstance(conf, "BLACKLIST_GET");
        job5.setJarByClass(PaMr.class);
        //job.setNumReduceTasks(1);
        FileInputFormat.addInputPath(job5, new Path(conf.get("inPath7")));
        FileInputFormat.addInputPath(job5, new Path(conf.get("inPath8")));
        JobConf mapConf5 = new JobConf(false);
        ChainMapper.addMapper(job5,
                PaMapper31.class,
                LongWritable.class,
                Text.class,
                Text.class,
                MapWritable.class,
                mapConf5);
        JobConf reduceConf5 = new JobConf(false);
        ChainReducer.setReducer(job5,
                PaReduce31.class,
                Text.class,
                MapWritable.class,
                NullWritable.class,
                Text.class,
                reduceConf5);
        Path outPath5 = new Path(conf.get("outPath5"));
        if (dfs.exists(outPath5)) {
            dfs.delete(outPath5, true);
        }
        FileOutputFormat.setOutputPath(job5, outPath5);


        Job job6 = Job.getInstance(conf, "outPaData");
        job6.setJarByClass(PaMr.class);
        FileInputFormat.addInputPath(job6, new Path(conf.get("outPath5") + "/par*"));
        FileInputFormat.addInputPath(job6, new Path(conf.get("outPath4") + "/par*"));
        FileInputFormat.addInputPath(job6, new Path(conf.get("outPath2") + "/par*"));

        FileInputFormat.addInputPath(job6, new Path(conf.get("inPath9")));

        if (strings[12].equals("mlp")) {
            job6.setJobName("mlpOutPaData");
            job6.addCacheFile(new URI("hdfs://data3:9000" + conf.get("inPath10")));

            job6.setMapperClass(MlpPaMapper3.class);
            job6.setMapOutputKeyClass(Text.class);
            job6.setMapOutputValueClass(MapWritable.class);

            job6.setReducerClass(MlpPaReduce3.class);
            job6.setOutputFormatClass(TableOutputFormat.class);

        } else {
            FileInputFormat.addInputPath(job6, new Path(conf.get("inPath10")));
            job6.setMapperClass(PaMapper3.class);
            job6.setMapOutputKeyClass(Text.class);
            job6.setMapOutputValueClass(MapWritable.class);

            job6.setNumReduceTasks(50);
            job6.setReducerClass(PaReduce3.class);
            job6.setOutputFormatClass(TableOutputFormat.class);
        }


        return (((job1.waitForCompletion(true) && job2.waitForCompletion(true)) & (job3.waitForCompletion(true) && job4.waitForCompletion(true)) & (job5.waitForCompletion(true))) && job6.waitForCompletion(true)) ? 0 : 1;

    }

}
