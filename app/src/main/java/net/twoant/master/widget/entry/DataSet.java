package net.twoant.master.widget.entry;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by S_Y_H on 2016/12/19.
 * 1
 */

public class DataSet implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<DataRow> rows;            // 数据

    private DataSet() {
        rows = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSet dataSet = (DataSet) o;

        return rows != null ? rows.equals(dataSet.rows) : dataSet.rows == null;

    }

    @Override
    public int hashCode() {
        return rows != null ? rows.hashCode() : 0;
    }

    static DataSet parseJson(JSONArray json) throws JSONException {
        DataSet set = new DataSet();
        if (null != json) {
            int size = json.length();
            for (int i = 0; i < size; ++i) {
                set.add(DataRow.parseJson(json.getJSONObject(i)));
            }
        }
        return set;
    }


    /**
     * 记录数量
     */
    public int size() {
        return rows == null ? 0 : rows.size();
    }

    public int getSize() {
        return size();
    }


    /**
     * 返回数据是否为空
     */
    public boolean isEmpty() {
        boolean result = true;
        result = null == rows || rows.isEmpty();
        return result;
    }

    /**
     * 读取一行数据
     */
    public DataRow getRow(int index) {
        DataRow row = null;
        if (null != rows && index < rows.size()) {
            row = rows.get(index);
        }
        if (null != row) {
            row.setContainer(this);
        }
        return row;
    }

    /**
     * 根据单个属性值读取一行
     *
     * @return
     */
    public DataRow getRow(String... params) {
        DataSet set = getRows(params);
        if (set.size() > 0) {
            return set.getRow(0);
        }
        return null;
    }

    /**
     * distinct
     */
    public DataSet distinct(String... keys) {
        DataSet result = new DataSet();
        if (null != rows) {
            int size = rows.size();
            for (int i = 0; i < size; i++) {
                DataRow row = rows.get(i);
                //查看result中是否已存在
                String[] params = packParam(row, keys);
                if (result.getRows(params).size() == 0) {
                    DataRow tmp = new DataRow();
                    for (String key : keys) {
                        tmp.put(key, row.get(key));
                    }
                    result.addRow(tmp);
                }
            }
        }
        return result;
    }


    /**
     * 筛选符合条件的集合
     */
    public DataSet getRows(String... params) {
        DataSet set = this;
        for (int i = 0; i < params.length; i += 2) {
            String key = params[i];
            String value = "";
            if (null == key) {
                continue;
            }
            if (key.contains(":")) {
                String tmp[] = key.split(":");
                key = tmp[0];
                value = tmp[1];
            } else {
                if (i + 1 < params.length) {
                    key = params[i];
                    value = params[i + 1];
                }
            }
            set = filter(set, key, value);
        }
        return set;
    }

    /**
     * 提取符合指定属性值的集合
     */
    private DataSet filter(DataSet src, String key, String value) {
        DataSet set = new DataSet();
        String tmpValue;
        for (int i = 0; i < src.size(); i++) {
            tmpValue = src.getString(i, key);
            if ((null == value && null == tmpValue)
                    || (null != value && value.equals(tmpValue))) {
                set.add(src.getRow(i));
            }
        }
        return set;
    }

    public DataSet getRows(int from, int to) {
        DataSet set = new DataSet();
        for (int i = from; i < this.size() && i <= to; i++) {
            set.addRow(this.getRow(i));
        }
        return set;
    }

    /**
     * 合计
     *
     * @param top 多少行
     */
    public BigDecimal sum(int top, String key) {
        BigDecimal result = new BigDecimal("0");
        int size = rows.size();
        if (size > top) {
            size = top;
        }
        for (int i = 0; i < size; i++) {
            BigDecimal tmp = getDecimal(i, key);
            if (null != tmp) {
                result = result.add(getDecimal(i, key));
            }
        }
        return result;
    }

    public BigDecimal sum(String key) {
        return sum(size(), key);
    }

    /**
     * 最大值
     *
     * @param top 多少行
     */
    public BigDecimal maxDecimal(int top, String key) {
        BigDecimal result = new BigDecimal(0);
        int size = rows.size();
        if (size > top) {
            size = top;
        }
        for (int i = 0; i < size; i++) {
            BigDecimal tmp = getDecimal(i, key);
            if (null != tmp && tmp.compareTo(result) > 0) {
                result = tmp;
            }
        }
        return result;
    }

    public BigDecimal maxDecimal(String key) {
        return maxDecimal(size(), key);
    }

    public int maxInt(int top, String key) {
        BigDecimal result = maxDecimal(top, key);
        return result.intValue();
    }

    public int maxInt(String key) {
        return maxInt(size(), key);
    }

    public double maxDouble(int top, String key) {
        BigDecimal result = maxDecimal(top, key);
        return result.doubleValue();
    }

    public double maxDouble(String key) {
        return maxDouble(size(), key);
    }

    public double max(int top, String key) {
        BigDecimal result = maxDecimal(top, key);
        return result.doubleValue();
    }

    public double max(String key) {
        return maxDouble(size(), key);
    }

    /**
     * 最小值
     */
    public BigDecimal minDecimal(int top, String key) {
        BigDecimal result = new BigDecimal(0);
        int size = rows.size();
        if (size > top) {
            size = top;
        }
        for (int i = 0; i < size; i++) {
            BigDecimal tmp = getDecimal(i, key);
            if (null != tmp && tmp.compareTo(result) < 0) {
                result = tmp;
            }
        }
        return result;
    }

    public BigDecimal minDecimal(String key) {
        return minDecimal(size(), key);
    }

    public int minInt(int top, String key) {
        BigDecimal result = minDecimal(top, key);
        return result.intValue();
    }

    public int minInt(String key) {
        return minInt(size(), key);
    }

    public double minDouble(int top, String key) {
        BigDecimal result = minDecimal(top, key);
        return result.doubleValue();
    }

    public double minDouble(String key) {
        return minDouble(size(), key);
    }

    public double min(int top, String key) {
        BigDecimal result = minDecimal(top, key);
        return result.doubleValue();
    }

    public double min(String key) {
        return minDouble(size(), key);
    }

    /**
     * key对应的value最大的一行
     * max与 maxRow区别:max只对number类型计算 其他类型异常
     */
    public DataRow maxRow(String key) {
        List<String> values = getStrings(key);
        if (null == values || values.size() == 0) {
            return null;
        }
        Collections.sort(values);
        return getRow(key, values.get(values.size() - 1));
    }

    public DataRow minRow(String key) {
        List<String> values = getStrings(key);
        if (null == values || values.size() == 0) {
            return null;
        }
        Collections.sort(values);
        return getRow(key, values.get(0));
    }

    /**
     * 平均值 空数据不参与加法但参与除法
     */
    public double avg(int top, String key) {
        BigDecimal result = new BigDecimal(0);
        int size = rows.size();
        if (size > top) {
            size = top;
        }
        int count = 0;
        for (int i = 0; i < size; i++) {
            BigDecimal tmp = getDecimal(i, key);
            if (null != tmp) {
                result = result.add(tmp);
            }
            count++;
        }
        if (count > 0) {
            result = result.divide(new BigDecimal(count));
        }
        return result.doubleValue();
    }

    public double avg(String key) {
        double result = 0.0;
        result = avg(size(), key);
        return result;
    }

    public void addRow(DataRow row) {
        if (null != row) {
            rows.add(row);
        }
    }

    public void addRow(int idx, DataRow row) {
        if (null != row) {
            rows.add(idx, row);
        }
    }

    /**
     * 提取单列值
     */
    public List<String> fetchValues(String key) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < size(); i++) {
            result.add(getString(i, key));
        }
        return result;
    }

    /**
     * 取单列不重复的值
     */
    public List<String> fetchDistinctValue(String key) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < size(); i++) {
            String value = getString(i, key);
            if (result.contains(value)) {
                continue;
            }
            result.add(value);
        }
        return result;
    }

    public DataSet put(int idx, String key, Object value) {
        DataRow row = getRow(idx);
        if (null != row) {
            row.put(key, value);
        }
        return this;
    }

    /**
     * String
     */
    public String getString(int index, String key) {
        String result = null;
        DataRow row = getRow(index);
        if (null != row)
            result = row.getString(key);
        return result;
    }

    public String getString(String key) {
        return getString(0, key);
    }

    public List<String> getStrings(String key) {
        return fetchValues(key);
    }

    public List<String> getDistinctStrings(String key) {
        return fetchDistinctValue(key);
    }

    public BigDecimal getDecimal(int idx, String key) {
        BigDecimal result = null;
        DataRow row = getRow(idx);
        if (null != row)
            result = row.getDecimal(key);
        return result;
    }

    public BigDecimal getDecimal(int idx, String key, double def) {
        return getDecimal(idx, key, new BigDecimal(def));
    }

    public BigDecimal getDecimal(int idx, String key, BigDecimal def) {
        BigDecimal result = getDecimal(idx, key);
        if (null == result) {
            result = def;
        }
        return result;
    }

    /**
     * int
     */
    public int getInt(int index, String key) {
        int result = 0;
        DataRow row = getRow(index);
        if (null != row)
            result = row.getInt(key);
        return result;
    }

    public int getInt(String key) {
        return getInt(0, key);
    }

    /**
     * double
     */
    public double getDouble(int index, String key) {
        double result = 0;
        DataRow row = getRow(index);
        if (null != row)
            result = row.getDouble(key);
        return result;
    }

    public double getDouble(String key) {
        return getDouble(0, key);
    }

    /**
     * 合并
     */
    public DataSet unionAll(DataSet set) {
        DataSet result = new DataSet();
        if (null != rows) {
            int size = rows.size();
            for (int i = 0; i < size; i++) {
                result.add(rows.get(i));
            }
        }
        int size = set.size();
        for (int i = 0; i < size; i++) {
            DataRow item = set.getRow(i);
            result.add(item);
        }
        return result;
    }


    public String[] packParam(DataRow row, String... keys) {
        if (null == keys || null == row) {
            return null;
        }
        String params[] = new String[keys.length * 2];
        int idx = 0;
        for (String key : keys) {
            if (null == key) {
                continue;
            }
            String k1 = key;
            String k2 = key;
            if (key.contains(":")) {
                String tmp[] = key.split(":");
                k1 = tmp[0];
                k2 = tmp[1];
            }
            params[idx++] = k1;
            params[idx++] = row.getString(k2);
        }
        return params;
    }

    /***********************************************
     * 实现接口
     ************************************************************/
    public boolean add(DataRow e) {
        return rows.add(e);
    }

    public boolean addAll(Collection<DataRow> c) {
        return rows.addAll(c);
    }

    public void clear() {
        rows.clear();
    }

    public boolean contains(DataRow o) {
        return rows.contains(o);
    }

    public boolean containsAll(Collection c) {
        return rows.containsAll(c);
    }

    public Iterator iterator() {
        return rows.iterator();
    }

    public boolean remove(DataRow o) {
        return rows.remove(o);
    }

    public boolean removeAll(Collection<DataRow> c) {
        return rows.removeAll(c);
    }

    public boolean retainAll(Collection c) {
        return rows.retainAll(c);
    }

    public Object[] toArray() {
        return rows.toArray();
    }

    public List<DataRow> getRows() {
        return rows;
    }

    /**************************
     * getter setter
     ***************************************/

    public void setRows(ArrayList<DataRow> rows) {
        this.rows = rows;
    }

    public DataSet order(final String... keys) {
        return asc(keys);
    }


    public Object put(String key, Object value) {
        for (DataRow row : rows) {
            row.put(key, value);
        }
        return this;
    }

    /**
     * 排序
     */
    public DataSet asc(final String... keys) {
        Collections.sort(rows, new Comparator<DataRow>() {
            public int compare(DataRow r1, DataRow r2) {
                int result = 0;
                for (String key : keys) {
                    Object v1 = r1.get(key);
                    Object v2 = r2.get(key);
                    if (null == v1) {
                        if (null == v2) {
                            continue;
                        }
                        return -1;
                    } else {
                        if (null == v2) {
                            return 1;
                        }
                    }
                    if (v1 instanceof Number || v2 instanceof Number) {
                        BigDecimal val1 = new BigDecimal(v1.toString());
                        BigDecimal val2 = new BigDecimal(v2.toString());
                        result = val1.compareTo(val2);
                    } else {
                        result = v1.toString().compareTo(v2.toString());
                    }
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        });
        return this;
    }

    public DataSet desc(final String... keys) {
        Collections.sort(rows, new Comparator<DataRow>() {
            public int compare(DataRow r1, DataRow r2) {
                int result = 0;
                for (String key : keys) {
                    Object v1 = r1.get(key);
                    Object v2 = r2.get(key);
                    if (null == v1) {
                        if (null == v2) {
                            continue;
                        }
                        return 1;
                    } else {
                        if (null == v2) {
                            return -1;
                        }
                    }
                    if (v1 instanceof Number || v2 instanceof Number) {
                        BigDecimal val1 = new BigDecimal(v1.toString());
                        BigDecimal val2 = new BigDecimal(v2.toString());
                        result = val2.compareTo(val1);
                    } else {
                        result = v2.toString().compareTo(v1.toString());
                    }
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        });
        return this;
    }
}
