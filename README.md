# KumuluzEE Database Schema Migrations

> KumuluzEE Database Schema Migrations project for database schema migrations with Liquibase.

KumuluzEE Database Schema Migrations is a database schema migration project for the KumuluzEE microservice framework 
that provides an easy way to migrate database schemas with [Liquibase](https://www.liquibase.com/). It supports 
migrations at application startup or at runtime when the application is already running.

## Usage

You can enable KumuluzEE database schema migrations with Liquibase by adding the following dependency:

```xml
<dependency>
    <groupId>com.kumuluz.ee.database-schema-migrations</groupId>
    <artifactId>kumuluzee-database-schema-migrations-liquibase</artifactId>
    <version>${kumuluzee-database-schema-migrations.version}</version>
</dependency>
```

**At least one data source** must be configured for the extension to work.
For example, PostgreSQL can be used by adding the following dependency:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>${postgresql.version}</version>
</dependency>
```

### Configure migrations

Liquibase database schema migrations are configured using the common KumuluzEE configuration framework.
The configuration properties can be defined via the environment variables or via the configuration file.
For more details, see the [KumuluzEE configuration wiki page](https://github.com/kumuluz/kumuluzee/wiki/Configuration)
and [KumuluzEE Config](https://github.com/kumuluz/kumuluzee-config).

To use Liquibase database schema migrations, **at least one data source** must be configured.

The only required Liquibase configuration property is `jndi-name`, which must correspond to a JNDI name of the 
preconfigured data source.

Minimum configuration for Liquibase database schema migration (without data source configuration):
```yaml
kumuluzee:
  database-schema-migrations:
    liquibase:
      changelogs:
        - jndi-name: jdbc/example-db             # Required
```

To specify the location of a custom changelog file, the `file` property can be used (default is
`db/changelog-master.xml`). The location must be specified relative to the `resource` directory.

Example configuration:
```yaml
kumuluzee:
  database-schema-migrations:
    liquibase:
      changelogs:
        - jndi-name: jdbc/example-db
          file: db/changelog-master.xml          # default: "db/changelog-master.xml"
```

#### Configure migrations at startup

There are two actions that can be performed when the application starts. 
One is `dropAll` and the other is `update`.
The `dropAll` action drops the database and the `update` action updates the database according to the changelog
at the location specified in the `file` property. Note that the `dropAll` action is executed before the `update` 
action if both are enabled.

Example configuration:
```yaml
kumuluzee:
  database-schema-migrations:
    liquibase:
      changelogs:
        - jndi-name: jdbc/example-db
          startup:
            drop-all: false                      # default: false  
            update: true                         # default: false
```

#### Disable migrations

To disable database schema migrations at application startup, set `kumuluzee.database-schema-migrations.enabled` 
to false (default is `true`).
```yaml
kumuluzee:
  database-schema-migrations:
    enabled: false                               # default: true
```

#### Contexts and labels

With contexts and labels, Liquibase provides a way to selectively execute *changeSets*.
Contexts allow you to select specific *changeSets* for execution, while labels allow you to select *changeSets* 
for execution using complex expressions (note that the comma (`,`) in labels means the same as the `or` operator).

Both contexts and labels can be configured in the KumuluzEE configuration file.

Example configuration:
```yaml
kumuluzee:
  database-schema-migrations:
    liquibase:
      changelogs:
        - jndi-name: jdbc/example-db
          labels: "label1 and !label2, label3"   # default: ""
          contexts: "context1, context2"         # default: ""
```

Note that contexts that are not specified are ignored by Liquibase. The same is true for labels.

### Database Schema Migrations via CDI

KumuluzEE database schema migrations provide LiquibaseContainer, a wrapper for the Liquibase object, that can be 
injected via CDI. The Liquibase object provides a way to perform schema migrations on the connected data source 
at runtime.

The LiquibaseContainer is created by first selecting an appropriate configuration based on the JNDI name specified
in the @LiquibaseChangelog annotation, and then connecting it to the appropriate data source.
The annotation can also be omitted if only one Liquibase migration is specified in the KumuluzEE configuration file.

Example:
```java
/*
 * Injects LiquibaseContainer if only 1 Liquibase 
 * configuration is specified in the config file.
 */
@Inject
private LiquibaseContainer liquibaseContainer;

/* 
 * Injects LiquibaseContainer if only 1 Liquibase 
 * configuration is specified in the config file.
 */
@Inject
@LiquibaseChangelog
private LiquibaseContainer liquibaseContainer2;

/* 
 * Injects LiquibaseContainer for changelog with the JNDI
 * name specified in the argument of the annotation.
 */
@Inject
@LiquibaseChangelog(jndiName = "jdbc/example-db")
private LiquibaseContainer liquibaseContainer3;

public void dropAll() throws LiquibaseException {

    Liquibase liquibase = liquibaseContainer.createLiquibase();

    liquibase.dropAll();
    liquibase.validate();

}
```