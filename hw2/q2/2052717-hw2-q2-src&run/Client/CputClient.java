import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class CputClient {

    static String filename="";//文件名
    static long chunkFileNum=0;
    // static String chunkname="";
    static String filewithoutPath="";

    public CputClient(String name){
        filename="src/"+name;
        filewithoutPath=name;
    }

    public static HashMap<String,String> table = new HashMap<>();
    public static int getFileLineNum(String filePath) {
        try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filePath))){
            lineNumberReader.skip(Long.MAX_VALUE);
            int lineNumber = lineNumberReader.getLineNumber();
            return lineNumber ;//实际上是读取换行符数量，写文本文件的时候是每行输出一个\n
        } catch (IOException e) {
            return -1;
        }
    }
    

    public static void uploadClient() throws IOException{
         String host1="192.168.56.102";
         String host2 = "192.168.56.104";
         String host ="";
         int port = 20527;

         String mapfilename = "src/map/map_"+filewithoutPath+".txt";
         int chunkFileNum=getFileLineNum(mapfilename);

         String progfilename = "src/uploadProg/prog_"+filewithoutPath+".txt";
         int uploadfileNum=getFileLineNum(progfilename);

       
         File progFile=new File("src/uploadProg/prog_"+filewithoutPath+".txt");
         BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(progFile,true)));

         for (int i=uploadfileNum;i<chunkFileNum;i++)//只upload以前没有传的部分
         {
            // if(table.get(chunkname+i)==1) host=host1;
            // else host=host2;
            host = table.get(filewithoutPath+i);
            
            System.out.println(filewithoutPath+i);
            System.out.println(host);
            try{
                Socket socket = new Socket(host,port);				
                OutputStream outputStream = socket.getOutputStream();		//创建输出流对象
                DataOutputStream out = new DataOutputStream(outputStream);	// 用于输出文件名
                File file = new File("src/chunks/"+filewithoutPath+i);
                FileInputStream inputStream = new FileInputStream(file);
            
                out.writeUTF(filewithoutPath+i);				//将文件名发送给服务器
        
                int len = -1;
                byte[] temp = new byte[1024];
                while((len = inputStream.read(temp))!=-1){
                outputStream.write(temp,0,len);
                }
                
                out.close();	//发送完毕后关闭输出流。
                outputStream.close();	
                System.out.println(filewithoutPath+i+"上传完成");
                
                bw.write(filewithoutPath+i+"\n");
                bw.flush();
            }catch(Exception e){
                e.printStackTrace(System.out);
                return;
            }
    }
    bw.close();

    }


    public void main() throws IOException{
        // Console cons=System.console();
        // String filewithoutPath=cons.readLine("请输入要上传文件的名称: ");
        // filename = "src/"+filewithoutPath;
        String mapfilename = "src/map/map_"+filewithoutPath+".txt";


        FileInputStream insMap = new FileInputStream(mapfilename);

        BufferedReader br = new BufferedReader(new InputStreamReader(insMap));

        String s=null;
        while ((s=br.readLine()) != null) {
            String []info=s.split(" ");
            table.put(info[0],info[1]);
        }
        insMap.close();
        br.close();

        // Iterator it = table.keySet().iterator();
        // while (it.hasNext()) {
            
        //     // 遍历 Map
        //     String key = (String) it.next();
        //     String val = table.get(key);
            
        //     System.out.println("块文件名：" + key + "，服务器:" + val);
        // }
        uploadClient();
    }
}
