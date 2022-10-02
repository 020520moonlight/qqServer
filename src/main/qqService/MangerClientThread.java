package main.qqService;

import java.util.HashMap;

/**
 * 该类用于管理和客户端进行通讯的类
 */
public class MangerClientThread {
    private static HashMap<String,ServerConnectClientThread> hashMap = new HashMap<>();

    public static void addConnectClientThread(String userID,ServerConnectClientThread serverConnectClientThread){
        hashMap.put(userID,serverConnectClientThread);
    }
    public static ServerConnectClientThread get(String userID){
        return hashMap.get(userID);
    }

    //返回在线用户列表
    public static String getOnlineUser(){
        StringBuilder onlineUser = null;
        //集合遍历，遍历 hashmap的key
        for (String key:hashMap.keySet()){
            onlineUser.append(key).append(" ");
        }
        return onlineUser.toString();
    }

}
