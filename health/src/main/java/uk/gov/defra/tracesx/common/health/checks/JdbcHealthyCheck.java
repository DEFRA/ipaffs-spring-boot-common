package uk.gov.defra.tracesx.common.health.checks;

import org.springframework.boot.actuate.health.Health;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.common.health.Check;

import java.util.List;

@Component
public class JdbcHealthyCheck implements Check {

    private JdbcTemplate jdbcTemplate;

    public JdbcHealthyCheck(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health check() {
        Health health;

        try {
            List<Object> results = jdbcTemplate.query("select 1", new SingleColumnRowMapper<>());
            if (results.size() != 1) {
                health = Health.down().build();
            } else {
                health = Health.up().build();
            }
        } catch (CannotGetJdbcConnectionException e) {
            health = Health.down().build();
        }

        return health;
    }
}
