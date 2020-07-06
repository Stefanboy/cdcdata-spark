package com.cdcdata.hadoop;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * 创建一个httpserver
 * 如果想要再加服务就再写一个路径不一样就行了 httpServer.createContext
 * 访问地址
 * HttpServer: http://localhost:9527/test
 */
public class CdcdataHttpServer {
    public static void main(String[] args) throws Exception {
        //创建httpserver
        HttpServer httpServer = HttpServer.create(new InetSocketAddress("localhost", 9527), 10);
        //请求/test
        httpServer.createContext("/test", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                System.out.println("接收到test请求");
                String msg="执行test方法运行成功...";
                //返回
                httpExchange.sendResponseHeaders(200,msg.getBytes().length);
                //
                OutputStream out = httpExchange.getResponseBody();
                out.write(msg.getBytes());
                out.flush();
                httpExchange.close();
            }
        });
        //启动
        httpServer.start();
    }
}
