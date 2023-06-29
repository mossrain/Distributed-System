import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class UploadClient {

    static String filename="";//文件名
    static long chunkFileNum=0;
    static String chunkname="";
    static String filewithoutPath="";

    public UploadClient(String name){
        filename="src/"+name;
        filewithoutPath=name;
    }

    public static HashMap<String,Integer> table = new HashMap<>();

    //测试文件分块
    public static void Chunk() throws IOException {
        //源文件
        
        File sourceFile = new File(filename);
        //块文件目录
        String chunkFileFolder = "src/chunks/";

        //先定义块文件大小
        long chunkFileSize = 1 * 1024 * 1024;

        //块数
        chunkFileNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkFileSize);
        System.out.println(chunkFileNum);

        Random ran = new Random();

        //创建读文件的对象
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");

        chunkname=sourceFile.getName();
        // .replace(".", "");

        System.out.println(filename);
        System.out.println(chunkname);
        //缓冲区
        byte[] b = new byte[1024];
       
        for (int i = 0; i < chunkFileNum; i++) {
            //块文件
            File chunkFile = new File(chunkFileFolder +chunkname+ i);

            System.out.println(chunkFile);
            //创建向块文件的写对象
            RandomAccessFile raf_write = new RandomAccessFile(chunkFile, "rw");

            //给该块分配服务器
            table.put(chunkFile.getName(),ran.nextInt(2));

            int len = -1;

            while ((len = raf_read.read(b)) != -1) {

                raf_write.write(b, 0, len);
                //如果块文件的大小达到 1M开始写下一块儿
                if (chunkFile.length() >= chunkFileSize) {
                    break;
                }
            }
            raf_write.close();


        }
        raf_read.close();
    }


    public static void uploadClient() throws IOException{
         String host1="192.168.56.102";
         String host2 = "192.168.56.104";
         String host ="";
         int port = 20527;
         File progFile=new File("src/uploadProg/prog_"+filewithoutPath+".txt");
         BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(progFile,true)));

         for (int i=0;i<chunkFileNum;i++)
         {
            if(table.get(chunkname+i)==1) host=host1;
            else host=host2;
            try{
                Socket socket = new Socket(host,port);				
                OutputStream outputStream = socket.getOutputStream();		//创建输出流对象
                DataOutputStream out = new DataOutputStream(outputStream);	// 用于输出文件名
                File file = new File("src/chunks/"+chunkname+i);
                FileInputStream inputStream = new FileInputStream(file);
            
                out.writeUTF(chunkname+i);				//将文件名发送给服务器
        
                int len = -1;
                byte[] temp = new byte[1024];
                while((len = inputStream.read(temp))!=-1){
                outputStream.write(temp,0,len);
                }
                
                out.close();	//发送完毕后关闭输出流。
                outputStream.close();	
                System.out.println(chunkname+i+"上传完成");
                
                bw.write(chunkname+i+"\n");
                bw.flush();
            }catch(Exception e){
                e.printStackTrace(System.out);
                return;//不停止的话，就会造成乱序上传，导致日志顺序紊乱
            }
    }
         bw.close();

    }


    public void main() throws IOException{
        // Console cons=System.console();
        // String filewithoutPath=cons.readLine("请输入要上传文件的名称: ");
        // filename = "src/"+filewithoutPath;
        Chunk();
        File mapTable=new File("src/map/map_"+filewithoutPath+".txt");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapTable,true)));
     
        Iterator it = table.keySet().iterator();
        while (it.hasNext()) {
            
            // 遍历 Map
            String key = (String) it.next();
            int val = table.get(key);
            String line="";
            if(val==1) line="192.168.56.102";
            else line="192.168.56.104";
            
    
            bw.write(key+" "+line+"\n");
            
            System.out.println("块文件名：" + key + "，服务器:" + line);
        }
        bw.flush();
        bw.close();
        uploadClient();
    }
}
