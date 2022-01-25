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
package com.kumuluz.ee.database.schema.migrations.liquibase.exceptions;

import com.kumuluz.ee.database.schema.migrations.common.exceptions.MigrationException;

/**
 * Exception indicating that multiple Liquibase configurations are provided but it's not clear
 * which one should be used.
 *
 * @author Din Music
 * @since 1.0.0
 */
public class AmbiguousLiquibaseConfigurationException extends MigrationException {

    public AmbiguousLiquibaseConfigurationException() {
        super("There are more than 1 Liquibase configurations provided." +
                " Please provide 'jndiName' of Liquibase configuration trough '@LiquibaseChangelog' annotation.");
    }

}
