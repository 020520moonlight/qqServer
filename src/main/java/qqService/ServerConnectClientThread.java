package qqService;

import model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * 该类对应的对象和某个客户端保持一个通讯
 */
public class ServerConnectClientThread extends Thread{
    private Socket socket;
    private String userID;//了解到这个服务端的用户ID

    public ServerConnectClientThread(Socket socket,String userID){
        this.userID=userID;
        this.socket=socket;
    }
    @Override
    public void run(){//这里线程处于run状态，可以发送和接收消息
        while (true){
            System.out.println("服务端和客户端保持通讯，读取数据");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                //后面使用message，这个对象制定了发送者
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
