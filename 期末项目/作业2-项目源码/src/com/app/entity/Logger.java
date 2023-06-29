package com.app.entity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/**
 * 日志管理类
 * 
 */
public class Logger {
    File file;
    String ip;
    public Logger(String ip){
        this.ip=ip;
        System.out.println("[query]:打开的文件："+ip+".log");
        file=new File(ip+".log");
        try {
            file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("[query]:"+file.getName()+"文件已存在！");
        }

    }

    public void writeInfo(String text){
        //将text写入到log最后一行
        try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)))){
            StringBuffer stringBuffer = new StringBuffer();
            long timestamp=System.currentTimeMillis();
            stringBuffer.append(String.valueOf(timestamp)+" ");
            stringBuffer.append(text);
            stringBuffer.append("\n");
            bw.write(stringBuffer.toString());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String query(String keyword){

        String result = "";
        try (Scanner scanner = new Scanner(file)) {
      
            System.out.println("[query]:开始查询文件");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
      
            if (line.contains(keyword)) {
                result = result + line + "\r\n";
            
            }
        }
        } catch (FileNotFoundException e) {
            System.out.println("[queryError]:找不到log.");
        }
        return result;
    }


}
