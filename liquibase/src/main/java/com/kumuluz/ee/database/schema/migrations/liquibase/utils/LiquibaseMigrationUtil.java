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
package com.kumuluz.ee.database.schema.migrations.liquibase.utils;

import com.kumuluz.ee.database.schema.migrations.common.MigrationUtil;
import com.kumuluz.ee.database.schema.migrations.liquibase.LiquibaseContainer;
import com.kumuluz.ee.database.schema.migrations.liquibase.LiquibaseContainerFactory;
import com.kumuluz.ee.database.schema.migrations.liquibase.configurations.LiquibaseConfig;
import com.kumuluz.ee.database.schema.migrations.liquibase.exceptions.LiquibaseMigrationException;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;

import java.util.List;

/**
 * Executes Liquibase database schema migrations.
 *
 * @author Din Music
 * @since 1.0.0
 */
public class LiquibaseMigrationUtil extends MigrationUtil {

    public LiquibaseMigrationUtil() {
        super();
    }

    @Override
    public void migrate() {

        List<LiquibaseConfig> liquibaseConfigs = LiquibaseConfigurationUtil.getInstance().getLiquibaseConfigs();

        for (LiquibaseConfig liquibaseConfig : liquibaseConfigs) {

            LiquibaseContainer liquibaseContainer = new LiquibaseContainerFactory(liquibaseConfig.getJndiName()).createLiquibaseContainer();
            Liquibase liquibase = liquibaseContainer.createLiquibase();

            try {
                // startup dropAll
                if (liquibaseConfig.isStartupDropAll()) {
                    liquibase.dropAll();
                }

                // startup update
                if (liquibaseConfig.isStartupUpdate()) {

                    Contexts contexts = new Contexts(liquibaseConfig.getContexts());
                    LabelExpression labelExpressions = new LabelExpression(liquibaseConfig.getLabels());

                    liquibase.update(contexts, labelExpressions);
                    liquibase.validate();
                }

                liquibase.close();

            } catch (Exception e) {
                throw new LiquibaseMigrationException(e);
            }
        }

    }

}
