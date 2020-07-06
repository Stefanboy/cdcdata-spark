package com.cdcdata.spark.mockddata;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *生成两个txt
 * 分别为user_click.txt 和product.txt
 * user_click
 * Userid, productid
 * 17654920,150332988
 * 17654920,150440980
 * 17008942,155347895
 * ...
 * 商品类目信息表：
 * productid,category_Id
 * 150332988,2900345
 * 155347895,2983345
 * ...
 *问题：
 * * 使用spark快速求出用户点击过的商品类目。
 * 1) 如果用户点击记录为三千万条数据，商品类目信息为二百万条记录
 * 2) 如果用户点击记录和商品类目信息记录都为三千万条记录
 * * 使用spark求出每个类目点击量最大的50个商品。
 */
public class AccessMockLocalTest {

    private static String[] empno = {"7369",
            "7499", "7521", "7566", "7654", "7698"
    };

    private static String[] emppost = {"CLERK",
            "SALESMAN", "MANAGER", "PRESIDENT", "ANALYST"
    };

    private static String[] deptno = {"20","30","40","20","20","20"
    };
    private static List<String> listuid = new ArrayList<>();//用户id
    private static List<String> listpid = new ArrayList<>();//商品id
    private static List<String> listcid = new ArrayList<>();//类目id


    public static void main(String[] args) throws Exception {
        for(int i=1 ; i < 101;i++){
            listuid.add(i+"");
        }
        for(int i=1500 ; i < 1601;i++){
            listpid.add(i+"");
        }
        for(int i=2900 ; i < 2911;i++){
            listcid.add(i+"");
        }
        String userClickDataPathname ="cdcdata-spark-sql/data/user_click1.txt";
        String productDataPathname ="cdcdata-spark-sql/data/product.txt1";
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(userClickDataPathname))));
        Random random = new Random();
        userClickData(writer,random);
        //productData(writer,random);

    }

    public static void userClickData(BufferedWriter writer,Random random) throws Exception {
        for (int i=0 ; i < 300;i++){
            writer.write(listuid.get(random.nextInt(listuid.size())));
            writer.write(",");
            writer.write(listpid.get(random.nextInt(listpid.size())));
            writer.newLine();
        }
        writer.flush();
        writer.close();

    }

    public static void productData(BufferedWriter writer,Random random) throws Exception {
        for (int i=0 ; i < 101;i++){
            writer.write(1500+i+"");
            writer.write(",");
            writer.write(listcid.get(random.nextInt(listcid.size())));
            writer.newLine();
        }
        writer.flush();
        writer.close();

    }

}
