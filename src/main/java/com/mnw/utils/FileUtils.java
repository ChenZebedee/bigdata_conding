package com.mnw.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by shaodi.chen on 2019/5/16.
 */
public class FileUtils {

    static Properties properties = new Properties();
    static {
        InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream("table-info.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get properties data properties.
     *
     * @param fileName the file name
     * @return the properties
     */
    public static Properties getPropertiesData() {

        return properties;
    }

}
