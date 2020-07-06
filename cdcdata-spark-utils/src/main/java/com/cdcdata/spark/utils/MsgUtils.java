package com.cdcdata.spark.utils;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 *
 * 发送邮件
 */
public class MsgUtils {

    public static void send(String recivers, String title, String content) throws Exception {


        //将邮件信息放入Properties
        Properties properties = new Properties();
        properties.setProperty("mail.host","smtp.qq.com");
        properties.setProperty("mail.transport.protocol","smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.ssl.enable","true");

        MailSSLSocketFactory factory = new MailSSLSocketFactory();
        factory.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.socketFactory", factory);

        //拿到发送邮件的邮箱的用户名和密码
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                String username = "xx@qq.com";
                String password = "";
                return new PasswordAuthentication(username, password);
            }
        };
        Session session = Session.getInstance(properties, authenticator);
        //创建信息
        MimeMessage message = new MimeMessage(session);
        //发送者
        InternetAddress from = new InternetAddress("xx@qq.com");
        message.setFrom(from);
        //接收者
        InternetAddress[] tos = InternetAddress.parse(recivers);
        message.setRecipients(Message.RecipientType.TO, tos);
        message.setSubject(title);//邮件标题
        message.setContent(content, "text/html;charset=UTF-8");//邮件内容
        //发送
        Transport.send(message);
    }

    public static void main(String[] args) throws Exception{
        send("xxx", "测试", "测试内容");
    }
}
