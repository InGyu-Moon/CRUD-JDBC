package com.example.Project.controller;

import com.example.Project.objects.io.CreateXLSX;
import com.example.Project.objects.io.InsertXLSX;
import com.example.Project.objects.manager.ConnManager;
import com.example.Project.objects.table.Table;
import com.example.Project.objects.user.Login;
import com.example.Project.objects.user.LoginRequest;
import com.example.Project.objects.user.TableRequest;
import com.example.Project.objects.user.User;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LoginConnController {
    private ConnManager connManager;
    private Login login;
    private User user;
        @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest loginRequest) {
        String host = loginRequest.getHost();
        String port = loginRequest.getPort();
        String dbname = loginRequest.getDbname();
        String username = loginRequest.getUser();
        String password = loginRequest.getPassword();
        Map<String, String> response = new HashMap<>();
       /* if (host == "" || port == "" || dbname == "" || username == "" || password == "") {
            System.out.println("blank error");
            response.put("status", "blank");
            response.put("message", "Input value is blank!!!");
            return response;
        }*/
        try{
            login = new Login(host,port,dbname);
            //System.out.println("login 생성");
            user = new User(username,password);
            //System.out.println("user 생성");
            connManager = new ConnManager(login,user);
            //System.out.println("connManager 생성");
            Connection conn = connManager.makeConn();
            //System.out.println("makeConn");
            if(conn!=null)
                conn.close();
            response.put("status", "success");
            response.put("message", "Login successful");
            System.out.println("login success");
            return response;
        }catch(Exception e){
            System.out.println("error = " + e);
            response.put("status", "fail");
            response.put("message", "Login fail try again");
            System.out.println("login fail");
            return response;
        }
    }
    @PostMapping("/getTablelist")
    public Map<String, Object> getTableList() {
        Map<String, Object> response = new HashMap<>();
        try{
            connManager.initTableList();
            ArrayList<String> tableList = user.getTableList();
            response.put("status", "success");
            response.put("message", "Table list retrieved");
            response.put("tableList", tableList);
            return response;

        }catch(Exception e){
            response.put("status", "fail");
            response.put("message", "Table list fail try again");
            return response;
        }
    }
    @PostMapping("/getTableName")
    public Table getTable(@RequestBody TableRequest tableRequest ) throws SQLException {

        String tableName = tableRequest.getTableName();
        Table table = connManager.getTableInfo(tableName);
        return table;
    }

    @PostMapping("/showData")
    public Object[][] getData(@RequestBody TableRequest tableRequest) throws SQLException {

        String tableName = tableRequest.getTableName();
        Table table = connManager.getTableInfo(tableName);
        try (Connection conn = connManager.makeConn()) {
            String sql = "SELECT * FROM " + tableName;

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                ResultSetMetaData rsmd = rs.getMetaData();
                int numColumns = rsmd.getColumnCount();

                List<Object[]> rows = new ArrayList<>();
                // add column names as the first row in the 2D array
                String[] columnNames = new String[numColumns];
                for (int i = 1; i <= numColumns; i++) {
                    columnNames[i - 1] = rsmd.getColumnName(i);
                }
                rows.add(columnNames);

                while (rs.next()) {
                    Object[] row = new Object[numColumns];
                    for (int i = 1; i <= numColumns; i++) {
                        row[i - 1] = rs.getObject(i);
                    }
                    rows.add(row);
                }

                Object[][] data = new Object[rows.size()][];
                rows.toArray(data);

                return data;

            } catch (SQLException e) {
                System.out.println("Error occurred: " + e.getMessage());
                return null;
            }
        }
    }


    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("tableName") String tableName) throws SQLException {
        //System.out.println("File name: " + file.getOriginalFilename());
        String _temp = file.getOriginalFilename();
        String fileName = _temp.substring(0, _temp.indexOf(".xlsx"));

        Table insertTable = connManager.getTableInfo(tableName);
        try {
            Connection conn = connManager.makeConn();
            InputStream inputStream = file.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            InsertXLSX insertXLSX = new InsertXLSX();

            if(insertXLSX.insertFile(conn,insertTable, workbook)){
                if(conn!=null)
                    conn.close();
                inputStream.close();
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else{
                if(conn!=null)
                    conn.close();
                inputStream.close();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }


        } catch (Exception e) {
            // Handle the exception
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/export")
    public ResponseEntity<String> handleFileExport(@RequestParam("tableName") String tableName) throws SQLException {


        try {
            Connection conn = connManager.makeConn();
            CreateXLSX createXLSX = new CreateXLSX();

            try{
                createXLSX.createFile(conn,tableName);
            }catch (Exception e){
                System.out.println("e = " + e);
                System.out.println("export fail");
            }

            if(conn!=null)
                conn.close();

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            // Handle the exception
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
