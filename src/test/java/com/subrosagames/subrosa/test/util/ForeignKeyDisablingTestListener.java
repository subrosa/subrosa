package com.subrosagames.subrosa.test.util;

import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import javax.sql.DataSource;

public class ForeignKeyDisablingTestListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        IDatabaseConnection dbConn = new DatabaseDataSourceConnection(
                testContext.getApplicationContext().getBean(DataSource.class)
        );
        dbConn.getConnection().prepareStatement("SET DATABASE REFERENTIAL INTEGRITY FALSE").execute();

    }
}