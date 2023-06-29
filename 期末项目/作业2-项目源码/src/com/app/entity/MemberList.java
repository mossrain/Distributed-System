package com.app.entity;

import java.util.ArrayList;

/**
 * 组成员列表实体类
 * 
 */
public class MemberList {
    public static ArrayList<MemberInfo> members=new ArrayList<>();

    public class MemberInfo{
        String timestamp;
        public String ip;
        // public int port=9000;

        MemberInfo(String timestamp,String ip){
            this.timestamp=timestamp;
            this.ip=ip;
            // this.port=port;
        }
    }

    //显示组成员列表
    public void show(){
        for (MemberInfo member : members) {
            System.out.println(member.timestamp + " " + member.ip);
        }
    }
    
    //返回字符串形式的组成员列表
    public String members_toString(){
        StringBuffer sb = new StringBuffer();
        for (MemberInfo member:members){
            sb.append(member.timestamp + " " + member.ip+"\n");
        }
        return sb.toString();
    }


    //添加组成员
    public void add(String timestamp,String ip){
        MemberInfo newmember=new MemberInfo(timestamp, ip);
        // System.out.println(newmember);
        for(int i=0;i<members.size();i++){
            if(members.get(i).ip.equals(ip)){
                members.get(i).timestamp=timestamp;//已存在则更新时间戳
                return;
            }
        }
        members.add(newmember);
    }

    //移除组成员
    //即使ip不存在也不会报错，只会什么也不做而已
    public void remove(String ip){
        for(int i=0;i<members.size();i++){
            if(members.get(i).ip.equals(ip)){
                members.remove(i);
                break;
            }
        }
    }

    //通过ip找到下一个结点，也就是要check的服务器
    public String findNextServer(String ip){
        int flag=-1;
        for(int i=0;i<members.size();i++){
            if(members.get(i).ip.equals(ip)){
                // members.remove(i);
                if(i==members.size()-1){
                    flag=0;
                }
                else flag=i+1;
                break;
            }

        }

        return members.get(flag).ip;
    }

    //寻找上一个结点
    public String findLastServer(String ip){
        int flag=-1;
        for(int i=0;i<members.size();i++){
            if(members.get(i).ip.equals(ip)){
                if(i==0){
                    flag=members.size()-1;
                }
                else flag=i-1;
                break;
            }

        }

        return members.get(flag).ip;
    }
    
}
