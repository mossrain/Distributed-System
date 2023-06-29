package com.app.server;

import com.app.common.ServerInfo;
import com.app.entity.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 多线程的方式启动Query服务
 * 暂采用tcp方式通信
 * 单socket连接还是反复socket连接（线程里再开子线程）暂未定
 */
public class Query implements Runnable {

    ServerInfo serverInfo = new ServerInfo();

    public void run() {
        try {
            System.out.println("[query]:启动query服务！");
            ServerSocket serverSocket = new ServerSocket(serverInfo.queryPort);
            Logger logger = new Logger("47.100.170.76");
            //1.建立连接等待客户端socket
            while (true) {
                Socket socket = serverSocket.accept();
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                String info = dataInputStream.readUTF();
                String result = logger.query(info);
                dataOutputStream.writeUTF(result);
            }
        } catch (IOException e) {
            System.out.println("[error]:query服务端启动失败！");
        }

    }



}
