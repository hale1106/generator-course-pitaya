package com.flyfinger.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

import com.flyfinger.test.dbunit.AbstractDBUnitSpringContextTests;

/**
 * 测试基类，继承至AbstractDBUnitSpringContextTests，支持用dbunit准备和清除数据，支持spring-test的事务， 可以做完测试后自动回滚。
 * 同时，拥有jdbcTemplate和simpleJdbcTemplate，用这两者查询最后的实际结果。
 */
@SuppressWarnings("deprecation")
@ContextConfiguration(locations = { "classpath*:/spring/applicationContext.xml" })
@TransactionConfiguration(transactionManager = "transactionManager")
public abstract class BaseUnitTest extends AbstractDBUnitSpringContextTests {

    /**
     * Count the rows in the given table.
     * 
     * @param tableName table name to count rows in
     * @return the number of rows in the table
     */
    protected int countRowsInTable(String tableName) {
        return SimpleJdbcTestUtils.countRowsInTable(this.simpleJdbcTemplate, tableName);
    }
 
    /**
     * Convenience method for deleting all rows from the specified tables. Use with caution outside of a transaction!
     * 
     * @param names the names of the tables from which to delete
     * @return the total number of rows deleted from all specified tables
     */
    protected int deleteFromTables(String... names) {
        return SimpleJdbcTestUtils.deleteFromTables(this.simpleJdbcTemplate, names);
    }

    protected List<Map<String, Object>> selectAll(String tableName) throws SQLException {

        List<Map<String, Object>> list = null;
        String sql = "select * from " + tableName;
        Connection conn = this.dataSource.getConnection();
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(sql);
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount(); // Map rowData;
        list = new ArrayList<Map<String, Object>>();
        while (rs.next()) {
            Map<String, Object> rowData = new HashMap<String, Object>();
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));
            }
            list.add(rowData);
        }
        rs.close();
        stm.close();
        conn.close();
        return list;
    }

    protected List<Map<String, Object>> selectAllBySql(String sql) throws SQLException {

        List<Map<String, Object>> list = null;
        Connection conn = this.dataSource.getConnection();
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(sql);
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount(); // Map rowData;
        list = new ArrayList<Map<String, Object>>();
        while (rs.next()) {
            Map<String, Object> rowData = new HashMap<String, Object>();
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));
            }
            list.add(rowData);
        }
        rs.close();
        stm.close();
        conn.close();
        return list;
    }

    // @Resource(name = "sessionFactory")
    // protected SessionFactory sessionFactory;

    /**
     * 将当前Hibernate中session内容flush到数据库，使spring的simpleJdbcTemplate可以访问之前对数据库的更改
     */
    // protected void flushCurrentSession() {
    // Session session = sessionFactory.getCurrentSession();
    // if (session != null) {
    // session.flush();
    // }
    // }

}
