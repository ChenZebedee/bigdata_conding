package com.mnw.writable;

import com.mnw.utils.HbaseUtils;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

/**
 * Created by shaodi.chen on 2019/4/23.
 */
@Data
public class SmWritable implements WritableComparable<SmWritable> {
    private static final Logger LOGGER          = LoggerFactory.getLogger(SmWritable.class);
    private              Text   tableName       = new Text("N");
    private              Text   orderSn         = new Text("N");
    private              Text   borrowerId      = new Text("N");
    //t_3rdapi_sm_loan
    private              Text   f_loan_action   = new Text("N");
    private              Text   f_platform_type = new Text("N");
    private              Text   f_variable      = new Text("N");
    private              Text   f_d3            = new Text("N");
    private              Text   f_d7            = new Text("N");
    private              Text   f_d30           = new Text("N");
    private              Text   f_d60           = new Text("N");
    private              Text   f_d90           = new Text("N");
    private              Text   f_d180          = new Text("N");
    private              Text   f_total         = new Text("N");

    //t_3rdapi_sm_lending
    private Text f_score      = new Text("N");
    private Text f_risk_level = new Text("N");

    //t_3rdapi_sm_hit
    private Text f_rule = new Text("N");

    //t_3rdapi_sm_relation
    private Text sr_f_id    = new Text("N");
    private Text sr_f_name  = new Text("N");
    private Text sr_f_value = new Text("N");

    private Text        outText            = new Text("N");
    private MapWritable outDataMapWritable = new MapWritable();

    /**
     * Sets sm loan.
     *
     * @param f_loan_action   the f loan action
     * @param f_platform_type the f platform type
     * @param f_variable      the f variable
     * @param f_d3            the f d 3
     * @param f_d7            the f d 7
     * @param f_d30           the f d 30
     * @param f_d60           the f d 60
     * @param f_d90           the f d 90
     * @param f_d180          the f d 180
     * @param f_total         the f total
     * @param relationMap     the relation map
     */
    public void setSmLoan(String f_loan_action, String f_platform_type, String f_variable, String f_d3, String f_d7, String f_d30, String f_d60, String f_d90, String f_d180, String f_total, Map<String, String> relationMap) {
        String key = "smloan__" + relationMap.get(f_loan_action) + "__" + relationMap.get(f_platform_type) + "__" + relationMap.get(f_variable);
        outDataMapWritable.put(new Text(key + "__f_d3"), new Text(f_d3));
        outDataMapWritable.put(new Text(key + "__f_d7"), new Text(f_d7));
        outDataMapWritable.put(new Text(key + "__f_d30"), new Text(f_d30));
        outDataMapWritable.put(new Text(key + "__f_d60"), new Text(f_d60));
        outDataMapWritable.put(new Text(key + "__f_d90"), new Text(f_d90));
        outDataMapWritable.put(new Text(key + "__f_d180"), new Text(f_d180));
        outDataMapWritable.put(new Text(key + "__f_total"), new Text(f_total));
    }

    /**
     * Sets sm lending.
     *
     * @param f_score      the f score
     * @param f_risk_level the f risk level
     */
    public void setSmLending(String f_score, String f_risk_level) {
        this.f_score.set(f_score);
        this.f_risk_level.set(f_risk_level);
    }

    /**
     * Sets sm hit.
     *
     * @param f_rule      the f rule
     * @param relationMap the relation map
     */
    public void setSmHit(String f_rule, Map<String, String> relationMap) {
        outDataMapWritable.put(new Text(relationMap.get(f_rule)), new Text("1"));
    }

    /**
     * Set osqd.
     *
     * @param OrderSn    the order sn
     * @param BorrowerId the borrower id
     */
    public void setOSQD(String OrderSn, String BorrowerId) {
        this.orderSn.set(OrderSn);
        this.borrowerId.set(BorrowerId);
        outDataMapWritable.put(new Text("borrowerId"), this.borrowerId);
    }

    /**
     * Add other out map.
     *
     * @param otherOutMapWriteable the other out map writeable
     */
    public void addOtherOutMap(MapWritable otherOutMapWriteable) {
        outDataMapWritable.putAll(otherOutMapWriteable);
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
        Put put = HbaseUtils.map2Put(orderSn, tableName, outDataMapWritable);
        put.addColumn(tableName.getBytes(), "f_score".getBytes(), f_score.getBytes());
        put.addColumn(tableName.getBytes(), "f_risk_level".getBytes(), f_risk_level.getBytes());
        return put;
    }


    @Override
    public int compareTo(SmWritable o) {
        return tableName.compareTo(o.getTableName()) + orderSn.compareTo(o.getOrderSn()) + borrowerId.compareTo(o.getBorrowerId()) + f_loan_action.compareTo(o.getF_loan_action()) + f_platform_type.compareTo(o.getF_platform_type()) + f_variable.compareTo(o.getF_variable()) + f_d3.compareTo(o.getF_d3()) + f_d7.compareTo(o.getF_d7()) + f_d30.compareTo(o.getF_d30()) + f_d60.compareTo(o.getF_d60()) + f_d90.compareTo(o.getF_d90()) + f_d180.compareTo(o.getF_d180()) + f_total.compareTo(o.getF_total()) + f_score.compareTo(o.getF_score()) + f_risk_level.compareTo(o.getF_risk_level()) + f_rule.compareTo(o.getF_rule()) + sr_f_id.compareTo(o.getSr_f_id()) + sr_f_name.compareTo(o.getSr_f_name()) + sr_f_value.compareTo(o.getSr_f_value());
    }

    @Override
    public void write(DataOutput out) throws IOException {
        tableName.write(out);
        orderSn.write(out);
        borrowerId.write(out);
        f_loan_action.write(out);
        f_platform_type.write(out);
        f_variable.write(out);
        f_d3.write(out);
        f_d7.write(out);
        f_d30.write(out);
        f_d60.write(out);
        f_d90.write(out);
        f_d180.write(out);
        f_total.write(out);
        f_score.write(out);
        f_risk_level.write(out);
        f_rule.write(out);
        sr_f_id.write(out);
        sr_f_name.write(out);
        sr_f_value.write(out);
        outDataMapWritable.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        tableName.readFields(in);
        orderSn.readFields(in);
        borrowerId.readFields(in);
        f_loan_action.readFields(in);
        f_platform_type.readFields(in);
        f_variable.readFields(in);
        f_d3.readFields(in);
        f_d7.readFields(in);
        f_d30.readFields(in);
        f_d60.readFields(in);
        f_d90.readFields(in);
        f_d180.readFields(in);
        f_total.readFields(in);
        f_score.readFields(in);
        f_risk_level.readFields(in);
        f_rule.readFields(in);
        sr_f_id.readFields(in);
        sr_f_name.readFields(in);
        sr_f_value.readFields(in);
        outDataMapWritable.readFields(in);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SmWritable)) return false;

        SmWritable that = (SmWritable) o;

        return new EqualsBuilder()
                .append(getTableName(), that.getTableName())
                .append(getOrderSn(), that.getOrderSn())
                .append(getBorrowerId(), that.getBorrowerId())
                .append(getF_loan_action(), that.getF_loan_action())
                .append(getF_platform_type(), that.getF_platform_type())
                .append(getF_variable(), that.getF_variable())
                .append(getF_d3(), that.getF_d3())
                .append(getF_d7(), that.getF_d7())
                .append(getF_d30(), that.getF_d30())
                .append(getF_d60(), that.getF_d60())
                .append(getF_d90(), that.getF_d90())
                .append(getF_d180(), that.getF_d180())
                .append(getF_total(), that.getF_total())
                .append(getF_score(), that.getF_score())
                .append(getF_risk_level(), that.getF_risk_level())
                .append(getF_rule(), that.getF_rule())
                .append(getSr_f_id(), that.getSr_f_id())
                .append(getSr_f_name(), that.getSr_f_name())
                .append(getSr_f_value(), that.getSr_f_value())
                .append(getOutText(), that.getOutText())
                .append(getOutDataMapWritable(), that.getOutDataMapWritable())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getTableName())
                .append(getOrderSn())
                .append(getBorrowerId())
                .append(getF_loan_action())
                .append(getF_platform_type())
                .append(getF_variable())
                .append(getF_d3())
                .append(getF_d7())
                .append(getF_d30())
                .append(getF_d60())
                .append(getF_d90())
                .append(getF_d180())
                .append(getF_total())
                .append(getF_score())
                .append(getF_risk_level())
                .append(getF_rule())
                .append(getSr_f_id())
                .append(getSr_f_name())
                .append(getSr_f_value())
                .append(getOutText())
                .append(getOutDataMapWritable())
                .toHashCode();
    }


    /**
     * To stringother string.
     *
     * @return the string
     */
    public String toStringother() {
        final StringBuilder sb = new StringBuilder();
        sb
                .append(tableName);
        sb.append("$|$")
                .append(orderSn);
        sb.append("$|$")
                .append(borrowerId);
        sb.append("$|$")
                .append(f_loan_action);
        sb.append("$|$")
                .append(f_platform_type);
        sb.append("$|$")
                .append(f_variable);
        sb.append("$|$")
                .append(f_d3);
        sb.append("$|$")
                .append(f_d7);
        sb.append("$|$")
                .append(f_d30);
        sb.append("$|$")
                .append(f_d60);
        sb.append("$|$")
                .append(f_d90);
        sb.append("$|$")
                .append(f_d180);
        sb.append("$|$")
                .append(f_total);
        sb.append("$|$")
                .append(f_score);
        sb.append("$|$")
                .append(f_risk_level);
        sb.append("$|$")
                .append(f_rule);
        sb.append("$|$")
                .append(sr_f_id);
        sb.append("$|$")
                .append(sr_f_name);
        sb.append("$|$")
                .append(sr_f_value);
        sb.append("$|$")
                .append(outText);
        sb.append("$|$")
                .append(outDataMapWritable);
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb
                .append(tableName);
        sb.append(",")
                .append(orderSn);
        sb.append(",")
                .append(borrowerId);
        sb.append(",")
                .append(f_loan_action);
        sb.append(",")
                .append(f_platform_type);
        sb.append(",")
                .append(f_variable);
        sb.append(",")
                .append(f_d3);
        sb.append(",")
                .append(f_d7);
        sb.append(",")
                .append(f_d30);
        sb.append(",")
                .append(f_d60);
        sb.append(",")
                .append(f_d90);
        sb.append(",")
                .append(f_d180);
        sb.append(",")
                .append(f_total);
        sb.append(",")
                .append(f_score);
        sb.append(",")
                .append(f_risk_level);
        sb.append(",")
                .append(f_rule);
        sb.append(",")
                .append(sr_f_id);
        sb.append(",")
                .append(sr_f_name);
        sb.append(",")
                .append(sr_f_value);
        sb.append(",")
                .append(outText);
        sb.append(",")
                .append(outDataMapWritable);
        return sb.toString();
    }

    /**
     * Gets table name str.
     *
     * @return the table name str
     */
    public String getTableNameStr() {
        return tableName.toString();
    }

    /**
     * Sets table name str.
     *
     * @param tableName the table name
     */
    public void setTableNameStr(String tableName) {
        this.tableName.set(tableName);
    }

    /**
     * Gets order sn str.
     *
     * @return the order sn str
     */
    public String getOrderSnStr() {
        return orderSn.toString();
    }

    /**
     * Sets order sn str.
     *
     * @param orderSn the order sn
     */
    public void setOrderSnStr(String orderSn) {
        this.orderSn.set(orderSn);
    }

    /**
     * Gets borrower id str.
     *
     * @return the borrower id str
     */
    public String getBorrowerIdStr() {
        return borrowerId.toString();
    }

    /**
     * Sets borrower id str.
     *
     * @param borrowerId the borrower id
     */
    public void setBorrowerIdStr(String borrowerId) {
        this.borrowerId.set(borrowerId);
    }

    /**
     * Gets f loan action str.
     *
     * @return the f loan action str
     */
    public String getF_loan_actionStr() {
        return f_loan_action.toString();
    }

    /**
     * Sets f loan action str.
     *
     * @param f_loan_action the f loan action
     */
    public void setF_loan_actionStr(String f_loan_action) {
        this.f_loan_action.set(f_loan_action);
    }

    /**
     * Gets f platform type str.
     *
     * @return the f platform type str
     */
    public String getF_platform_typeStr() {
        return f_platform_type.toString();
    }

    /**
     * Sets f platform type str.
     *
     * @param f_platform_type the f platform type
     */
    public void setF_platform_typeStr(String f_platform_type) {
        this.f_platform_type.set(f_platform_type);
    }

    /**
     * Gets f variable str.
     *
     * @return the f variable str
     */
    public String getF_variableStr() {
        return f_variable.toString();
    }

    /**
     * Sets f variable str.
     *
     * @param f_variable the f variable
     */
    public void setF_variableStr(String f_variable) {
        this.f_variable.set(f_variable);
    }

    /**
     * Gets f d 3 str.
     *
     * @return the f d 3 str
     */
    public String getF_d3Str() {
        return f_d3.toString();
    }

    /**
     * Sets f d 3 str.
     *
     * @param f_d3 the f d 3
     */
    public void setF_d3Str(String f_d3) {
        this.f_d3.set(f_d3);
    }

    /**
     * Gets f d 7 str.
     *
     * @return the f d 7 str
     */
    public String getF_d7Str() {
        return f_d7.toString();
    }

    /**
     * Sets f d 7 str.
     *
     * @param f_d7 the f d 7
     */
    public void setF_d7Str(String f_d7) {
        this.f_d7.set(f_d7);
    }

    /**
     * Gets f d 30 str.
     *
     * @return the f d 30 str
     */
    public String getF_d30Str() {
        return f_d30.toString();
    }

    /**
     * Sets f d 30 str.
     *
     * @param f_d30 the f d 30
     */
    public void setF_d30Str(String f_d30) {
        this.f_d30.set(f_d30);
    }

    /**
     * Gets f d 60 str.
     *
     * @return the f d 60 str
     */
    public String getF_d60Str() {
        return f_d60.toString();
    }

    /**
     * Sets f d 60 str.
     *
     * @param f_d60 the f d 60
     */
    public void setF_d60Str(String f_d60) {
        this.f_d60.set(f_d60);
    }

    /**
     * Gets f d 90 str.
     *
     * @return the f d 90 str
     */
    public String getF_d90Str() {
        return f_d90.toString();
    }

    /**
     * Sets f d 90 str.
     *
     * @param f_d90 the f d 90
     */
    public void setF_d90Str(String f_d90) {
        this.f_d90.set(f_d90);
    }

    /**
     * Gets f d 180 str.
     *
     * @return the f d 180 str
     */
    public String getF_d180Str() {
        return f_d180.toString();
    }

    /**
     * Sets f d 180 str.
     *
     * @param f_d180 the f d 180
     */
    public void setF_d180Str(String f_d180) {
        this.f_d180.set(f_d180);
    }

    /**
     * Gets f total str.
     *
     * @return the f total str
     */
    public String getF_totalStr() {
        return f_total.toString();
    }

    /**
     * Sets f total str.
     *
     * @param f_total the f total
     */
    public void setF_totalStr(String f_total) {
        this.f_total.set(f_total);
    }

    /**
     * Gets f score str.
     *
     * @return the f score str
     */
    public String getF_scoreStr() {
        return f_score.toString();
    }

    /**
     * Sets f score str.
     *
     * @param f_score the f score
     */
    public void setF_scoreStr(String f_score) {
        this.f_score.set(f_score);
    }

    /**
     * Gets f risk level str.
     *
     * @return the f risk level str
     */
    public String getF_risk_levelStr() {
        return f_risk_level.toString();
    }

    /**
     * Sets f risk level str.
     *
     * @param f_risk_level the f risk level
     */
    public void setF_risk_levelStr(String f_risk_level) {
        this.f_risk_level.set(f_risk_level);
    }

    /**
     * Gets f rule str.
     *
     * @return the f rule str
     */
    public String getF_ruleStr() {
        return f_rule.toString();
    }

    /**
     * Sets f rule str.
     *
     * @param f_rule the f rule
     */
    public void setF_ruleStr(String f_rule) {
        this.f_rule.set(f_rule);
    }

    /**
     * Gets sr f id str.
     *
     * @return the sr f id str
     */
    public String getSr_f_idStr() {
        return sr_f_id.toString();
    }

    /**
     * Sets sr f id str.
     *
     * @param sr_f_id the sr f id
     */
    public void setSr_f_idStr(String sr_f_id) {
        this.sr_f_id.set(sr_f_id);
    }

    /**
     * Gets sr f name str.
     *
     * @return the sr f name str
     */
    public String getSr_f_nameStr() {
        return sr_f_name.toString();
    }

    /**
     * Sets sr f name str.
     *
     * @param sr_f_name the sr f name
     */
    public void setSr_f_nameStr(String sr_f_name) {
        this.sr_f_name.set(sr_f_name);
    }

    /**
     * Gets sr f value str.
     *
     * @return the sr f value str
     */
    public String getSr_f_valueStr() {
        return sr_f_value.toString();
    }

    /**
     * Sets sr f value str.
     *
     * @param sr_f_value the sr f value
     */
    public void setSr_f_valueStr(String sr_f_value) {
        this.sr_f_value.set(sr_f_value);
    }

    /**
     * Gets out string str.
     *
     * @return the out string str
     */
    public String getOutStringStr() {
        return outText.toString();
    }

    /**
     * Sets out string str.
     *
     * @param outString the out string
     */
    public void setOutStringStr(String outString) {
        this.outText.set(outString);
    }


}
