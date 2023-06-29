package com.app.server;

import java.io.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.app.common.ServerInfo;
// import com.app.server.Query;

public class QueryServer {
    static ServerInfo serverInfo = new ServerInfo();
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;

    public static void main(String[] args) throws Exception {

        //端口号为1
        ServerSocket serverSocket = new ServerSocket(serverInfo.queryPort);
        
        while(true){
            //连接client端
            System.out.println("服务器已启动，等待连接...");
            Socket socket = serverSocket.accept();
            System.out.println("连接成功！");
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            //获取client发送过来的参数
            String type=dataInputStream.readUTF();
            String name=dataInputStream.readUTF();
            int start=dataInputStream.readInt();
            int end = dataInputStream.readInt();

            //执行查询功能
            // String result = search(type,name,start,end);
            Query q=new Query(name, start, end,serverInfo.filePathList[1]);
            List<String> res=q.search();
            // return res;
            //返回结果
            
            String result="";
            if(res.size()>=1) result+=res.get(0);
            for(int i=1;i<res.size();i++){
                result+=","+res.get(i);
            }

            System.out.println("发回客户端：");
            System.out.println(result);
            dataOutputStream.writeUTF(result);
            

            socket.close();

        }
    }
}
