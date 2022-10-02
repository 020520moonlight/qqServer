package main.model;

public interface MessqgeType {
    //在接口中定义了一些常量，不同的床量的值有不同的消息类型
    String MESSAGE_LOGIN_SECCESS="1";
    String MESSAGE_LOGIN_FAIL="2";
    String MESSAGE_COMM_MES="3";//普通信息包
    String MESSAGE_GET_ONLINEFRIEND="4";//要求返回在线用户
    String MESSAGE_RET_ONLINEFRIEND="5";//返回在线用户
    String MESSAGE_CLIENT_EXIT = "6";//客户端请求退出
    String MESSAGE_CLIENT_TOALL = "7";//群发的消息

    String MESSAGE_FILE_MES = "8";//发送文件


}
