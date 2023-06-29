import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class  DownloadServerThread implements Runnable{
	Socket sc;				
	String filename;
	public DownloadServerThread(Socket sc1,String name){	//初始化
		sc = sc1;
		filename = name;
	}

	public void run(){
		try{
			
        // InputStream inputStream=sc.getInputStream();
        // byte[] bytes = new byte[1024];
        // //一般来说1024个字节大小存一个歌名肯定够了，但如果需要接收一长串的话可以 s += new String(bytes,0,len)
        // int len = inputStream.read(bytes);
        // System.out.println("len=" + len);
        // String s = new String(bytes,0,len);
        // System.out.println("客户想要下载：" + s);
 
 
        //根据请求内容向客户端返回相应的文件
        OutputStream outputStream = sc.getOutputStream();
        BufferedInputStream bis;
        bis = new BufferedInputStream(new FileInputStream("/home/chen/ds/"+filename));
        byte[] data = new byte[1024];
        int len = 0;
        while( (len = bis.read(data)) != -1 ) {
            outputStream.write(data, 0, len);
        }
        System.out.println("文件"+filename+"传输完毕。");
         bis.close();


        // if (s.equals("lin.jpg")) {
        // } 
        // else {
        //     System.out.println("抱歉，没有资源。");
        // }
        sc.shutdownOutput(); //打上结束标记
 
        //关闭资源
        outputStream.close();
        sc.close();
        // serverSocket.close();
 
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}
}
