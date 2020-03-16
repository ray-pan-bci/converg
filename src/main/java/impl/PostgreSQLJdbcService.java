package impl;

import common.AbstractJdbcService;
import model.Column;
import model.DataSource;
import org.apache.commons.lang3.StringUtils;
import util.FileUtil;
import util.LogUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class PostgreSQLJdbcService extends AbstractJdbcService {

    public PostgreSQLJdbcService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String loadDriverClass() {
        return "org.postgresql.Driver";
    }

    @Override
    protected String schemaPattern() {
        //mysql的schemaPattern为数据库名称
        String url = getDataSource().getJdbcUrl();
        //jdbc:mysql://localhost:3306/iso_db?useUnicode=true&characterEncoding=UTF-8
        String database = url.substring(url.indexOf("/", 13) + 1);
        int index = database.indexOf("?");
        if (index > 0) {
            database = database.substring(0, index);
        }
        return database;
    }

    /**
     * no table name
     *
     * @return
     */
    @Override
    public List<String> getAllUserTableSql() {
        Connection conn = getConnection();
        if (conn == null) {
            return null;
        }
        ResultSet rs = null;
        String sql = "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = '" + this.getDataSource().getSchema() + "'";
        PreparedStatement pStmt = null;
        List<String> result = new ArrayList<>();
        try {
            pStmt = conn.prepareStatement(sql);
            rs = pStmt.executeQuery();
            if (rs != null) {
                //数据库列名
                ResultSetMetaData data = rs.getMetaData();
                //遍历结果   getColumnCount 获取表列个数
                while (rs.next()) {
                    result.add(rs.getString(1));
                }
            }
        } catch (Exception e) {
            LogUtil.debug(e.getMessage(), e);
        } finally {
            close(conn, null, rs);
        }

        return result;
    }

    /**
     * tableName asd%;adc;
     * @param tableName
     * @return
     */
    @Override
    public List<String> getParaTablesSql(String tableName) {
        Connection conn = getConnection();
        if (conn == null) {
            return null;
        }
        String[] tableNames = tableName.split(";");
        StringJoiner sb = new StringJoiner(" or ");
        Arrays.stream(tableNames).forEach(e -> sb.add("table_name like '" + e + "'"));
        ResultSet rs = null;
        String sql = "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = '" + this.getDataSource().getSchema() + "'" +
                " and (" + sb.toString() + ")";
        PreparedStatement pStmt = null;
        List<String> result = new ArrayList<>();
        try {
            pStmt = conn.prepareStatement(sql);
            rs = pStmt.executeQuery();
            if (rs != null) {
                //数据库列名
                ResultSetMetaData data = rs.getMetaData();
                //遍历结果   getColumnCount 获取表列个数
                while (rs.next()) {
                    result.add(rs.getString(1));
                }
            }
        } catch (Exception e) {
            LogUtil.debug(e.getMessage(), e);
        } finally {
            close(conn, null, rs);
        }

        return result;
    }

    @Override
    public List<Column> getTableColumnsAndType(String tvName) {
        if (StringUtils.isBlank(tvName)) {
            tvName = this.getDataSource().gettvName();
        }
        String sql = FileUtil.getFile("postgresIR.sql")
                .replace("#{dbName}", this.getDataSource().getDbName())
                .replace("#{schema}", this.getDataSource().getSchema())
                .replace("#{tbName}", tvName);

        Connection conn = null;
        PreparedStatement pStmt = null; //定义盛装SQL语句的载体pStmt    
        ResultSet rs = null;//定义查询结果集rs
        List<Column> columns = new ArrayList<>();
        try {
            conn = this.getConnection();
            pStmt = conn.prepareStatement(sql);//<第4步>获取盛装SQL语句的载体pStmt    
            rs = pStmt.executeQuery();//<第5步>获取查询结果集rs     
            if (rs != null) {
                //数据库列名
                ResultSetMetaData data = rs.getMetaData();
                //遍历结果   getColumnCount 获取表列个数
                while (rs.next()) {
                    columns.add(new Column(
                            rs.getString(4)
                            , rs.getString(5)
                            , rs.getString(6).equals("required") ? "true" : "false"));
                }
            }
        } catch (Exception e) {
            LogUtil.debug(e.getMessage(), e);
        } finally {
            this.close(conn, pStmt, rs);
        }
        return columns;
    }


}
