package com.example.Project.objects.table;

import java.sql.*;
import java.util.ArrayList;

public class Table {
    private String tableName;
    private ArrayList<ColumnInfo> colList = new ArrayList<ColumnInfo>();
    public Table( String tableName) throws SQLException {
        this.tableName = tableName;
    }

    public void addColumn(ColumnInfo col){
        this.colList.add(col);
    }

    public ColumnInfo getColumn(int idx){
        return colList.get(idx);
    }
    public ColumnInfo getColumnByNum(int colId){
        for (ColumnInfo col : colList){
            if (col.getColId()==colId){
                return col;
            }
        }
        return null;
    }

    public ColumnInfo getColumnByName(String name){
        for (ColumnInfo col : colList){
            if (col.getColName().equalsIgnoreCase(name)){
                return col;
            }
        }
        return null;
    }

    public ArrayList<ColumnInfo> getColList() {
        return colList;
    }

    public String getTableName(){
        return tableName;
    }
}
