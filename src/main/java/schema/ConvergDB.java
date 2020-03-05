package schema;

import com.alibaba.fastjson.JSONObject;
import util.JDBCUtil;

import java.sql.Connection;
import java.util.Scanner;

/**
 * @Author Kevin
 * @Date 2020/3/4
 */
public class ConvergDB {
    public static void main(String args[]){

        System.out.println( "===============================\n" +
                            "ConvergDB JDBC schema extractor\n" +
                            "===============================\n");

        ConvergDB.convergDBAction();

    }






    /**
     * 该方法执行数据库连接、元数据获取、schema文件输出
     */
    private static void convergDBAction(){
        String userName = System.getProperty("table");
        String passwd = System.getProperty("table");
        String database = System.getProperty("database");
        String engine = System.getProperty("db_engine");
        String info = "Attempting connection to "+database +"as analytics_user...";
        System.out.println(info);

        //创建连接
        Connection conn = null;
        conn = new JDBCUtil().getConn(database,engine);
        if(null == conn){
            System.out.println("\n" +
                    "Sorry, your connection is wrong. Please check the connection parameters");
            return;
        }
        System.out.println("Connected!");
        System.out.println("Extracting schema for the following objects:");

        JSONObject json = null;
        //抽取元数据
//      json = new GetMetaDataImp().getMetaData("","");
        if(json == null){
            System.out.println("Extracting schema failed!");
            return;
        }

        System.out.println("Schema extracted successfully!");

        Scanner scan = new Scanner(System.in);
        System.out.println("Please provide path for ConvergDB schema output:");
        String outPath;
        if (scan.hasNext()) {
            outPath = scan.next();
            System.out.println("Schema written to: \n" + outPath);
        }else{
            System.out.println("no output path");
        }

        ExtraSchema extraSchema = new ExtraSchema();
//        try {
//
//        }catch (){
//
//        }
        extraSchema.exportSchema(json);

        System.out.println("Process complete");

    }
}
