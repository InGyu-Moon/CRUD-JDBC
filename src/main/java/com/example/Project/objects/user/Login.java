package com.example.Project.objects.user;
public class Login {
    private String host;
    private String port;
    private String dbName;

    public Login(String host, String port, String dbName) throws Exception {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        checkParam();
    }
    public void checkParam()throws Exception{
        // [0] HOST 체크
        if (this.host == null ||
                this.host.trim().length() == 0){
            throw new Exception("Parameter HOST is invalid");
        }
    }
    public String makeUrl(){
        String url = "jdbc:oracle:thin:@" + host + ":" + port + "/"+ dbName;
        return url;
    }

}
