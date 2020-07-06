package com.cdcdata.java.io.chap02;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 为每一个socket创建一个线程，单独处理该socket的相关业务
 */
public class SocketHandler implements Runnable{

    private Socket socket;//绑定某个具体的socket
    private QQServer qqServer;

    public SocketHandler(QQServer qqServer,Socket socket){
        this.socket = socket;
        this.qqServer = qqServer;
    }

    /**
     * socket连接上来之后，要做如下事情
     * 1、添加到在线列表
     * 2、获取reader
     * 3、获取消息
     * 4、转发
     * 5、判断是否退出，退出则移除socket
     */
    @Override
    public void run() {

        try {
            qqServer.addClient(socket);
            //接收信息
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = null;
            while ((message = reader.readLine()) != null){
                System.out.println("客户端"+socket.getPort()+"发送的信息"+message);
                qqServer.forwordMessage(socket,message);
                if(qqServer.toQuit(message)){
                    break;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                qqServer.quitClient(socket);//把退出的socket移除

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
