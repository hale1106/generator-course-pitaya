package com.flyfinger.test.dbunit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * DBunit和Spring-test结合的基类，如果要用dbunit准备和清楚数据库数据，直接在方法上面加上@DBUnitFile注解
 * 
 */
@SuppressWarnings("deprecation")
@TestExecutionListeners({ DBUnitExecutionListener.class, TransactionalTestExecutionListener.class })
@Transactional
public abstract class AbstractDBUnitSpringContextTests extends AbstractJUnit4SpringContextTests {
    
    private Log logger = LogFactory.getLog(AbstractDBUnitSpringContextTests.class);
    
    protected DataSource dataSource;
    
    protected DataSourceDatabaseTester dataSourceDatabaseTester;
    
    protected String dbunitFile;
    
    protected DatabaseOperation setUpOperation = DatabaseOperation.CLEAN_INSERT;
    
    protected DatabaseOperation tearDownOperation = DatabaseOperation.NONE;
    
    protected SimpleJdbcTemplate simpleJdbcTemplate;
    
    @Resource(name = "jdbcTemplate")
    protected JdbcTemplate jdbcTemplate;
    
    protected IDataSet getDataSet() {
        IDataSet dataSet = null;
        try {
            // dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(
            // dbunitFile));
            FlatXmlDataSetBuilder fxmlb = new FlatXmlDataSetBuilder();
            fxmlb.setColumnSensing(true);
            dataSet = fxmlb.build(AbstractDBUnitSpringContextTests.class
                    .getResourceAsStream(dbunitFile));
        } catch (DataSetException e) {
            logger.error(e.getStackTrace());
            e.printStackTrace();
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            e.printStackTrace();
        }
        return dataSet;
    };
    
    public void beforeForDBUnit() throws Exception {
        if (!isDBUnitFileExists()) {
            return; // 如果没有数据则什么也不做，兼容不用dbunit的情况
        }
        
        dataSourceDatabaseTester = new DataSourceDatabaseTester(getDataSource());
        dataSourceDatabaseTester.setDataSet(getDataSet());
        
        dataSourceDatabaseTester.setSetUpOperation(setUpOperation);
        dataSourceDatabaseTester.onSetup();
    }
    
    public void afterForDBUnit() throws Exception {
        if (!isDBUnitFileExists()) {
            return; // 如果没有数据则什么也不做，兼容不用dbunit的情况
        }
        
        dataSourceDatabaseTester.setTearDownOperation(tearDownOperation);
        dataSourceDatabaseTester.onTearDown();
    }
    
    private boolean isDBUnitFileExists() {
        if (dbunitFile != null && !"".equals(dbunitFile.trim())) {
            return true;
        }
        return false;
    }
    
    public DataSource getDataSource() {
        return dataSource;
    }
    
    protected String backupTables(String path, String... names) throws DatabaseUnitException,
            SQLException, FileNotFoundException, IOException {
        if (names.length < 1)
            return null;
        IDatabaseConnection connection = new DatabaseConnection(this.dataSource.getConnection());
        DatabaseConfig config = connection.getConfig();
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory2());
        IDataSet actualDataSet = connection.createDataSet(names);
        FlatXmlDataSet.write(actualDataSet, new FileOutputStream(path));
        connection.close();
        return path;
    }
    
    protected String backupTable(String path, String table, String sql)
            throws DatabaseUnitException, SQLException, FileNotFoundException, IOException {
        IDatabaseConnection connection = new DatabaseConnection(this.dataSource.getConnection());
        QueryDataSet partialDataSet = new QueryDataSet(connection);
        partialDataSet.addTable(table, sql);
        FlatXmlDataSet.write(partialDataSet, new FileOutputStream(path));
        connection.close();
        return path;
    }
    
    @Resource(name = "dataSource")
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }
    
    public String getDbunitFile() {
        return dbunitFile;
    }
    
    public void setDbunitFile(String dbunitFile) {
        this.dbunitFile = dbunitFile;
    }
    
}
