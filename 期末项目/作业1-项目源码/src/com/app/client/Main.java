package com.app.client;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String args[]) throws IOException, InterruptedException {
        System.out.println("进入DBLP数据查询系统...（输入q退出）");
        Scanner scan = new Scanner(System.in);
        String name = "";
        int start = -1;
        int end = -1;
       
        while (true) { //暂时不实现轮询功能
            System.out.println("");
            System.out.println("请输入要查询的作者姓名...)");
            if (scan.hasNextLine()) {
                name = scan.nextLine();
                if (name.equals("q"))
                    return;
           }
           
            System.out.println("请输入要查询的论文范围的年份下限(包括输入的年份，若无最小年份限制则输入-1)...");
            if (scan.hasNextLine())
                try {
                    String op = scan.nextLine();
                    if (op.equals("q"))
                        return;
                    start = Integer.parseInt(op);
                } catch (NumberFormatException e) {
                    System.out.println("请输入合理的年份！");
                    // continue;
                }
            System.out.println("请输入要查询的论文范围的年份上限(包括输入的年份，若无最大年份限制则输入-1)...");
            if (scan.hasNextLine())
                try {
                    String op = scan.nextLine();
                    if (op.equals("q"))
                        return;
                    end = Integer.parseInt(op);
                } catch (NumberFormatException e) {
                    System.out.println("请输入合理的年份！");
                    // continue;
                }

 
            RequestClient requestClient = new RequestClient(name, start, end);
            int []ans=requestClient.search("hash");
            int sum=0;
            for(int i=0;i<3;i++){//0 1 2
                //
                System.out.println(ans[i]);
                sum+=ans[i];
            }
             System.out.println(name+"在"+start+"-"+end+"年发表论文在DBLP有记录的有"+sum+"篇");


        }

    }

}