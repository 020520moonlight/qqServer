package main.qqService;

import main.model.Message;
import main.model.MessqgeType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class SendNewsToAll implements Runnable{
    private Scanner scanner = new Scanner(System.in);
    @Override
    public void run() {
        while (true){//为了可以多次推送
            System.out.println("请输入想要推送的消息/消息【输入exit表示退出推送服务】");
            String news = scanner.next();
            if (news.equals("exit")){
                break;
            }
            //构建一个消息，消息类型为群发消息
            Message message = new Message();
            message.setMessageType(MessqgeType.MESSAGE_CLIENT_TOALL);
            message.setSender("服务器");
            message.setContent(news);
            message.setSendTime(new Date().toString());
            System.out.println("服务器推送消息给所有人 说:"+news);
            //拿到所有在线的用户线程，得到socket并发送
            HashMap<String,ServerConnectClientThread> hashMap = MangerClientThread.getHashMap();
            for (String key : hashMap.keySet() ){
                ServerConnectClientThread serverConnectClientThread = hashMap.get(key);

                try {
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }
}
