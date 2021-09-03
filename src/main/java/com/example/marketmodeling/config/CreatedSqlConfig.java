package com.example.marketmodeling.config;

import java.lang.reflect.Field;
import java.util.Map;

public class CreatedSqlConfig {

    /**
     * 初始化数据保存
     * @param tableName
     * @return
     */
    public static String initCreatedTable(Class<?>t,String tableName) {

        Field[] clunms = t.getDeclaredFields();

        String sqlCommon = "Insert INTO " + tableName + "(";

        int index = 0;

        for (Field f : clunms) {

            String name = f.getName();

            name = underline(name);

            index++;


            if (name.equals("id")){
                continue;
            }

            //仅有一个参数或是最后一个参数
            if ((index == 1 && clunms.length == 1) || clunms.length == index) {
                sqlCommon += String.format("`%s`)", name);
            } else {
                //Constants.SQL.ADD 和 and是一样的
                //sqlCommon += key+"="+value+" "+ Constants.SQL.ADD +" ";
                sqlCommon += String.format("`%s`,", name);
            }
        }

        sqlCommon = String.format("%s VALUES(", sqlCommon);

        index = 0;

        for (Field f : clunms) {

            String name = f.getName();

            index++;

            if (name.equals("id")){
                continue;
            }

            if ((index == 1 && clunms.length == 1) || clunms.length == index) {
                sqlCommon += String.format("?)", name);
            } else {
                sqlCommon += String.format("?,", name);
            }
        }

        return sqlCommon;
    }

    /**
     * 驼峰转下划线
     * @param name
     * @return
     */
    private static String underline(String name) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < name.length(); ++i) {
            char ch = name.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                char ch_ucase = (char) (ch + 32);
                if (i > 0) {
                    buf.append('_');
                }
                buf.append(ch_ucase);
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * 初始化保存sql
     * @param tableName
     * @param kvMap
     * @return
     */
    public static String iniSaveSql(String tableName, Map<String, String> kvMap) {

        String sql = String.format("insert into %s (", tableName);

        String values = "values (";

        for (String column : kvMap.values()) {

            sql = sql + String.format("`%s`,", column);

            values = values + String.format(":%s,", column);
        }

        sql = sql + "})";

        sql = sql.replace(",}", "");

        values = values + "})";

        values = values.replace(",}", "");

        return String.format("%s%s", sql, values);
    }

}
