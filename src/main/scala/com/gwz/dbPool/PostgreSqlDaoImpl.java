package com.gwz.dbPool;

import com.alibaba.fastjson.JSONObject;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PostgreSqlDaoImpl {

    public static void main(String[] args) {
        //insert
//        String insert = "INSERT INTO user_tbl(name, signup_date) VALUES(?,?);";
//        List<Object[]> params = new ArrayList<Object[]>();
//        Date date = new Date(System.currentTimeMillis());
//        params.add(new Object[]{"张三",date});
//        params.add(new Object[]{"李四",date});
//        //PostgreSqlDaoImpl.insert(insert,params);
//
//        //select
//        String select = "select * from user_tbl";
//        PostgreSqlDaoImpl.select(select);
//
//        //update
//        String update ="update user_tbl set name=? where name=?";
//        Object[] param = new Object[]{"张二","张三"};
//        System.out.println("修改行数："+PostgreSqlDaoImpl.update(update, param));

        String json = "{\"1\":1.0,\"3\":3.0,\"3\":4.0,\"4\":5.0}";
        JSONObject jsonObject = JSONObject.parseObject(json);

        System.out.println(jsonObject.getDouble("3"));
        jsonObject.remove("3");

        System.out.println(jsonObject.getDouble("3"));
        //motif(jsonObject,"0_4",6.0);

    }

    /**
     * 查询
     * @param sql
     */
    public static void select(String sql){

        JDBCHelper jdbcHelper = JDBCHelper.getInstance();

        jdbcHelper.executeQuery(sql, null, new JDBCHelper.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while(rs.next()){
                    String name = rs.getString(1);
                    Date signup_date = rs.getDate(2);
                    System.out.println(name+"-----"+signup_date);
                }
            }
        });
    }

    /**
     * 新增
     * @param sql
     * @param insertParams
     */
    public static void insert(String sql,List<Object[]> insertParams){

        JDBCHelper jdbcHelper = JDBCHelper.getInstance();

        jdbcHelper.executeBatch(sql,insertParams);
    }

    /**
     * 修改
     * @param sql
     * @param updateParams
     */
    public static int update(String sql,Object[] updateParams){

        JDBCHelper jdbcHelper = JDBCHelper.getInstance();

        int row = jdbcHelper.executeUpdate(sql, updateParams);

        return row;
    }

    public static void motif(JSONObject jsonObject, String hhmm,Double value){
        int hh = Integer.valueOf(hhmm.split("_")[0]);
        int mm = Integer.valueOf(hhmm.split("_")[1]);

        //计算当前时间的位置
        int computerIndex = hh * 60 + mm;
        String indexStr = String.valueOf(computerIndex);
        Double v1 = jsonObject.getDouble(indexStr);
        if(null != v1){
            jsonObject = toOneBefore(jsonObject, computerIndex - 1, v1);
            jsonObject.put(indexStr,v1);
        }else{
            jsonObject.put(indexStr,value);
        }
        System.out.println(jsonObject.toJSONString());

    }

    public static JSONObject toOneBefore(JSONObject jsonObject,int index,Double value){
        Double v1 = jsonObject.getDouble(String.valueOf(index));
        if(null != v1){
            jsonObject = toOneBefore(jsonObject, index - 1,v1);
            jsonObject.put(String.valueOf(index),value);
        }else{
            jsonObject.put(String.valueOf(index),v1);
        }
        return jsonObject;
    }


}
