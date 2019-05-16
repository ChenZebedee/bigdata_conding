package com.mnw.data.config;

import com.mnw.utils.HbaseUtils;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shaodi.chen on 2019/4/18.
 */
@Data
public class HBaseData {

    private String              rowKey;
    private String              columnsName;
    private Map<String, String> dataMap = new LinkedHashMap<>();


    /**
     * Instantiates a new H base data.
     */
    public HBaseData() {
    }

    /**
     * Instantiates a new H base data.
     *
     * @param rowKey      the row key
     * @param columnsName the columns name
     */
    public HBaseData(String rowKey, String columnsName) {
        this.rowKey = rowKey;
        this.columnsName = columnsName;
    }

    /**
     * Instantiates a new H base data.
     *
     * @param rowKey        the row key
     * @param columnsName   the columns name
     * @param dataList      the data list
     * @param indexs        the indexs
     * @param columnMapping the column mapping
     */
    public HBaseData(String rowKey, String columnsName, List<String> dataList, List<Integer> indexs, Map<String, String> columnMapping) {
        this(rowKey, columnsName);
        for (String data : dataList) {
            this.dataMap.putAll(HbaseUtils.row2Column(data, indexs, columnMapping));
        }
    }


}
