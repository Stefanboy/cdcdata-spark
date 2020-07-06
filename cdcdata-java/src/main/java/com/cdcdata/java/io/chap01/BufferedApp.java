package com.cdcdata.java.io.chap01;

import java.io.*;

/**
 * 缓冲流操作文件读取和写出
 */
public class BufferedApp {
    public static void main(String[] args) {
        //read();
        write();
    }
    //写出缓冲流
    private static void write() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("cdcdata-java/out/wc3.txt")));
            writer.write("www.cdcdata.com");
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    //读取缓冲流
    private static void read() {
        BufferedReader reader = null;
        //文件读进来可以是字节流的 字节流==>字符流
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("cdcdata-java/data/wc.data")));
            String message = null;
            while ((message = reader.readLine()) != null){
                System.out.println(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
