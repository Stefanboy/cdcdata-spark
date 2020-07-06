package com.cdcdata.java.io.chap02;

import com.cdcdata.java.io.chap01.Constansts;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QQServer {

    final int PORT = 8888;
    ServerSocket serverSocket = null;
    //框 维护在线用户的
    private Map<Integer, Writer> onlineClients;

    //定义线程池
    private ExecutorService executorService;

    public QQServer(){
        onlineClients = new HashMap<>();
        //固定的线程池
        executorService = Executors.newFixedThreadPool(10);
    }
    /**
     * 新的socket上线
     * 添加用户
     * @param socket
     */
    public synchronized void addClient(Socket socket) throws Exception {

        if (null != socket) {
            int port = socket.getPort();//每个客户端port是唯一的 所以能代表一个用户
            //writer 表示发出去的writer
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            onlineClients.put(port,writer);
            System.out.println("客户端"+socket.getPort()+"已连接到服务器");
        }
    }
    //退出
    public synchronized void quitClient(Socket socket) throws Exception {
        if (null != socket) {
            int port = socket.getPort();
            //退出时 只是移除map的key还不够  还得关掉writer
            if (onlineClients.containsKey(port)) {
                Writer writer = onlineClients.get(port);
                writer.close();
            }
            onlineClients.remove(port);

        }
    }
    //转发消息给其他用户(转发消息就不需要转发给自己了)
    public void forwordMessage(Socket socket,String message) throws Exception {
        for(Integer port:onlineClients.keySet()){//当前在线用户列表
            if (!port.equals(socket.getPort())) {//排除自己
                if (!message.equalsIgnoreCase("quit")) {
                    Writer writer = onlineClients.get(port);
                    writer.write("客户端"+socket.getPort()+"发送的信息"+message+"\n");
                    writer.flush();
                }
            }
        }
    }
    //检查客户端发送过来的消息是否是退出指令
    public boolean toQuit(String message){
        return Constansts.QUIT.equalsIgnoreCase(message);
    }
    //启动服务
    public void start(){
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("启动服务器,监听的端口是:"+PORT);
            while (true){
                Socket socket = serverSocket.accept();
                //为每个socket创建一个线程,专门用于处理该socket的通信
                //new Thread(new SocketHandler(this,socket)).start();
                executorService.execute(new SocketHandler(this,socket));



            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

    }

    public void close(){
        if (null != serverSocket) {
            try {
                serverSocket.close();
                System.out.println("服务器关闭...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {

        new QQServer().start();
    }
}
