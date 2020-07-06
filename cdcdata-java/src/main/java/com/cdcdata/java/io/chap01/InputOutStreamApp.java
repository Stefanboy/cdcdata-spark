package com.cdcdata.java.io.chap01;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 字节流操作文件的输入和输出
 * 字节流的实现比较麻烦
 * 能用字符流就不用字节流
 */
public class InputOutStreamApp {
    public static void main(String[] args) {
        //read();
        write();
    }

    private static void write() {
        //先把数据读进来 然后再写出去
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream("cdcdata-java/data/wc.data");
            fos = new FileOutputStream("cdcdata-java/out/wc.data");
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = fis.read(buffer,0,buffer.length))!= -1){
                fos.write(buffer,0,length);
                fos.flush();//写完要flush
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null != fis){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(null != fos){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
    //读取数据
    private static void read() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("cdcdata-java/data/wc.data");
            byte[] buffer = new byte[1024];
            //表示返回的offset的值
            int length = 0;
            //TODO...业务逻辑

            while ((length =fis.read(buffer,0,buffer.length)) != -1){
                String result = new String(buffer,0,length);
                System.out.println(result);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (null != fis){
                    fis.close();//先判断fis不为空在关
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
