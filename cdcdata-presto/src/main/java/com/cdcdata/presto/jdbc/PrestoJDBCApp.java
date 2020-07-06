package com.cdcdata.presto.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class PrestoJDBCApp {
    public static void main(String[] args) throws Exception{
        Class.forName("com.facebook.presto.jdbc.PrestoDriver");
        String url = "jdbc:presto://jd:8780/hive/cdcdata_offline_dw";
        Connection connection = DriverManager.getConnection(url,"test","");

        String sql = "select province,traffics from dws_accecc_proince_traffic";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()){
            String province = rs.getString("province");
            long traffics = rs.getLong("traffics");
            System.out.println(province+"\t"+traffics);
        }
        rs.close();
        stmt.close();
        connection.close();

    }
}
