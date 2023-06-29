import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
/**
 * TCP实现文件下载
 * 服务端程序，要求接收客户端发来的歌名，返回对应的歌曲mp3文件
 * 同样适用于其他二进制文件如视频、图片等
 */
public class DownloadServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(20527);

        while(true){
            System.out.println("服务器已启动，等待连接...");
            Socket socket = serverSocket.accept();
            System.out.println("连接成功！");

            DataInputStream in = new DataInputStream(socket.getInputStream());
			String filename = in.readUTF();	//读取客户端发送过来的文件名
            System.out.println("下载"+filename);
            ExecutorService exec=Executors.newCachedThreadPool();
            exec.execute(new Thread(new DownloadServerThread(socket, filename)));
        }
    }
}