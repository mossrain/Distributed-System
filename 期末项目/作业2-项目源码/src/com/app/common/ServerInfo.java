package com.app.common;

import java.util.ArrayList;
/**
 * 存储所有服务器结点（正常或者已离开的都算）
 * 存储一些需要共享的配置信息
 * 
 */
public class ServerInfo {

    public static String []ip={"101.34.3.28","47.100.170.76","1.15.100.12"};//
    // public static String []receive_port={"2717","4399","3635"};
    // public static String []send_port={"",""};
    public final String introducerIp = "47.100.170.76";

    //监听新节点加入与客户端查询
    public final int introducerPort = 2718;

    //监听节点的变化
    public final int introducerListPort = 2720;

    public final int broadcastPort=5500;

    public final int heartbeatingPort=8765;

    public final int gossipPort = 9010;

    public final int queryPort = 20527;

    //连续2次未回应，即认为断开连接
    public final int tolerate = 2;

    //丢包率, 默认为0
    public final double loss = 0;
    //全局状态列表
    //public static ArrayList<Integer> state = new ArrayList<>();

}
