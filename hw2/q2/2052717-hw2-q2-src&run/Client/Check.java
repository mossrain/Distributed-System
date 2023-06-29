import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Check {

    static String host1="192.168.56.102";
    static String host2="192.168.56.104";
    static int port=20527;
   

    public void main() throws IOException{
        System.out.println("文件分块存储情况：");
        String FileFolderPath = "src/map/";
        //块文件目录对象
        File FileFolder = new File(FileFolderPath);
        //块文件列表
        File[] files = FileFolder.listFiles();
        //将块文件排序，按名称升序
        List<File> fileList = Arrays.asList(files);

        System.out.println(fileList);

        byte[] b = new byte[1024];

        

        for (File file : fileList) {
            int chunkNum1=0;
            int chunkNum2=0;
            List<String> chunkOrderList=new ArrayList<String>();
            //创建一个读块文件的对象
            HashMap map=new HashMap<String,String>();
            String filename=file.getName();
            filename=filename.substring(4,filename.length()-4);
            System.out.println("对应文件："+filename);
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String s=null;
            while ((s=br.readLine()) != null) {
                String []info=s.split(" ");
                map.put(info[0],info[1]);
                if(info[1].equals(host1)) chunkNum1++;
                else if(info[1].equals(host2)) chunkNum2++;
                chunkOrderList.add(info[0]);
            }
            inputStream.close();
		    br.close();

            System.out.println(host1+"包含的该文件数据块个数："+chunkNum1);
            System.out.println(host2+"包含的该文件数据块个数："+chunkNum2);
            Collections.sort(chunkOrderList, (o1, o2) -> {
                int len1=o1.length();
                int len2=o2.length();
                if (len1!=len2) {
                   if(len1>len2) return 1;
                   else return -1;
                }
                else {
                    if(o1.compareTo(o2)>0) return 1;
                    else return -1;
                }
    
            });
            System.out.println("块文件升序id序列："+chunkOrderList);

            System.out.println("该文件数据块的所在服务器地址：");
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
            
            // 遍历 Map
            String key = (String) it.next();
            String val = (String) map.get(key);
            System.out.println("块文件名：" + key + "，服务器:" + val);
           }
           System.out.println("-----------------------------------------");
        }

    
        System.out.println("服务器连通情况：");
        try{
               Socket socket = new Socket(host1,port);	
               System.out.println(host1+"可以连通！");
               socket.close();
           }catch(Exception e){
            //    e.printStackTrace(System.out);
               System.out.println(host1+"无法连通！");
           }

        try{
            Socket socket = new Socket(host2,port);
            System.out.println(host2+"可以连通！");	
            socket.close();
        }catch(Exception e){
            // e.printStackTrace(System.out);
            System.out.println(host2+"无法连通！");
        }
   }
        
    
}
