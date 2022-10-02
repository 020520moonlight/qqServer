package main.qqService;


import main.model.Message;
import main.model.MessqgeType;
import main.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 这是服务端在监听9999端口，等待客户端的连接，保持通信
 */
public class qqServer {
    private ServerSocket serverSocket = null;

    private static ConcurrentHashMap<String,User> users = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ArrayList<Message>> offOnlineMessages = new ConcurrentHashMap<>();

    static {
        users.put("100",new User("100","123456"));
        users.put("123",new User("123","123"));
        users.put("123",new User("abc","123"));
        users.put("admin",new User("admin","admin"));
        users.put("100",new User("100","123"));
        users.put("1",new User("1","1"));
    }
    private boolean check(String userID,String pwd){
        User user= users.get(userID);
        if (user == null){
            System.out.println("用户名不存在");
            return false;
        }
        if(!user.getPassword().equals(pwd)){
            System.out.println("密码不存在");
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        new qqServer();
    }
    public qqServer() throws IOException {
        try {
            System.out.println("服务端在监听9999端口");
            //端口可以写在配置文件里卖弄
            serverSocket = new ServerSocket(9999);
            //启动一个推送线程的服务
            new Thread(new SendNewsToAll()).start();
            //监听是循环的，当和某个客户端建立连接后，会继续监听
            while (true){
                Socket socket = serverSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //得到socket关联的输出流
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                //读取客户端发送的User对象，在db里面进行验证
                User user = (User) ois.readObject();
                //构建一个message，准备回复客户端
                Message message = new Message();
                //先死后活
                if (check(user.getUserId(), user.getPassword())){
                    //登陆成功
                    message.setMessageType(MessqgeType.MESSAGE_LOGIN_SECCESS);
                    oos.writeObject(message);
                    //创建一个线程和客户端保持通讯,该线程也需持有socket对象
                    ServerConnectClientThread serverConnectClientThread =
                            new ServerConnectClientThread(socket, user.getUserId());
                    //启动线程
                    serverConnectClientThread.start();
                    //把该线程放入到一个集合中进行管理
                    MangerClientThread.addConnectClientThread(user.getUserId(), serverConnectClientThread);

                    //遍历离线消息队列，如果登录账号有离线信息则输出到socket出来
                    for (String offLineUser : offOnlineMessages.keySet()){
                        if(user.getUserId().equals(offLineUser)){
                            System.out.println("输出离线消息");
                            ArrayList<Message> messageArrayList = offOnlineMessages.get(offLineUser);
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(MangerClientThread.get(user.getUserId()).getSocket().getOutputStream());
                            for (Message m : messageArrayList){
                                objectOutputStream.writeObject(m);
                            }
                        }
                    }
                }
                else {
                    //登陆失败
                    System.out.println("登陆失败"+user.getUserId());
                    message.setMessageType(MessqgeType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    //关闭socket
                    socket.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally{
            //如果服务端退出了while，说明服务器端不在监听，关闭ServerSocket
            serverSocket.close();
        }


    }
}
