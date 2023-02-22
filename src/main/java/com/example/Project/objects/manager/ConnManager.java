package com.example.Project.objects.manager;
import com.example.Project.objects.table.ColumnInfo;
import com.example.Project.objects.table.Table;
import com.example.Project.objects.user.Login;
import com.example.Project.objects.user.User;

import java.sql.*;
public class ConnManager {
    private User user;
    private Login login;
    //private Connection conn;

    public ConnManager( Login login,User user) throws ClassNotFoundException {
        this.user = user;
        this.login = login;
        Class.forName("oracle.jdbc.driver.OracleDriver");
    }
    public Connection makeConn() throws SQLException {
        //DriverManager.getConnection 변수 선언
        String url = login.makeUrl();
        Connection conn = (DriverManager.getConnection(url, user.getUserName(), user.getPassword()));
        System.out.println("DB 접속 성공...");
        return conn;
    }
    public User getUser(){
        return this.user;
    }
    public void initTableList() throws SQLException {
        String temp = user.getUserName().toUpperCase();

        Connection cx = makeConn();
        ResultSet rs = null;
        try{
            rs = cx.getMetaData().getTables(null, temp, null , new String[]{"TABLE"});
            user.getTableList().clear();

            while(rs.next()) {
                String table = rs.getString("TABLE_NAME");
                user.getTableList().add(table);
            }

        }catch( Exception e){
            System.out.println("");
        }finally {
            if (rs != null)
                rs.close();
            if (cx != null)
                cx.close();
        }
    }

    public Table getTableInfo(String tableName) throws SQLException {
        Table table= new Table(tableName);
        Connection cx = makeConn();
        ResultSet rs = null;
        Statement st = null;
        try {
            String sql = "select * from " + tableName;
            st = cx.createStatement();
            rs = st.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int colNum = rsmd.getColumnCount();
            for (int i = 0; i < colNum; i++) {

                // 변수 생성
                String colName = rsmd.getColumnName(i + 1);
                String coltype = rsmd.getColumnTypeName(i + 1);
                int colMaxLen = rsmd.getColumnDisplaySize(i + 1);

                //System.out.println(colNum +", " + colName +", " + coltype +", " + colMaxLen);

                // 값 체크




                // 값 부여
                ColumnInfo col = new ColumnInfo(colName);
                col.setColNum(i);
                col.setColType(coltype);
                col.setColMaxLen(colMaxLen);
                table.addColumn(col);
            }
        } catch (Exception e) {
            System.out.println("Exception occur");
        } finally {
            if (st != null)
                st.close();
            if (rs != null)
                rs.close();
            if (cx != null)
                cx.close();
        }
        return table;
    }


}
