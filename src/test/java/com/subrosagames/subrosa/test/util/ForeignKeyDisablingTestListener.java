package com.subrosagames.subrosa.test.util;

import java.sql.PreparedStatement;
import javax.sql.DataSource;

import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Test listener that disables referential integrity checks during tests.
 */
public class ForeignKeyDisablingTestListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        IDatabaseConnection dbConn = new DatabaseDataSourceConnection(
                testContext.getApplicationContext().getBean(DataSource.class)
        );
        try (PreparedStatement statement = dbConn.getConnection().prepareStatement("SET DATABASE REFERENTIAL INTEGRITY FALSE")) {
            statement.execute();
        }
    }
}
