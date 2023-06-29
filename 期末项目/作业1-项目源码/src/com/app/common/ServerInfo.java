package com.app.common;

public class ServerInfo {
    //存储关于服务器列表的信息
    //每个服务器有一个端口用于与queryClient通信，一个端口与dataNode通信
    public int serverNum = 4;
    // public final int[] queryPortList = new int[]{2717,4399};
    public final int queryPort=2717;
    public final int dataPort = 8081;
    public final String[] ipList = new String[]{"1.15.100.12","47.100.170.76","101.34.3.28","47.97.182.183"};
    public final String[] filePathList = new String[]{
        "/home/ubuntu/files", "/root/usr/chen/files", "/home/ubuntu/files","/root/files"};
}
