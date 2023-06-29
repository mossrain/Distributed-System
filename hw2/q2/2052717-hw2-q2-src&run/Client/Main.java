import java.io.IOException;
import java.util.Scanner;

public class Main{
    public static void main(String args[]) throws IOException{
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入指令：");
        String cmd=sc.next().toString();
       

        if(cmd.equals("put")){
            String filename=sc.next().toString();

            long startTime =  System.currentTimeMillis();
            UploadClient upload=new UploadClient(filename);
            upload.main();
            long endTime=System.currentTimeMillis();
            long usedTime=(endTime-startTime);
            System.out.println("本次上传所用时间："+usedTime+"ms");
        }
        else if(cmd.equals("get")){
            String filename=sc.next().toString();
            
            long startTime =  System.currentTimeMillis();
            DownloadClient download=new DownloadClient(filename);
            download.main();
            long endTime=System.currentTimeMillis();
            long usedTime=(endTime-startTime);
            System.out.println("本次下载所用时间："+usedTime+"ms");
        }
        else if(cmd.equals("check")){
            Check check=new Check();
            check.main();
        }
        else if(cmd.equals("cput")){
            String filename=sc.next().toString();

            long startTime =  System.currentTimeMillis();
            CputClient cput=new CputClient(filename);
            cput.main();
            long endTime=System.currentTimeMillis();
            long usedTime=(endTime-startTime);
            System.out.println("本次续传所用时间："+usedTime+"ms");
        }
        else if(cmd.equals("cget")){
            String filename=sc.next().toString();
            
            long startTime =  System.currentTimeMillis();
            CgetClient cget=new CgetClient(filename);
            cget.main();
            long endTime=System.currentTimeMillis();
            long usedTime=(endTime-startTime);
            System.out.println("本次续载所用时间："+usedTime+"ms");
        }

    }


}