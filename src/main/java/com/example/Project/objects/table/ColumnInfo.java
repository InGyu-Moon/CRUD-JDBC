package com.example.Project.objects.table;
public class ColumnInfo {
    private int colId;
    private String colName;
    private String colType;
    private int colMaxLen;


    public ColumnInfo(String nm){
        this.colName = nm;
    }

    public void setColNum(int colId){
        this.colId = colId;
    }

    public String getColName(){
        return this.colName;
    }

    public int getColId() {
        return colId;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColType() {
        return colType;
    }

    public void setColType(String colType) {
        this.colType = colType;
    }

    public int getColMaxLen() {
        return colMaxLen;
    }

    public void setColMaxLen(int colMaxLen) {
        this.colMaxLen = colMaxLen;
    }
}
