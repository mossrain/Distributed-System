package com.app.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.app.common.ServerInfo;
import com.app.entity.MemberList;
import com.app.entity.MemberList.MemberInfo;

public class Introducer {
    public static String ip="47.100.170.76";
    static ServerInfo serverInfo=new ServerInfo();

    //introducer节点也需要维护一个全局的表，且保证其是最新的，这样其他节点加进来的时候才不会出问题
    static MemberList memberList=new MemberList();


    public static void addMember(String ipToAdd){
        long timestamp=System.currentTimeMillis();
        //始终等待新节点来找introducer
        //成功加入
        memberList.add(String.valueOf(timestamp), ipToAdd);
        spreadNewMember(String.valueOf(timestamp), ipToAdd);
        System.out.println("[introducer]:"+ipToAdd+"加入组！");
        
    }

    static class RunnableBroadCast implements Runnable {
        String timeStamp;
        static String ip;
        String destip;
        Thread t;

        RunnableBroadCast(String timeStamp,String ip,String destip){
            this.destip=destip;
            this.ip=ip;
            this.timeStamp=timeStamp;
        }
        public void start(){
            if(t==null){
                t=new Thread(this);
                t.start();
            }
        }

        public void sendMessage() throws UnknownHostException {
              /*
            * 向members发送新成员join消息
            */
            // 1.定义服务器的地址、端口号、数据
            InetAddress address  = InetAddress.getByName(destip);
            //新加入节点的ip地址
            byte[] data = (timeStamp+" "+ip).getBytes();//发送ip
            // 2.创建数据报，包含发送的数据信息
            DatagramPacket packet = new DatagramPacket(data, data.length, address, serverInfo.broadcastPort);

         
            
            try (// 3.创建DatagramSocket对象
                DatagramSocket socket = new DatagramSocket()) {
                // 4.向服务器端发送数据报
                socket.send(packet);

                System.out.println("[introducer]:成功向" + destip + "发送新成员信息！");

                /*
                * 接收服务器端响应的数据
                */
                // 1.创建数据报，用于接收服务器端响应的数据
                byte[] data2 = new byte[1024];
                DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
                // 2.接收服务器响应的数据
                socket.receive(packet2);
                // 3.读取数据
                String reply = new String(data2, 0, packet2.getLength());

                System.out.println("[introducer]:从"+destip+"获取到reply: " + reply);

                // 4.关闭资源
                socket.close();
            } catch (IOException e) {
                //出现异常
                System.out.println( "[introducerError]:无法向"+destip+"发送新成员加入消息！");
                return;
            }
        }

        @Override
        public void run() {
            try {
                sendMessage();
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    static class ReceiveChange implements Runnable{
        @Override
        public void run() {
            while (true) {
                try (DatagramSocket socket = new DatagramSocket(serverInfo.introducerListPort)) {
                    // 2.创建数据报，用于接收客户端发送的数据
                    byte[] data = new byte[1024];// 创建字节数组，指定接收的数据包的大小
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    // 3.接收客户端发送的数据
                    socket.receive(packet);// 此方法在接收到数据报之前会一直阻塞
                    // 4.读取数据
                    String info = new String(data, 0, packet.getLength());
                    // 5.解析数据
                    String[] words = info.trim().split("\\s+");

                    if (words.length != 4 || !words[0].equals("gossip"))
                    {
                        System.out.println("[introducerError]:消息格式无效！");
                        continue;
                    }

                    if (words[2].equals("failure") || words[2].equals("leave")) {
                        memberList.remove(words[3]);
                    }
                } catch (IOException e) {
                    System.out.println("[introducerError]:监听列表变化出现故障！");
                    e.printStackTrace();
                }
            }
        }

    }

    private static void spreadNewMember(String timeStamp,String ip){
        for (MemberInfo member:memberList.members){
            RunnableBroadCast r=new RunnableBroadCast(timeStamp, ip, member.ip);
            r.start();
        }

    }


    public static void main(String []args){
        Thread listenListThread = new Thread(new ReceiveChange());
        listenListThread.start();
        while(true){
            //introducer执行该程序
            try (/*
            * 接收其他server发送的数据
            */
            // 1.创建服务器端DatagramSocket，指定端口
                DatagramSocket socket = new DatagramSocket(serverInfo.introducerPort)) {
                // 2.创建数据报，用于接收客户端发送的数据
                byte[] data = new byte[1024];// 创建字节数组，指定接收的数据包的大小
                DatagramPacket packet = new DatagramPacket(data, data.length);
                // 3.接收客户端发送的数据
                System.out.println("[introducer]: introducer("+ip+")已经启动，等待对方发送数据");
                socket.receive(packet);// 此方法在接收到数据报之前会一直阻塞
                // 4.读取数据
                String info = new String(data, 0, packet.getLength());
                System.out.println("[introducer]:对方发来消息：" + info);

                /*
                * 向客户端响应数据
                */
                // 1.定义客户端的地址、端口号、数据
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                //将新加入的节点更新至本地列表（关于端口的管理待定，端口可能是不需要维护在全局表中的）
                if(!info.equals("client"))
                    addMember(info);

                byte[] data2 = memberList.members_toString().getBytes();
                // 2.创建数据报，包含响应的数据信息
                DatagramPacket packet2 = new DatagramPacket(data2, data2.length, address, port);
                // 3.响应客户端
                // 4.向全局更新memberlist
                socket.send(packet2);

                // 5.关闭资源
                socket.close();

                } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("[error]:接收错误！");
                e.printStackTrace();
                }
        }
}

}

