package com.app.datanode;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

import com.app.common.ServerInfo;

/**
 * 实现存储负载均衡
 * 将dblp.xml文件随机分块并存储到每个StorageServer中
 */
public class DataNode {
    static String xmlPath = "E:\\underreality\\大三上\\分布式系统\\dblp.xml";

    public static long getFileLineNum(String filePath) {
        try {
            return Files.lines(Paths.get(filePath)).count();
        } catch (IOException e) {
            return -1;
        }
    }

    //判断一行是否是一段xml的开头
    private static boolean isStart(String line) {
        return line.contains("mdate") && line.contains("key");
    }


    //将一个xml文件均衡分割成num份
    private static void splitXml(int num) {
        //创建io流
        long FileLine = getFileLineNum(xmlPath);
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        //创建分割文件的指标
        //每个小文件的大致大小
        long FileNodeLine = FileLine / num;
        //分割线
        ArrayList<Long> SplitLine = new ArrayList<Long>();
        //起始文件行
        String FileNodeHeader = "";
        for (int i = 1; i < num; i++)
            SplitLine.add(FileNodeLine * i);
        SplitLine.add(FileLine);
        try {
            File file = new File(xmlPath);
            String name = file.getName();
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String line = "";
            //当前文件行序号
            long NodeLine = 1;
            //依次分割填充每个文件
            for (int Fileindex = 1; Fileindex <= num; Fileindex++) {
                int lastIndexOf = name.lastIndexOf(".");
                //源文件的去后缀名称
                String substring = name.substring(0, lastIndexOf);
                //源文件的后缀
                String substring2 = name.substring(lastIndexOf, name.length());
                FileOutputStream fos = new FileOutputStream(substring + "_" + Fileindex + substring2);
                OutputStreamWriter osr = new OutputStreamWriter(fos);
                BufferedWriter bw = new BufferedWriter(osr);
                //将文件头部行写入子文件
                bw.write(FileNodeHeader);
                //读取基本分块
                while (NodeLine <= SplitLine.get(Fileindex - 1)) {
                    line = br.readLine();
                    //读取行
                    //处理尾标签放在下一行头部带来的切割文件的问题

//                    if (line.length() >= 2 && line.charAt(0) == '<' && line.charAt(1) == '/' && line.indexOf('>') != line.lastIndexOf('>')) {
//                        int index = line.indexOf('>');
//                        String line1 = line.substring(0, index + 1);
//                        line1 = line1 + "\r\n";
//                        //不需要更新NodeLine!
//                        String line2 = line.substring(index + 1);
//                        line2 = line2 + "\r\n";
//                        line = line1 + line2;
//                    } else {
//                        line = line + "\r\n";
//                    }
                    line = line + "\r\n";
                    bw.write(line);
                    NodeLine++;
                }
                //一直遇到头部，停止读取，跳到下一个文件
                while (NodeLine <= FileLine) {
                    line = br.readLine();
                    if (line.length() >= 2 && line.charAt(0) == '<' && line.charAt(1) == '/' && line.indexOf('>') != line.lastIndexOf('>')) {
                        int index = line.indexOf('>');
                        String line1 = line.substring(0, index + 1);
                        line1 = line1 + "\r\n";
                        bw.write(line1);
                        String line2 = line.substring(index + 1);
                        line2 = line2 + "\r\n";
                        line = line2;
                    } else {
                        line = line + "\r\n";
                    }
                    if (isStart(line)) {
                        FileNodeHeader = line;
                        NodeLine++;
                        break;
                    }
                    bw.write(line);
                    NodeLine++;
                }
                //存在依赖关系，只需关闭bw流即可
                bw.close();
            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * DataNode脚本：
     * 1、通过spiltXml函数实现分割xml文件
     * 2、随机存储xml文件到服务器，实现负载均衡
     *
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        ServerInfo serverInfo = new ServerInfo();
        //分隔xml文件
        splitXml(4);
       //根据算法分配文件，实现负载均衡
    //    ArrayList<UploadClient>UpLoadClients = new ArrayList<UploadClient>();
    //    //建立client列表
    //    for(int i=0;i< serverInfo.serverNum;i++){
    //        UploadClient client = new UploadClient(serverInfo.ipList[i],serverInfo.dataPort);
    //        UpLoadClients.add(client);
    //    }
    //    //根据算法发送文件
    //    for(int i=0;i< serverInfo.serverNum;i++){
    //        //分块文件名格式:  dblp_i.xml
    //        String filePath = "E:\\underreality\\大三上\\分布式系统\\dblp\\"+i+".xml";

    //        //服务器中块的命名格式: 块号_副本号.xml
    //        //每个文件存2个副本是固定的
    //        UpLoadClients.get(i% serverInfo.serverNum).send(filePath,i+"-0.xml");
    //        UpLoadClients.get((i+1)% serverInfo.serverNum).send(filePath,i+"-1.xml");
        //    UpLoadClients.get((i+2)% serverInfo.serverNum).send(filePath,i+"_2.xml");
    //    }
    }


}
