package com.cdcdata.java.io.chap02;

import com.cdcdata.java.io.chap01.Constansts;

import java.io.*;
import java.net.Socket;

/**
 * 客户端
 */
public class QQClient {

    private BufferedWriter writer;
    private BufferedReader reader;

    private Socket socket;
    final String DEFAULT_SERVER_HOST = "localhost";
    final int DEFAULT_PORT = 8888;

    public QQClient(){

    }

    public QQClient(Socket socket){
        this.socket = socket;
    }
    //发消息
    public void send(String message) throws Exception {
        if(!socket.isOutputShutdown()){
            writer.write(message + "\n");
            writer.flush();
        }
    }
    //接消息
    public String receiver() throws Exception {
        String message = null;
        if(!socket.isOutputShutdown()){
            message = reader.readLine();

        }
        return message;
    }
    //退出
    public boolean toQuit(String message){
        return Constansts.QUIT.equalsIgnoreCase(message);
    }

    public void start(){
        try {
            //创建socket
            socket = new Socket(DEFAULT_SERVER_HOST,DEFAULT_PORT);
            //io

            //客户端接收Server返回的数据
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //发送数据到Server
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //说话的线程
            new Thread(new InputHandler(this)).start();


            //转发的线程
            String message = null;
            while ((message = receiver())!= null){
                System.out.println(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                    System.out.println("客户端关闭");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public static void main(String[] args) {
        new QQClient().start();
    }
}
