package com.mnw.reduce;

import com.alibaba.fastjson.JSON;
import com.mnw.data.constant.PunctuationConst;
import com.mnw.utils.DataUtils;
import com.mnw.utils.HBaseUtils;
import com.mnw.writable.ContactWritable;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Phone 2 h base reduce.
 *
 * @author shaodi.chen
 * @date 2019 /7/30
 */
public class Phone2HBaseReduce extends TableReducer<Text, Text, NullWritable> {

    Connection connection;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        connection = HBaseUtils.getConnection();
    }

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) {
        List<Put> userTablePutList = new ArrayList<>();
        List<Put> wideTablePutList = new ArrayList<>();
        String    userId           = "";
        for (Text value : values) {
            if (DataUtils.isJsonArray(value.toString())) {
                String                valueStr            = DataUtils.phoneFormat(value.toString());
                List<ContactWritable> contactWritableList = JSON.parseArray(valueStr, ContactWritable.class);
                for (ContactWritable contact : contactWritableList) {
                    for (String phone : StringUtils.split(contact.getPhone(), PunctuationConst.COMMA, -1)) {
                        if (DataUtils.isNotPhone(phone)){
                            continue;
                        }
                        String nameColumnData = contact.getName();
                        Put    putContact     = new Put(Bytes.toBytes(phone));
                        putContact.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"), Bytes.toBytes(nameColumnData));
                        userTablePutList.add(putContact);
                    }
                }
            } else {
                userId = value.toString();
            }
        }

        if (StringUtils.equals("",userId)||StringUtils.equals(" ",userId)||userTablePutList.size()<1){
            return;
        }

        for (Put putContact : userTablePutList) {
            Put wideTablePut = new Put(putContact);
            wideTablePut.addColumn(Bytes.toBytes("user"), Bytes.toBytes(userId), Bytes.toBytes("1"));
            wideTablePutList.add(wideTablePut);
        }


        HBaseUtils.saveData2Hbase("userInfo:" + userId, userTablePutList, connection);
        HBaseUtils.saveData2Hbase("3rdapi:phoneWideTable", wideTablePutList, connection);

    }


}
