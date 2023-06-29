package com.app.client;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import com.app.common.ServerInfo;

public class QueryClient {

    static ServerInfo serverInfo = new ServerInfo();

    static ArrayList<String> ipList = new ArrayList<>();

    public static void main(String[] args) throws UnknownHostException {
        System.out.println("进入组成员日志查询系统...（输入q退出）");
        Scanner scan = new Scanner(System.in);
        String info = "";

        while (true) {
            System.out.println("");
            System.out.println("请输入要查询的关键字...");
            if (scan.hasNextLine()) {
                info = scan.nextLine();
                if (info.equals("q"))
                    return;
                query(info);
            }

        }
    }

    static class queryThread implements Runnable {
        static String ip;
        static String word;

        queryThread(String ip, String word) {

            queryThread.ip = ip;
            queryThread.word = word;
        }

        @Override
        public void run() {
            try {
                Socket socket = new Socket(ip, serverInfo.queryPort);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream.writeUTF(word);
                String result = dataInputStream.readUTF();
                System.out.println(result);
            } catch (IOException e) {
               System.out.println("[queryError]:无法与"+ip+"通信！");
            }
        }

    }

    public static void query(String info)  {
        //1.与introducer通信，获取现在存活的ip列表
        try {
            ipList = getIpList();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        //2.向各个ip发送请求
        System.out.println("[query]:开始查询...");
        System.out.println("[query]:结果如下");
        System.out.println("==========================");
        for (String ip : ipList) {
            Thread t = new Thread(new queryThread(ip, info));
            t.start();
        }
    }

    //与introducer通信获取最新的列表
    public static ArrayList<String> getIpList() throws UnknownHostException {
        InetAddress address;
        //introducer的ip地址
        address = InetAddress.getByName(serverInfo.introducerIp);
        //新加入节点的ip地址
        byte[] data = "client".getBytes();//发送ip
        // 2.创建数据报，包含发送的数据信息
        DatagramPacket packet = new DatagramPacket(data, data.length, address, serverInfo.introducerPort);
        try (// 3.创建DatagramSocket对象
             DatagramSocket socket = new DatagramSocket()
        ) {
            // 4.向服务器端发送数据报
            socket.send(packet);
            /*
             * 接收服务器端响应的数据
             */
            // 1.创建数据报，用于接收服务器端响应的数据
            byte[] data2 = new byte[1024];
            DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
            // 2.接收服务器响应的数据
            socket.setSoTimeout(10000);
            socket.receive(packet2);
            // 3.读取数据
            String reply = new String(data2, 0, packet2.getLength());
            System.out.println("[query]:从introducer获取到最新的成员列表: \n" + reply);
            // 4.关闭资源
            socket.close();

            if (reply.length() > 0) {//reply应为新的组成员列表的内容
                String[] newMemberList = reply.split("\n");
                ipList.clear();
                for (String newmember : newMemberList) {
                    String newmember_ip = newmember.split(" ")[1];
                    ipList.add(newmember_ip);
                }
            }
        } catch (IOException e) {
            //出现异常
            System.out.println("[queryError]:无法与introducer通信！");
            return ipList;
        }
        return ipList;
    }


}