package com.example.Project.objects.io;

import com.example.Project.objects.table.Table;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InsertXLSX {
       public boolean insertFile(Connection conn, Table table,XSSFWorkbook workbook) throws IOException, SQLException {
        DataTypeMapper dtm = new DataTypeMapper();
        boolean flag = true; // 에러발생시 addbatch 실행 유무, true->addbatch(O)
        boolean rollback = false; //rollback 유무, true->rollback(O)

        XSSFSheet sheet = workbook.getSheetAt(0);

        int rows = sheet.getPhysicalNumberOfRows();

        //xlsxColNameList xlsx파일의 column들의 이름 리스트
        //만든 xlsxColNameList는 findSameCol 함수에서 db와 비교함
        ArrayList<String> xlsxColNameList = new ArrayList<>();
        XSSFRow row = sheet.getRow(0);
        int _temp = row.getPhysicalNumberOfCells();
        for (int i = 0; i < _temp; i++){
            XSSFCell cell = row.getCell(i);
            xlsxColNameList.add(cell.getStringCellValue());
        }

        /**
         * findSameCol(Table table,ArrayList<String> xlsxColNameList)
         * db와 xlsx파일에서 동일한 column을 구해 각가의 index저장
         * db index, xlsx index
         */
        ArrayList<Pair<Integer, Integer>> sameColList = findSameCol(table,xlsxColNameList);
        conn.setAutoCommit(false);

        /**
         * makeInsertSql(Table table)
         * return String = insert into tableName (a,b,c) values(?,?,?)
         */
        String sql = makeInsertSql(table);

        PreparedStatement pstmt = conn.prepareStatement(sql);
        int count = 0;

        for(int i=1; i<rows; i++) {
            row = sheet.getRow(i);
            int cells = row.getPhysicalNumberOfCells();
            flag=true;
            for (Pair<Integer, Integer> pair : sameColList) {
                if(pair.getSecond()==-1){
                    pstmt.setNull(pair.getFirst() + 1, Types.NULL);
                }
                else{
                    /**
                     * getFirst()==db index
                     * getSecond()==xlsx index
                     */
                    XSSFCell cell = row.getCell(pair.getSecond());

                    if (cell == null) {
                        continue;
                    }
                    /**
                     * areTypesEquivalent(String dbType, CellType cellType)
                     * dbtype == celltype return true
                     */
                    if(!dtm.areTypesEquivalent(table.getColumn(pair.getFirst()).getColType(),cell.getCellType())){

                        try{
                            if(cell.getCellType()== CellType.STRING){
                                switch(table.getColumn(pair.getFirst()).getColType()){
                                    case "NUMBER":
                                        double _num = Double.parseDouble(cell.getStringCellValue());
                                        pstmt.setDouble(pair.getFirst() + 1,_num);
                                    case "DATE":
                                        Date _tempDate = cell.getDateCellValue();
                                        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        pstmt.setString(pair.getFirst() + 1, String.valueOf(newFormat.format(_tempDate)));
                                    default:
                                        System.out.println("unknown type in switch(table.getColumn(pair.getFirst()).getColType())");
                                        System.out.println("table.getColumn(pair.getFirst()).getColType() = " + table.getColumn(pair.getFirst()).getColType());
                                }
                            }
                        }catch (Exception e){

                                WriteErrorLog writeErrorLog = new WriteErrorLog();
                                writeErrorLog.writeLogFile(i+1,pair.getFirst()+1,
                                        table.getColumn(pair.getFirst()).getColType(),cell.getCellType());
                                //에러 발생시 addbatch(X) rollback(O)
                                flag = false; //addbatch 유무 flag==true ->addbacth(O)
                                rollback=true;//rollback 유무 rollback==true -> rollback(O)

                        }



                    }else {
                        //스위치문을 이용해, 셀의 타입에 따라 가져오는 방법이 다름.
                        switch (cell.getCellType()) {
                            case FORMULA:
                                pstmt.setString(pair.getFirst() + 1, cell.getCellFormula());
                                break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    Date _tempDate = cell.getDateCellValue();
                                    SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    pstmt.setString(pair.getFirst() + 1, String.valueOf(newFormat.format(_tempDate)));
                                } else {
                                    pstmt.setDouble(pair.getFirst() + 1, cell.getNumericCellValue());
                                }
                                break;
                            case STRING:
                                pstmt.setString(pair.getFirst() + 1, cell.getStringCellValue());
                                break;
                            default:
                                System.out.println("unknown cell type");
                                break;
                        }
                    }
                }
            }
            if(flag){
                pstmt.addBatch();
                count++;
                if ((count % 10) == 0) {
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                    //conn.commit();
                }
            }
        }
        pstmt.executeBatch();
        if(rollback){
            conn.rollback();
            return false;
        }
        else{
            conn.commit();
            return true;
        }

    }
    public String makeInsertSql(Table table) throws SQLException {

        String sql1 = "Insert Into " + table.getTableName() + " (";
        String sql2 = "values(";

        int cols = table.getColList().size();
        for(int i=0;i<cols;i++){

            sql1 += table.getColumnByNum(i).getColName();
            sql2 += "?";

            if(i==cols-1){
                sql1+=") ";
                sql2+=") ";
            }
            else{
                sql1 +=", ";
                sql2 += ", ";
            }
        }
        String returnSql = sql1 + sql2;
        return returnSql;
    }
    public ArrayList<Pair<Integer, Integer>> findSameCol(Table table,ArrayList<String> xlsxColNameList){

        int colNum = table.getColList().size();
        ArrayList colNameList = new ArrayList<>();
        for(int i=0;i<colNum;i++){
            colNameList.add(table.getColumn(i).getColName());
        }

        ArrayList<Pair<Integer, Integer>> sameColList = new ArrayList<>();
        /**
         * getFirst()==db index
         * getSecond()==xlsx index
         */
        for(int i=0;i<colNum;i++){
            for(int j=0;j<xlsxColNameList.size();j++){
                if(colNameList.get(i).equals(xlsxColNameList.get(j))){
                    sameColList.add(new Pair<>(i,j));
                    break;
                }
                if(j==2){
                    sameColList.add(new Pair<>(i,-1));
                }
            }
        }
        return sameColList;
    }


}