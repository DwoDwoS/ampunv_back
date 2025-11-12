package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class V8__Update_Admin_Password extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(
                new SingleConnectionDataSource(context.getConnection(), true)
        );
        String adminPasswordHash = System.getenv("ADMIN_PASSWORD_HASH");

        if (adminPasswordHash == null || adminPasswordHash.isEmpty()) {
            throw new IllegalStateException(
                    "Variable d'environnement ADMIN_PASSWORD_HASH non définie"
            );
        }

        jdbcTemplate.update(
                "UPDATE users SET password = ? WHERE email = 'admin@ampunv.com'",
                adminPasswordHash
        );
    }
}