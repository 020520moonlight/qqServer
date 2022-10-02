package main.qqService;

import main.model.Message;
import main.model.MessqgeType;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * 该类对应的对象和某个客户端保持一个通讯
 */
public class ServerConnectClientThread extends Thread{
    private Socket socket;
    private String userID;//了解到这个服务端的用户ID，以后后面的socket太多，增加辨识度

    public ServerConnectClientThread(Socket socket,String userID){
        this.userID=userID;
        this.socket=socket;
    }

    public Socket getSocket(){
        return socket;
    }
    @Override
    public void run(){//这里线程处于run状态，可以发送和接收消息
        while (true){
            System.out.println("服务端和客户端"+userID+"保持通讯，读取数据");
            try {//这里线程出错
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                //后面使用message
                //根据message的类型做相应的业务处理
                if (message.getMessageType().equals(MessqgeType.MESSAGE_GET_ONLINEFRIEND)){
                    //客户端要在线用户
                    System.out.println(message.getSender()+"要在线用户列表");
                    //在服务端管理线程集合知道，因为它管理的每个线程里面的socket对象有uid信息
                    String onlineUser = MangerClientThread.getOnlineUser();
                    //拿到内容准备返回,构建一个message对象返回给客户端
                    Message message1 = new Message();
                    message1.setMessageType(MessqgeType.MESSAGE_RET_ONLINEFRIEND);
                    message1.setContent(onlineUser);
                    message1.setReciever(message.getSender());
                    //返回给客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message1);
                }else if (message.getMessageType().equals(MessqgeType.MESSAGE_CLIENT_EXIT)){
                    //客户端要退出来
                    System.out.println(message.getSender()+"要退出系统了");
                    //将客户端对应线程从线程集合谜面移除
                    MangerClientThread.removeConnectClientThread(message.getSender());
                    //注意这个线程对象不止一个，集合里面线程很多，关闭连接
                    socket.close();
                    //退出线程
                    break;
                }else if (message.getMessageType().equals(MessqgeType.MESSAGE_COMM_MES)){
                    //拿到普通聊天消息
                    //根据message过去getter id 然后再得到对应的线程
                    ServerConnectClientThread serverConnectClientThread = MangerClientThread.get(message.getReciever());
                    //得到对应的socket对象，将message对象转发给指定的客户端
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);
                    //如果客户不在线，可以保存到数据库，实现离线留言
                }else if (message.getMessageType().equals(MessqgeType.MESSAGE_CLIENT_TOALL)){
                    //群发信息，需要遍历管理线程集合排除自己的所有线程的socket对象都得到，然后把message进行转发
                    HashMap<String,ServerConnectClientThread> hashMap = MangerClientThread.getHashMap();
                    for (String key : hashMap.keySet()){
                        //key就是在线的用户ID
                        //排除群发消息的发送用户
                        ServerConnectClientThread serverConnectClientThread = hashMap.get(key);
                        if (!key.equals(message.getSender())){
                            ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                            oos.writeObject(message);
                        }
                    }
                } else if (message.getMessageType().equals(MessqgeType.MESSAGE_FILE_MES)) {
                    //根据接收者id，获得对应线程和socket对象，将message对象转发
                    ServerConnectClientThread serverConnectClientThread = MangerClientThread.get(message.getReciever());
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);
                } else{
                    //其他类型的业务处理

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
    }


}
