package com.mnw.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.io.Files;
import com.mnw.data.config.HBaseData;
import com.mnw.data.constant.DataConstant;
import com.mnw.data.constant.PunctuationConst;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by shaodi.chen on 2019/4/17.
 */
public class HBaseUtils {

    private static Connection    connection;
    private static Configuration conf = new Configuration();

    static {
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("hbase.zookeeper.quorum", "192.168.1.71,192.168.1.72,192.168.1.73");
        conf.set("hbase.master", "192.168.1.72:60010");
        conf.set("mapreduce.output.fileoutputformat.compress", "false");
        conf.set("mapreduce.task.timeout", "1800000");

        try {
            connection = ConnectionFactory.createConnection(conf);
            if (connection == null || connection.isClosed()) {
                connection = ConnectionFactory.createConnection(conf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets conn.
     *
     * @return the conn
     */
    public static Connection getConn() {
        return connection;
    }

    /**
     * Save data 2 hbase boolean.
     *
     * @param tableName the table name
     * @param put       the put
     * @return the boolean
     */
    public static boolean saveData2Hbase(String tableName, Put put) {
        boolean isSuccess = false;
        try (HTable hTable = (HTable) connection.getTable(TableName.valueOf(tableName))) {
            hTable.put(put);
            isSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }


    /**
     * Save data 2 hbase boolean.
     *
     * @param tableName the table name
     * @param putList   the put list
     * @return the boolean
     */
    public static boolean saveData2Hbase(String tableName, List<Put> putList, Connection connection) {
        boolean isSuccess = false;
        try (
                Admin admin = connection.getAdmin()
        ) {
            TableName hTableName = TableName.valueOf(tableName);
            if (!admin.tableExists(hTableName)) {
                HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
                hTableDescriptor.addFamily(new HColumnDescriptor("info"));
                admin.createTable(hTableDescriptor);
            }
            HTable hTable = (HTable) connection.getTable(hTableName);
            hTable.setAutoFlushTo(false);
            hTable.setWriteBufferSize(10 * 1024 * 1024);
            hTable.put(putList);
            hTable.flushCommits();
            hTable.close();
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        }
        return isSuccess;
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
        Map<String, String> outMap = new HashMap<>(1);
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
        InputStream inStream   = HBaseUtils.class.getClassLoader().getResourceAsStream(fileName);
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
     * @param orderSn            the order sn
     * @param tableName          the table name
     * @param outDataMapWritable the out data map writable
     * @return the put
     */
    public static Put map2Put(Text orderSn, Text tableName, MapWritable outDataMapWritable) {
        if (StringUtils.equals(orderSn.toString(), DataConstant.NULL_STR)) {
            return new Put(orderSn.getBytes());
        }
        Put put = new Put(orderSn.toString().getBytes());
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


    public static void sumExistsColumn(String tableName, String familyName, String insertFamilyName, String insertColumnName, List<String> rowKeyList) {
        try {
            Map<String, Map<String, String>> dataMap    = queryTableTestBatch(tableName, familyName, rowKeyList);
            Connection                       connection = getConnection();
            Table                            table      = connection.getTable(TableName.valueOf(tableName));
            List<Put>                        putList    = new ArrayList<>();
            for (String rowKey : rowKeyList) {
                Put put = new Put(Bytes.toBytes(rowKey));
                put.addColumn(Bytes.toBytes(insertFamilyName), Bytes.toBytes(insertColumnName), Bytes.toBytes(String.valueOf(dataMap.get(rowKey).size())));
                putList.add(put);
            }
            table.put(putList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Qurry table test batch map.
     *
     * @param tableName  the table name
     * @param rowkeyList the rowkey list
     * @return the map
     * @throws IOException the io exception
     */
    public static Map<String, Map<String, String>> queryTableTestBatch(String tableName, List<String> rowkeyList) throws IOException {
        Connection                       connection = getConnection();
        Table                            table      = connection.getTable(TableName.valueOf(tableName));
        Map<String, Map<String, String>> outMap     = new HashMap<>();
        List<Get>                        getList    = rowKeyList2GetList(rowkeyList);

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

    public static Map<String, Map<String, String>> queryTableTestBatch(String tableName, String familyName, List<String> rowKeyList) {
        Map<String, Map<String, String>> outMap = new HashMap<>();
        try {
            Connection connection = getConnection();
            Table      table      = connection.getTable(TableName.valueOf(tableName));
            List<Get>  getList    = rowKeyList2GetList(rowKeyList, familyName);
            Result[]   results    = table.get(getList);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outMap;

    }

    /**
     * Query table row key exists int.
     * 批量判断 rowKey 是否存在
     *
     * @param tableName  the table name
     * @param rowKeyList the row key list
     * @return the int
     */
    public static int queryTableRowKeyExists(String tableName, List<String> rowKeyList, Connection connection) {
        int existsNum = 0;
        try {
            Table     table            = connection.getTable(TableName.valueOf(tableName));
            List<Get> getList          = rowKeyList2GetList(rowKeyList);
            boolean[] getBooleanValues = table.existsAll(getList);

            for (boolean isExists : getBooleanValues) {
                if (isExists) {
                    existsNum++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return existsNum;
    }

    public static List<String> queryTableRowKey(String tableName, Connection connection) {
        List<String> rowKeyList = new ArrayList<>();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Scan  scan  = new Scan();
            scan.setBatch(2000);
            ResultScanner scanner = table.getScanner(scan);
            for (Result data : scanner) {
                rowKeyList.add(Bytes.toString(data.getRow()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rowKeyList;
    }

    public static List<String> queryTableColumnData(String tableName, String familyName, String columnName, Connection connection, List<String> outColumns) {
        List<String> outData = new ArrayList<>();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Scan  scan  = new Scan();
            scan.setBatch(2000);
            ResultScanner scanner = table.getScanner(scan);
            for (Result data : scanner) {
                Map<byte[], byte[]> outValueMap    = data.getFamilyMap(Bytes.toBytes(familyName));
                List<String>        columnDataList = new ArrayList<>();
                for (String outColumn : outColumns) {
                    columnDataList.add(Bytes.toString(outValueMap.get(Bytes.toBytes(outColumn))));
                }
                String sum = Bytes.toString(data.getValue(Bytes.toBytes("info"), Bytes.toBytes("sum")));
                if (DataUtils.isNumeric(sum) && Integer.parseInt(sum) > 1) {
                    addData2File("/home/hadoop/data/phone/out/sum.csv", Bytes.toString(data.getRow()) + "," + StringUtils.join(columnDataList, PunctuationConst.COMMA) + "," + sum);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outData;
    }

    public static List<String> listTableName(String nameSpace, Connection connection) {
        List<String> tableNameList = new ArrayList<>();
        try {
            Admin       admin      = connection.getAdmin();
            TableName[] tableNames = admin.listTableNamesByNamespace(nameSpace);
            for (TableName tableName : tableNames) {
                tableNameList.add(tableName.getNameAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tableNameList;
    }

    /**
     * Row key list 2 get list list.
     *
     * @param rowKeyList the row key list
     * @return the list
     */
    static List<Get> rowKeyList2GetList(List<String> rowKeyList) {
        List<Get> getList = new ArrayList<>();
        for (String rowKey : rowKeyList) {
            Get get = new Get(Bytes.toBytes(rowKey));
            getList.add(get);
        }

        return getList;
    }

    static List<Get> rowKeyList2GetList(List<String> rowKeyList, String familyName) {
        List<Get> getList        = new ArrayList<>();
        byte[]    familyNameByte = Bytes.toBytes(familyName);
        for (String rowKey : rowKeyList) {
            Get get = new Get(Bytes.toBytes(rowKey));
            get.addFamily(familyNameByte);
            getList.add(get);
        }
        return getList;
    }

    static List<Get> rowKeyList2GetList(List<String> rowKeyList, String familyName, String columnName) {
        List<Get> getList        = new ArrayList<>();
        byte[]    familyNameByte = Bytes.toBytes(familyName);
        byte[]    columnNameByte = Bytes.toBytes(columnName);
        for (String rowKey : rowKeyList) {
            Get get = new Get(Bytes.toBytes(rowKey));
            get.addColumn(familyNameByte, columnNameByte);
            getList.add(get);
        }
        return getList;
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


    public static void checkSum(String keyFilePath, String outPath, List<String> checkStringList) {
        try {
            Connection connection = getConnection();
            for (String tableName : Files.readLines(new File(keyFilePath), StandardCharsets.UTF_8)) {
                List<String>        rowkeyList = queryTableRowKey(tableName, connection);
                Map<String, String> valueMap   = new LinkedHashMap<>();
                if (rowkeyList.size() == 0) {
                    continue;
                }
                for (String ableName2 : checkStringList) {
                    int check = queryTableRowKeyExists(ableName2, rowkeyList, connection);
                    valueMap.put(ableName2, String.valueOf(check));
                }
                addData2File(outPath, tableName + "," + StringUtils.join(valueMap.values()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
