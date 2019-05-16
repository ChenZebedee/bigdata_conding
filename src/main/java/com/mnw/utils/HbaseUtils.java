package com.mnw.utils;

import com.alibaba.fastjson.JSON;
import com.mnw.data.config.HBaseData;
import com.mnw.data.constant.DataConstant;
import com.mnw.data.constant.PunctuationConst;
import com.mnw.data.constant.TableNameConst;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by shaodi.chen on 2019/4/17.
 */
public class HbaseUtils {

    /**
     * Gets row key.
     *
     * @param indexs the indexs
     * @param fields the fields
     * @return the row key
     */
    public static String getRowKey(String indexs, String[] fields) {
        return fields[Integer.parseInt(indexs)];
    }

    /**
     * Data 2 put put.
     *
     * @param hBaseData the h base data
     * @return the put
     */
    public static Put data2Put(HBaseData hBaseData) {
        Put put = new Put(hBaseData.getRowKey().getBytes());
        for (Map.Entry<String, String> echoData : hBaseData.getDataMap().entrySet()) {
            put.addColumn(hBaseData.getColumnsName().getBytes(), echoData.getKey().getBytes(), echoData.getValue().getBytes());
        }
        return put;
    }

    /**
     * Row 2 column map.
     *
     * @param data          the data
     * @param indexs        the indexs
     * @param columnMapping the column mapping
     * @return the map
     */
    public static Map<String, String> row2Column(String data, List<Integer> indexs, Map<String, String> columnMapping) {
        List<String> dataList = Arrays.asList(StringUtils.split(data, PunctuationConst.COMMA, -1));
        String       value    = dataList.get(indexs.get(indexs.size() - 1));
        indexs.remove(indexs.size() - 1);
        List<String> columnList = new ArrayList<>();
        for (int index : indexs) {
            columnList.add(columnMapping.get(dataList.get(index)));
        }
        Map<String, String> outMap = new HashMap<>();
        outMap.put(StringUtils.join(columnList, PunctuationConst.DOUBLE_UNDERLINE), value);
        return outMap;
    }

    /**
     * Get properties data properties.
     *
     * @param fileName the file name
     * @return the properties
     */
    public static Properties getPropertiesData(String fileName) {
        InputStream inStream   = HbaseUtils.class.getClassLoader().getResourceAsStream(fileName);
        Properties  properties = new Properties();
        try {
            properties.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * Map 2 put put.
     *
     * @param OrderSn            the order sn
     * @param tableName          the table name
     * @param outDataMapWritable the out data map writable
     * @return the put
     */
    public static Put map2Put(Text OrderSn, Text tableName, MapWritable outDataMapWritable) {
        if (StringUtils.equals(OrderSn.toString(), DataConstant.NULL_STR)) {
            return new Put(OrderSn.getBytes());
        }
        Put put = new Put(OrderSn.toString().getBytes());
        for (Map.Entry otherDataEntry : outDataMapWritable.entrySet()) {
            put.addColumn(tableName.getBytes(), otherDataEntry.getKey().toString().getBytes(), otherDataEntry.getValue().toString().getBytes());
        }
        return put;
    }

    /**
     * Gets connection.
     *
     * @return the connection
     * @throws IOException the io exception
     */
    public static Connection getConnection() throws IOException {
        return ConnectionFactory.createConnection(systemConf(new Configuration()));
    }

    /**
     * System conf configuration.
     *
     * @param configuration the configuration
     * @return the configuration
     */
    public static Configuration systemConf(Configuration configuration) {
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.zookeeper.quorum", "192.168.1.71,192.168.1.72,192.168.1.73");
        configuration.set("hbase.master", "192.168.1.72:60010");
        configuration.set("mapreduce.output.fileoutputformat.compress", "false");
        configuration.set("mapreduce.map.memory.mb", "8192");
        configuration.set("mapreduce.reduce.memory.mb", "4096");
        configuration.set("mapreduce.map.java.opts", "-Xmx7152m");
        configuration.set("mapreduce.reduce.java.opts", "-Xmx3152m");
        configuration.set("mapred.max.split.size", "83886080");
        configuration.set("mapred.min.split.size.per.node", "83886080");
        configuration.set("mapred.min.split.size.per.rack", "83886080");
        configuration.set("mapreduce.task.timeout", "1800000");
        return configuration;
    }


    /**
     * Qurry table test batch map.
     *
     * @param tableName  the table name
     * @param rowkeyList the rowkey list
     * @return the map
     * @throws IOException the io exception
     */
    public static Map<String, Map<String, String>> qurryTableTestBatch(String tableName, List<String> rowkeyList) throws IOException {
        List<Get>                        getList    = new ArrayList();
        Connection                       connection = getConnection();
        Table                            table      = connection.getTable(TableName.valueOf(tableName));
        Map<String, Map<String, String>> outMap     = new HashMap<>();
        for (String rowkey : rowkeyList) {
            Get get = new Get(Bytes.toBytes(rowkey));
            getList.add(get);
        }
        Result[] results = table.get(getList);
        for (Result result : results) {
            Map<String, String> dataMap = new HashMap<>();
            if (result.isEmpty()) {
                continue;
            }
            for (Cell kv : result.rawCells()) {
                dataMap.put(Bytes.toString(CellUtil.cloneQualifier(kv)), Bytes.toString(CellUtil.cloneValue(kv)));
            }
            outMap.put(Bytes.toString(result.getRow()), dataMap);
        }
        return outMap;
    }


    /**
     * Map 2 string string.
     *
     * @param fileName the file name
     * @param keyName  the key name
     * @param inMap    the in map
     * @return the string
     */
    public static String map2String(String fileName, String keyName, Map<String, String> inMap) {
        Properties   properties  = getPropertiesData(fileName);
        String       columns     = properties.getProperty(keyName);
        String[]     columnsList = StringUtils.split(columns, PunctuationConst.COMMA);
        List<String> dataList    = new ArrayList<>();
        for (String column : columnsList) {
            if (inMap.containsKey(column)) {
                dataList.add(inMap.get(column));
            } else {
                dataList.add("NULL");
            }
        }
        return StringUtils.join(dataList, PunctuationConst.COMMA);
    }

    /**
     * Get data 4 row key list.
     *
     * @param rowKeyList the row key list
     * @param fileName   the file name
     * @param keyName    the key name
     * @param inMap      the in map
     * @return the list
     */
    public static List<String> getData4RowKey(List<String> rowKeyList, String fileName, String keyName, Map<String, Map<String, String>> inMap) {
        List<String> dataList = new ArrayList<>();
        for (String rowKey : rowKeyList) {
            if (inMap.containsKey(rowKey)) {
                dataList.add(rowKey + "," + map2String(fileName, keyName, inMap.get(rowKey)));
            } else {
                dataList.add(rowKey + "," + map2String(fileName, keyName, new HashMap<>()));
            }
        }
        return dataList;
    }

    /**
     * Get row key list 4 file list.
     *
     * @param filePath the file path
     * @return the list
     */
    public static List<String> getRowKeyList4File(String filePath) {
        File   file       = new File(filePath);
        Long   fileLength = file.length();
        byte[] fileData   = new byte[fileLength.intValue()];

        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileData);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> dataList = getRowKeyList4String(new String(fileData), "\n");
        if (dataList.size() == 1) {
            dataList = getRowKeyList4String(new String(fileData), "\r\n");
        }
        return dataList;
    }

    /**
     * Get row key list 4 string list.
     *
     * @param data     the data
     * @param splitter the splitter
     * @return the list
     */
    public static List<String> getRowKeyList4String(String data, String splitter) {
        return Arrays.asList(StringUtils.split(data, splitter));
    }


    /**
     * Add data 2 file.
     *
     * @param filePath the file path
     * @param data     the data
     */
    public static void addData2File(String filePath, String data) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            bufferedWriter.append(data + "\n");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add data list 2 file.
     *
     * @param dataList the data list
     * @param filePath the file path
     */
    public static void addDataList2File(List<String> dataList, String filePath) {
        for (String data : dataList) {
            addData2File(filePath, data);
        }
    }

    /**
     * Add cache map.
     *
     * @param context the context
     */
    public static void addCacheMap(Mapper.Context context) {
        Map<String, String> cacheData = new HashMap<>();
        //预处理，把要关联的文件加载到缓存中
        URI[]      URI  = new URI[0];
        FileSystem hdfs = null;

        try {
            URI = context.getCacheFiles();
            hdfs = FileSystem.get(context.getConfiguration());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //新的检索缓存文件的API是 context.getCacheFiles() ，而 context.getLocalCacheFiles() 被弃用
        //然而 context.getCacheFiles() 返回的是 HDFS 路径； context.getLocalCacheFiles() 返回的才是本地路径

        //这里只缓存了一个文件，所以取第一个即可
        String line = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(hdfs.open(new Path(URI[0].getPath())), StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                String[] columnData = StringUtils.split(line, PunctuationConst.SPLITTER_USE, -1);
                if (StringUtils.equals(columnData[0], TableNameConst.T_3RDAPI_SM_RELATION)) {
                    cacheData.put(columnData[1], columnData[2]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Map writable 2 string string.
     *
     * @param mapWritable the map writable
     * @param columns     the columns
     * @param tableName   the table name
     * @return the string
     */
    public static String mapWritable2String(MapWritable mapWritable, String columns, String tableName) {
        String[]     columnList = StringUtils.split(columns, PunctuationConst.COMMA);
        List<String> dataList   = new ArrayList<>();
        dataList.add(tableName);
        for (String column : columnList) {
            if (mapWritable.containsKey(column)) {
                dataList.add(mapWritable.get(column).toString());
            } else {
                dataList.add("NULL");
            }
        }
        return StringUtils.join(dataList, PunctuationConst.SPLITTER_USE);
    }

    /**
     * String 2 map writable map writable.
     *
     * @param data    the data
     * @param columns the columns
     * @return the map writable
     */
    public static MapWritable string2MapWritable(String data, String columns) {
        List<String> dataList    = Arrays.asList(StringUtils.split(data, PunctuationConst.SPLITTER_USE, -1));
        List<String> columnList  = Arrays.asList(StringUtils.split(columns, PunctuationConst.COMMA, -1));
        MapWritable  mapWritable = new MapWritable();
        for (int i = 0; i < columnList.size(); i++) {
            mapWritable.put(new Text(columnList.get(i)), new Text(dataList.get(i + 1)));
        }
        return mapWritable;
    }

    /**
     * Map writable 2 json string string.
     *
     * @param mapWritable the map writable
     * @return the string
     */
    public static String mapWritable2JsonString(MapWritable mapWritable) {
        Map<String, String> outMap = new HashMap<>();
        for (Map.Entry<Writable, Writable> data : mapWritable.entrySet()) {
            outMap.put(data.getKey().toString(), data.getValue().toString());
        }
        return JSON.toJSONString(outMap);
    }

    /**
     * Json string 2 map writable map writable.
     *
     * @param jsonString the json string
     * @return the map writable
     */
    public static MapWritable JsonString2MapWritable(String jsonString) {
        Map<String, String> dataMap        = JSON.parseObject(jsonString, HashMap.class);
        MapWritable         outMapWritable = new MapWritable();
        for (Map.Entry<String, String> dataEntry : dataMap.entrySet()) {
            outMapWritable.put(new Text(dataEntry.getKey()), new Text(dataEntry.getValue()));
        }
        return outMapWritable;

    }

    /**
     * Map writable add key head map writable.
     *
     * @param mapWritable the map writable
     * @param head        the head
     * @return the map writable
     */
    public static MapWritable mapWritableAddKeyHead(MapWritable mapWritable, String head) {
        MapWritable outMapWritable = new MapWritable();
        for (Map.Entry<Writable, Writable> data : mapWritable.entrySet()) {
            outMapWritable.put(new Text(head + "__" + data.getKey().toString()), data.getValue());
        }

        return outMapWritable;
    }

    /**
     * Map writable remove data string.
     *
     * @param mapWritable the map writable
     * @param key         the key
     * @return the string
     */
    public static String mapWritableRemoveData(MapWritable mapWritable, String key) {
        String outStr;
        if (mapWritable.containsKey(new Text(key))) {
            outStr = mapWritable.get(new Text(key)).toString();
            mapWritable.remove(new Text(key));
        } else {
            outStr = DataConstant.NULL_STR;
        }
        return outStr;
    }


    /**
     * Gets bqs cache map.
     *
     * @param uri     the uri
     * @param context the context
     * @return the bqs cache map
     */
    public static Map<String, String> getBqsCacheMap(URI[] uri, Configuration context) {
        Map<String, String> cacheData = new HashMap<>();
        FileSystem          hdfs      = null;
        try {
            hdfs = FileSystem.get(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(hdfs.open(new Path(uri[0].getPath())), StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                String[] columnData = StringUtils.split(line, PunctuationConst.EQUAL, -1);
                cacheData.put(columnData[0], columnData[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("cacheData::" + cacheData.isEmpty());
        return cacheData;

    }

}
