import java.io.*;
import java.lang.reflect.Executable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.stream.FileCacheImageOutputStream;
 
public class CgetClient {
    static String filewithoutPath="";

    public CgetClient(String name){
          filewithoutPath=name;
    }
    public static void mergeFile() throws IOException {
        //块文件目录
        String chunkFileFolderPath = "src/downloads/";
        //块文件目录对象
        File chunkFileFolder = new File(chunkFileFolderPath);
        //块文件列表
        File[] files = chunkFileFolder.listFiles();
        //将块文件排序，按名称升序
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, (o1, o2) -> {
            int len1=o1.getName().length();
            int len2=o2.getName().length();
            if (len1!=len2) {
               if(len1>len2) return 1;
               else return -1;
            }
            else {
                if(o1.getName().compareTo(o2.getName())>0) return 1;
                else return -1;
            }

        });
        // System.out.println(fileList);

        //合并文件
        // System.out.println(chunkFileFolderPath);
        File mergeFile = new File("src/download_"+filewithoutPath);
        //创建新文件
        boolean newFile = mergeFile.createNewFile();

        //创建写对象
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");

        byte[] b = new byte[1024];
        for (File chunkFile : fileList) {
            //创建一个读块文件的对象
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r");
            int len = -1;
            while ((len = raf_read.read(b)) != -1) {
                raf_write.write(b, 0, len);
            }
            raf_read.close();
        }
        raf_write.close();
    }

    public static int getFileLineNum(String filePath) {
        try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filePath))){
            lineNumberReader.skip(Long.MAX_VALUE);
            int lineNumber = lineNumberReader.getLineNumber();
            return lineNumber ;//实际上是读取换行符数量，写文本文件的时候是每行输出一个\n
        } catch (IOException e) {
            return -1;
        }
    }
    public void main() throws IOException {

        // String host1 ="";
        // String host2="192.168.56.104";
        String host="";

        // Scanner sc = new Scanner(System.in);
        // System.out.println("请输入您想下载的文件名称：");//lin.jpg

        // filewithoutPath=sc.next();
        String filename = "src/map/map_"+filewithoutPath+".txt";

        int lineNum=getFileLineNum(filename);
		System.out.println("块数"+lineNum);

        String progfilename = "src/downloadProg/prog_"+filewithoutPath+".txt";
        int downloadfileNum=getFileLineNum(progfilename);

		String line;
		InputStream fis = new FileInputStream(filename);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);

        File progFile=new File("src/downloadProg/prog_"+filewithoutPath+".txt");
        BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(progFile,true)));

        int runCnt=0;
		try {
		
		while ((line = br.readLine()) != null) {
            
            if(++runCnt<=downloadfileNum) continue;
		
		// Do your thing with line
		    // System.out.println(line);
			String[] words = line.split(" ");
			System.out.println("从服务器"+words[1]+"下载");

            filename=words[0];//切换成块文件名称
            host=words[1];
            
            
        Socket socket = new Socket(InetAddress.getByName(host),20527);
        System.out.println("成功连接到服务器: "+host);

        //给服务器发送文件名
        DataOutputStream outputStream = new DataOutputStream (socket.getOutputStream());
        outputStream.writeUTF(filename);

        System.out.println("请求文件: " + filename);
 
        // ExecutorService exec=Executors.newCachedThreadPool();
        // exec.execute(new Thread(new ClientThread(socket,filename)));

        //接收服务器的回应
        InputStream inputStream = socket.getInputStream();
        //开辟本地文件的输出流，以存放从客户端发来的文件
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("src/downloads/" + filename ));
        byte[] data = new byte[1024];
        int len = 0;
        while ( (len = inputStream.read(data)) != -1 ) {
            bos.write(data, 0, len);
        }
        System.out.println("文件已成功接收！");
 
        inputStream.close();
        outputStream.close();
        bos.close();
        socket.close();
        bw.write(filename+"\n");
        bw.flush();

		}
		

		}catch(Exception e){
            e.printStackTrace();
            bw.close();
            return;
        }

        bw.close();
        mergeFile();
    }
}