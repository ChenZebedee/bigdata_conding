package com.mnw.reduce;

import com.mnw.data.constant.TableNameConst;
import com.mnw.writable.SmWritable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

import java.io.IOException;

/**
 * The type Sm reduce 1.
 */
public class SmReduce1 extends TableReducer<Text, SmWritable, NullWritable> {

    @Override
    protected void reduce(Text key, Iterable<SmWritable> values, Context context) throws IOException, InterruptedException {
        SmWritable midWritable = new SmWritable();
        midWritable.setTableNameStr("smOut");
        if (CollectionUtils.size(values) < 2) {
            return;
        }
        for (SmWritable smWritable : values) {
            switch (smWritable.getTableName().toString()) {
                case TableNameConst.T_3RDAPI_ORDER_SN_QUERY_DATA:
                    midWritable.setOSQD(smWritable.getOrderSnStr(), smWritable.getBorrowerIdStr());
                    break;
                case TableNameConst.T_3RDAPI_SM_HIT:
                    midWritable.addOtherOutMapWritable(smWritable.getOutDataMapWritable());
                    break;
                case TableNameConst.T_3RDAPI_SM_LENDING:
                    midWritable.setSmLending(smWritable.getF_scoreStr(), smWritable.getF_risk_levelStr());
                    break;
                case TableNameConst.T_3RDAPI_SM_LOAN:
                    midWritable.addOtherOutMapWritable(smWritable.getOutDataMapWritable());
                    break;
                default:
                    break;
            }
        }
        if (StringUtils.equals(midWritable.getOrderSnStr(), "N")) {
            return;
        }
        context.write(NullWritable.get(), midWritable.getPut());
    }
}

