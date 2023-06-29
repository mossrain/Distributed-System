package com.app.server;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class  UploadServerThread implements Runnable{
	Socket sc;				
	String filename;
	public UploadServerThread(Socket sc1,String name){	//初始化
		sc = sc1;
		filename = name;
	}

	public void run(){
		try{
			OutputStream outputStream = new FileOutputStream(filename);		//创建指定文件的输出流对象
			InputStream inputStream = sc.getInputStream();					//获取socket对象的输入流
			int len = 0;
			byte[] temp = new byte[1024];									//每次写入1024个字节
			while((len=inputStream.read(temp))!=-1){						//若已读完输入流中的所有数据，则输入流长度为-1
				outputStream.write(temp,0,len);						//往指定文件中写入
			}	
			outputStream.close();					//单个文件传送完毕后关闭输出流
            System.out.println(filename+"传输完成！");
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}
}
