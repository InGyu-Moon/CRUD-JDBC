package com.example.Project.objects.user;


import java.util.ArrayList;

/**
 * User의 정보를 입력받아 해당 정보를 저장
 * ConnManager를 통해 User의 Connection을 저장
 */
public class User {
    private String userName;
    private String password;
    private ArrayList tableList = new ArrayList<String>();

    public User(String userName, String password) throws Exception {
        this.userName = userName;
        this.password = password;
        checkParam();
    }
    private void checkParam()throws Exception{
        // [0] userName 체크
        if (this.userName == null ||
                this.userName.trim().length() == 0){
            throw new Exception("Parameter userName is invalid");
        }
    }


    public ArrayList getTableList(){ return tableList; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }




}