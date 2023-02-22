package com.example.Project.objects.io;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

public class CreateXLSX {

    public void createFile(Connection conn, String dbName) throws SQLException, IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(dbName);
        Row xRow = null;
        Cell xCell = null;

        String sql = "select * from " + dbName;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();

        int colNum = rsmd.getColumnCount();


        String filePath = "C:\\test\\";

        File file = new File(filePath + dbName + ".xlsx");
        FileOutputStream fos = new FileOutputStream(file);

        xRow = sheet.createRow(0);
        for (int i = 1; i <= colNum; i++) {
            xCell = xRow.createCell(i - 1);
            xCell.setCellValue(rsmd.getColumnName(i));
            //System.out.println(rsmd.getColumnName(i)+", ");
        }
        int row = 1;
        while (rs.next()) {
            xRow = sheet.createRow(row);
            for (int i = 1; i <= colNum; i++) {
                String temp = rs.getString(i);
                //System.out.println(temp+", ");
                xCell = xRow.createCell(i - 1);
                xCell.setCellValue(temp);
            }
            row++;
        }
        workbook.write(fos);
        rs.close();
    }
}

