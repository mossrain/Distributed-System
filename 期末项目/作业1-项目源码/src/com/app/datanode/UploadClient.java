package com.app.datanode;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;



public class UploadClient {

    static String ip;
    static int port;

    /**
     * uploadClient类：与一个对应的DataServer建立联系
     *
     * @param targetIp   目标服务器的ip
     * @param targetPort 目标服务器的端口
     */
    public UploadClient(String targetIp, int targetPort) {
        ip = targetIp;
        port = targetPort;
    }

    /**
     * 传输一个文件到此client对应的server上
     *
     * @param filePath 要发送的文件地址
     * @param fileName 服务器端将此文件存储为什么名字
     * @throws IOException socket通信故障
     */
    public void send(String filePath,String fileName) throws IOException {
        File file = new File(filePath);
        try {
            Socket socket = new Socket(ip, port);
            OutputStream outputStream = socket.getOutputStream();        //创建输出流对象
            DataOutputStream out = new DataOutputStream(outputStream);    // 用于输出文件名 socket输出流
            FileInputStream inputStream = new FileInputStream(file);   //读取要发送的本地文件  文件输入流
            out.writeUTF(fileName);                //将文件名发送给服务器
            int len = -1;
            byte[] temp = new byte[1024];
            while ((len = inputStream.read(temp)) != -1) {
                outputStream.write(temp, 0, len);
            }
            out.close();    //发送完毕后关闭输出流。
            outputStream.close();
            System.out.println("成功发送"+fileName);
        } catch (Exception e) {
            System.out.println("UploadClient通信故障！");
            e.printStackTrace(System.out);
            return;//不停止的话，就会造成乱序上传，导致日志顺序紊乱
        }

    }
}
