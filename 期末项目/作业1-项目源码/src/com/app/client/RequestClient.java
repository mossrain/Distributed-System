package com.app.client;

import com.app.common.ServerInfo;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestClient {

    public static Logger logger=new Logger();
    private int []chunk={0,0,0,0};//目前4块, 要求块序号从0开始
    
    private String requestName;
    private int startYear;
    private int endYear;
    static ServerInfo serverInfo = new ServerInfo();

    // public static long exetime=0;


    public RequestClient(String name, int start, int end) {
        requestName = name;
        startYear = start;
        endYear = end;
    }

    public void printInfo() {
        System.out.println("姓名：" + requestName + "  年份：" + startYear + "-" + endYear);
    }

    // public void showResult(){

    // }

    //向各个输出流里写入String
    private static void writeUTF(ArrayList<DataOutputStream> outputStreams, String info) throws IOException {
        for (int i = 0; i < outputStreams.size(); i++) {
            (outputStreams.get(i)).writeUTF(info);
        }
        return;
    }

    //向各个输出流中写入int
    private static void writeInt(ArrayList<DataOutputStream> outputStreams, int info) throws IOException {
        for (int i = 0; i < outputStreams.size(); i++) {
            (outputStreams.get(i)).writeInt(info);
        }
        return;
    }


    class RunnableSearch implements Runnable{

        private Thread t;
        private String threadName;
        private String type;
        private String ip;
        private int port;
        private CountDownLatch threadsSignal;

        RunnableSearch(String name,String type,String ip,int port,CountDownLatch threadsSignal){
            threadName=name;
            this.type=type;
            this.ip=ip;
            this.port=port;
            this.threadsSignal=threadsSignal;

            System.out.println("客户端创建线程"+name);
            System.out.println("尝试通过端口"+port+"连接服务器"+ip);
        }

        public void start(){
            // System.out.println("线程开始");
            if(t==null){
                t=new Thread(this,threadName);
                t.start();
            }
        }


        public void run(){
            
            System.out.println("线程"+threadName+"开始运行");
            ArrayList<DataOutputStream> dataOutputStreams = new ArrayList<>();
            ArrayList<DataInputStream> dataInputStreams = new ArrayList<>();
            Socket socket;
            
            //与服务器建立连接

                try {
                    socket = new Socket(ip, port);
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    
                    dataOutputStreams.add(dataOutputStream);
                    dataInputStreams.add(dataInputStream);
                    System.out.println("服务器" +ip + "连接成功！");
                    logger.writeInfo("服务器" +ip + "连接成功！");
                } catch (IOException e) {
                    System.out.println("服务器" + ip + "连接错误！");
                    logger.writeInfo("服务器" + ip + "连接错误！");
                    threadsSignal.countDown();
                    return;
                }
            try {
               
                writeUTF(dataOutputStreams,type);
                writeUTF(dataOutputStreams,requestName);
                writeInt(dataOutputStreams,startYear);
                writeInt(dataOutputStreams,endYear);
    
                // 获取服务端的返回信息
                //todo:确定返回信息的格式以及返回信息的处理
                for (int i = 0; i < dataInputStreams.size(); i++) {
                    String result = (dataInputStreams.get(i)).readUTF();
                    System.out.println("服务器发回结果："+result);
                    logger.writeInfo("服务器发回结果："+result);
                    //1-2-3  块-副本号-查询结果

                    int chunkIdx=0;
                    int queryRes=0;
                    //汇总多个服务端结果，得到作者论文总数
                    for (String res: result.split(",")){
                        int t=2;
                        while(t>=1){
                            Pattern p=Pattern.compile("([\\d]*?)-");
                            Matcher m=p.matcher(res);
                           
                            if(m.find()){
                                String st=m.group(1);
                                if(t==2)  chunkIdx=Integer.valueOf(st);
                                else queryRes=Integer.valueOf(st);
                            }
                            chunk[chunkIdx]=queryRes;
                            if(t==2) res=new StringBuffer(res).reverse().toString();
                            t--;
                        }
                        
                    }
                }
    
            } catch (IOException e) {
                System.out.println("socket通信失败！");
                threadsSignal.countDown();
                return;
            }
    
            //输出结果

    
            //关闭socket
            try {
                socket.close();
                System.out.println("线程"+threadName+"socket已关闭");
            } catch (IOException e) {
                System.out.println("关闭socket失败！");
            }

            threadsSignal.countDown();

                
            
            // }
        }
    }


    /**
     * requestClient的主体功能函数，与服务端交互实现查询的具体操作
     *
     * @param type  common:采取默认方式  hash:采取哈希表方式
     * @throws InterruptedException
     */
    public int[] search(String type) throws InterruptedException {
        //与服务器建立连接
        CountDownLatch threadSignal=new CountDownLatch(serverInfo.serverNum);
        long commonStartTime = System.currentTimeMillis();
        long commonUsedTime=0;
        System.out.println("与服务器建立连接...");
        for (int i = 0; i < serverInfo.serverNum; i++) {
            RunnableSearch r=new RunnableSearch("t"+i, type, serverInfo.ipList[i], serverInfo.queryPort,threadSignal);
            r.start();
        }

        
        threadSignal.await();

        long commonEndTime = System.currentTimeMillis();
        commonUsedTime += (commonEndTime - commonStartTime);

       
        System.out.println("使用"+type+"类型查询所用时间：" + commonUsedTime + "ms");
        logger.writeInfo("使用"+type+"类型查询所用时间：" + commonUsedTime + "ms");

        return chunk;
        
    }
}
