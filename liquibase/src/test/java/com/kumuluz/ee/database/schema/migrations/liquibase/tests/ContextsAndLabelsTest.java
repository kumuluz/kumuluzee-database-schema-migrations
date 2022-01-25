/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.kumuluz.ee.database.schema.migrations.liquibase.tests;

import com.kumuluz.ee.database.schema.migrations.liquibase.tests.beans.UnannotatedLiquibaseContainer;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.enterprise.inject.spi.CDI;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Tests Liquibase contexts and labels.
 *
 * @author Din Music
 * @since 1.0.0
 */
public class ContextsAndLabelsTest extends Arquillian {

    public static final String SELECT_ALL = "SELECT * FROM TEST_TABLE";

    @Deployment
    public static JavaArchive deployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(UnannotatedLiquibaseContainer.class)
                .addAsResource("correct-config.yml", "config.yml")
                .addAsResource("test-changelog.xml", "db/changelog.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void contextsAndLabelsTest() throws LiquibaseException, SQLException {

        UnannotatedLiquibaseContainer liquibaseContainer = CDI.current().select(UnannotatedLiquibaseContainer.class).get();
        Liquibase liquibase = liquibaseContainer.getLiquibase();

        Statement stmt = DriverManager
                .getConnection(liquibase.getDatabase().getConnection().getURL())
                .createStatement();

        // Migrates schema only - There should be no rows inserted
        liquibase.update("init");

        ResultSet rs = stmt.executeQuery(SELECT_ALL);
        Assert.assertFalse(rs.next());

        // Insert sample row - There should be one row
        liquibase.update(new Contexts("insert_sample_row"));

        rs = stmt.executeQuery(SELECT_ALL);
        while (rs.next()) {
            Assert.assertEquals("123456789", rs.getString(1));
            Assert.assertEquals("This is test field.", rs.getString(2));
        }

        liquibase.update(new Contexts(), new LabelExpression("v1.1 and !drop_table"));

        rs = stmt.executeQuery(SELECT_ALL);
        Assert.assertFalse(rs.next());

        liquibase.dropAll();

        Assert.assertThrows(org.h2.jdbc.JdbcSQLSyntaxErrorException.class, () -> stmt.executeQuery(SELECT_ALL));
    }
}
