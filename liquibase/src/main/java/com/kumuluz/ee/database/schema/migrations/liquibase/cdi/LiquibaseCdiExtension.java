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
package com.kumuluz.ee.database.schema.migrations.liquibase.cdi;

import com.kumuluz.ee.common.config.DataSourceConfig;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.database.schema.migrations.liquibase.LiquibaseContainerProducer;
import com.kumuluz.ee.database.schema.migrations.liquibase.annotations.LiquibaseChangelog;
import com.kumuluz.ee.database.schema.migrations.liquibase.configurations.LiquibaseConfig;
import com.kumuluz.ee.database.schema.migrations.liquibase.utils.LiquibaseConfigurationUtil;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.DeploymentException;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import java.util.List;

/**
 * Validates injection points and Liquibase configuration.
 *
 * @author Din Music
 * @since 1.0.0
 */
public class LiquibaseCdiExtension implements Extension {

    /**
     * Validates every LiquibaseContainer injection.
     *
     * @param pip - Observed ProcessInjectionPoint event
     */
    public void validateInjectionPoints(@Observes ProcessInjectionPoint<?, ?> pip) {

        if (pip.getInjectionPoint().getBean().getBeanClass().isInstance(LiquibaseContainerProducer.class)) {

            final LiquibaseConfigurationUtil liquibaseConfigurationUtil = LiquibaseConfigurationUtil.getInstance();

            // In order to inject Liquibase container, at least one liquibase configuration needs to be provided
            if (liquibaseConfigurationUtil.getLiquibaseConfigs().size() == 0) {
                pip.addDefinitionError(new DeploymentException("Liquibase configuration needs to be provided in KumuluzEE config."));
                return;
            }

            LiquibaseChangelog liquibaseChangelog = pip.getInjectionPoint().getAnnotated().getAnnotation(LiquibaseChangelog.class);

            if (liquibaseChangelog == null || liquibaseChangelog.jndiName().equals("")) {

                // If no jndiName is specified and more than 1 configuration is preset than an error must be thrown
                if (liquibaseConfigurationUtil.getLiquibaseConfigs().size() > 1) {
                    pip.addDefinitionError(new DeploymentException("Injection point '"
                            + pip.getInjectionPoint()
                            + "' annotated with @LiquibaseChangelog has an empty jndiName, but there are "
                            + "multiple configurations provided."));
                }

            } else {

                // Validate that referenced Liquibase changelog is present in KumuluzEE configuration file
                for (LiquibaseConfig config : liquibaseConfigurationUtil.getLiquibaseConfigs()) {
                    if (config.getJndiName().equals(liquibaseChangelog.jndiName())) {
                        return;
                    }
                }

                pip.addDefinitionError(new DeploymentException("No liquibase configurations has been found for jndi name '"
                        + liquibaseChangelog.jndiName() + "'."));
            }
        }
    }

    /**
     * Validates that each Liquibase configuration is referencing a configured data source
     * and that the provided changelog file exists.
     *
     * @param event - Observed AfterBeanDiscovery event
     */
    public void validateConfigurations(@Observes AfterBeanDiscovery event) {

        List<DataSourceConfig> dataSourceConfigs = EeConfig.getInstance().getDatasources();
        List<LiquibaseConfig> liquibaseConfigs = LiquibaseConfigurationUtil.getInstance().getLiquibaseConfigs();

        for (LiquibaseConfig config : liquibaseConfigs) {

            DataSourceConfig dataSourceConfig = dataSourceConfigs.stream()
                    .filter(ds -> ds.getJndiName().equals(config.getJndiName()))
                    .findFirst()
                    .orElse(null);

            if (dataSourceConfig == null) {
                event.addDefinitionError(new DeploymentException("Liquibase configuration with jndi name '" + config.getJndiName()
                        + "' does not match any data source's jndi name."));
            }

            if (ClassLoader.getSystemClassLoader().getResource(config.getFile()) == null) {
                event.addDefinitionError(new DeploymentException("Liquibase changelog file '" + config.getFile()
                        + "' does not exist."));
            }
        }
    }

}
