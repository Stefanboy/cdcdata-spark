package com.cdcdata.java.io.chap01;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 字符流对文件的写入和写出
 * 第一版 最原始版本
 */
public class ReaderWriterApp {
    public static void main(String[] args) {
        //read();
        write();
    }
    //字符流写出
    private static void write() {

        FileWriter writer = null;
        try {
            writer = new FileWriter("cdcdata-java/out/wc2.data");
            writer.write("www.cdcdata.com...\n");
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

    //字符流读取
    private static void read() {
        FileReader reader = null;
        try {
            reader = new FileReader("cdcdata-java/data/wc.data");
            char[] buffer = new char[1024];
            int length = 0;
            while ( (length =reader.read(buffer,0,buffer.length))!= -1){
                String result = new String(buffer,0,length);
                System.out.println(result);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != reader){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
