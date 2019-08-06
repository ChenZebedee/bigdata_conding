package com.mnw;

import com.mnw.utils.HBaseUtils;
import org.apache.hadoop.hbase.client.Connection;

import java.io.IOException;
import java.util.List;

/**
 * Created by shaodi.chen on 2019/8/5.
 */
public class PhoneSum {
    public static void main(String[] args) {
        Connection connection = null;
        try {
            connection = HBaseUtils.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> rowKeyList = HBaseUtils.queryTableRowKey("3rdapi:phoneWideTable", connection);
        HBaseUtils.sumExistsColumn("3rdapi:phoneWideTable", "user", "info", "sum", rowKeyList);
    }
}
