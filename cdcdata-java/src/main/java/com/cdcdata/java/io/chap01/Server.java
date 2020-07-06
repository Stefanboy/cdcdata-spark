package com.cdcdata.java.io.chap01;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket的server
 */
public class Server {


    public static void main(String[] args) {
        final int PORT = 8888;
        ServerSocket serverSocket = null;
        try {
            //创建server
            serverSocket = new ServerSocket(PORT);
            System.out.println("启动服务器,监听的端口是:"+PORT);

            while (true){
                //client跟server建立连接
                Socket socket = serverSocket.accept();//阻塞的
                System.out.println("客户端"+socket.getPort()+"已建立连接");

                //通过字符流接收和写出数据
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                //接收客户端发送过来的数据
                String message;
                //这段循环的意思是 只要reader(客户端)不死 就一直读
                while ((message = reader.readLine()) != null){
                    if (null != message) {
                        System.out.println("客户端"+socket.getPort()+"发送的信息"+message);
                        //返回给客户的数据
                        writer.write("----"+message+"\n");
                        writer.flush();

                        //判断客户端是否退出
                        if(Constansts.QUIT.equalsIgnoreCase(message)){
                            System.out.println("客户端"+socket.getPort()+"已退出");
                            break;

                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (null != serverSocket) {
                try {
                    serverSocket.close();
                    System.out.println("服务器关闭...");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
