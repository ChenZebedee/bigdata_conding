package com.mnw.reduce;

import com.alibaba.fastjson.JSON;
import com.mnw.utils.DataUtils;
import com.mnw.utils.HbaseUtils;
import com.mnw.writable.ContactWritable;
import com.mnw.writable.PhoneWritable;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

import javax.xml.crypto.Data;
import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Phone 2 h base reduce.
 *
 * @author shaodi.chen
 * @date 2019 /7/30
 */
public class Phone2HBaseReduce extends Reducer<Text, Text, NullWritable, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) {
        List<Put> userTablePutList = new ArrayList<>();
        List<Put> wideTablePutList = new ArrayList<>();
        String    userId           = "";
        for (Text value : values) {
            String valueIn = value.toString();
            if (DataUtils.isJson(valueIn)) {
                String valueStr = DataUtils.phoneFormat(valueIn);
                PhoneWritable contactList = JSON.parseObject(valueStr, PhoneWritable.class);
                for (ContactWritable contact : contactList.getPhoneMapList()) {
                    String rowKeyStr      = contact.getPhone();
                    String nameColumnData = contact.getName();
                    Put    putContact     = new Put(rowKeyStr.getBytes());
                    putContact.addColumn("info".getBytes(), "name".getBytes(), nameColumnData.getBytes());
                    userTablePutList.add(putContact);
                }
            } else {
                userId = valueIn;
            }
        }

        for (Put putContact : userTablePutList) {
            Put wideTablePut = new Put(putContact);
            wideTablePut.addColumn("user".getBytes(), userId.getBytes(), "1".getBytes());
            wideTablePutList.add(wideTablePut);
        }

        HbaseUtils.saveData2Hbase("userInfo:" + userId, userTablePutList);
        HbaseUtils.saveData2Hbase("3rdapi:phoneWideTable", wideTablePutList);

    }

}
