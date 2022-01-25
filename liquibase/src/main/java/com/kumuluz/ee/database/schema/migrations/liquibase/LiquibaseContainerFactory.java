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
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.database.schema.migrations.liquibase.configurations.LiquibaseConfig;
import com.kumuluz.ee.database.schema.migrations.liquibase.exceptions.AmbiguousLiquibaseConfigurationException;
import com.kumuluz.ee.database.schema.migrations.liquibase.exceptions.InvalidLiquibaseConfigurationException;
import com.kumuluz.ee.database.schema.migrations.liquibase.exceptions.LiquibaseConfigurationNotFoundException;
import com.kumuluz.ee.database.schema.migrations.liquibase.utils.LiquibaseConfigurationUtil;

import java.util.List;

/**
 * {@link LiquibaseContainer} factory.
 *
 * @author Din Music
 * @since 1.0.0
 */
public class LiquibaseContainerFactory {

    private final String jndiName;

    public LiquibaseContainerFactory(String jndiName) {
        this.jndiName = jndiName;
    }

    /**
     * Creates new LiquibaseContainer based on provided JNDI name.
     *
     * @return Returns LiquibaseContainer object.
     */
    public LiquibaseContainer createLiquibaseContainer() {

        LiquibaseConfig liquibaseConfig = getLiquibaseConfig(jndiName);
        DataSourceConfig dataSourceConfig = getDataSourceConfig(liquibaseConfig.getJndiName());

        return new LiquibaseContainer(liquibaseConfig, dataSourceConfig);
    }

    /**
     * Gets Liquibase configuration.
     *
     * @param jndiName - Liquibase configuration JNDI name.
     * @return Function returns LiquibaseConfig based on provided JNDI name.
     * @throws AmbiguousLiquibaseConfigurationException - Exception is thrown when Liquibase configuration JNDI name
     *                                                  is not specified, but there are more than 1 configurations
     *                                                  available.
     * @throws LiquibaseConfigurationNotFoundException  - Exception is thrown when Liquibase configuration with provided
     *                                                  JNDI name could not be found.
     */
    private LiquibaseConfig getLiquibaseConfig(String jndiName) throws AmbiguousLiquibaseConfigurationException, InvalidLiquibaseConfigurationException {

        List<LiquibaseConfig> liquibaseConfigs = LiquibaseConfigurationUtil.getInstance().getLiquibaseConfigs();

        if (jndiName == null || jndiName.equals("")) {

            if (liquibaseConfigs.size() == 1) {
                return liquibaseConfigs.get(0);
            } else {
                throw new AmbiguousLiquibaseConfigurationException();
            }

        } else {

            return liquibaseConfigs
                    .stream()
                    .filter(config -> config.getJndiName().equals(jndiName))
                    .findFirst()
                    .orElseThrow(() -> new LiquibaseConfigurationNotFoundException(jndiName));
        }
    }

    /**
     * Gets data source configuration.
     *
     * @param jndiName - Data source configuration JNDI name.
     * @return Returns DataSourceConfig based on provided JNDI name.
     * @throws InvalidLiquibaseConfigurationException - Exception is thrown when no data source configuration matches
     *                                                provided JNDI name.
     */
    private DataSourceConfig getDataSourceConfig(String jndiName) throws InvalidLiquibaseConfigurationException {

        List<DataSourceConfig> dataSourceConfigs = EeConfig.getInstance().getDatasources();

        return dataSourceConfigs
                .stream()
                .filter(ds -> ds.getJndiName().equals(jndiName))
                .findFirst()
                .orElseThrow(() -> new InvalidLiquibaseConfigurationException(jndiName));
    }
}
