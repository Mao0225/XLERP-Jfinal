package com.xlerp.index;

import java.util.List;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.AnsiSqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
/**
 * JFinal对接达梦数据库初步测试CRUD
 * 使用Db+Record模式
 *
 * @ClassName:  DmTest
 * @author: JFinal学院-小木 QQ：909854136
 * @date:   2020年9月13日
 *
 */
public class DmTest {
    String url = "jdbc:dm://localhost:5236";
    String username = "SYSDBA";
    String password = "Mao0225.";
    DruidPlugin druidPlugin=null;
    ActiveRecordPlugin arp=null;

    public boolean start() {
        druidPlugin=new DruidPlugin(url, username, password);
        arp=new ActiveRecordPlugin(druidPlugin);
        arp.setDialect(new AnsiSqlDialect());
        arp.setShowSql(true);
        arp.setDevMode(true);
        arp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
        boolean success=druidPlugin.start();
        if(success) {
            success=arp.start();
        }
        return success;
    }


    public static void main(String[] args) {
        DmTest test = new DmTest();
        //启动连接
        boolean success=test.start();
        if(success) {
            try {
                // 插入数据
                System.out.println("--- 插入 ---");
                test.insertTable();

                //查询
                System.out.println("--- 显示插入结果 ---");
                test.queryTable();

                //修改
                System.out.println("--- 修改 ---");
                test.updateTable();

                //查询
                System.out.println("--- 显示修改结果 ---");
                test.queryTable();

                //删除
                System.out.println("--- 删除 ---");
                test.deleteTable();

                //在删除后查询
                System.out.println("--- 在删除后查询 ---");
                test.queryTable();

            } finally {
                test.stop();
            }
        }

    }


    /**
     * 结束
     */
    private void stop() {
        arp.stop();
        druidPlugin.stop();

    }

    /**
     * 删除数据
     */
    private void deleteTable() {
        System.out.println("=====DOING:执行数据删除=====");
        int count=Db.update("delete from DMHR.CITY where CITY_ID='DY'");
        if(count==1) {
            System.out.println("=====SUCCESS:数据删除成功=====");
        }else {
            System.out.println("=====ERROR:数据删除失败=====");
        }
    }


    /**
     * 修改更新数据
     */
    private void updateTable() {
        System.out.println("=====DOING:执行数据更新=====");
        int count=Db.update("update DMHR.CITY set CITY_NAME='东营_修改后' where CITY_ID='DY'");
        if(count==1) {
            System.out.println("=====SUCCESS:数据修改成功=====");
        }else {
            System.out.println("=====ERROR:数据修改失败=====");
        }
    }


    /**
     * 查询数据
     */
    private void queryTable() {
        System.out.println("=====DOING:执行数据查询=====");
        List<Record> records=Db.find("select * from DMHR.CITY");
        if(records!=null&&records.size()>0) {
            System.out.println("=====SUCCESS:数据查询成功=====");
            int index=0;
            for(Record record:records) {
                index++;
                System.out.println(index+":"+JsonKit.toJson(record));
            }
        }else {
            System.out.println("=====RESULT:暂无数据=====");
        }
    }



    /**
     * 插入数据
     */
    private void insertTable() {
        System.out.println("=====DOING:执行数据插入=====");
        Record record=new Record();
        record.set("CITY_ID", "DY");
        record.set("CITY_NAME", "东营");
        record.set("REGION_ID", 1);
        boolean success=Db.save("DMHR.CITY", record);
        if(success) {
            System.out.println("=====SUCCESS:数据插入成功=====");
            System.out.println(record.toJson());
        }else {
            System.out.println("=====ERROR:数据插入失败=====");
        }
    }




}
