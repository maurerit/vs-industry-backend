
# Non-Vanilla SQL Compliance Issues in Database Migrations

After reviewing all the migration files in `src/main/resources/db/migration`, I've identified several areas where the SQL scripts are not compliant with standard vanilla SQL. These inconsistencies could cause portability issues when migrating to different database systems.

## 1. Missing Semicolons

Several migration files are missing semicolons at the end of SQL statements:

- `V1.0.6__warehouse_items.sql`
- `V1.0.11__products_to_ignore.sql`
- `V1.0.12__corporation_members.sql`
- `V1.0.16__add_extra_cost_table.sql`

While some database engines might be forgiving about this, standard SQL requires semicolons to terminate statements.

## 2. Inconsistent Case Usage

There's inconsistency in the case used for SQL keywords and table names:

- Some files use lowercase for SQL keywords (e.g., `create table`, `alter table`)
- Others use uppercase (e.g., `ADD COLUMN`, `DEFAULT`)
- Table names sometimes appear in uppercase (`INDUSTRY_JOBS`) and sometimes in lowercase

## 3. Non-Standard Data Types

Several non-standard or database-specific data types are used:

- `bit` for boolean values in `V1.0.5__market_transactions.sql` (SQL Server/MySQL specific)
- Mixing `real`, `decimal`, and `double` for floating-point numbers
- Using `text` data type which may have different implementations across databases

## 4. Inconsistent Default Timestamp Values

Different syntax is used for default timestamp values:

- `default current_timestamp` in some files
- `default now()` in others (PostgreSQL specific)

## 5. Inconsistent Primary Key Definitions

Primary keys are defined in multiple ways:

- Inline with column definition: `column_name type primary key`
- As a separate constraint in the CREATE TABLE statement: `PRIMARY KEY (column)`
- As an ALTER TABLE statement: `alter table table_name add constraint table_PK primary key (column)`

## 6. Inconsistent Foreign Key Definitions

Similar to primary keys, foreign keys are defined inconsistently:

- Inline within CREATE TABLE statements
- As separate ALTER TABLE statements

## 7. Missing Table Constraints

Some tables lack proper constraints:

- `corporation_member` table in `V1.0.12__corporation_members.sql` has no primary key defined

## 8. Inconsistent Naming Conventions

There's no consistent naming convention for constraints:

- Some follow patterns like `table_PK`, `table_FK1`
- Others have no explicit names, relying on database-generated names

## 9. Database-Specific SQL

Some migrations use database-specific SQL that may not work across all database systems:

- The use of `now()` function
- Table and column names with mixed case sensitivity
- The SQL in `V1.0.30__remove_test_reaction_blueprints.sql` uses subqueries in a way that might not be portable

## Recommendations

To improve SQL compliance and portability:

1. Standardize on SQL-92 compliant syntax where possible
2. Use consistent case for SQL keywords (preferably uppercase)
3. Consistently terminate all statements with semicolons
4. Use standard data types (e.g., `BOOLEAN` instead of `bit`)
5. Adopt a consistent approach to defining constraints
6. Use explicit names for all constraints following a consistent naming convention
7. Use standard timestamp defaults that work across database systems
8. Define primary keys for all tables
9. Test migrations against multiple database systems if cross-database compatibility is required