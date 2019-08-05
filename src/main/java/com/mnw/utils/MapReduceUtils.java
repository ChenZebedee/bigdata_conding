package com.mnw.utils;

import org.apache.hadoop.mapreduce.Job;

import java.io.IOException;

/**
 * Created by shaodi.chen on 2019/5/20.
 */
public class MapReduceUtils {

    public Job getNewJobs() throws IOException {
        return Job.getInstance();
    }
}
