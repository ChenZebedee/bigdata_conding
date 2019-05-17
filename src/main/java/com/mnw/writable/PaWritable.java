package com.mnw.writable;

import com.mnw.utils.HBaseUtils;
import lombok.Data;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by shaodi.chen on 2019/4/23.
 */
@Data
public class PaWritable implements WritableComparable<PaWritable> {
    private static final Logger LOGGER    = LoggerFactory.getLogger(PaWritable.class);
    private              Text   tableName = new Text("N");
    private              Text   orderSn   = new Text("N");

    private Text        outText            = new Text("N");
    private MapWritable outDataMapWritable = new MapWritable();


    /**
     * Map 2 put put.
     *
     * @return the put
     */
    public Put map2Put() {
        return HBaseUtils.map2Put(orderSn, tableName, outDataMapWritable);
    }


    @Override
    public int compareTo(PaWritable o) {
        return 1;
    }

    @Override
    public void write(DataOutput out) throws IOException {

    }

    @Override
    public void readFields(DataInput in) throws IOException {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PaWritable)) return false;

        PaWritable that = (PaWritable) o;

        return true;

    }

    @Override
    public int hashCode() {
        return 1;
    }


    /**
     * To stringother string.
     *
     * @return the string
     */
    public String toStringother() {
        final StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

    /**
     * Set order sn str.
     *
     * @param orderSnStr the order sn str
     */
    public void setOrderSnStr(String orderSnStr) {
        orderSn.set(orderSnStr);
        addData2Map("orderSn", orderSnStr);
    }

    /**
     * Set table name str.
     *
     * @param tableNameStr the table name str
     */
    public void setTableNameStr(String tableNameStr) {
        tableName.set(tableNameStr);

    }

    /**
     * Add data 2 map.
     *
     * @param key  the key
     * @param data the data
     */
    public void addData2Map(String key, String data) {
        outDataMapWritable.put(new Text(key), new Text(data));
    }

    /**
     * Add other out map writable.
     *
     * @param otherDataMapWritable the other data map writable
     */
    public void addOtherOutMapWritable(MapWritable otherDataMapWritable) {
        outDataMapWritable.putAll(otherDataMapWritable);
    }

    /**
     * Gets put.
     *
     * @return the put
     */
    public Put getPut() {
        Put put = HBaseUtils.map2Put(orderSn, tableName, outDataMapWritable);
        return put;
    }
}
