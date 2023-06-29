import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.swing.*;
import javax.xml.ws.Response;

public class Jump {

    static String startSite="https://www.mit.edu";

    public static int statNum=0;

    public static int edgeNum=0;

    public static HashMap<String ,Integer> map=new HashMap<>();//记录终点及其入度
    
    public static void getMaxInDegree(){
        Set<String> set=map.keySet();
        map.entrySet();
        List<HashMap.Entry<String,Integer>> list = new ArrayList(map.entrySet());
        Collections.sort(list, (o1, o2) -> (o2.getValue() - o1.getValue()));//升序
        int maxv=list.get(0).getValue();
        System.out.println(list.get(0).getKey()+"具有最大入度"+maxv);

        for(int i=1;i<list.size();i++){
            if(list.get(i).getValue()==maxv){
                System.out.println(list.get(i).getKey()+"具有最大入度"+maxv);
            }
            else break;
        }
    }

    //获取一级域名
    public static String getTopDomain(String url,Pattern pattern) {
        String result = url;
        try {
            Matcher matcher = pattern.matcher(url);
            if( matcher.find())
                result = matcher.group();
            else
                System.out.println("匹配不到"+url+" 的一级域名");
        } catch (Exception e) {
            System.out.println("无法获取"+url+" 的一级域名");
            // e.printStackTrace();
        }
//        System.out.println(result);
        return result;
    }

    //一级域名正则表达式
    static final String RE_TOP = "[\\w-]+\\.(com.cn|net.cn|gov.cn|org\\.nz|org.cn|com|net|org|gov|us|cc|biz|info|cn|co|edu)\\b()*";

   //三级域名正则表达式
    static final String RE_TOP_3 ="(\\w*\\.?){3}\\.(com.cn|net.cn|gov.cn|org\\.nz|org.cn|com|net|org|gov|us|cc|biz|info|cn|co)$";

    //建立http/https连接
    public static String buildConnect(String strServer) throws IOException {
        String filename =  statNum+".html";
        File file = new File(filename);
        // System.out.println("73 writer:"+filename+" "+strServer);
        if (!file.exists()) file.createNewFile();//不存在则创建一个
        FileWriter writer = new FileWriter(file);
        String str="";
        try {
            URL url = new URL(strServer);
            InputStream in =url.openStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader bufr = new BufferedReader(isr);
           
            while ((str = bufr.readLine()) != null) {
//                System.out.println(str);
                writer.write(str);
            }
            writer.flush();
            writer.close();
            bufr.close();
            isr.close();
            in.close();
        } catch (Exception e) {
            System.out.println("无法连接到"+strServer);
            // e.printStackTrace();
        }
        return filename;
    }

    //获取html文本中的超链接
    public static List<String> getList(final String s) {

        Pattern p=Pattern.compile("<a href=(.*?)</a>");
        Matcher ma=p.matcher(s);
        final List<String> list =new ArrayList<String>();
        while(ma.find()){
            list.add(ma.group());
        }

        Pattern p1=Pattern.compile("\"http(.*?)\"");//获取
        Matcher ma1= p1.matcher(list.toString());
        final List<String> list1 = new ArrayList<String>();
        while (ma1.find()){
            list1.add(ma1.group());
        }

        final List<String> list2 = new ArrayList<String>();
        Pattern p2=Pattern.compile("\"(.*?)\"");
        Matcher ma2= p2.matcher(list1.toString());
        while (ma2.find()){
            list2.add(ma2.group(1));
        }

        return list2;
    }

    //解析出html中的所有超链接，通过检查一级域名是否匹配筛选出外部url
    public static List<String> parser(String strServer,String filename) throws IOException {
//        Pattern pm= Pattern.compile(RE_TOP_3 , Pattern.CASE_INSENSITIVE);
//        String filename=getTopDomain(strServer,pm);
        // System.out.println("126 read: "+filename+" "+strServer);
        File file = new File(filename);//定义一个file对象，用来初始化FileReader
        FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
        BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
        StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
        String s = "";
        while ((s =bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
            sb.append(s + "\n");//将读取的字符串添加换行符后累加存放在缓存中
        }
        bReader.close();
        String str = sb.toString();
        List<String> list=getList(str);

        List<String> retList=new ArrayList<String>();

//记录该url的历史访问，确保一个url不会对一个外部链接重复访问两次
        List<String> histVis = new ArrayList<String>();

        for (String link:list){

            Pattern pattern= Pattern.compile(RE_TOP , Pattern.CASE_INSENSITIVE);
            String originDomain=getTopDomain(strServer,pattern);
            String linkDomain=getTopDomain(link,pattern);

            if (!Objects.equals(originDomain, linkDomain)&& !histVis.contains(link))  {
         //是外部域名且和初始网址不相同，可以访问
                histVis.add(link);
                retList.add(link);
                // System.out.println("add link "+link);
            }
        }

        return retList;
    }

    //递归访问所有的外部url
    public static void recursive(List<String> urls,int jumpNum,String strServer) throws IOException {

        int count=0;

        if(jumpNum>4) return;//最多四跳

        for (String str:urls){

            if(count>=6) return;//最多6个外部url

            else count++;

            System.out.println(++statNum+": "+strServer+"-->"+str);//计数君

            edgeNum++;

            if(!map.containsKey(str)) map.put(str,1);
            else map.put(str,map.get(str)+1);

            String filename=buildConnect(str);

            List<String> retList=parser(str,filename);//retList包含所有外部url

            recursive(retList,jumpNum+1,str);

        }
    }

    public static void main(String []args) throws IOException {
        long startTime =  System.currentTimeMillis();
        //初始网页
        String strServer=startSite;

        // histVis.add(strServer);

        String filename=buildConnect(strServer);

        List<String> retList=parser(strServer,filename);//retList包含所有外部url

        recursive(retList,1,strServer);
        long endTime=System.currentTimeMillis();
        double usedTime=1.0*(endTime-startTime)/1000.0;

        System.out.println("边数："+edgeNum+"");
        System.out.println("结点数："+map.size()+"");
        System.out.println("总用时："+usedTime+"s");
        getMaxInDegree();

    }
}

