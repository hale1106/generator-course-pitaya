package com.flyfinger.test.dbunit;

import java.sql.Types;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;

public class MySqlDataTypeFactory2 extends MySqlDataTypeFactory {

    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        if (sqlType == Types.OTHER) {
            // BOOLEAN
            if ("bit".equals(sqlTypeName)) {
                return DataType.BOOLEAN;
            }
        }
        if (sqlType == Types.INTEGER){
            return DataType.BIGINT;
        }
        return super.createDataType(sqlType, sqlTypeName);
    }

}
