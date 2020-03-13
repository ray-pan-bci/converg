package metadata;

import common.JdbcService;
import common.JdbcServiceFactory;
import model.DataSource;
import model.Relation;
import util.LogUtil;

import java.util.List;


public class ANSIMetaData {

    /**
     * Middle tier, providing data to the outside
     *
     * @return List<Column>
     */
    public static List<Relation> getANSIMetaData() {

        String userName = System.getProperty("userName");
        String passwd = System.getProperty("passwd");
        String database = System.getProperty("database");
        String engine = System.getProperty("db_engine");
        String schema = System.getProperty("schema");
        String dbName = System.getProperty("db_name");
        String table = System.getProperty("table");
        DataSource param = new DataSource(engine, database, userName, passwd, dbName, schema, table);
        JdbcService jdbcService = JdbcServiceFactory.getJdbcService(param);
        //Connection test
        boolean testConn = jdbcService.test();
        if(!testConn)
            return null;
        List<Relation> relations = jdbcService.getAllTablesColumnsAndType();
        LogUtil.info("Schema extracted successfully!");
        return relations;
    }


}
