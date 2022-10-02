package main.qqService;

import main.model.Message;
import main.model.MessqgeType;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
    @Override
    public void run(){//这里线程处于run状态，可以发送和接收消息
        while (true){
            System.out.println("服务端和客户端"+userID+"保持通讯，读取数据");
            try {
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
                }else{
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
