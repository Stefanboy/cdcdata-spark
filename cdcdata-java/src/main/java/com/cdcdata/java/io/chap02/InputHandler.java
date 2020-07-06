package com.cdcdata.java.io.chap02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 处理用户输入的线程
 */
public class InputHandler implements Runnable{
    private QQClient qqClient;
    public InputHandler(QQClient qqClient){
        this.qqClient = qqClient;
    }
    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                //读取客户端输入的消息
                String input = reader.readLine();
                //发送消息到服务器
                qqClient.send(input);

                if(qqClient.toQuit(input)){
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
