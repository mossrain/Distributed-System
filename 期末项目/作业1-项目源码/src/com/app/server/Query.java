package com.app.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.app.common.ServerInfo;

public class Query{
    static ArrayList<String> query_result=new ArrayList<>();
    static String dirpath;

    static String name;
    static int start,end;

    Query(String name,int start,int end,String dirpath) throws Exception{
        Query.name=name;
        Query.start=start;
        Query.end=end;
        Query.dirpath=dirpath;
        query_result.clear();
        // search();
    }

    public ArrayList<File> getFiles(String path) throws Exception {
        //目标集合fileList
        ArrayList<File> fileList = new ArrayList<File>();
        File file = new File(path);
        if(file.isDirectory()){
            File []files = file.listFiles();
            for(File fileIndex:files){
                    //如果这个文件是目录，则进行递归搜索
                if(fileIndex.isDirectory()){
                        getFiles(fileIndex.getPath());
                }else{ 
                    int flag=fileIndex.getName().lastIndexOf('.');
                    String fileType="";
                    if(flag>0){
                        fileType=fileIndex.getName().substring(flag+1);
                    } 
                    if(fileType.equals("json")){
                    //如果文件是json文件，则将文件句柄放入集合中
                        fileList.add(fileIndex);
                    }
                }
            }
        }
        return fileList;
    }


    class RunnableQuery implements Runnable {
        private Thread t;
        private String threadName;
        File file;
        private CountDownLatch threadsSignal;

        RunnableQuery(String name,File file,CountDownLatch threadsSignal){
            threadName=name;
            this.file=file;
            this.threadsSignal=threadsSignal;
            System.out.println("服务器创建线程"+name);
            System.out.println("尝试访问文件"+file.getName());
        }

        public void start(){
            System.out.println("线程"+threadName+"开始");
            if(t==null){
                t=new Thread (this,threadName);
                t.start();
            }
        }

        public void run(){
            String filename=file.getName();
            filename=filename.substring(0, filename.length()-5);
            int cnt=query(name,file.getPath(),start,end);

            query_result.add(filename+"-"+cnt);

            threadsSignal.countDown();
            System.out.println("线程"+threadName+"关闭");
        }

    }

    public List<String> search() throws Exception{
        ArrayList<File> fileList = getFiles(dirpath);
        CountDownLatch threadSignal=new CountDownLatch(fileList.size());
        for (int i=0;i<fileList.size();i++){
            RunnableQuery r=new RunnableQuery("t"+i, fileList.get(i),threadSignal);
            r.start();
        }

        threadSignal.await();
        query_result.trimToSize();
        return query_result;
    }

    static int query(String author,String filepath,int start,int end){
        int cnt=0;
        // Path path=Paths.get(filepath);
        boolean noYearLimit=false;
        if(start==-1&&end==-1) noYearLimit=true;
       
        boolean record=false;
        int year=0,num=0;
        if(end<0) end=9999;
        System.out.println(filepath);
        
        try (Scanner scanner = new Scanner(new File(filepath))) {

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("\""+author+"\":")) {
                System.out.println("找到作者"+author);
                record=true;
            }
            else if(line.contains("}")){
                if(record) return cnt;
            }
            else if(record){
                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher = pattern.matcher(line);
                int i=0;
                while (matcher.find()) {
                    if(i==0) year= Integer.valueOf(matcher.group());
                    else num=Integer.valueOf(matcher.group());
                    i++;
                }

                if(noYearLimit) cnt+=num;
                else if(start<=year&&year<=end) cnt+=num;

            }
        }
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在.");
        }

        return cnt;
    }

}