package net.twoant.master.widget.entry;

import net.twoant.master.common_utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by S_Y_H on 2016/12/19.
 * 数据
 */

public class DataRow extends HashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 1L;

    private DataSet container;//包含当前对象的容器

    public DataRow() {
    }

    public DataRow(Map<String, Object> map) {
        this();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            put(key.toUpperCase(), value);
        }
    }

    public static DataRow parse(Object obj) {
        DataRow row = new DataRow();
        try {
            if (null != obj) {
                if (obj instanceof JSONObject) {
                    row = parseJson((JSONObject) obj);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return row;
    }

    public static DataRow parseJson(String json) {
        if (null != json) {
            try {
                return parseJson(new JSONObject(json));
            } catch (Exception e) {
                LogUtils.e(e.toString());
            }
        }
        return null;
    }

    public static DataRow parseJson(JSONObject json) throws JSONException {
        DataRow row = new DataRow();
        if (null == json) {
            return row;
        }
        Iterator<String> itr = json.keys();
        while (itr.hasNext()) {
            String key = itr.next();
            Object val = json.get(key);
            if (null != val) {
                if (val instanceof JSONObject) {
                    row.put(key, parseJson((JSONObject) val));
                } else if (val instanceof JSONArray) {
                    row.put(key, DataSet.parseJson((JSONArray) val));
                } else {
                    row.put(key, val);
                }
            }
        }
        return row;
    }

    public ArrayList<String> getKeys() {
        ArrayList<String> keys = new ArrayList<>();
        Set<String> strings = this.keySet();
        for (String s : strings) {
            keys.add(s);
        }
        return keys;
    }

    public Object put(String key, Object value) {
        if (null != key) {
            return super.put(key.toUpperCase(), value);
        }
        return null;
    }

    public Object get(String key) {
        Object result = null;
        if (null != key) {
            result = super.get(key.toUpperCase());
        }
        return result;
    }

    public DataRow getRow(String key) {
        if (null == key) {
            return null;
        }
        Object obj = get(key);
        if (null != obj && obj instanceof DataRow) {
            return (DataRow) obj;
        }
        return null;
    }

    public DataSet getSet(String key) {
        if (null == key) {
            return null;
        }
        Object obj = get(key);
        if (null != obj && obj instanceof DataSet) {
            return (DataSet) obj;
        }
        return null;
    }

    public String getStringDef(String key, String def) {
        String result = getString(key);
        if (result == null || result.length() == 0 || "null".equalsIgnoreCase(result)) {
            return def;
        }
        return result;
    }

    public String getStringNvl(String key, String... defs) {
        String result = getString(key);
        if (BasicUtil.isEmpty(result)) {
            if (null == defs || defs.length == 0) {
                result = "";
            } else {
                result = BasicUtil.nvl(defs).toString();
            }
        }
        return result;
    }

    public String getString(String key) {
        String result = null;
        Object value = get(key);
        if (null != value)
            result = value.toString();
        return result;
    }

    /**
     * boolean类型true 解析成 1
     */
    public int getInt(String key) {
        int result = 0;
        try {
            Object val = get(key);
            if (null != val) {
                if (val instanceof Boolean && (Boolean) val) {
                    result = 1;
                } else {
                    result = Integer.parseInt(val.toString());
                }
            }
        } catch (Exception e) {
            result = 0;
        }
        return result;
    }

    public double getDouble(String key) {
        double result;
        Object value = get(key);
        try {
            result = Double.parseDouble(value.toString());
        } catch (Exception e) {
            result = 0;
        }
        return result;
    }

    public float getFloat(String key) {
        float result;
        Object value = get(key);
        try {
            result = Float.parseFloat(value.toString());
        } catch (Exception e) {
            result = 0;
        }
        return result;
    }

    public long getLong(String key) {
        long result;
        try {
            Object value = get(key);
            result = Long.parseLong(value.toString());
        } catch (Exception e) {
            result = 0;
        }
        return result;
    }

    public boolean getBoolean(String key, boolean def) {
        return BasicUtil.parseBoolean(getString(key), def);
    }

    /**
     * 包含当前对象的容器
     */
    public DataSet getContainer() {
        return container;
    }

    public void setContainer(DataSet container) {
        this.container = container;
    }


    public BigDecimal getDecimal(String key) {
        BigDecimal result;
        try {
            result = new BigDecimal(getString(key));
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    public BigDecimal getDecimal(String key, double def) {
        return getDecimal(key, new BigDecimal(def));
    }

    public BigDecimal getDecimal(String key, BigDecimal def) {
        BigDecimal result = getDecimal(key);
        if (null == result) {
            result = def;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DataRow dataRow = (DataRow) o;

        return container != null ? container.equals(dataRow.container) : dataRow.container == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (container != null ? container.hashCode() : 0);
        return result;
    }
}
