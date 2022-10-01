package qqService;

import model.Message;
import model.MessqgeType;
import model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 这是服务端在监听9999端口，等待客户端的连接，保持通信
 */
public class qqServer {
    private ServerSocket serverSocket;
    public qqServer() throws IOException, ClassNotFoundException {
        System.out.println("服务端在监听9999端口");
        //端口可以写在配置文件里卖弄
        serverSocket = new ServerSocket(9999);
        //监听是循环的，当和某个客户端建立连接后，会继续监听
        while (true){
            Socket socket = serverSocket.accept();
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            //得到socket关联的输出流
            ObjectOutputStream obs = new ObjectOutputStream(socket.getOutputStream());
            //读取客户端发送的User对象，在db里面进行验证
            User user = (User) objectInputStream.readObject();
            //构建一个message，准备回复客户端
            Message message = new Message();
            //先死后活
            if (user.getUserId().equals("100")&&user.getPassword().equals("123456")){
                //登陆成功
                message.setMessageType(MessqgeType.MESSAGE_LOGIN_SECCESS);
                obs.writeObject(message);
                //创建一个线程和客户端保持通讯,该线程也需持有socket对象
                ServerConnectClientThread serverConnectClientThread =
                        new ServerConnectClientThread(socket, user.getUserId());
                //启动线程
                serverConnectClientThread.start();
                //把该线程放入到一个集合中进行管理
                MangerClientThread.addConnectClientThread(user.getUserId(), serverConnectClientThread);

            }else {
                //登陆失败
                message.setMessageType(MessqgeType.MESSAGE_LOGIN_FAIL);
                obs.writeObject(message);
                //关闭socket
                socket.close();
            }

            //如果服务端退出了while，说明服务器端不在监听，一次关闭ServerSocket
            serverSocket.close();
        }
    }
}
