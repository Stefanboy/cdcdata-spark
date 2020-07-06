package com.cdcdata.java.io.chap01;

import java.io.*;
import java.net.Socket;

/**
 * Socket的客户端
 */
public class Client {
    public static void main(String[] args) {
        final int PORT = 8888;
        final String HOST = "localhost";
        Socket socket = null;
        try {
            //创建客户端的socket
            socket = new Socket("", PORT);

            while (true){

                //客户端接收Server返回的数据
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //发送数据到Server
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                //同时还得有一个输入内容的流 比如QQ中的往聊天框打字的
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                String requestMessage = consoleReader.readLine();

                //将消息发送给服务器
                writer.write(requestMessage+"\n");
                writer.flush();

                //服务器返回的内容
                String responseMessage = reader.readLine();
                System.out.println("接收到服务器端返回的数据"+responseMessage);

                //判断客户端是否退出
                if(Constansts.QUIT.equalsIgnoreCase(requestMessage)){
                    break;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != socket){
                try {
                    socket.close();
                    System.out.println("客户端已关闭...");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
