package com.mnw;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 *
 * @author shaodi.chen
 * @date 2019/7/30
 */
public class Phone4UserSum extends Configured implements Tool {


    private static final Logger LOGGER = LoggerFactory.getLogger(Phone4UserSum.class);


    public static void main(String[] args) {
        Date startTime = new Date();
        LOGGER.info(String.valueOf(startTime.getTime()));
        Configuration configuration = new Configuration();
        try {
            int status = ToolRunner.run(configuration, new Phone4UserSum(), args);
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
        return 0;
    }
}
