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

import com.kumuluz.ee.database.schema.migrations.common.MigrationUtil;
import com.kumuluz.ee.database.schema.migrations.liquibase.utils.LiquibaseMigrationUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

/**
 * Migration initializer.
 *
 * @author Din Music
 * @since 1.0.0
 */
@ApplicationScoped
public class MigrationInitializer {

    /**
     * Triggers Liquibase migration when application scope is initialized.
     *
     * @param init - Initialized application scope object.
     */
    public void migrate(@Observes @Initialized(ApplicationScoped.class) Object init) {
        MigrationUtil migrationUtil = new LiquibaseMigrationUtil();
        migrationUtil.migrate();
    }

}
