package com.example.Project.objects.io;

import org.apache.poi.ss.usermodel.CellType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WriteErrorLog {
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    String nowTime = now.format(formatter);
    String fileName = ("C:\\test\\fail_" + nowTime +".txt");
    File txtFile = new File(fileName);

    public WriteErrorLog() throws IOException {
        txtFile.createNewFile();
    }

    public void writeLogFile(int i,int j,String str1, CellType str2) throws IOException {
        try (FileWriter writer = new FileWriter(txtFile, true)) {
            writer.write(i  +" row "+j+" col");
            writer.write(" -> error : " + str1 + " != " + str2+"\n");
        }
    }
}
