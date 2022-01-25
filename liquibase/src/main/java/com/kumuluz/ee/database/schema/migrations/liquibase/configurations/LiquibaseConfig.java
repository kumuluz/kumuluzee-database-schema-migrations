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
package com.kumuluz.ee.database.schema.migrations.liquibase.configurations;

import java.util.List;

/**
 * Liquibase configuration.
 *
 * @author Din Music
 * @since 1.0.0
 */
public class LiquibaseConfig {

    private String jndiName;
    private String file;
    private boolean startupDropAll;
    private boolean startupUpdate;
    private List<String> contexts;
    private List<String> labels;

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public boolean isStartupDropAll() {
        return startupDropAll;
    }

    public void setStartupDropAll(boolean startupDropAll) {
        this.startupDropAll = startupDropAll;
    }

    public boolean isStartupUpdate() {
        return startupUpdate;
    }

    public void setStartupUpdate(boolean startupUpdate) {
        this.startupUpdate = startupUpdate;
    }

    public List<String> getContexts() {
        return contexts;
    }

    public void setContexts(List<String> contexts) {
        this.contexts = contexts;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
