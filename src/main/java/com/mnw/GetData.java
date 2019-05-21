package com.mnw;

import com.mnw.utils.HbaseUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by shaodi.chen on 2019/4/26.
 */
public class GetData {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        List<String> rowKeyList     = HbaseUtils.getRowKeyList4File(args[0]);
        String       tableName      = args[1];
        String       propertiesName = args[2];
        String       columnName     = args[3];
        String       outFilePath    = args[4];

        try {
            Map<String, Map<String, String>> getDataMap = HbaseUtils.qurryTableTestBatch(tableName, rowKeyList);
            /*for (Map.Entry<String,Map<String,String>> getDataEntry:getDataMap.entrySet()){
                String outStr=getDataEntry.getKey()+","+HbaseUtils.map2String("table-info.properties","sm_out",getDataEntry.getValue());
                HbaseUtils.addData2File("/home/hadoop/data/get/sm_out",outStr);
            }*/
            HbaseUtils.addDataList2File(HbaseUtils.getData4RowKey(rowKeyList, "table-info.properties", columnName, getDataMap), outFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
