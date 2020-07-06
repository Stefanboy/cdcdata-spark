/*
package com.cdcdata.kudu;

import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

*/
/**
 *java方式操作kudu的api
 *//*

public class KuduAPIApp {
    String masterAddress = "jd:7051";
    KuduClient client = null;
    //构建kudu的列
    private ColumnSchema newColumn(String name, Type type, Boolean pk) {
        //创建列的schema
        ColumnSchema.ColumnSchemaBuilder column = new ColumnSchema.ColumnSchemaBuilder(name, type);
        //指定主键
        column.key(pk);
        return column.build();
    }

    //建表
    @Test
    public void createTable() throws Exception {
        LinkedList<ColumnSchema> columns = new LinkedList<>();
        columns.add(newColumn("id", Type.INT32, true));
        columns.add(newColumn("name", Type.STRING, false));
        Schema schema = new Schema(columns);

        CreateTableOptions options = new CreateTableOptions();
        //设置副本数(默认3副本)
        options.setNumReplicas(1);

        LinkedList<String> partitions = new LinkedList<>();
        //设置分区id
        partitions.add("id");
        //设置分片数量
        options.addHashPartitions(partitions, 3);
        //建表
        client.createTable("student", schema, options);
    }
    //插入数据
    @Test
    public void insert() throws Exception {
        //写数据之前 要把表打开
        KuduTable table = client.openTable("student");
        KuduSession kuduSession = client.newSession();
        kuduSession.setMutationBufferSpace(3000);
        for (int i = 0; i < 10; i++) {
            //拿到一行数据
            Insert insert = table.newInsert();
            //拿到对应的列
            insert.getRow().addInt("id", i + 1);
            insert.getRow().addString("name", "PK" + i);
            kuduSession.flush();
            //把操作apply上去
            kuduSession.apply(insert);
        }
    }
    //查询
    @Test
    public void query()throws Exception{
        //打开表
        KuduTable table = client.openTable("student");
        //查询到结果
        KuduScanner scanner = client.newScannerBuilder(table).build();
        //拿出查询结果
        while(scanner.hasMoreRows()) {
            for(RowResult result : scanner.nextRows()) {
                System.out.println(result.getInt("id") + "\t" + result.getString("name"));
            }
        }
    }
    //修改数据
    @Test
    public void update() throws Exception{
        KuduTable table = client.openTable("student");
        KuduSession kuduSession = client.newSession();
        Update update = table.newUpdate();
        // update xxx set id=1
        PartialRow row = update.getRow();
        row.addInt("id",1);
        row.addString("name", "ruozedata");
        kuduSession.apply(update);
    }
    //删除
    @Test
    public void delete() throws Exception{
        KuduTable table = client.openTable("student");
        KuduSession kuduSession = client.newSession();
        Delete delete = table.newDelete();
        //把id为2的数据删除
        delete.getRow().addInt("id",2);
        kuduSession.apply(delete);
    }

    @Test
    public void drop() throws Exception{
        client.deleteTable("student");
    }

    //创建客户端
    @Before
    public void setUp() {
        client = new KuduClient.KuduClientBuilder(masterAddress)
                .defaultSocketReadTimeoutMs(6000).build();
    }

    @After
    public void tearDown() {
        if (null != client) {
            try {
                client.close();
            } catch (KuduException e) {
                e.printStackTrace();
            }
        }
    }
}
*/
