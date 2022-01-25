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
package com.kumuluz.ee.database.schema.migrations.liquibase;

import com.kumuluz.ee.common.config.DataSourceConfig;
import com.kumuluz.ee.database.schema.migrations.liquibase.configurations.LiquibaseConfig;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Liquibase container initializes Liquibase object based on provided JNDI name.
 *
 * @author Din Music
 * @since 1.0.0
 */
public class LiquibaseContainer {

    private final LiquibaseConfig liquibaseConfig;
    private final DataSourceConfig dataSourceConfig;

    public LiquibaseContainer(LiquibaseConfig liquibaseConfig, DataSourceConfig dataSourceConfig) {
        this.liquibaseConfig = liquibaseConfig;
        this.dataSourceConfig = dataSourceConfig;
    }

    /**
     * Creates Liquibase object based on configuration provided with JNDI name.
     *
     * @return Returns Liquibase object.
     */
    public Liquibase createLiquibase() {

        try {
            Connection connection = DriverManager.getConnection(
                    dataSourceConfig.getConnectionUrl(),
                    dataSourceConfig.getUsername(),
                    dataSourceConfig.getPassword());

            ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(Thread.currentThread().getContextClassLoader());
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            return new Liquibase(liquibaseConfig.getFile(), resourceAccessor, database);

        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    public LiquibaseConfig getLiquibaseConfig() {
        return liquibaseConfig;
    }

    public Contexts getContexts() {
        return new Contexts(liquibaseConfig.getContexts());
    }

    public LabelExpression getLabels() {
        return new LabelExpression(liquibaseConfig.getLabels());
    }

    public String getDataSourceJndiName() {
        return dataSourceConfig.getJndiName();
    }

}