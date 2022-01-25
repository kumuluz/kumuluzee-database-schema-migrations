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

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.database.schema.migrations.liquibase.configurations.LiquibaseConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Reads Liquibase configuration from KumuluzEE configuration file.
 *
 * @author Din Music
 * @since 1.0.0
 */
public class LiquibaseConfigurationUtil {

    private static final String LIQUIBASE_CHANGELOGS_CONFIG_PREFIX = "kumuluzee.database-schema-migrations.liquibase.changelogs";
    private static final String DEFAULT_MASTER_CHANGELOG = "db/changelog-master.xml";
    private static LiquibaseConfigurationUtil instance;
    private List<LiquibaseConfig> liquibaseConfigs;

    private static synchronized void init() {
        if (instance == null) {
            instance = new LiquibaseConfigurationUtil();
        }
    }

    public static LiquibaseConfigurationUtil getInstance() {
        if (instance == null) {
            init();
        }
        return instance;
    }

    private LiquibaseConfigurationUtil() {
        readConfigs();
    }

    private void readConfigs() {

        liquibaseConfigs = new ArrayList<>();

        final ConfigurationUtil config = ConfigurationUtil.getInstance();
        int changelogCount = config.getListSize(LIQUIBASE_CHANGELOGS_CONFIG_PREFIX).orElse(0);

        for (int i = 0; i < changelogCount; i++) {

            String changelogPrefix = LIQUIBASE_CHANGELOGS_CONFIG_PREFIX + "[" + i + "]";
            Optional<String> jndiName = config.get(changelogPrefix + ".jndi-name");

            if (jndiName.isPresent()) {

                LiquibaseConfig liquibaseConfig = new LiquibaseConfig();
                liquibaseConfig.setJndiName(jndiName.get());
                liquibaseConfig.setFile(config.get(changelogPrefix + ".file")
                        .orElse(DEFAULT_MASTER_CHANGELOG));
                liquibaseConfig.setContexts(config.get(changelogPrefix + ".contexts")
                        .map(s -> Arrays.asList(s.split(",")))
                        .orElse(new ArrayList<>()));
                liquibaseConfig.setLabels(config.get(changelogPrefix + ".labels")
                        .map(s -> Arrays.asList(s.split(",")))
                        .orElse(new ArrayList<>()));
                liquibaseConfig.setStartupDropAll(config
                        .getBoolean(changelogPrefix + ".startup.drop-all")
                        .orElse(false));
                liquibaseConfig.setStartupUpdate(config
                        .getBoolean(changelogPrefix + ".startup.update")
                        .orElse(false));

                liquibaseConfigs.add(liquibaseConfig);
            }
        }
    }

    public List<LiquibaseConfig> getLiquibaseConfigs() {
        return liquibaseConfigs;
    }

}
