import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class UploadServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(20527);		//服务器端口号
	
		while(true){
			System.out.println("server等待连接中...");
			Socket accept = serverSocket.accept();				//在此处阻塞，只有当有文件发送过来时才执行后面的操作

			System.out.println("连接成功！");

			DataInputStream in = new DataInputStream(accept.getInputStream());
			String filename = in.readUTF();	
								//读取客户端发送过来的文件名
            System.out.println("读取文件名："+filename);
			ExecutorService exec = Executors.newCachedThreadPool();	//创建一个执行器对象来为我们管理Thread对象
			
			exec.execute(new Thread(new ServerThread(accept,filename))); //启动任务
		}
    }
}
