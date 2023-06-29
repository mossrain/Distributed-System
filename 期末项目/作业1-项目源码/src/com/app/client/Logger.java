package com.app.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Logger {
    File file;
    public Logger(){
        file=new File("client.log");
        try {
            file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println(file.getName()+"文件已存在！");
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
}
